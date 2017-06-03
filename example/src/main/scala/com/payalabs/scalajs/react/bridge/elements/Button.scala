package com.payalabs.scalajs.react.bridge.elements

import com.payalabs.scalajs.react.bridge.WithProps

import scala.scalajs.js

/**
 * Bridge to [ReactBootstrap](http://react-bootstrap.github.io/)'s Button component
 */
object Button extends ReactBootstrapComponent {
  def apply(bsStyle: js.UndefOr[String] = js.undefined,
            bsSize: js.UndefOr[String] = js.undefined,
            active: js.UndefOr[Boolean] = js.undefined,
            block: js.UndefOr[Boolean] = js.undefined,
            disabled: js.UndefOr[Boolean] = js.undefined,
            href: js.UndefOr[String] = js.undefined,
            onClick: js.UndefOr[() => Unit] = js.undefined): WithProps = autoConstruct
}
