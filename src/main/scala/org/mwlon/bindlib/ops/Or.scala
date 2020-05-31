package org.mwlon.bindlib.ops

import org.mwlon.bindlib.Func
import org.mwlon.bindlib.{BindCache, Bindings, Constant, Func}

case class Or(bs: Func[Boolean]*) extends Func[Boolean] {
  override val name: String = "Or"
  override val children: Seq[Func[_]] = bs

  override def cachedBind(bindings: Bindings)(implicit bindCache: BindCache): Func[Boolean] = {
    val boundBs = bs.map(bindCache.get)
    if (boundBs.exists(_.getOption.exists((value) => value))) {
      Constant(true)
    } else {
      val incomplete = boundBs.filter(_.getOption.isEmpty)
      if (incomplete.isEmpty) {
        Constant(false)
      } else if (incomplete.length == 1) {
        incomplete.head
      } else {
        Or(incomplete: _*)
      }
    }
  }

  override def toCode: String = s"(${bs.map(_.toCode).mkString(" || ")})"
}
