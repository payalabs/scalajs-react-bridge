package com.payalabs.scalajs.react.bridge

import scala.scalajs.js


trait JsWriter[T] {
  def toJs(value: T): js.Any
}
object JsWriter {
  def apply[A](f: A => js.Any): JsWriter[A] = new JsWriter[A] {
    override def toJs(value: A) = f(value)
  }
}
