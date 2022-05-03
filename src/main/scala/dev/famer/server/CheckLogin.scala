package dev.famer.server

import cats.implicits.*
import cats.data.EitherT
import cats.effect.Async
import sttp.model.StatusCode
import sttp.tapir.*
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.generic.auto.*
import io.circe.generic.semiauto.*
import io.circe.Codec

object CheckLogin:
  enum CheckLoginResponse:
    case Unauthorized(error: String)
    case InternalServerError(error: String)

  object CheckLoginResponse:
    given Codec[CheckLoginResponse.Unauthorized] = deriveCodec[CheckLoginResponse.Unauthorized]
    given Codec[CheckLoginResponse.InternalServerError] = deriveCodec[CheckLoginResponse.InternalServerError]

  val ep: Endpoint[Unit, String, CheckLoginResponse, Login.UserInfo, Any] =
    endpoint
      .get
      .in("api" / "user" / "me")
      .in(cookie[String]("token"))
      .out(jsonBody[Login.UserInfo])
      .errorOut(oneOf[CheckLoginResponse](
        oneOfVariant(statusCode(StatusCode.Unauthorized).and(jsonBody[CheckLoginResponse.Unauthorized])),
        oneOfVariant(statusCode(StatusCode.InternalServerError).and(jsonBody[CheckLoginResponse.InternalServerError])),
      ))

  def logic[F[_]: Async](token: String): F[Either[CheckLoginResponse, Login.UserInfo]] =
    val flow = for {
      userInfoAndExp <- EitherT.fromEither[F](Login.getUserInfoAndExp(token))
        .leftMap(e => CheckLoginResponse.Unauthorized(e))
    } yield userInfoAndExp._1

    flow.value

  def router[F[_]: Async] = ep.serverLogic(logic[F])