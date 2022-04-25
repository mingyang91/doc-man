package dev.famer.document.datatypes

import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec

case class Device(requester: String,
                  address: String,
                  model: String,
                  deviceName: String,
                  sampleName: String,
                  sampleNo: String,
                  deviceNo: String,
                  vendor: String,
                  place: String,
                  accordingTo: String,
                  equipment: String,
                  testItem: String)

object Device:
  given Codec[Device] = deriveCodec[Device]