package org.mwlon.bindlib

case class Constant[T](value: T)(implicit codifier: ConstantCodifier[T]) extends Func[T] {
  val children: Seq[Func[_]] = Seq()
  override val name: String = ""

  override def toString: String = value.toString
  override def cachedBind(bindings: Bindings)(implicit bindCache: BindCache): Func[T] = {
    this
  }
  override def getOption: Option[T] = {
    Some(value)
  }

  override def toCode: String = codifier.toCode(value)
}
