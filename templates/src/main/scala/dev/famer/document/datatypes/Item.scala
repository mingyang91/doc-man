package dev.famer.document.datatypes

case class Item(name: String,
                conditionFactor: String,
                defaultValue: String,
                result: String,
                acceptanceRequire: String,
                stateRequire: String,
                conclusion: String
               )
