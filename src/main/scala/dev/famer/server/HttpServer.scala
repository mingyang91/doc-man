package dev.famer.server

import cats.effect.{IO, Resource}
import org.http4s.ember.server.EmberServerBuilder
import com.comcast.ip4s.*
import org.http4s.server.{Router, Server}

object HttpServer:
  def start(host: Host, port: Port): Resource[IO, Server] =
    val app = Router("/" -> Render.routes[IO]).orNotFound

    EmberServerBuilder
      .default[IO]
      .withHost(host)
      .withPort(port)
      .withHttpApp(app)
      .build