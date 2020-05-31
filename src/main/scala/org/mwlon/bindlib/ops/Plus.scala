package org.mwlon.bindlib.ops

import org.mwlon.bindlib.Func
import org.mwlon.bindlib.{BindCache, Bindings, Constant, ConstantCodifier, Func}

case class Plus[T](nums: Func[T]*)(implicit numeric: Numeric[T], codifier: ConstantCodifier[T]) extends Func[T] {
  override val name: String = "Plus"
  override val children: Seq[Func[_]] = nums

  override def cachedBind(bindings: Bindings)(implicit bindCache: BindCache): Func[T] = {
    val boundNums = nums.map(bindCache.get)
    val constantTerm = boundNums.flatMap((num) => num.getOption).sum
    val incompleteTerms = boundNums.filter((num) => num.getOption.isEmpty)
    (constantTerm, incompleteTerms.length) match {
      case (0, 0) => Constant(0.asInstanceOf[T])
      case (0, 1) => incompleteTerms.head
      case (0, _) => Plus(incompleteTerms: _*)
      case (x, 0) => Constant(x)
      case (x, 1) => Plus(Constant(x), incompleteTerms.head)
      case (x, _) =>
        val allTerms = Seq(Constant(x)) ++ incompleteTerms
        Plus(allTerms: _*)
    }
  }

  override def toCode: String = s"(${nums.map(_.toCode).mkString(" + ")})"
}
