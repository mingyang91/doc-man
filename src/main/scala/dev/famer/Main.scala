package dev.famer
import cats.effect.{ExitCode, IO, IOApp}
import dev.famer.document.Utils
import java.nio.file.Paths
import fs2.io.file.{Files, Path}
import fs2.Stream

object Main extends IOApp:
  override def run(args: List[String]): IO[ExitCode] =
    {
      val origin$ = fs2.io.readClassLoaderResource[IO]("template1.docx", 64 * 1024, this.getClass.getClassLoader)
      val dist = Path("/Users/famer.me/Downloads/report.docx")
      for
        _ <- Files[IO].deleteIfExists(dist)
        _ <- Files[IO].createFile(dist)
        _ <- origin$.through(Files[IO].writeAll(dist)).compile.drain
        file <- Utils.render[IO](dist.toNioPath)
      yield file
    } *> IO.println("Welcome to your first Cats-Effect app!")
      .as(ExitCode.Success)



