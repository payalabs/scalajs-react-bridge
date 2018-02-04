package com.payalabs.scalajs.react.bridge.test

import scala.scalajs.js

import com.payalabs.scalajs.react.bridge.{JsWriter, ReactBridgeComponent, ReactBridgeComponentNoSpecialProps, ReactBridgeComponentNoSpecialPropsNoChildren, WithPropsAndTagsMods, WithPropsNoChildren}
import japgolly.scalajs.react._
import japgolly.scalajs.react.test.ReactTestUtils
import japgolly.scalajs.react.test.ReactTestUtils.MountedOutput
import japgolly.scalajs.react.test.raw.ReactAddonsTestUtils.Simulate
import japgolly.scalajs.react.vdom.all._
import org.scalajs.dom.raw.Node
import org.scalatest.FunSuite


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
  def apply(name: js.UndefOr[String] = js.undefined, age: js.UndefOr[Int] = js.undefined): WithPropsNoChildren = this.autoNoChildren
}

class ReactBridgeComponentTest extends FunSuite {

  test("all primitive types supported") {
    object TestComponent extends ReactBridgeComponent {
      def apply(boolean: js.UndefOr[Boolean] = js.undefined,
                byte: js.UndefOr[Byte] = js.undefined,
                short: js.UndefOr[Short] = js.undefined,
                int: js.UndefOr[Int] = js.undefined,
                float: js.UndefOr[Float] = js.undefined,
                double: js.UndefOr[Double] = js.undefined
               ): WithPropsNoChildren = this.autoNoChildren
    }

    val testComponent = TestComponent(
      boolean = true,
      byte = 1.toByte,
      short = 2.toShort,
      int = 3,
      float = 5.1f,
      double = 6.2
    )

    val mounted = ReactTestUtils.renderIntoDocument(testComponent)
    assert(mounted.getDOMNode.querySelector("#boolean").getAttribute("data-test") === "true")
    assert(mounted.getDOMNode.querySelector("#byte").getAttribute("data-test") === "1")
    assert(mounted.getDOMNode.querySelector("#short").getAttribute("data-test") === "2")
    assert(mounted.getDOMNode.querySelector("#int").getAttribute("data-test") === "3")
    assert(mounted.getDOMNode.querySelector("#float").getAttribute("data-test") === 5.1f.toString)
    assert(mounted.getDOMNode.querySelector("#double").getAttribute("data-test") === 6.2.toString)
  }

  test("scalar properties omitted dom attr") {
    object TestComponent extends ReactBridgeComponent {
      def apply(name: js.UndefOr[String] = js.undefined, age: js.UndefOr[Int] = js.undefined): WithPropsNoChildren = this.autoNoChildren
    }

    val testComponent = TestComponent(name = "foo", age = 25) // the implicit conversion from WithPropsNoChildren kicks in to allow skipping dom props

    val mounted = ReactTestUtils.renderIntoDocument(testComponent)
    assert(mounted.getDOMNode.querySelector("#name").getAttribute("data-test") === "foo")
    assert(mounted.getDOMNode.querySelector("#age").getAttribute("data-test") === "25")
  }

  test("scalar properties without a possibility to specify dom attr") {
    object TestComponent extends ReactBridgeComponent {

      def apply(name: js.UndefOr[String] = js.undefined, age: js.UndefOr[Int] = js.undefined): VdomElement = this.autoNoTagModsNoChildren
    }

    val testComponent = TestComponent(name = "foo", age = 25)

    val mounted = ReactTestUtils.renderIntoDocument(testComponent)
    assert(mounted.getDOMNode.querySelector("#name").getAttribute("data-test") === "foo")
    assert(mounted.getDOMNode.querySelector("#age").getAttribute("data-test") === "25")
  }

  test("scalar properties without a possibility to specify dom attr but with children") {
    object TestComponent extends ReactBridgeComponent {
      def apply(name: js.UndefOr[String] = js.undefined, age: js.UndefOr[Int] = js.undefined): WithPropsAndTagsMods = this.autoNoTagMods
    }

    val testComponent = TestComponent(name = "foo", age = 25)(
      "textChild", span("spanChild")
    )

    val mounted = ReactTestUtils.renderIntoDocument(testComponent)
    assert(mounted.getDOMNode.querySelector("#name").getAttribute("data-test") === "foo")
    assert(mounted.getDOMNode.querySelector("#age").getAttribute("data-test") === "25")
    assertChildren(mounted)
  }


  test("scalar properties and dom properties") {
    object TestComponent extends ReactBridgeComponent {
      def apply(name: js.UndefOr[String] = js.undefined, age: js.UndefOr[Int] = js.undefined): WithPropsNoChildren = this.autoNoChildren
    }

    val testComponent = TestComponent(name = "foo", age = 25)(id := "test-id", className := "test-classname")

    val mounted = ReactTestUtils.renderIntoDocument(testComponent)
    assert(mounted.getDOMNode.querySelector("#name").getAttribute("data-test") === "foo")
    assert(mounted.getDOMNode.querySelector("#age").getAttribute("data-test") === "25")
    assert(mounted.getDOMNode.querySelector("#id").getAttribute("data-test") === "test-id")
    assert(mounted.getDOMNode.querySelector("#className").getAttribute("data-test") === "test-classname")
  }

  test("no special properties") {
    object TestComponent extends ReactBridgeComponentNoSpecialProps

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
    object TestComponent extends ReactBridgeComponentNoSpecialPropsNoChildren

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

  test("enumeration properties") {
    object ColorEnumeration extends Enumeration {
      val Red: Value = Value("red")
      val Green: Value = Value("green")
      val Blue: Value = Value("blue")
    }

    object TestComponent extends ReactBridgeComponent {
      def apply(foregroundColor: js.UndefOr[ColorEnumeration.Value],
                backgroundColor: js.UndefOr[ColorEnumeration.Value]): WithPropsNoChildren = this.autoNoChildren
    }

    val testComponent = TestComponent(foregroundColor = ColorEnumeration.Red, backgroundColor = ColorEnumeration.Green)()

    val mounted = ReactTestUtils.renderIntoDocument(testComponent)
    assert(mounted.getDOMNode.querySelector("#foregroundColor").getAttribute("data-test") === "red")
    assert(mounted.getDOMNode.querySelector("#backgroundColor").getAttribute("data-test") === "green")
  }

  test("array properties") {
    object TestComponent extends ReactBridgeComponent {
      def apply(names: js.UndefOr[Seq[String]], ages: js.UndefOr[scala.collection.immutable.Seq[Int]]): WithPropsNoChildren = this.autoNoChildren
    }

    val testComponent = TestComponent(names = Seq("foo", "bar"), ages = scala.collection.immutable.Seq(5,10))()

    val mounted = ReactTestUtils.renderIntoDocument(testComponent)
    assert(mounted.getDOMNode.querySelector("#names").getAttribute("data-test") === "[foo,bar]")
    assert(mounted.getDOMNode.querySelector("#ages").getAttribute("data-test") === "[5,10]")
  }


  test("map properties") {
    object TestComponent extends ReactBridgeComponent {
      def apply(map: js.UndefOr[Map[String, js.Any]] = js.undefined): WithPropsNoChildren = this.autoNoChildren
    }

    val testComponent =
      TestComponent(map = Map[String, js.Any]("one" -> 1, "two" -> "2", "foo" -> "bar"))()

    val mounted = ReactTestUtils.renderIntoDocument(testComponent)
    assert(mounted.getDOMNode.querySelector("#map").getAttribute("data-test") === "{one->1,two->2,foo->bar}")
  }


  test("value class object properties") {
    object TestComponent extends ReactBridgeComponent {
      def apply(name: js.UndefOr[NameType] = js.undefined): WithPropsNoChildren = this.autoNoChildren
    }

    val testComponent = TestComponent(name = new NameType("test-name"))

    val mounted = ReactTestUtils.renderIntoDocument(testComponent)
    assert(mounted.getDOMNode.querySelector("#name").getAttribute("data-test") === "test-name")
  }


  test("non value class object properties") {
    object TestComponent extends ReactBridgeComponent {
      def apply(map: js.UndefOr[Person] = js.undefined): WithPropsNoChildren = this.autoNoChildren
    }

    val testComponent = TestComponent(map = Person("test-person", 10))

    val mounted = ReactTestUtils.renderIntoDocument(testComponent)
    assert(mounted.getDOMNode.querySelector("#map").getAttribute("data-test") === "{name->test-person,age->10}")
  }

  test("seq of object properties") {
    object TestComponent extends ReactBridgeComponent {
      def apply(map: js.UndefOr[Seq[Person]] = js.undefined): WithPropsNoChildren = this.autoNoChildren
    }

    val testComponent = TestComponent(map = Seq(Person("First", 10), Person("Second", 11)))
    val mounted = ReactTestUtils.renderIntoDocument(testComponent)
    assert(mounted.getDOMNode.querySelector("#map").getAttribute("data-test") === "[{name->First,age->10},{name->Second,age->11}]")
  }


  test("function properties") {
    object TestComponent extends ReactBridgeComponent {
      def apply(onSomething1: js.UndefOr[Int => Unit] = js.undefined,
                onSomething2: js.UndefOr[(Int, String) => Unit] = js.undefined,
                onSomething3: js.UndefOr[(Int, String, js.Array[Any]) => Unit] = js.undefined): WithPropsNoChildren = this.autoNoChildren
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

    // Reset in case JsWriter accidentally invokes the functions
    something1 = false
    something2 = false
    something3 = false

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
                onSomething3: js.UndefOr[(Int, String, js.Array[js.Any]) => Callback] = js.undefined):
        WithPropsNoChildren = this.autoNoChildren
    }

    var something1 = false
    var something2 = false
    var something3 = false

    def change1(i: Int): Callback = Callback {
      something1 = true
      assert(i === 1)
    }

    def change2(i: Int, s: String): Callback = Callback {
      something2 = true
      assert(i === 1)
      assert(s === "two")
    }

    def change3(i: Int, s: String, a: js.Array[js.Any]): Callback = Callback {
      something3 = true
      assert(i === 1)
      assert(s === "two")
      assert(a.toArray === Array(3, "four"))
    }

    val testComponent = TestComponent(onSomething1 = change1 _, onSomething2 = change2 _, onSomething3 = change3 _)

    val mounted = ReactTestUtils.renderIntoDocument(testComponent)

    // Reset in case JsWriter accidentally invokes the callbacks
    something1 = false
    something2 = false
    something3 = false

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

  test("properties without js.UndefOr container") {
    object TestComponent extends ReactBridgeComponent {
      def apply(name: String, age: Int): WithPropsNoChildren = this.autoNoChildren
    }

    val testComponent = TestComponent(name = "foo", age = 25)()

    val mounted = ReactTestUtils.renderIntoDocument(testComponent)
    assert(mounted.getDOMNode.querySelector("#name").getAttribute("data-test") === "foo")
    assert(mounted.getDOMNode.querySelector("#age").getAttribute("data-test") === "25")
  }

  test("undefined props do not get passed to the underlying component") {
    object TestComponent extends ReactBridgeComponent {
      def apply(name: js.UndefOr[String] = js.undefined, age: js.UndefOr[Int] = js.undefined): WithPropsNoChildren = this.autoNoChildren
    }

    val testComponent = TestComponent(age = 25)

    val mounted = ReactTestUtils.renderIntoDocument(testComponent)
    println(mounted.getDOMNode.outerHTML)
    assert(mounted.getDOMNode.querySelector("#name") == null)
    assert(mounted.getDOMNode.querySelector("#age").getAttribute("data-test") === "25")
  }

  test("supplied key") {
    object TestComponent extends ReactBridgeComponentNoSpecialPropsNoChildren

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
