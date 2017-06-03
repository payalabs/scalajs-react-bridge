package com.payalabs.scalajs.react.bridge.test

import com.payalabs.scalajs.react.bridge.{JsWriter, ReactBridgeComponent, ReactBridgeComponentNoProps, ReactBridgeComponentNoPropsNoChildren, WithPropsNoChildren}
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.all._
import japgolly.scalajs.react.test.ReactTestUtils
import japgolly.scalajs.react.test.ReactTestUtils.MountedOutput
import org.scalatest.FunSuite
import japgolly.scalajs.react.test.raw.ReactAddonsTestUtils.Simulate
import org.scalajs.dom.raw.Node

import scala.scalajs.js

class NameType(val name :String) extends AnyVal

object NameType {
  implicit object NameTypeWriter extends JsWriter[NameType] {
    def toJs(value: NameType): js.Any = value.name.asInstanceOf[js.Any]
  }
}

case class Person(name: String, age: Int)

object Person {
  implicit object PersonWriter extends JsWriter[Person] {
    def toJs(value: Person): js.Any = js.Dynamic.literal(name = value.name, age = value.age)
  }
}

object TestComponent extends ReactBridgeComponent {
  def apply(name: js.UndefOr[String] = js.undefined, age: js.UndefOr[Int] = js.undefined): WithPropsNoChildren = this.autoConstructNoChildren
}

class ReactBridgeComponentTest extends FunSuite {

  test("scalar properties without dom properties") {
    object TestComponent extends ReactBridgeComponent {
      def apply(name: js.UndefOr[String] = js.undefined, age: js.UndefOr[Int] = js.undefined): WithPropsNoChildren = this.autoConstructNoChildren
    }

    val testComponent = TestComponent(name = "foo", age = 25) // the implicit conversion from WithPropsNoChildren kicks in to allow skipping dom props

    val mounted = ReactTestUtils.renderIntoDocument(testComponent)
    assert(mounted.getDOMNode.querySelector("#name").getAttribute("data-test") === "foo")
    assert(mounted.getDOMNode.querySelector("#age").getAttribute("data-test") === "25")
  }

  test("scalar properties and dom properties") {
    object TestComponent extends ReactBridgeComponent {
      def apply(name: js.UndefOr[String] = js.undefined, age: js.UndefOr[Int] = js.undefined): WithPropsNoChildren = this.autoConstructNoChildren
    }

    val testComponent = TestComponent(name = "foo", age = 25)(id := "test-id", className := "test-classname")

    val mounted = ReactTestUtils.renderIntoDocument(testComponent)
    assert(mounted.getDOMNode.querySelector("#name").getAttribute("data-test") === "foo")
    assert(mounted.getDOMNode.querySelector("#age").getAttribute("data-test") === "25")
    assert(mounted.getDOMNode.querySelector("#id").getAttribute("data-test") === "test-id")
    assert(mounted.getDOMNode.querySelector("#className").getAttribute("data-test") === "test-classname")
  }

  test("no special properties") {
    object TestComponent extends ReactBridgeComponentNoProps

    val testComponent = TestComponent(id := "test-id", className := "test-classname")(
      "textChild",
      span("spanChild")
    )

    val mounted = ReactTestUtils.renderIntoDocument(testComponent)

    assert(mounted.getDOMNode.querySelector("#id").getAttribute("data-test") === "test-id")
    assert(mounted.getDOMNode.querySelector("#className").getAttribute("data-test") === "test-classname")
    assertChildren(mounted)
  }

  test("no special properties no children") {
    object TestComponent extends ReactBridgeComponentNoPropsNoChildren

    val testComponent = TestComponent(id := "test-id", className := "test-classname")

    val mounted = ReactTestUtils.renderIntoDocument(testComponent)

    assert(mounted.getDOMNode.querySelector("#id").getAttribute("data-test") === "test-id")
    assert(mounted.getDOMNode.querySelector("#className").getAttribute("data-test") === "test-classname")
  }

  test("Non-nested classes work too") {
    // Basically test that the logic to extract getSimpleName (see ReactBridgeComponent.componentName) works for
    // top-level classes (which is going to be the most common case)
    val testComponent = TestComponent(name = "foo", age = 25)(id := "test-id", className := "test-classname")

    val mounted = ReactTestUtils.renderIntoDocument(testComponent)
    assert(mounted.getDOMNode.querySelector("#name").getAttribute("data-test") === "foo")
    assert(mounted.getDOMNode.querySelector("#age").getAttribute("data-test") === "25")
    assert(mounted.getDOMNode.querySelector("#id").getAttribute("data-test") === "test-id")
    assert(mounted.getDOMNode.querySelector("#className").getAttribute("data-test") === "test-classname")
  }

  test("array properties") {
    object TestComponent extends ReactBridgeComponent {
      def apply(names: js.UndefOr[Seq[String]], ages: js.UndefOr[scala.collection.immutable.Seq[Int]]): WithPropsNoChildren = this.autoConstructNoChildren
    }

    val testComponent = TestComponent(names = Seq("foo", "bar"), ages = scala.collection.immutable.Seq(5,10))()

    val mounted = ReactTestUtils.renderIntoDocument(testComponent)
    assert(mounted.getDOMNode.querySelector("#names").getAttribute("data-test") === "[foo,bar]")
    assert(mounted.getDOMNode.querySelector("#ages").getAttribute("data-test") === "[5,10]")
  }


  test("map properties") {
    object TestComponent extends ReactBridgeComponent {
      def apply(map: js.UndefOr[Map[String, Any]] = js.undefined): WithPropsNoChildren = this.autoConstructNoChildren
    }

    val testComponent = TestComponent(map = Map("one" -> 1, "two" -> "2", "foo" -> "bar"))()

    val mounted = ReactTestUtils.renderIntoDocument(testComponent)
    assert(mounted.getDOMNode.querySelector("#map").getAttribute("data-test") === "{one->1,two->2,foo->bar}")
  }


  test("value class object properties") {
    object TestComponent extends ReactBridgeComponent {
      def apply(name: js.UndefOr[NameType] = js.undefined): WithPropsNoChildren = this.autoConstructNoChildren
    }

    val testComponent = TestComponent(name = new NameType("test-name"))

    val mounted = ReactTestUtils.renderIntoDocument(testComponent)
    assert(mounted.getDOMNode.querySelector("#name").getAttribute("data-test") === "test-name")
  }


  test("non value class object properties") {
    object TestComponent extends ReactBridgeComponent {
      def apply(map: js.UndefOr[Person] = js.undefined): WithPropsNoChildren = this.autoConstructNoChildren
    }

    val testComponent = TestComponent(map = Person("test-person", 10))

    val mounted = ReactTestUtils.renderIntoDocument(testComponent)
    assert(mounted.getDOMNode.querySelector("#map").getAttribute("data-test") === "{name->test-person,age->10}")
  }

  test("seq of object properties") {
    object TestComponent extends ReactBridgeComponent {
      def apply(map: js.UndefOr[Seq[Person]] = js.undefined): WithPropsNoChildren = this.autoConstructNoChildren
    }

    val testComponent = TestComponent(map = Seq(Person("First", 10), Person("Second", 11)))
    val mounted = ReactTestUtils.renderIntoDocument(testComponent)
    assert(mounted.getDOMNode.querySelector("#map").getAttribute("data-test") === "[{name->First,age->10},{name->Second,age->11}]")
  }


  test("function properties") {
    object TestComponent extends ReactBridgeComponent {
      def apply(onSomething1: js.UndefOr[Int => Unit] = js.undefined,
                onSomething2: js.UndefOr[(Int, String) => Unit] = js.undefined,
                onSomething3: js.UndefOr[(Int, String, js.Array[Any]) => Unit] = js.undefined): WithPropsNoChildren = this.autoConstructNoChildren
    }

    var something1 = false
    var something2 = false
    var something3 = false

    def change1(i: Int): Unit = {
      something1 = true
      assert(i === 1)
    }

    def change2(i: Int, s: String): Unit = {
      something2 = true
      assert(i === 1)
      assert(s === "two")
    }

    def change3(i: Int, s: String, a: js.Array[Any]): Unit = {
      something3 = true
      assert(i === 1)
      assert(s === "two")
      assert(a.toArray === Array(3, "four"))
    }

    val testComponent =
      TestComponent(
        onSomething1 = change1 _,
        onSomething2 = change2 _,
        onSomething3 = change3 _)

    val mounted = ReactTestUtils.renderIntoDocument(testComponent)

    js.Dynamic.global.TestComponent.eventTestData = js.Array(1)
    Simulate.click(mounted.getDOMNode.querySelector("#onSomething1"))
    assert(something1 === true)

    js.Dynamic.global.TestComponent.eventTestData = js.Array(1, "two")
    Simulate.click(mounted.getDOMNode.querySelector("#onSomething2"))
    assert(something2 === true)

    js.Dynamic.global.TestComponent.eventTestData = js.Array(1, "two", js.Array[Any](3, "four"))
    Simulate.click(mounted.getDOMNode.querySelector("#onSomething3"))
    assert(something3 === true)
  }

  test("function properties that return Callback") {
    object TestComponent extends ReactBridgeComponent {
      def apply(onSomething1: js.UndefOr[Int => Callback] = js.undefined,
                onSomething2: js.UndefOr[(Int, String) => Callback] = js.undefined,
                onSomething3: js.UndefOr[(Int, String, js.Array[Any]) => Callback] = js.undefined,
                onSomething4: js.UndefOr[Callback] = js.undefined): WithPropsNoChildren = this.autoConstructNoChildren
    }

    var something1 = false
    var something2 = false
    var something3 = false
    var something4 = false

    def change1(i: Int): Callback = Callback {
      something1 = true
      assert(i === 1)
    }

    def change2(i: Int, s: String): Callback = Callback {
      something2 = true
      assert(i === 1)
      assert(s === "two")
    }

    def change3(i: Int, s: String, a: js.Array[Any]): Callback = Callback {
      something3 = true
      assert(i === 1)
      assert(s === "two")
      assert(a.toArray === Array(3, "four"))
    }

    def change4: Callback = Callback {
      something4 = true
    }


    val testComponent = TestComponent(onSomething1 = change1 _, onSomething2 = change2 _, onSomething3 = change3 _, onSomething4 = change4)

    val mounted = ReactTestUtils.renderIntoDocument(testComponent)

    js.Dynamic.global.TestComponent.eventTestData = js.Array(1)
    Simulate.click(mounted.getDOMNode.querySelector("#onSomething1"))
    assert(something1 === true)

    js.Dynamic.global.TestComponent.eventTestData = js.Array(1, "two")
    Simulate.click(mounted.getDOMNode.querySelector("#onSomething2"))
    assert(something2 === true)

    js.Dynamic.global.TestComponent.eventTestData = js.Array(1, "two", js.Array[Any](3, "four"))
    Simulate.click(mounted.getDOMNode.querySelector("#onSomething3"))
    assert(something3 === true)

    Simulate.click(mounted.getDOMNode.querySelector("#onSomething4"))
    assert(something4 === true)
  }

  test("properties without js.UndefOr container") {
    object TestComponent extends ReactBridgeComponent {
      def apply(name: String, age: Int): WithPropsNoChildren = this.autoConstructNoChildren
    }

    val testComponent = TestComponent(name = "foo", age = 25)()

    val mounted = ReactTestUtils.renderIntoDocument(testComponent)
    assert(mounted.getDOMNode.querySelector("#name").getAttribute("data-test") === "foo")
    assert(mounted.getDOMNode.querySelector("#age").getAttribute("data-test") === "25")
  }

  test("supplied key") {
    object TestComponent extends ReactBridgeComponentNoPropsNoChildren

    // Indirectly test that passed key makes it to the underlying components
    // If the keys don't make it to the underlying component (or the keys aren't unique),
    // React throws a warning that PhantomJS treats as an error and the test fails (correctly)
    val testComponent = List(1,2,3).toVdomArray(k => TestComponent(key := k))

    ReactTestUtils.renderIntoDocument(div(testComponent))
  }

  // Assumes that the children are ("textChild", span("spanChild"))
  private def assertChildren(mounted: MountedOutput): Unit = {
    val childNodes = mounted.getDOMNode.querySelector("#children").childNodes

    var assertions = 0
    for (i <- 0 until childNodes.length) {
      val childNode = childNodes(i)

      if (childNode.nodeType == Node.TEXT_NODE) {
        assert(childNode.textContent == "textChild")
        assertions += 1
      } else if (childNode.nodeType == Node.ELEMENT_NODE) {
        assert(childNode.textContent == "spanChild")
        assert(childNode.nodeName == "SPAN")
        assertions += 1
      } else if (childNode.nodeType != Node.COMMENT_NODE) {
        assert(false, "Non comment node found besides a text and a span node")
      }
    }

    assert(assertions == 2)
  }

}
