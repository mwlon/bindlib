package org.mwlon.bindlib

trait ConstantCodifier[T] {
  def toCode(t: T): String
}

object ConstantCodifier {
  implicit object BoolCodifier extends ConstantCodifier[Boolean] {
    override def toCode(t: Boolean): String = t.toString
  }

  implicit object DoubleCodifier extends ConstantCodifier[Double] {
    override def toCode(t: Double): String = t.toString
  }

  implicit object IntCodifier extends ConstantCodifier[Int] {
    override def toCode(t: Int): String = t.toString
  }

  implicit object StringCodifier extends ConstantCodifier[String] {
    override def toCode(t: String): String = "\"" + t.replace("\\", "\\\\").replace("\"", "\\\"") + "\""
  }
}
