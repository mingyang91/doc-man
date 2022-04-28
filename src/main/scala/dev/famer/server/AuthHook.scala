package dev.famer.server

import cats.Monad
import cats.data.EitherT
import cats.effect.{Async, IO, MonadCancelThrow, Sync}
import cats.implicits.*
import dev.famer.document.datatypes.*
import dev.famer.server.AuthHook.AuthHookResponse.Unauthorized
import dev.famer.server.Login.UserInfo
import fs2.io.file.{Files, Path}
import io.circe.generic.semiauto.deriveCodec
import io.circe.{Codec, Decoder, Encoder, Json}
import org.http4s.HttpRoutes
import sttp.model.StatusCode
import sttp.tapir.*
import sttp.tapir.generic.auto.*
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.server.http4s.Http4sServerInterpreter
import org.typelevel.log4cats.Logger
import pdi.jwt.{JwtAlgorithm, JwtCirce, JwtOptions}
import sttp.model.headers.Cookie

import java.time.format.DateTimeFormatter
import java.time.{Instant, ZoneOffset}
import scala.util.Try

object AuthHook:
  val ep: Endpoint[Unit, AuthHookPayload, AuthHookResponse, Map[String, String], Any] =
    endpoint
      .post
      .in("private-api" / "auth-hook")
      .in(jsonBody[AuthHookPayload])
      .out(jsonBody[Map[String, String]])
      .errorOut(oneOf[AuthHookResponse](
        oneOfVariant(statusCode(StatusCode.Unauthorized).and(jsonBody[AuthHookResponse.Unauthorized])),
        oneOfVariant(statusCode(StatusCode.InternalServerError).and(jsonBody[AuthHookResponse.Failed])),
      ))

  given Codec[AuthHookPayload] = deriveCodec[AuthHookPayload]

  given Codec[AuthHookRequest] = deriveCodec[AuthHookRequest]

  given Schema[Json] = Schema.binary

  given Codec[AuthHookResponse.Unauthorized] = deriveCodec[AuthHookResponse.Unauthorized]

  given Codec[AuthHookResponse.Failed] = deriveCodec[AuthHookResponse.Failed]

  def router[F[_]: Async: Logger] = ep.serverLogic(logic[F])

  def logic[F[_]: Async: Logger](payload: AuthHookPayload): F[Either[AuthHookResponse, Map[String, String]]] =
    val flow = for
      cookieStr <- EitherT.fromOption[F](payload.headers.get("Cookie"), Unauthorized("No cookie found"))
      cookies   <- EitherT.fromEither[F](Cookie.parse(cookieStr))
                          .leftMap(Unauthorized(_))
      _         <- EitherT.rightT(Logger[F].debug("auth-hook" + payload.toString))
      token     <- EitherT.fromOption[F](cookies.find(_.name == "token"), Unauthorized("No token found in cookie"))
      claim     <- EitherT(Async[F].delay(JwtCirce.decode(token.value, SECRET, JwtAlgorithm.HS256 :: Nil).toEither))
                          .leftMap(e => Unauthorized(e.getMessage))
      json      <- EitherT.fromEither(io.circe.parser.parse(claim.content))
                          .leftMap(e => Unauthorized(e.getMessage))
      info      <- EitherT.fromEither(json.as[UserInfo])
                          .leftMap(e => Unauthorized(e.getMessage))
      exp       <- EitherT.fromOption(claim.expiration, Unauthorized("Token not have expire"))
    yield
      val expires = Instant.ofEpochSecond(exp)
      Map(
        "X-Hasura-User-Id" -> info.id.toString,
        "X-Hasura-Role" -> info.role,
        "X-Hasura-Is-Owner" -> (info.role == "admin").toString,
        "Expires" -> expires.atOffset(ZoneOffset.UTC).format(DateTimeFormatter.RFC_1123_DATE_TIME)// "Mon, 30 Mar 2033 13:25:18 GMT"
      )

    flow.value

  // {
  //  "headers": {
  //    "header-key1": "header-value1",
  //    "header-key2": "header-value2"
  //  },
  //  "request": {
  //    "variables": {
  //      "a": 1
  //    },
  //    "operationName": "UserQuery",
  //    "query": "query UserQuery($a:  Int) {\n  users(where:  {id:  {_eq:  $a}}){\n    id\n  }\n}\n"
  //  }
  //}
  case class AuthHookPayload(headers: Map[String, String],
                             request: AuthHookRequest)

  case class AuthHookRequest(variables: Option[Map[String, Json]],
                             operationName: Option[String],
                             query: String)

  enum AuthHookResponse:
    case Unauthorized(message: String)
    case Failed(message: String)
