package dev.famer.server

import cats.Monad
import cats.data.EitherT
import cats.effect.kernel.MonadCancelThrow
import cats.effect.{Async, Clock, IO}
import cats.implicits.given
import doobie.*
import doobie.implicits.given
import io.circe.{generic => circeGeneric, syntax => circeSyntax, Decoder, Encoder}
import circeGeneric.semiauto.deriveCodec
import circeSyntax.given
import sttp.model.headers.Cookie.SameSite
import sttp.model.headers.CookieValueWithMeta
import sttp.model.{StatusCode, Uri}
import sttp.tapir.*
import sttp.tapir.generic.auto.given
import sttp.tapir.json.circe.jsonBody
import pdi.jwt.{Jwt, JwtAlgorithm, JwtCirce, JwtClaim}
import scala.concurrent.duration.*

import java.time.Instant

object Login:

  case class LoginRequest(username: String, password: String) derives Encoder.AsObject, Decoder

  case class UserInfo(id: Int, role: String, username: String) derives Encoder.AsObject, Decoder

  enum AuthResponse derives Encoder.AsObject, Decoder:
    case Unauthorized(message: String)
    case Failed(message: String)

  case class LoginResponse(token: String) derives Encoder.AsObject, Decoder

  private val ep: Endpoint[Unit, LoginRequest, AuthResponse, (LoginResponse, CookieValueWithMeta), Any] =
    endpoint.post
      .in("api" / "login")
      .in(jsonBody[LoginRequest])
      .out(jsonBody[LoginResponse] and setCookie("token"))
      .errorOut(
        oneOf[AuthResponse](
          oneOfVariant(statusCode(StatusCode.Unauthorized).and(jsonBody[AuthResponse])),
          oneOfVariant(statusCode(StatusCode.InternalServerError).and(jsonBody[AuthResponse]))
        )
      )

  protected def logicImpl(timestamp: FiniteDuration, row: UserIdAndRole, username: String): (LoginResponse, CookieValueWithMeta) =
    val exp      = timestamp + 30.days
    val userInfo = UserInfo(row.id, row.role, username)
    val claim = JwtClaim(
      content = userInfo.asJson.noSpaces,
      issuedAt = Some(timestamp.toSeconds),
      expiration = Some(exp.toSeconds)
    )
    val token = JwtCirce.encode(claim, SECRET, JwtAlgorithm.HS256)

    val cookie = CookieValueWithMeta(
      value = token,
      expires = Some(Instant.ofEpochMilli(exp.toMillis)),
      maxAge = None,
      domain = None,
      path = Some("/"),
      secure = false,
      httpOnly = true,
      sameSite = Some(SameSite.Strict),
      otherDirectives = Map.empty
    )
    val body = LoginResponse(token)
    body -> cookie
  end logicImpl

  private def failedMessage(username: String) = AuthResponse.Unauthorized(s"用户名(${username})不存在或密码错误")
  def logic[F[_]: Async: Transactor](req: LoginRequest): F[Either[AuthResponse, (LoginResponse, CookieValueWithMeta)]] =
    val timestampImpl = EitherT.liftF(Clock[F].realTime)
    val rowImpl       = EitherT.fromOptionF(fopt = execute[F](req.username, req.password), ifNone = failedMessage(req.username))
    val flow          = for (timestamp <- timestampImpl; row <- rowImpl) yield logicImpl(timestamp, row, req.username)
    flow.value
  end logic

  def router[F[_]: Async: Transactor] = ep.serverLogic(logic[F])

  def getUserInfoAndExp(token: String): Either[String, (UserInfo, Long)] =
    val claimImpl1 = JwtCirce.decode(token, SECRET, JwtAlgorithm.HS256 :: Nil)
    val claimImpl2 = for (e <- claimImpl1.toEither.left) yield e.getMessage

    def infoImpl1(using claim: JwtClaim)                 = io.circe.parser.decode[UserInfo](claim.content)
    val infoImpl2: JwtClaim ?=> Either[String, UserInfo] = for (e <- infoImpl1.left) yield e.getMessage

    def toRight(using claim: JwtClaim) = claim.expiration.toRight("Token not have expire")

    for
      given JwtClaim <- claimImpl2
      info           <- infoImpl2
      exp            <- toRight
    yield info -> exp
  end getUserInfoAndExp

  case class UserIdAndRole(id: Int, role: String)

  def findUser(username: String, password: String): ConnectionIO[Option[UserIdAndRole]] =
    val sqlM = sql"""SELECT id, role FROM "user" WHERE username = $username and password = $password"""
    sqlM.query[UserIdAndRole].option
  end findUser

  def execute[F[_]: Async](username: String, password: String)(using xa: Transactor[F]): F[Option[UserIdAndRole]] =
    findUser(username, password).transact(xa)

end Login
