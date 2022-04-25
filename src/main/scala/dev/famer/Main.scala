package dev.famer
import cats.effect.{ExitCode, IO, IOApp}
import dev.famer.document.Utils
import java.nio.file.Paths
import fs2.io.file.{Files, Path}

object Main extends IOApp:
  override def run(args: List[String]): IO[ExitCode] =
    {
      val origin = Path.fromNioPath(Paths.get(this.getClass.getClassLoader.getResource("template1.docx").toURI))
      val dist = Path("/Users/famer.me/Downloads/report.docx")
      for
        _ <- Files[IO].deleteIfExists(dist)
        _ <- Files[IO].copy(origin, dist)
        file <- Utils.render[IO](dist.toNioPath)
      yield file
    } *> IO.println("Welcome to your first Cats-Effect app!")
      .as(ExitCode.Success)



