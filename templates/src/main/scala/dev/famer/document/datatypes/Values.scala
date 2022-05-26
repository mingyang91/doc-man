package dev.famer.document.datatypes

opaque type Values = Map[String, String]
extension (values: Values)
  def field(key: String): String = values.getOrElse(key, s"$key unsetted")

object Values {
  def apply(underlying: Map[String, String]): Values = underlying
}
