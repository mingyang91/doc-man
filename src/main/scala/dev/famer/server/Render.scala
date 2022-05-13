package dev.famer.server

import cats.Monad
import cats.implicits._
import sttp.tapir.*
import sttp.tapir.generic.auto.*
import sttp.tapir.server.http4s.Http4sServerInterpreter
import cats.effect.{Async, IO, MonadCancelThrow, Sync}
import dev.famer.document.datatypes.{Device, Item, RenderParameters, ReportInfo, Values}
import fs2.io.file.{Files, Path}
import org.http4s.HttpRoutes
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.serverless.aws.lambda.AwsCatsEffectServerInterpreter
import dev.famer.document.Utils

object Render:

  val ep: Endpoint[Unit, (String, RenderParameters), Unit, TapirFile, Any] =
    endpoint
      .post
      .in("api" / "render")
      .in(query[String]("template"))
      .in(jsonBody[RenderParameters])
      .out(fileBody)

  def logic[F[_]: Monad: Async](template: String, params: RenderParameters): F[Either[Unit, TapirFile]] =
    val origin$ = fs2.io.readClassLoaderResource[F]("template1.docx", 64 * 1024, this.getClass.getClassLoader)
    for
      dist <- Files[F].createTempFile
      _ <- origin$.through(Files[F].writeAll(dist)).compile.drain
      _ <- Utils.render[F](dist.toNioPath, params)
    yield Right(dist.toNioPath.toFile)

  def router[F[_]: Monad: Async] = ep.serverLogic(logic)
