import dev.famer.document.datatypes.{Device, Item, RenderParameters, ReportInfo}
import cats.effect.IO
import cats.effect.testing.specs2.CatsEffect
import dev.famer.document.Utils
import fs2.io.file.Files
import org.specs2.mutable.SpecificationLike

import java.time.LocalDate
import java.time.format.DateTimeFormatter

class RenderSpec extends SpecificationLike with CatsEffect:

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

  private val testParams = RenderParameters(
    device = device,
    info = reportInfo,
    items1 = List(item1),
    items2 = List(item2),
  )

  "Render" should {
    "generate docx file" in {
      val origin$ = fs2.io.readClassLoaderResource[IO]("template1.docx", 64 * 1024, this.getClass.getClassLoader)
      for
        tmp <- Files[IO].createTempFile
        _   <- origin$.through(Files[IO].writeAll(tmp)).compile.drain
        _   <- Utils.render[IO](tmp.toNioPath, testParams)
        len <- Files[IO].size(tmp)
      yield
        len must_!== 0
    }
  }

