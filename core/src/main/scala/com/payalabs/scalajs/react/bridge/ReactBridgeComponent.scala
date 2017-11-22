package com.payalabs.scalajs.react.bridge

import scala.language.implicitConversions
import scala.language.experimental.macros
import scala.reflect.macros.blackbox._
import scala.scalajs.js
import js.Dynamic.global
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.VdomElement

/**
 * See project's [README.md](https://github.com/payalabs/scalajs-react-bridge)
 */

abstract class ReactBridgeComponent {

  /**
    * JS namespace for the underlying component.
    *
    * Certain libraries such as `ReactBootstrap` define components in a namespace.
    * This property allows specifying a namespace for the bridge to look for
    * the underlying component's function.
    *
    * See [ReactBootstrapComponent](https://github.com/payalabs/scalajs-react-bridge-example/src/main/scala/com/payalabs/scalajs/react/bridge/elements/ReactBootstrapBridge.scala)
    * in the example app for typical usage.
    *
    * A '.' separated string of namespace (a package-like string)
    */
  protected lazy val componentNamespace: String = ""


  // Class names generated by Scala for inner types (such as one used from our test cases have the following form)
  // com.payalabs.scalajs.react.bridge.test.ReactBridgeComponentTest$TestComponent$20$
  // We would have liked to use getSimpleName, but it fails miserably (see https://github.com/scala/bug/issues/2034)
  // So, we use our own "parsing" to extract the simple name
  protected lazy val componentName: String = this.getClass.getName.split('.').last.split('$').reverse.dropWhile(_.forall(_.isDigit)).head

  protected lazy val jsComponent = {
    val componentPrefixes = if (componentNamespace.trim.isEmpty) Array[String]() else componentNamespace.split('.')

    val componentFunction = componentPrefixes.foldLeft(global) {
      _.selectDynamic(_)
    }.selectDynamic(componentName)

    JsComponent[js.Object, Children.Varargs, Null](componentFunction)
  }

  def auto: WithProps = macro ReactBridgeComponent.autoImpl

  def autoNoChildren: WithPropsNoChildren = macro ReactBridgeComponent.autoNoChildrenImpl

  def autoNoTagMods: WithPropsAndTagsMods = macro ReactBridgeComponent.autoNoTagModsImpl

  def autoNoTagModsNoChildren: VdomElement = macro ReactBridgeComponent.autoNoTagModsNoChildrenImpl

}

object ReactBridgeComponent {
  def autoImpl(c: Context): c.Expr[WithProps] = {
    import c.universe._

    val props = computeParams(c)

    c.Expr[WithProps](
      q"""
         {
           import com.payalabs.scalajs.react.bridge.JsWriter
           import com.payalabs.scalajs.react.bridge.ReactBridgeComponent

           new WithProps(${c.prefix.tree}.jsComponent, ReactBridgeComponent.propsToDynamic($props))
         }"""
    )
  }

  def autoNoChildrenImpl(c: Context): c.Expr[WithPropsNoChildren] = {
    import c.universe._

    val props = computeParams(c)

    c.Expr[WithPropsNoChildren](
      q"""
         {
           import com.payalabs.scalajs.react.bridge.JsWriter
           import com.payalabs.scalajs.react.bridge.ReactBridgeComponent

           new WithPropsNoChildren(${c.prefix.tree}.jsComponent, ReactBridgeComponent.propsToDynamic($props))
         }"""
    )
  }

  def autoNoTagModsImpl(c: Context): c.Expr[WithPropsAndTagsMods] = {
    import c.universe._

    val props = computeParams(c)

    c.Expr[WithPropsAndTagsMods](
      q"""
         {
           import com.payalabs.scalajs.react.bridge.JsWriter
           import com.payalabs.scalajs.react.bridge.ReactBridgeComponent

           new WithPropsAndTagsMods(${c.prefix.tree}.jsComponent, ReactBridgeComponent.propsToDynamic($props), List())
         }"""
    )
  }

  def autoNoTagModsNoChildrenImpl(c: Context): c.Expr[VdomElement] = {
    import c.universe._

    val props = computeParams(c)

    c.Expr[VdomElement](
      q"""
         {
           import com.payalabs.scalajs.react.bridge._
           import com.payalabs.scalajs.react.bridge.ReactBridgeComponent

           new WithPropsAndTagModsAndChildren(${c.prefix.tree}.jsComponent, ReactBridgeComponent.propsToDynamic($props), List()).apply
         }"""
    )
  }

  /**
    * Convert params passed to the apply method into their JS equivalent and pack them into a js.Dynamic
    * @param c
    * @return
    * @see JsWriter
    */
  private def computeParams(c: Context): c.Expr[List[(String, Option[js.Any])]] = {
    import c.universe._

    val props = {
      val params = c.internal.enclosingOwner.asMethod.paramLists.flatten.filter(!_.isImplicit)
      val convertedProps = params.map { param =>
        val rawParamType = c.typecheck(Ident(param.name)).tpe
        val converted = {
          if (rawParamType.typeConstructor == typeOf[scala.scalajs.js.UndefOr[Any]].typeConstructor) {
            val paramType = rawParamType.typeArgs.head
            val converter = q"implicitly[JsWriter[$paramType]]"
            q"${param.name.toTermName}.map(v => $converter.toJs(v)).toOption"
          } else {
            val paramType = rawParamType
            val converter = q"implicitly[JsWriter[$paramType]]"
            q"Some($converter.toJs(${param.name.toTermName}))"
          }
        }
        (param.name.toString, converted)
      }

      convertedProps
    }

    c.Expr[List[(String, Option[js.Any])]](q"$props")
  }

  def propsToDynamic(props: List[(String, Option[js.Any])]): js.Object = {
    import scala.scalajs.js.Dynamic.literal

    val jsProps = literal()
    props.foreach { case (k, jsV) =>
      jsV.foreach { v =>
        jsProps.updateDynamic(k)(v)
      }
    }

    jsProps.asInstanceOf[js.Object]
  }


}