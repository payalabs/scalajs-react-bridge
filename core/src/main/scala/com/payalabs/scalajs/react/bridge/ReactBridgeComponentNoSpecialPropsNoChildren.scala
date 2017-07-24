package com.payalabs.scalajs.react.bridge

import japgolly.scalajs.react.vdom.{TagMod, VdomElement}

abstract class ReactBridgeComponentNoSpecialPropsNoChildren extends ReactBridgeComponent {
  def apply(attrAndChildren: TagMod*): VdomElement = {
    val (props, children) = extractPropsAndChildren(attrAndChildren)

    new WithPropsAndTagModsAndChildren(jsComponent, props, children).apply
  }
}

