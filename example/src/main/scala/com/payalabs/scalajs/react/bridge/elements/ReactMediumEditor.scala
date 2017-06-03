package com.payalabs.scalajs.react.bridge.elements

import com.payalabs.scalajs.react.bridge.{ReactBridgeComponent, WithPropsNoChildren}
import japgolly.scalajs.react.{Callback, Key}

import scala.scalajs.js

/**
 * Bridge to [ReactMediumEditor](https://github.com/wangzuo/react-medium-editor)'s component
 */
object ReactMediumEditor extends ReactBridgeComponent {
  def apply(text: js.UndefOr[String] = js.undefined,
            onChange: js.UndefOr[String => Callback] = js.undefined,
            options: js.UndefOr[Map[String, Any]] = js.undefined): WithPropsNoChildren = autoConstructNoChildren
}
