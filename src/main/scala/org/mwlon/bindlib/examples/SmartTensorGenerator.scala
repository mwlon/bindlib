package org.mwlon.bindlib.examples

import org.mwlon.bindlib.Func
import org.mwlon.bindlib.ops.Plus
import org.mwlon.bindlib.types.BindVar
import org.mwlon.bindlib.Bindings
import org.mwlon.bindlib.ops.{Plus, Times}
import org.mwlon.bindlib.types.{IntVar, BindVar}

import scala.collection.mutable


object SmartTensorGenerator {
  def build(data: Array[Double], shape: Array[Int]): Tensor = {
    val shapeProd = shape.product
    var stride = shapeProd
    val strides = shape.map((side) => {
      stride /= side
      stride
    })
    val args = shape.indices.map((i) => IntVar(TensorTemplate.varname(i)))
    val mults = shape.indices.map((i) => IntVar(s"mult$i"))
    val prodTerms = shape.indices.map((i) => Times(mults(i), args(i)))
    val bindingMap = mutable.Map.empty[BindVar[_], Any]
    shape.indices.foreach((i) => {
      bindingMap(mults(i)) = strides(i)
    })
    val getFlatIndFunc = Plus(prodTerms: _*)
      .bind(Bindings(bindingMap.toMap))
    val clazz = TensorTemplate.generate(getFlatIndFunc, shape)
    clazz.getDeclaredConstructors()(0).newInstance(data).asInstanceOf[Tensor]
  }
}
