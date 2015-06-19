package com.payalabs.scalajs.react.bridge

import japgolly.scalajs.react.{ReactElement, React}
import japgolly.scalajs.react.test.ReactTestUtils
import org.scalatest.FunSuite

import scala.scalajs.js

class ReactBridgeComponentTest extends FunSuite {

  test("scalar properties") {
    case class TestComponent(id: js.UndefOr[String] = js.undefined, className: js.UndefOr[String] = js.undefined,
                             ref: js.UndefOr[String] = js.undefined, key: js.UndefOr[Any] = js.undefined,
                             name: js.UndefOr[String] = js.undefined, age: js.UndefOr[Int] = js.undefined) extends ReactBridgeComponent

    val testComponent: ReactElement = TestComponent(name = "foo", age = 25)()
    println(React.renderToStaticMarkup(testComponent))

    val mounted = ReactTestUtils.renderIntoDocument(testComponent)
    assert(mounted.getDOMNode().querySelector("#name").textContent === "foo")
    assert(mounted.getDOMNode().querySelector("#age").textContent === "25")
  }

  test("array properties") {
    case class TestComponent(id: js.UndefOr[String] = js.undefined, className: js.UndefOr[String] = js.undefined,
                             ref: js.UndefOr[String] = js.undefined, key: js.UndefOr[Any] = js.undefined,
                             names: js.UndefOr[Seq[String]] = js.undefined) extends ReactBridgeComponent

    val testComponent: ReactElement = TestComponent(names = Seq("foo", "bar"))()
    println(React.renderToStaticMarkup(testComponent))

    val mounted = ReactTestUtils.renderIntoDocument(testComponent)
    assert(mounted.getDOMNode().querySelector("#names").textContent === "[foo,bar]")
  }

  test("map properties") {
    case class TestComponent(id: js.UndefOr[String] = js.undefined, className: js.UndefOr[String] = js.undefined,
                             ref: js.UndefOr[String] = js.undefined, key: js.UndefOr[Any] = js.undefined,
                             props: js.UndefOr[Map[String, Any]] = js.undefined) extends ReactBridgeComponent

    val testComponent: ReactElement = TestComponent(props = Map("one" -> 1, "two" -> "2", "foo" -> "bar"))()
    println(React.renderToStaticMarkup(testComponent))

    val mounted = ReactTestUtils.renderIntoDocument(testComponent)
    assert(mounted.getDOMNode().querySelector("#props").textContent === "{one->1,two->2,foo->bar}")
  }


  test("function properties") {
    case class TestComponent(id: js.UndefOr[String] = js.undefined, className: js.UndefOr[String] = js.undefined,
                             ref: js.UndefOr[String] = js.undefined, key: js.UndefOr[Any] = js.undefined,
                             onSomething1: js.UndefOr[Int => Unit] = js.undefined,
                             onSomething2: js.UndefOr[(Int, String) => Unit] = js.undefined,
                             onSomething3: js.UndefOr[(Int, String, js.Array[Any]) => Unit] = js.undefined) extends ReactBridgeComponent

    var something1 = false
    var something2 = false
    var something3 = false

    def change1(i: Int) = {
      something1 = true
      assert(i === 1)
    }

    def change2(i: Int, s: String) = {
      something2 = true
      assert(i === 1)
      assert(s === "two")
    }

    def change3(i: Int, s: String, a: js.Array[Any]) = {
      something3 = true
      assert(i === 1)
      assert(s === "two")
      assert(a.toArray === Array(3, "four"))
    }


    val testComponent: ReactElement = TestComponent(onSomething1 = change1 _, onSomething2 = change2 _, onSomething3 = change3 _)()
    println(React.renderToStaticMarkup(testComponent))

    val mounted = ReactTestUtils.renderIntoDocument(testComponent)

    js.Dynamic.global.TestComponent.eventTestData = js.Array(1)
    ReactTestUtils.Simulate.click(mounted.getDOMNode().querySelector("#onSomething1"))
    assert(something1 === true)

    js.Dynamic.global.TestComponent.eventTestData = js.Array(1, "two")
    ReactTestUtils.Simulate.click(mounted.getDOMNode().querySelector("#onSomething2"))
    assert(something2 === true)

    js.Dynamic.global.TestComponent.eventTestData = js.Array(1, "two", js.Array[Any](3, "four"))
    ReactTestUtils.Simulate.click(mounted.getDOMNode().querySelector("#onSomething3"))
    assert(something3 === true)
  }
}
