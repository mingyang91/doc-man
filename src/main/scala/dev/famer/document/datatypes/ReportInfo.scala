package dev.famer.document.datatypes

import io.circe.{Decoder, Encoder}

case class ReportInfo(reportNo: String,
                      date: String) derives Encoder.AsObject, Decoder
