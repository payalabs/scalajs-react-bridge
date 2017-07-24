package com.payalabs.scalajs.react.bridge

import japgolly.scalajs.react.vdom.{VdomElement, VdomNode}
import japgolly.scalajs.react.vdom.Implicits._

import scala.scalajs.js

class WithPropsAndTagModsAndChildren(jsComponent: JsComponentType, jsProps: js.Object, children: List[VdomNode]) {
  def apply: VdomElement = jsComponent(jsProps)(children: _*)
}
