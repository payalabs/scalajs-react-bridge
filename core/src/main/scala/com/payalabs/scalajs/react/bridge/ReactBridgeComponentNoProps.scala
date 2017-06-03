package com.payalabs.scalajs.react.bridge

import japgolly.scalajs.react.vdom.TagMod

abstract class ReactBridgeComponentNoProps extends ReactBridgeComponent {
  def apply(attrAndChildren: TagMod*): WithPropsAndTagsMods = {
    val (props, children) = extractPropsAndChildren(attrAndChildren)

    new WithPropsAndTagsMods(jsComponent, props, children)
  }
}

