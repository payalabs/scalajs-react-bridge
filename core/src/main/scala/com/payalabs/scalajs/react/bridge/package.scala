package com.payalabs.scalajs.react

import scala.language.experimental.macros
import com.payalabs.scalajs.react.bridge.JsWriter
import japgolly.scalajs.react.component.Js
import japgolly.scalajs.react.{CallbackTo, Children, CtorType}
import japgolly.scalajs.react.vdom.{TagMod, VdomNode}

import scala.scalajs.js
import scala.scalajs.js.Object

trait LowPriorityImplicits {
  implicit def anyWriter[T]: JsWriter[T] = _.asInstanceOf[js.Any]
}

package object bridge extends GeneratedImplicits with LowPriorityImplicits {
  implicit def callbackWriter[T]: JsWriter[CallbackTo[T]] = _.toJsFn

  implicit def seqWriter[T : JsWriter]: JsWriter[Seq[T]] = {
    val elementWriter = implicitly[JsWriter[T]]

    (value: Seq[T]) => js.Array(value.map(e => elementWriter.toJs(e)): _*)
  }

  implicit def immutableSeqWriter[T : JsWriter]: JsWriter[scala.collection.immutable.Seq[T]] = {
    val elementWriter = implicitly[JsWriter[T]]

    (value: scala.collection.immutable.Seq[T]) => js.Array(value.map(e => elementWriter.toJs(e)): _*)
  }

  implicit def mapWriter[T : JsWriter]: JsWriter[Map[String, T]] = {
    val elementWriter = implicitly[JsWriter[T]]

    (value: Map[String, T]) => {
      val converted = value.map { case (k, v) => (k, elementWriter.toJs(v)) }
      js.Dictionary(converted.toSeq: _*)
    }
  }

  type JsComponentType = Js.ComponentSimple[Object, CtorType.Summoner.Aux[Object, Children.Varargs, CtorType.PropsAndChildren]#CT, Js.UnmountedWithRawType[Object, Null, Js.RawMounted]]

  def extractPropsAndChildren(attrAndChildren: Seq[TagMod]): (js.Object, List[VdomNode]) = {
    import japgolly.scalajs.react.vdom.Implicits._

    val b = new japgolly.scalajs.react.vdom.Builder.ToJs {}
    attrAndChildren.toTagMod.applyTo(b)
    b.addClassNameToProps()
    b.addStyleToProps()

    (b.props, b.childrenAsVdomNodes)
  }


}
