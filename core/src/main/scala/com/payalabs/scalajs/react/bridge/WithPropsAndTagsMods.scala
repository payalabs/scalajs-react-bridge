package com.payalabs.scalajs.react.bridge

import japgolly.scalajs.react.vdom.{VdomElement, VdomNode}

import scala.scalajs.js

import scala.language.implicitConversions

class WithPropsAndTagsMods(jsComponent: JsComponentType, jsProps: js.Object, tagModChildren: List[VdomNode]) {
  def apply(children: VdomNode*): VdomElement = {
    new WithPropsAndTagModsAndChildren(jsComponent, jsProps, tagModChildren ++ children).apply
  }
}

object WithPropsAndTagsMods {
  implicit def toVdomNode(wp: WithPropsAndTagsMods): VdomNode = wp.apply()
}
