package dev.famer.document.datatypes

import io.circe.{Decoder, Encoder}


case class RenderParameters(device: Device,
                            info: ReportInfo,
                            items1: List[Item],
                            items2: List[Item]) derives Encoder.AsObject, Decoder
