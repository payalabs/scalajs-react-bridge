package com.payalabs.scalajs.react.bridge.test

import scala.concurrent.Future
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.scalajs.js

import com.payalabs.scalajs.react.bridge._
import org.scalatest.FunSuite


class JsWriterTest extends FunSuite {
  test("unionWriter") {
    val writer = unionWriter[Seq[String], Future[String]]
    val value1 = Seq("hello", "goodbye")
    val value2 = Future.successful("hello")
    val res1 = writer.toJs(value1)
    val res2 = writer.toJs(value2)
    assert(res1.isInstanceOf[js.Array[_]])
    assert(!res1.isInstanceOf[js.Promise[_]])
    assert(!res2.isInstanceOf[js.Array[_]])
    assert(res2.isInstanceOf[js.Promise[_]])
  }
}
