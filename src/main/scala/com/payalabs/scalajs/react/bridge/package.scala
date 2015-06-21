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

  implicit def mapWriter[T : JsWriter]: JsWriter[Map[String, T]] = {
    val elementWriter = implicitly[JsWriter[T]]

    new JsWriter[Map[String, T]] {
      def toJs(value: Map[String, T]): js.Any = {
        val literal = js.Dynamic.literal()
        value.foreach { case (k, v) =>
          literal.updateDynamic(k)(elementWriter.toJs(v))
        }
        literal
      }
    }
  }
}
