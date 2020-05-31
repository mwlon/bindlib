package org.mwlon.bindlib.ops

import org.mwlon.bindlib.Func
import org.mwlon.bindlib.types.IntVar
import org.mwlon.bindlib.{BindCache, Bindings, Func}

case class Multi(ops: Seq[Func[Unit]]) extends Func[Unit] {
  override val name: String = "Multi"
  override val children: Seq[Func[_]] = ops

  override def cachedBind(bindings: Bindings)(implicit bindCache: BindCache): Func[Unit] = {
    val boundOps = ops.map(bindCache.get).filter(_ match {
      case Noop => false
      case _ => true
    })
    boundOps.length match {
      case 0 => Noop
      case 1 => boundOps.head
      case _ => Multi(boundOps)
    }
  }

  override def toCode: String = ops.map(_.toCode).mkString("\n")
}
