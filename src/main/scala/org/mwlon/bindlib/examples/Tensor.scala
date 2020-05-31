package org.mwlon.bindlib.examples

abstract class Tensor(data: Array[Double], shape: Array[Int]) {
  def getFlatInd(inds: Array[Int]): Int

  def get(inds: Array[Int]): Double = {
    data(getFlatInd(inds))
  }

  def set(inds: Array[Int], value: Double): Unit = {
    data(getFlatInd(inds)) = value
  }
}
