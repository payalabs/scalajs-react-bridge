package com.payalabs.scalajs.react

import com.payalabs.scalajs.react.bridge.JsWriter

import scala.scalajs.js

trait LowPriorityImplicits {
  implicit def anyWriter[T]: JsWriter[T] = {
    new JsWriter[T] {
      override def toJs(value: T): js.Any = value.asInstanceOf[js.Any]
    }
  }
}

package object bridge extends GeneratedImplicits with LowPriorityImplicits {
  implicit def seqWriter[T : JsWriter]: JsWriter[Seq[T]] = {
    val elementWriter = implicitly[JsWriter[T]]

    new JsWriter[Seq[T]] {
      def toJs(value: Seq[T]): js.Any = {
        js.Array(value.map(e => elementWriter.toJs(e)): _*)
      }
    }
  }

  implicit def immutableSeqWriter[T : JsWriter]: JsWriter[scala.collection.immutable.Seq[T]] = {
    val elementWriter = implicitly[JsWriter[T]]

    new JsWriter[scala.collection.immutable.Seq[T]] {
      def toJs(value: scala.collection.immutable.Seq[T]): js.Any = {
        js.Array(value.map(e => elementWriter.toJs(e)): _*)
      }
    }
  }

  implicit def mapWriter[T : JsWriter]: JsWriter[Map[String, T]] = {
    val elementWriter = implicitly[JsWriter[T]]

    new JsWriter[Map[String, T]] {
      def toJs(value: Map[String, T]): js.Any = {
        val converted = value.map { case (k,v) => (k, elementWriter.toJs(v)) }
        js.Dictionary(converted.toSeq: _*)
      }
    }
  }
}
