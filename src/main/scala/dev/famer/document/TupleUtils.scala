package dev.famer.document

import dev.famer.document.datatypes.Device

import scala.Tuple.Zip
import scala.compiletime.constValueTuple
import scala.deriving.Mirror
import scala.reflect.Typeable

object TupleUtils:
  inline private def labelsOf[A](using p: Mirror.ProductOf[A]) =
    constValueTuple[p.MirroredElemLabels]

  extension [A <: Product](a: A)(using p: Mirror.ProductOf[A], fr: ToMap[Zip[p.MirroredElemLabels, p.MirroredElemTypes]])
    inline def toMap: Map[String, String] =
      val labels = labelsOf[A]
      val values = Tuple.fromProductTyped(a)
      fr.toMap(labels zip values)

  trait Encoder[A]:
    def encode(a: A): String

  given Encoder[String] with
    def encode(a: String): String = a

  given [A]: Encoder[A] with
    def encode(a: A): String = a.toString

  trait ToMap[A]:
    def toMap(a: A): Map[String, String]

  given ToMap[EmptyTuple] with
    def toMap(a: EmptyTuple): Map[String, String] = Map.empty

  given [A: Encoder, H <: (String, A), T <: Tuple: ToMap]: ToMap[H *: T] with
    def toMap(a: H *: T): Map[String, String] =
      val (key, value) = a.head
      val t            = a.tail
      summon[ToMap[T]].toMap(t) + (key -> summon[Encoder[A]].encode(value))
