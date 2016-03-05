package com.payalabs.scalajs.react.bridge

import japgolly.scalajs.react._

import scala.language.experimental.macros
import scala.reflect.macros.blackbox._
import scala.scalajs.js
import scala.scalajs.js.Dynamic.literal
import js.Dynamic.global

trait JsWriter[T] {
  def toJs(value: T) : js.Any
}

/**
 * See project's [README.md](https://github.com/payalabs/scalajs-react-bridge)
 */
abstract class ReactBridgeComponent {
  // To force all components to define at least these four common properties
  val id: js.UndefOr[String]
  val className: js.UndefOr[String]
  val ref: js.UndefOr[String]
  val key: js.UndefOr[Any]

  def component(componentPrefixes: Array[String], componentName: String, propsList: List[(String, js.UndefOr[js.Any])], children: js.Any*): ReactElement = {
    val props = literal()
    propsList.foreach { case (k, jsV) =>
      jsV.foreach { v =>
        props.updateDynamic(k)(v)
      }
    }

    val componentFunction = componentPrefixes.foldLeft(global) {
      _.selectDynamic(_)
    }.selectDynamic(componentName)
    val factory = React.createFactory(componentFunction.asInstanceOf[ReactClass[Any, Any, Any, TopNode]])
    factory(props.asInstanceOf[WrapObj[Any]], children.asInstanceOf[Seq[ReactNode]]:_*)
  }
}

object ReactBridgeComponent {

  // See https://meta.plasm.us/posts/2013/06/21/macro-methods-and-subtypes
  implicit class ReactNativeComponentThisThing[A <: ReactBridgeComponent](val value: A) extends AnyVal {
    def apply(children: js.Any*): ReactElement = macro ReactBridgeComponent.applyImpl[A]
  }

  def applyImpl[A <: ReactBridgeComponent : c.WeakTypeTag]
    (c: Context)(children: c.Expr[js.Any]*): c.Expr[ReactElement] = {

    import c.universe._
    val tpe = weakTypeTag[A].tpe

    val typeShortName = tpe.typeSymbol.fullName.split('.').last

    val params = computeParams(c)(tpe)

    val componentNamespace = tpe.baseClasses.flatMap {
      ts => ts.annotations.filter(_.tree.tpe == typeOf[ComponentNamespace])
    }.headOption.map(a => a.tree.children.tail.head.toString.tail.init.split('.')).getOrElse(Array[String]())

    val componentTree =
      q"""
         import com.payalabs.scalajs.react.bridge.JsWriter
         ${c.prefix.tree}.value.component($componentNamespace, $typeShortName, $params, ..$children)
      """

    c.Expr[ReactElement](componentTree)
  }

  private def computeParams(c: Context)(tpe: c.universe.Type): List[(String, c.universe.Tree)] = {
    import c.universe._

    tpe.decls.collect {
      case param if param.isMethod && param.asMethod.isCaseAccessor =>
        val rawParamType = param.asMethod.returnType
        val converted = {
          if (rawParamType.typeConstructor == typeOf[scala.scalajs.js.UndefOr[Any]].typeConstructor) {
            val paramType = rawParamType.typeArgs.head
            val converter = q"implicitly[JsWriter[$paramType]]"
            q"${c.prefix.tree}.value.${param.name.toTermName}.map(v => $converter.toJs(v))"
          } else {
            val paramType = rawParamType
            val converter = q"implicitly[JsWriter[$paramType]]"
            q"$converter.toJs(${c.prefix.tree}.value.${param.name.toTermName})"
          }
        }
        (param.name.toString, converted)
    }.toList
  }
}
