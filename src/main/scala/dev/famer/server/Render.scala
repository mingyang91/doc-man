package dev.famer.server

import cats.Monad
import sttp.tapir.*
import sttp.tapir.server.http4s.Http4sServerInterpreter
import cats.effect.{IO, Sync}
import org.http4s.HttpRoutes
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.serverless.aws.lambda.AwsCatsEffectServerInterpreter

object Render:

  val ep: Endpoint[Unit, Unit, Unit, String, Any] =
    endpoint
      .get
      .in("api")
      .out(jsonBody[String])

  def router[F[_]: Monad] = ep.serverLogic(_ => Monad[F].pure(Right("Hello")))
