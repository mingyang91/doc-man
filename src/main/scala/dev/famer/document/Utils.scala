package dev.famer.document

import cats.implicits.*
import cats.effect.{Async, MonadCancelThrow, Resource, Sync}
import dev.famer.document.datatypes.{Device, Item, ReportInfo, Values}
import fs2.io.file.Files
import fs2.{Chunk, Stream}

import java.net.URI
import java.nio.file.{FileSystem, FileSystems, Path}
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import scala.jdk.CollectionConverters._

object Utils {

  import dev.famer.document.TupleUtils.*

  private val device = Device(
    requester = "上海市黄浦区外滩街道社区卫生服务中心",
    address = "宁波路321号",
    model = "Q-Rad",
    deviceName = "医用检测X射线DR机",
    sampleName = "医用检测X射线DR机",
    sampleNo = "Hxxxx-1",
    deviceNo = "KL10019",
    vendor = "锐珂（上海）医疗器材有限公司",
    place = "摄片机房",
    accordingTo = "WS 76—2020《医用X射线诊断设备质量控制检测规范》",
    equipment = "B-PIRANHA多功能 X射线测试仪，DR质量模体",
    testItem = "设备质量控制（状态监测）"
  )

  private val item1 = Item(
    name = "管电压指示的偏离",
    conditionFactor = "100 mA,0.125s",
    defaultValue = "60kV",
    result = "-3.12%(-1.87kV)",
    acceptanceRequire = "±5.0%或±5.0 kV内,以较大者控制",
    stateRequire = "±5.0%或±5.0 kV内,以较大者控制",
    conclusion = "合格"
  )

  private val item2 = Item(
    name = "探测器剂量指示(DDI)",
    conditionFactor = "（10μGy）\n70kV, 10mAs",
    defaultValue = "",
    result = "1767 (-1.98%)\n（平均像素值）",
    acceptanceRequire = "DDI (10μGy)计算值与测量值±20.0%，DDI或平均像素值建立基线值",
    stateRequire = "DDI测量值与计算值±20.0%，或基线值±20.0%",
    conclusion = "合格"
  )

  private val reportInfo = ReportInfo(
    reportNo = "FYS-2022-H-XXXX",
    date = LocalDate.now()
      .format(DateTimeFormatter.ofPattern("yyyy年MM月dd日")),
  )

  def renderDocument(): String =
    html.report.render(
      Values(device.toMap ++ reportInfo.toMap),
      List.tabulate(5)(_ => Values(item1.toMap)),
      List.tabulate(5)(_ => Values(item2.toMap)),
    ).body

  def renderHeader1(): String =
    html.header1.render(Values(reportInfo.toMap)).body

  private val PATH_TO_DOCUMENT_XML = "word/document.xml"
  private val PATH_TO_HEADER1_XML = "word/header1.xml"

  def replaceIntoDocx[F[_] : Async : MonadCancelThrow](fs: FileSystem, target: String, bytes: Chunk[Byte]): F[Unit] =
    val path = fs2.io.file.Path.fromNioPath(fs.getPath(target))
    for
      _ <- Files[F].delete(path)
      _ <- Files[F].createFile(path)
      pipe = Files[F].writeAll(path)
      _ <- Stream.chunk[F, Byte](bytes).through(pipe).compile.drain
    yield ()


  private val env = Map("create" -> "false").asJava

  def useZipFS[F[_] : Sync](path: Path): Resource[F, FileSystem] =
    val uri = path.toUri
    val full = URI.create("jar:" + uri.toString)
    val acquire = Sync[F].blocking(FileSystems.newFileSystem(full, env))

    def release(fs: FileSystem) = Sync[F].blocking(fs.close())

    Resource.make[F, FileSystem](acquire)(release)


  def replace[F[_] : Sync : Async : MonadCancelThrow](path: Path,
                                                      headerBytes: Chunk[Byte],
                                                      bodyBytes: Chunk[Byte]): F[Unit] =
    useZipFS[F](path).use { zipFs =>
      replaceIntoDocx(zipFs, PATH_TO_DOCUMENT_XML, bodyBytes) *>
        replaceIntoDocx(zipFs, PATH_TO_HEADER1_XML, headerBytes)
    }


  private val XML_HEADER = Chunk.array("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>".getBytes())

  def render[F[_] : Sync : Async : MonadCancelThrow](path: Path): F[Unit] =
    val header = renderHeader1()
    val content = renderDocument()
    replace[F](
      path,
      XML_HEADER ++ Chunk.array(header.getBytes()),
      XML_HEADER ++ Chunk.array(content.getBytes())
    )
}
