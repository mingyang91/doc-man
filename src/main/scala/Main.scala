import dev.famer.document.Utils
import zio.*
import zio.Console.printLine
import zio.nio.file.{Files, Path}

object Main extends ZIOAppDefault:
  override def run: ZIO[Any, Any, Any] =
    {
      val origin = Path(this.getClass.getClassLoader.getResource("template1.docx").toURI)
      val dist = Path("/Users/famer.me/Downloads/report.docx")
      for
        _ <- Files.deleteIfExists(dist)
        _ <- Files.copy(origin, dist)
        file <- Utils.render(dist)
      yield file
    } *> printLine("Welcome to your first ZIO app!")
