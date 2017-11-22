package com.payalabs.scalajs.react.bridge.test

import scala.scalajs.js

import com.payalabs.scalajs.react.bridge.ReactBridgeComponent


/**
  * This is here to "test" (at compile-time) that ReactComponentBridge works with generics
  */
object GenericComponent extends ReactBridgeComponent {
  def apply[A <: js.Any](param: A): Any = auto
}
