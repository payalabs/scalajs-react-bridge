package com.payalabs.scalajs.react.bridge.elements

import com.payalabs.scalajs.react.bridge.ReactBridgeComponent

/**
  * Common class for all [ReactBootstrap](http://react-bootstrap.github.io/)'s components
  */
abstract class ReactBootstrapComponent extends ReactBridgeComponent {
  override def componentNamespace: String = "ReactBootstrap"
}
