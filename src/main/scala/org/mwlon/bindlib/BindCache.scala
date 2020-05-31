package org.mwlon.bindlib

import scala.collection.mutable

class BindCache(bindings: Bindings) {
  val map: mutable.Map[Func[_], Func[_]] = mutable.Map()

  def get[T](func: Func[T]): Func[T] = {
    val cached = map.get(func)
    if (cached.isEmpty) {
      val res = func.cachedBind(bindings)(this)
      map(func) = res
      res
    } else {
      cached.get match {
        case f: Func[T] => f
        case _ => throw new Error("what is this garbage")
      }
    }
  }
}
