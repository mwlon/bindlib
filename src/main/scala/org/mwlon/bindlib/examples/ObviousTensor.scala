package org.mwlon.bindlib.examples

class ObviousTensor(data: Array[Double], shape: Array[Int]) extends Tensor(data, shape) {
  val dims: Int = shape.length
  val strides: Array[Int] = {
    val shapeProd = shape.product
    var stride = shapeProd
    shape.map((side) => {
      stride /= side
      stride
    })
  }

  override def getFlatInd(inds: Array[Int]): Int = {
    //this is fairly slow because it wastes time on the loop check that i < dims and unnecessarily multiplies by 1
    var flatInd = 0
    for (i <- 0 until dims) {
      flatInd += strides(i) * inds(i)
    }
    flatInd
  }
}
