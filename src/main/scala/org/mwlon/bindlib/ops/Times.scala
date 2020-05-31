package org.mwlon.bindlib.ops

import org.mwlon.bindlib.Func
import org.mwlon.bindlib.{BindCache, Bindings, Constant, ConstantCodifier, Func}

case class Times[T](nums: Func[T]*)(implicit numeric: Numeric[T], codifier: ConstantCodifier[T]) extends Func[T] {
  override val name: String = "Times"
  override val children: Seq[Func[_]] = nums

  override def cachedBind(bindings: Bindings)(implicit bindCache: BindCache): Func[T] = {
    val boundNums = nums.map(bindCache.get)
    val constantTerm = boundNums.flatMap((num) => num.getOption).product
    val incompleteTerms = boundNums.filter((num) => num.getOption.isEmpty)
    (constantTerm, incompleteTerms.length) match {
      case (0, _) => Constant(0.asInstanceOf[T])
      case (_, 0) => Constant(constantTerm)
      case (1, 1) => incompleteTerms.head
      case (1, _) => Times(incompleteTerms: _*)
      case _ =>
        val allTerms = Seq(Constant(constantTerm)) ++ incompleteTerms
        Times(allTerms: _*)
    }
  }

  override def toCode: String = s"(${nums.map(_.toCode).mkString(" * ")})"
}
