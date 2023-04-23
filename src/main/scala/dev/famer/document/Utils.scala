package dev.famer.document

import cats.implicits.*
import cats.effect.{Async, MonadCancelThrow, Resource, Sync}
import dev.famer.document.datatypes.{Device, Item, RenderParameters, ReportInfo, Values}
import fs2.io.file.Files
import fs2.{Chunk, Stream}

import java.net.URI
import java.nio.file.{FileSystem, FileSystems, Path}
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import scala.jdk.CollectionConverters.*

object Utils {

  import dev.famer.document.TupleUtils.*

  def renderDocument(params: RenderParameters): String =
    html.report
      .render(
        Values(params.device.toMap ++ params.info.toMap),
        params.items1.map(_.toMap).map(Values.apply),
        params.items2.map(_.toMap).map(Values.apply)
      )
      .body

  def renderHeader1(params: RenderParameters): String =
    html.header1.render(Values(params.info.toMap)).body

  private val PATH_TO_DOCUMENT_XML = "word/document.xml"
  private val PATH_TO_HEADER1_XML  = "word/header1.xml"

  def replaceIntoDocx[F[_]: Async: MonadCancelThrow](fs: FileSystem, target: String, bytes: Chunk[Byte]): F[Unit] =
    val path = fs2.io.file.Path.fromNioPath(fs.getPath(target))
    for
      _ <- Files[F].delete(path)
      _ <- Files[F].createFile(path)
      pipe = Files[F].writeAll(path)
      _ <- Stream.chunk[F, Byte](bytes).through(pipe).compile.drain
    yield ()

  private val env = Map("create" -> "false").asJava

  def useZipFS[F[_]: Sync](path: Path): Resource[F, FileSystem] =
    val uri     = path.toUri
    val full    = URI.create("jar:" + uri.toString)
    val acquire = Sync[F].blocking(FileSystems.newFileSystem(full, env))

    def release(fs: FileSystem) = Sync[F].blocking(fs.close())

    Resource.make[F, FileSystem](acquire)(release)

  def replace[F[_]: Sync: Async: MonadCancelThrow](path: Path, headerBytes: Chunk[Byte], bodyBytes: Chunk[Byte]): F[Unit] =
    useZipFS[F](path).use { zipFs =>
      replaceIntoDocx(zipFs, PATH_TO_DOCUMENT_XML, bodyBytes) *>
        replaceIntoDocx(zipFs, PATH_TO_HEADER1_XML, headerBytes)
    }

  private val XML_HEADER = Chunk.array("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>".getBytes())

  def render[F[_]: Sync: Async: MonadCancelThrow](path: Path, params: RenderParameters): F[Unit] =
    val header  = renderHeader1(params)
    val content = renderDocument(params)
    replace[F](
      path,
      XML_HEADER ++ Chunk.array(header.getBytes()),
      XML_HEADER ++ Chunk.array(content.getBytes())
    )
}
