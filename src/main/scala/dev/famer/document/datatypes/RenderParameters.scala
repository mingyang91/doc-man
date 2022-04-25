package dev.famer.document.datatypes

import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec


case class RenderParameters(device: Device,
                            info: ReportInfo,
                            items1: List[Item],
                            items2: List[Item])
object RenderParameters:
  given Codec[RenderParameters] = deriveCodec[RenderParameters]
