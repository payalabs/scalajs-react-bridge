package com.payalabs.scalajs.react.bridge

import scala.annotation.StaticAnnotation

/**
 * Annotation to specify JS namespace for the underlying component.
 *
 * Certain libraries such as `ReactBootstrap` define components in a namespace.
 * This annotation allows specifying a namespace for the bridge to look for
 * the underlying component's function.
 *
 * See [ReactBootstrapComponent](https://github.com/payalabs/scalajs-react-bridge-example/src/main/scala/com/payalabs/scalajs/react/bridge/elements/ReactBootstrapBridge.scala)
 * in the example app for typical usage.
 *
 * @param value a '.' separated string of namespace (a package-like string)
 */
case class ComponentNamespace(value: String) extends StaticAnnotation

