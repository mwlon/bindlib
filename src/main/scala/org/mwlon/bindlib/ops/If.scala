package org.mwlon.bindlib.ops

import org.mwlon.bindlib.Func
import org.mwlon.bindlib.{BindCache, Bindings, Func}

case class If[T](b: Func[Boolean], left: Func[T], right: Func[T]) extends Func[T] {
  override val name: String = "If"
  override val children: Seq[Func[_]] = Seq(b, left, right)

  override def cachedBind(bindings: Bindings)(implicit bindCache: BindCache): Func[T] = {
    val bBound = bindCache.get(b)
    bBound.getOption match {
      case Some(true) => bindCache.get(left)
      case Some(false) => bindCache.get(right)
      case _ => If(bBound, bindCache.get(left), bindCache.get(right))
    }
  }

  override def toCode: String = f"if (${b.toCode}) ${left.toCode} else ${right.toCode}"
}
