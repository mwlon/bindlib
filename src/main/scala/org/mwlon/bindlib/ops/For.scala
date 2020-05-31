package org.mwlon.bindlib.ops

import org.mwlon.bindlib.Func
import org.mwlon.bindlib.{BindCache, Bindings, Func}
import org.mwlon.bindlib.types.IntVar

case class For(loopVar: IntVar, n: Func[Int], op: Func[Unit], config: ForConfig = ForConfig()) extends Func[Unit] {
  override val name: String = "For"
  override val children: Seq[Func[_]] = Seq(n, op)

  override def cachedBind(bindings: Bindings)(implicit bindCache: BindCache): Func[Unit] = {
    val boundN = bindCache.get(n)
    val boundOp = bindCache.get(op)
    (boundN.getOption, boundOp) match {
      case (_, Noop) => Noop
      case (Some(0), _) => Noop
      case (Some(1), _) => boundOp.bind(Map(loopVar -> 0))
      case (Some(nLoops), _) if nLoops <= config.fullExpansionLimit =>
        Multi((0 until nLoops).map((i) => boundOp.bind(Map(loopVar -> i))))
      case (None, _) =>
        For(loopVar, boundN, boundOp, config)
    }
  }

  override def toCode: String = {
    s"""for (${loopVar.name} <- 0 until ${n.toCode}) {
       |${op.toCode}
       |}
       |""".stripMargin
  }
}

case class ForConfig(fullExpansionLimit: Int = 1)
