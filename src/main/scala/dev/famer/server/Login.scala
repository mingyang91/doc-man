package dev.famer.server

import cats.Monad
import cats.data.EitherT
import cats.effect.kernel.MonadCancelThrow
import cats.effect.{Async, Clock, IO}
import cats.implicits.*
import doobie.*
import doobie.implicits.*
import io.circe.generic.semiauto.deriveCodec
import io.circe.{Codec, Decoder, Encoder, Json}
import io.circe.syntax.*
import sttp.model.headers.Cookie.SameSite
import sttp.model.headers.CookieValueWithMeta
import sttp.model.{StatusCode, Uri}
import sttp.tapir.*
import sttp.tapir.generic.auto.*
import sttp.tapir.json.circe.jsonBody
import pdi.jwt.{Jwt, JwtAlgorithm, JwtCirce, JwtClaim}
import scala.concurrent.duration._

import java.time.Instant

object Login:

  case class LoginRequest(username: String, password: String) derives Encoder.AsObject, Decoder

  case class UserInfo(id: Int, role: String, username: String) derives Encoder.AsObject, Decoder

  enum AuthResponse:
    case Unauthorized(message: String)
    case Failed(message: String)

  object AuthResponse:
    given Codec[AuthResponse.Unauthorized] = deriveCodec[AuthResponse.Unauthorized]
    given Codec[AuthResponse.Failed] = deriveCodec[AuthResponse.Failed]

  val ep: Endpoint[Unit, LoginRequest, AuthResponse, CookieValueWithMeta, Any] =
    endpoint
      .post
      .in("api" / "login")
      .in(jsonBody[LoginRequest])
      .out(setCookie("token"))
      .errorOut(oneOf[AuthResponse](
        oneOfVariant(statusCode(StatusCode.Unauthorized).and(jsonBody[AuthResponse.Unauthorized])),
        oneOfVariant(statusCode(StatusCode.InternalServerError).and(jsonBody[AuthResponse.Failed]))
      ))

  def logic[F[_] : Async : Transactor](req: LoginRequest): F[Either[AuthResponse, CookieValueWithMeta]] =
    val flow = for
      timestamp <- EitherT.liftF(Clock[F].realTime)
      row       <- EitherT.fromOptionF(
                      fopt = execute[F](req.username, req.password),
                      ifNone = AuthResponse.Unauthorized(s"用户名(${req.username})不存在或密码错误")
                  )
    yield
      val exp = timestamp + 30.days
      val claim = JwtClaim(
        content = UserInfo(row.id, row.role, req.username).asJson.noSpaces,
        issuedAt = Some(timestamp.toSeconds),
        expiration = Some(exp.toSeconds)
      )
      val token = JwtCirce.encode(claim, SECRET, JwtAlgorithm.HS256)

      CookieValueWithMeta(
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

    flow.value

  def router[F[_] : Async: Transactor] = ep.serverLogic(logic[F])

  def getUserInfoAndExp(token: String): Either[String, (UserInfo, Long)] =
    for
      claim     <- JwtCirce.decode(token, SECRET, JwtAlgorithm.HS256 :: Nil)
                      .toEither
                      .left.map(e => e.getMessage)
      info      <- io.circe.parser.decode[UserInfo](claim.content)
                      .left.map(e => e.getMessage)
      exp       <- claim.expiration
                      .toRight("Token not have expire")
    yield info -> exp

  case class UserIdAndRole(id: Int, role: String)

  def findUser(username: String, password: String): ConnectionIO[Option[UserIdAndRole]] =
    sql"""SELECT id, role FROM "user" WHERE username = $username and password = $password"""
      .query[UserIdAndRole]
      .option

  def execute[F[_] : Async : Transactor](username: String, password: String): F[Option[UserIdAndRole]] =
    summon[Transactor[F]].trans.apply(findUser(username, password))

