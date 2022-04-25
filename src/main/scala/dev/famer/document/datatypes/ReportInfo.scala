package dev.famer.document.datatypes

import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec

case class ReportInfo(reportNo: String,
                      date: String)

object ReportInfo:
  given Codec[ReportInfo] = deriveCodec[ReportInfo]