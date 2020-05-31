package org.mwlon.bindlib

import org.mwlon.bindlib.types.BindVar

case class Bindings(map: Map[BindVar[_], Any]) {
  def get[T](v: BindVar[T]): Option[T] = {
    map.get(v).map(v.check)
  }
}
