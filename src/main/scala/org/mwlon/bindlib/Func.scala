package org.mwlon.bindlib

import org.mwlon.bindlib.types.BindVar

import scala.collection.mutable

trait Func[+T] {
  val name: String
  val children: Seq[Func[_]]
  private var cachedVariables: Option[Set[BindVar[_]]] = None
  def variables: Set[BindVar[_]] = {
    cachedVariables.orElse({
      val resMut = mutable.Set[BindVar[_]]()
      println(children)
      children.foreach((child) => {
        resMut.addAll(child.variables)
      })
      val res = Some(resMut.toSet)
      cachedVariables = res
      res
    }).get
  }

  def bind(bindings: Bindings): Func[T] = {
    implicit val bindCache: BindCache = new BindCache(bindings)
    bindCache.get(this)
  }
  def cachedBind(bindings: Bindings)(implicit bindCache: BindCache): Func[T]
  def getOption: Option[T] = None
  def get: T = {
    val res = getOption
    if (res.isEmpty) {
      throw new Error("This still depends on variables: " + variables.map(_.name).mkString(", "))
    }
    res.get
  }

  override def toString: String = {
    s"${name}(${children.map(_.toString).mkString(", ")})"
  }

  def toCode: String
}
