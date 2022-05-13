package dev.famer.document.datatypes

import io.circe.{Decoder, Encoder}

case class Item(name: String,
                conditionFactor: String,
                defaultValue: String,
                result: String,
                acceptanceRequire: String,
                stateRequire: String,
                conclusion: String) derives Encoder.AsObject, Decoder
