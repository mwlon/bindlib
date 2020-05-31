package org.mwlon.bindlib.ops

import org.mwlon.bindlib.Func
import org.mwlon.bindlib.{BindCache, Bindings, Func}

object Noop extends Func[Unit] {
  override val name: String = "Noop"
  override val children: Seq[Func[_]] = Seq()

  override def cachedBind(bindings: Bindings)(implicit bindCache: BindCache): Func[Unit] = this

  override def toCode: String = ""
}
