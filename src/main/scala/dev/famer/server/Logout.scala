package dev.famer.server

import cats.Monad
import cats.data.EitherT
import cats.effect.kernel.MonadCancelThrow
import cats.effect.{Async, Clock, IO}
import cats.implicits.*
import sttp.model.headers.Cookie.SameSite
import sttp.model.headers.CookieValueWithMeta
import sttp.model.{StatusCode, Uri}
import sttp.tapir.*
import sttp.tapir.generic.auto.*
import sttp.tapir.json.circe.jsonBody

import java.time.Instant
import scala.concurrent.duration.*

object Logout:

  val ep: Endpoint[Unit, Unit, Unit, CookieValueWithMeta, Any] =
    endpoint.get.in("api" / "logout").out(setCookie("token"))

  def logic[F[_]: Async]: F[Either[Unit, CookieValueWithMeta]] =
    Async[F].pure(
      Right(
        CookieValueWithMeta(
          value = "",
          expires = None,
          maxAge = Some(0),
          domain = None,
          path = Some("/"),
          secure = false,
          httpOnly = true,
          sameSite = Some(SameSite.Strict),
          otherDirectives = Map.empty
        )
      )
    )

  def router[F[_]: Async] = ep.serverLogic(_ => logic[F])
