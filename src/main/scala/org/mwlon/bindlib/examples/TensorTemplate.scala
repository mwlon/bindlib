package org.mwlon.bindlib.examples

import org.mwlon.bindlib.Func

import scala.reflect.runtime.universe
import scala.tools.reflect.ToolBox

object TensorTemplate {
  var count = 0
  val indsArrayName = "inds"
  val tb = universe.runtimeMirror(getClass.getClassLoader).mkToolBox()

  def varname(i: Int): String = {
    s"$indsArrayName($i)"
  }

  def generate(flatIndFunc: Func[Int], shape: Array[Int]): Class[_] = {
    val shapeStr = s"Array(${shape.mkString(",")})"
    val className = s"TensorImpl$count"
    val objDef =
      s"""
        |import org.mwlon.bindlib.examples.Tensor
        |
        |class $className(data: Array[Double]) extends Tensor(data, ${shapeStr}) {
        |  override def getFlatInd($indsArrayName: Array[Int]): Int = {
        |    ${flatIndFunc.toCode}
        |  }
        |}
        |
        |scala.reflect.classTag[$className].runtimeClass
        |""".stripMargin
    println(s"generated tensor code for $className:\n=====\n$objDef\n=====")
    count += 1
    val res = tb.compile(tb.parse(objDef))()
    res.asInstanceOf[Class[_]]
  }
}
