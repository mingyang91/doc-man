package dev.famer.server

import cats.Monad
import cats.effect.{Async, IO, Resource}
import org.http4s.ember.server.EmberServerBuilder
import com.comcast.ip4s.*
import dev.famer.server.AuthHook.router
import doobie.util.transactor.Transactor
import org.http4s.HttpRoutes
import org.http4s.server.{Router, Server}
import sttp.tapir.server.http4s.Http4sServerInterpreter
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger

object HttpServer:

  private def makeTransactor[F[_]: Async]: Transactor[F] =
    Transactor.fromDriverManager[F](
      driver = "org.postgresql.Driver",
      url = sys.env.getOrElse("POSTGRES_URL", "jdbc:postgresql://postgres:5432/postgres"),
      user = sys.env.getOrElse("POSTGRES_USERNAME", "postgres"),
      pass = sys.env.getOrElse("POSTGRES_PASSWORD", "postgres")
    )

  def routes[F[_]: Async]: HttpRoutes[F] =
    given Transactor[F] = makeTransactor
    given Logger[F] = Slf4jLogger.getLogger[F]

    Http4sServerInterpreter[F]().toRoutes(
      List(
        Login.router[F],
        Logout.router[F],
        Render.router[F],
        AuthHook.router[F],
        CheckLogin.router[F],
      )
    )

  def start(host: Host, port: Port): Resource[IO, Server] =

    val app = Router(
      "/" -> routes[IO],
    ).orNotFound

    EmberServerBuilder
      .default[IO]
      .withHost(host)
      .withPort(port)
      .withHttpApp(app)
      .build