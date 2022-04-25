package dev.famer

import cats.effect.{ExitCode, IO, IOApp}
import dev.famer.document.Utils
import dev.famer.document.datatypes.{Device, Item, RenderParameters, ReportInfo}
import dev.famer.server.HttpServer

import java.nio.file.Paths
import fs2.io.file.{Files, Path}
import fs2.Stream
import com.comcast.ip4s.*

import java.time.LocalDate
import java.time.format.DateTimeFormatter

object Main extends IOApp:
  override def run(args: List[String]): IO[ExitCode] =
    HttpServer.start(ip"::", port"9000")
      .use(_ => IO.never)
      .as(ExitCode.Success)



