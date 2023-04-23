package dev.famer.server

import cats.Monad
import cats.implicits.*
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
    endpoint.post.in("api" / "render").in(query[String]("template")).in(jsonBody[RenderParameters]).out(fileBody)

  def copy[F[_]: Monad: Async](src: String, dst: fs2.io.file.Path): F[Unit] =
    fs2.io
      .readClassLoaderResource[F]("template1.docx", 64 * 1024, this.getClass.getClassLoader)
      .through(Files[F].writeAll(dst))
      .compile
      .drain

  def logic[F[_]: Monad: Async](template: String, params: RenderParameters): F[Either[Unit, TapirFile]] =
    for
      dst <- Files[F].createTempFile
      _   <- copy[F]("template1.docx", dst)
      _   <- Utils.render[F](dst.toNioPath, params)
    yield Right(dst.toNioPath.toFile)

  def router[F[_]: Monad: Async] = ep.serverLogic(logic)
