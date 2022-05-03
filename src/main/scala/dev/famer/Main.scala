package dev.famer

import cats.effect.kernel.Resource
import cats.effect.{ExitCode, IO, IOApp, ResourceApp}
import dev.famer.document.Utils
import dev.famer.document.datatypes.{Device, Item, RenderParameters, ReportInfo}
import dev.famer.server.HttpServer

import java.nio.file.Paths
import fs2.io.file.{Files, Path}
import fs2.Stream
import com.comcast.ip4s.*

import java.time.LocalDate
import java.time.format.DateTimeFormatter

object Main extends ResourceApp.Forever:
  def run(args: List[String]): Resource[IO, Unit] =
    HttpServer.start(ip"0.0.0.0", port"9000").map(_ => ())

