package com.payalabs.scalajs.react.bridge

import japgolly.scalajs.react._

import scala.language.experimental.macros
import scala.reflect.macros.blackbox._
import scala.scalajs.js
import scala.scalajs.js.Dynamic.literal
import js.Dynamic.global

/**
 * See project's [README.md](https://github.com/payalabs/scalajs-react-bridge)
 */
abstract class ReactBridgeComponent {
  // To force all components to define at least these four common properties
  val id: js.UndefOr[String]
  val className: js.UndefOr[String]
  val ref: js.UndefOr[String]
  val key: js.UndefOr[Any]

  def component(componentPrefixes: Array[String], componentName: String, propsList: List[(String, js.UndefOr[Any])], children: js.Any*): ReactElement = {
    val props = literal()
    propsList.foreach { case (k, v) =>
      v.foreach { v =>
        val jsV = ReactBridgeComponent.convert(v)
        props.updateDynamic(k)(jsV)
      }
    }

    val componentFunction = componentPrefixes.foldLeft(global) {
      _.selectDynamic(_)
    }.selectDynamic(componentName)
    val factory = React.createFactory(componentFunction.asInstanceOf[ReactComponentType[Any, Any, Any, TopNode]])
    factory(props.asInstanceOf[WrapObj[Any]], children.asInstanceOf[Seq[ReactNode]])
  }
}

object ReactBridgeComponent {

  // See https://meta.plasm.us/posts/2013/06/21/macro-methods-and-subtypes
  implicit class ReactNativeComponentThisThing[A <: ReactBridgeComponent](val value: A) extends AnyVal {
    def apply(children: js.Any*): ReactElement = macro ReactBridgeComponent.applyImpl[A]
  }

  def applyImpl[A <: ReactBridgeComponent : c.WeakTypeTag](c: Context)(children: c.Expr[js.Any]*): c.Expr[ReactElement] = {
    import c.universe._
    val tpe = weakTypeTag[A].tpe

    val typeShortName = tpe.typeSymbol.fullName.split('.').last

    val params = computeParams(c)(tpe)

    val componentNamespace = tpe.baseClasses.flatMap {
      ts => ts.annotations.filter(_.tree.tpe == typeOf[ComponentNamespace])
    }.headOption.map(a => a.tree.children.tail.head.toString.tail.init.split('.')).getOrElse(Array[String]())

    val componentTree = q"""${c.prefix.tree}.value.component($componentNamespace, $typeShortName, $params, ..$children)"""

    c.Expr[ReactElement](componentTree)
  }

  private def computeParams(c: Context)(tpe: c.universe.Type): List[(String, c.universe.Tree)] = {
    import c.universe._

    tpe.decls.collect {
      case param if param.isMethod && param.asMethod.isCaseAccessor =>
        (param.name.toString, q"${c.prefix.tree}.value.${param.name.toTermName}")
    }.toList
  }

  val converters = Seq[PartialFunction[Any, js.Any]](toLiteral, toArray, toJsFunction, toAny)

  def convert(value: Any): js.Any = {
    val converter = converters.find { converter => converter.isDefinedAt(value) }
    converter.get.apply(value)
  }

  private def toJsFunction: PartialFunction[Any, js.Function] = {
    import js.Any._
    {
      case jf: Function0[_] => fromFunction0(jf)
      case jf: Function1[_, _] => fromFunction1(jf)
      case jf: Function2[_, _, _] => fromFunction2(jf)
      case jf: Function3[_, _, _, _] => fromFunction3(jf)
      case jf: Function4[_, _, _, _, _] => fromFunction4(jf)
      case jf: Function5[_, _, _, _, _, _] => fromFunction5(jf)
      case jf: Function6[_, _, _, _, _, _, _] => fromFunction6(jf)
      case jf: Function7[_, _, _, _, _, _, _, _] => fromFunction7(jf)
      case jf: Function8[_, _, _, _, _, _, _, _, _] => fromFunction8(jf)
      case jf: Function9[_, _, _, _, _, _, _, _, _, _] => fromFunction9(jf)
      case jf: Function10[_, _, _, _, _, _, _, _, _, _, _] => fromFunction10(jf)
      case jf: Function11[_, _, _, _, _, _, _, _, _, _, _, _] => fromFunction11(jf)
      case jf: Function12[_, _, _, _, _, _, _, _, _, _, _, _, _] => fromFunction12(jf)
      case jf: Function13[_, _, _, _, _, _, _, _, _, _, _, _, _, _] => fromFunction13(jf)
      case jf: Function14[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _] => fromFunction14(jf)
      case jf: Function15[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _] => fromFunction15(jf)
      case jf: Function16[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _] => fromFunction16(jf)
      case jf: Function17[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _] => fromFunction17(jf)
      case jf: Function18[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _] => fromFunction18(jf)
      case jf: Function19[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _] => fromFunction19(jf)
      case jf: Function20[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _] => fromFunction20(jf)
      case jf: Function21[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _] => fromFunction21(jf)
      case jf: Function22[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _] => fromFunction22(jf)
    }
  }

  private def toLiteral: PartialFunction[Any, js.Dynamic] = {
    case map: Map[String, js.Any] =>
      val literal = js.Dynamic.literal()
      map.foreach { case (k, v) =>
        literal.updateDynamic(k)(v)
      }
      // The following line (used instead of the current impl caused ScalaJS optimizer to crash)
      //js.Dynamic.literal.applyDynamic("apply")(map.toSeq: _*)
      literal
  }

  private def toArray: PartialFunction[Any, js.Array[_]] = {
    case seq: Seq[_] => js.Array(seq: _*)
  }

  private def toAny: PartialFunction[Any, js.Any] = {
    case obj: ReactBridgeObject => obj.toJS
    case value => value.asInstanceOf[js.Any]
  }
}

/**
 *  extends this, to convert scala classes to js literals
 */
trait ReactBridgeObject extends Any {
  def toJS : js.Any
}