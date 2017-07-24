package com.payalabs.scalajs.react.bridge.elements

import com.payalabs.scalajs.react.bridge.{ReactBridgeComponent, WithPropsNoChildren}
import japgolly.scalajs.react.{Callback, CallbackTo}

import scala.scalajs.js

/**
 * Bridge to [TagsInput](https://github.com/olahol/react-tagsinput)'s component
 */
object TagsInput extends ReactBridgeComponent {
  def apply(defaultValue: js.UndefOr[Seq[String]] = js.undefined,
            value: js.UndefOr[Seq[String]] = js.undefined,
            placeholder: js.UndefOr[String] = js.undefined,
            onChange: js.UndefOr[js.Array[String] => Callback] = js.undefined,
            validate: js.UndefOr[String => CallbackTo[Boolean]] = js.undefined,
            transform: js.UndefOr[String => CallbackTo[String]] = js.undefined): WithPropsNoChildren = autoNoChildren
}
