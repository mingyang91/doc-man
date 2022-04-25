package dev.famer.document.datatypes

class Values(private val underlying: Map[String, String]) {
  def apply(key: String): String = underlying.getOrElse(key, s"$key unsetted")
  private[famer] def getInstance: Map[String, String] = underlying
}

object Values {
  def apply(underlying: Map[String, String]): Values = new Values(underlying)
}
