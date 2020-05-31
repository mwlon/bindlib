package org.mwlon.bindlib

import org.mwlon.bindlib.ops.Or
import org.mwlon.bindlib.types.DoubleVar
import org.mwlon.bindlib.examples.{ObviousTensor, SmartTensorGenerator}

object Main {
  def main(args: Array[String]): Unit = {
//    val b0 = MyBool("b0")
//    val b1 = MyBool("b1")
//    val b2 = MyBool("b2")
//    val d0 = MyDouble("d0")
//    val d1 = MyDouble("d1")
//    val d2 = MyDouble("d2")
//    val d3 = MyDouble("d3")
//    val i0 = MyInt("i0")
//    val i1 = MyInt("i1")
//    val i2 = MyInt("i2")
//    val i3 = MyInt("i3")
//    val i4 = MyInt("i4")
//    val i5 = MyInt("i5")
//    val tree = Plus(Times(i0, i1), Times(i2, i3), Times(i4, i5))
//
//    println(tree)
//    val bindings = Bindings(Map(
//      i1 -> 1.0,
//      i3 -> 0.0
//    ))
//    println(tree.bind(bindings))

    val data = (0 until 35).map(_.toDouble).toArray
    val shape = Array(7, 5, 1)
    val t0 = new ObviousTensor(data, shape)
    val t1 = SmartTensorGenerator.build(data, shape)

    val n = 10000000
    val time0 = System.currentTimeMillis()
    val arr = Array(3, 2, 0)
    (0 until n).foreach((i) => {
      t0.set(arr, t0.get(arr))
    })
    println(System.currentTimeMillis() - time0)
    val time1 = System.currentTimeMillis()
    (0 until n).foreach((i) => {
      t1.set(arr, t1.get(arr))
    })
    println(System.currentTimeMillis() - time1)
  }
}
