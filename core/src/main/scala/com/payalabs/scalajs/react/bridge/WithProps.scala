package com.payalabs.scalajs.react.bridge

import japgolly.scalajs.react.vdom.{TagMod, VdomElement, VdomNode}

import scala.scalajs.js

class WithProps(jsComponent: JsComponentType, jsProps: js.Object) {
  def apply(attrAndChildren: TagMod*): WithPropsAndTagsMods = {
    val (props, children) = extractPropsAndChildren(attrAndChildren)

    val mergedProps = mergeJSObjects(props.asInstanceOf[js.Dynamic], jsProps.asInstanceOf[js.Dynamic])

    new WithPropsAndTagsMods(jsComponent, mergedProps.asInstanceOf[js.Object], children)
  }

  def apply(children: VdomNode*): VdomElement = {
    apply(List.empty[TagMod]: _*)(children: _*)
  }

  private def mergeJSObjects(objs: js.Dynamic*): js.Dynamic = {
    val result = js.Dictionary.empty[Any]
    for (source <- objs) {
      for ((key, value) <- source.asInstanceOf[js.Dictionary[Any]])
        result(key) = value
    }
    result.asInstanceOf[js.Dynamic]
  }
}
