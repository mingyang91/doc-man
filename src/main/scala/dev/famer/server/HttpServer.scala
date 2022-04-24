//package dev.famer.server
//
//import sttp.tapir.serverless.aws.lambda.AwsCatsEffectServerInterpreter
//import zhttp.http.*
//import zhttp.service.Server
//import zio.*
//import zio.stream.ZStream
//
//import java.net.{Inet4Address, InetAddress}
//import java.nio.charset.StandardCharsets
//import java.nio.file.Paths
//import zio.json.*
//
//
//case class Parameters(top: Map[String, String],
//                      items1: List[Map[String, String]],
//                      items2: List[Map[String, String]])
//
//object Parameters:
//  given JsonDecoder[Parameters] = DeriveJsonDecoder.gen
//
//object HttpServer:
//  import FileUtils._
//
//  private val router: Http[Any, Throwable, Request, Response] = Http.collectZIO[Request] {
//    case req@(Method.POST -> !! / "report" / "render") =>
//      renderLogic(req)
//    case Method.GET -> !! / "report" / "render" =>
//      ZIO.succeed(Response.text("Use POST please."))
//  }
//
//  def renderLogic(req: Request): Task[Response] =
//    val flow = for
//      buf <- req.data.toByteBuf
//        .mapError(e => Response.text(e.getMessage).setStatus(Status.InternalServerError))
//      content = buf.toString(StandardCharsets.UTF_8)
//      params <- ZIO.fromEither(content.fromJson[Parameters])
//        .mapError(e => Response.text(e).setStatus(Status.BadRequest))
//      _ <- ZIO.logDebug(s"params: $params")
//    yield
//      val path = Paths.get("/Users/famer.me/Downloads/report.docx")
//      val filestream = ZStream.fromPath(path)
//      val resp = Response(
//        data = HttpData.fromStream(filestream),
//      )
//        .withContentDisposition(s"attachment; filename*=${path.getName}")
//
//      MediaType.forFileExtension(path.getExtension)
//        .fold(resp)(resp.withMediaType)
//
//    flow.merge
//
//
//  def start() =
//    ZIO.logLevel(LogLevel.All) {
//      Server.start[Any](9000, router)
//    }
//
//
//object FileUtils:
//  extension (f: java.nio.file.Path)
//    def getExtension: String =
//      val name = f.getName.toString()
//      name.lastIndexOf('.') match
//        case -1 => ""
//        case i  => name.takeRight(name.length - 1 - i)
//
