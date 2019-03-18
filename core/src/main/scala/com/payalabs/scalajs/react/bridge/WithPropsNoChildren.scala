package com.payalabs.scalajs.react.bridge

import scala.language.implicitConversions

import japgolly.scalajs.react.vdom.{TagMod, VdomElement}

import scala.scalajs.js

class WithPropsNoChildren(jsComponent: JsComponentType, jsProps: js.Object) {
  def apply(attrAndChildren: TagMod*): VdomElement = {
    new WithProps(jsComponent, jsProps).apply(attrAndChildren: _*).apply()
  }
}

object WithPropsNoChildren {
  implicit def toVdomElement(wp: WithPropsNoChildren): VdomElement = wp.apply()
}