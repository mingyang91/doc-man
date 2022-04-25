package dev.famer.document.datatypes

import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec

case class Item(name: String,
                conditionFactor: String,
                defaultValue: String,
                result: String,
                acceptanceRequire: String,
                stateRequire: String,
                conclusion: String
               )

object Item:
  given Codec[Item] = deriveCodec[Item]