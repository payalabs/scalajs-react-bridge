package com.payalabs.scalajs.react.bridge

import scala.scalajs.js

trait JsWriter[T] {
  def toJs(value: T): js.Any
}
