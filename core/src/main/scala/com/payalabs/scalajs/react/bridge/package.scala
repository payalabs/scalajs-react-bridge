package com.payalabs.scalajs.react

import scala.language.experimental.macros
import scala.scalajs.js
import scala.scalajs.js.JSConverters.JSRichOption
import scala.scalajs.js.Object

import japgolly.scalajs.react.component.Js
import japgolly.scalajs.react.vdom.{TagMod, VdomNode}
import japgolly.scalajs.react.{CallbackTo, Children, CtorType}


package object bridge extends GeneratedImplicits {
  def writerFromConversion[A](implicit conv: A => js.Any): JsWriter[A] = JsWriter(x => x)
  implicit def stringWriter: JsWriter[String] = writerFromConversion[String]
  implicit def intWriter: JsWriter[Int] = writerFromConversion[Int]
  implicit def boolWriter: JsWriter[Boolean] = writerFromConversion[Boolean]
  implicit def unitWriter: JsWriter[Unit] = writerFromConversion[Unit]
  implicit def jsAnyWriter[A <: js.Any]: JsWriter[A] = JsWriter(identity)

  implicit def callbackToWriter[T](implicit writerT: JsWriter[T]): JsWriter[CallbackTo[T]] =
    JsWriter(value => value.map(writerT.toJs).runNow())

  implicit def undefOrWriter[A](implicit writerA: JsWriter[A]): JsWriter[js.UndefOr[A]] =
    JsWriter(_.map(writerA.toJs))

  implicit def optionWriter[A](implicit writerA: JsWriter[A]): JsWriter[Option[A]] =
    JsWriter(_.map(writerA.toJs).orUndefined)

  implicit def seqWriter[T: JsWriter]: JsWriter[Seq[T]] = {
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
