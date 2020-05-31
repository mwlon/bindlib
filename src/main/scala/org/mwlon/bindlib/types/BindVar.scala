package org.mwlon.bindlib.types

import org.mwlon.bindlib.Func
import org.mwlon.bindlib.{BindCache, Bindings, Constant, ConstantCodifier, Func}

class BindVar[T](override val name: String)(implicit converter: ConstantCodifier[T]) extends Func[T] {
  val children: Seq[Func[_]] = Seq()

  def check(value: Any): T = {
    value match {
      case a: T =>
        a
      case _ =>
        throw new Error("what is this")
    }
  }

  override def cachedBind(bindings: Bindings)(implicit bindCache: BindCache): Func[T] = {
    bindings
      .get(this)
      .map(Constant(_))
      .getOrElse(this)
  }

  override def toString: String = "$" + name

  //eventually would need to validate this as real variable name
  override def toCode: String = name
}
