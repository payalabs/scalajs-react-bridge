package com.payalabs.scalajs.react.bridge.test

import com.payalabs.scalajs.react.bridge.{ReactBridgeComponent, JsWriter}
import japgolly.scalajs.react._
import japgolly.scalajs.react.test.ReactTestUtils
import org.scalatest.FunSuite

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

class ReactBridgeComponentTest extends FunSuite {
  test("scalar properties") {
    case class TestComponent(id: js.UndefOr[String] = js.undefined, className: js.UndefOr[String] = js.undefined,
                             ref: js.UndefOr[String] = js.undefined, key: js.UndefOr[Any] = js.undefined,
                             name: js.UndefOr[String] = js.undefined, age: js.UndefOr[Int] = js.undefined) extends ReactBridgeComponent

    val testComponent: ReactElement = TestComponent(name = "foo", age = 25)()

    val mounted = ReactTestUtils.renderIntoDocument(testComponent)
    assert(mounted.getDOMNode().querySelector("#name").textContent === "foo")
    assert(mounted.getDOMNode().querySelector("#age").textContent === "25")
  }


  test("array properties") {
    case class TestComponent(id: js.UndefOr[String] = js.undefined, className: js.UndefOr[String] = js.undefined,
                             ref: js.UndefOr[String] = js.undefined, key: js.UndefOr[Any] = js.undefined,
                             names: js.UndefOr[Seq[String]], ages: js.UndefOr[scala.collection.immutable.Seq[Int]]) extends ReactBridgeComponent

    val testComponent: ReactElement = TestComponent(names = Seq("foo", "bar"), ages = scala.collection.immutable.Seq(5,10))()

    val mounted = ReactTestUtils.renderIntoDocument(testComponent)
    assert(mounted.getDOMNode().querySelector("#names").textContent === "[foo,bar]")
    assert(mounted.getDOMNode().querySelector("#ages").textContent === "[5,10]")
  }


  test("map properties") {
    case class TestComponent(id: js.UndefOr[String] = js.undefined, className: js.UndefOr[String] = js.undefined,
                             ref: js.UndefOr[String] = js.undefined, key: js.UndefOr[Any] = js.undefined,
                             props: js.UndefOr[Map[String, Any]] = js.undefined) extends ReactBridgeComponent

    val testComponent: ReactElement = TestComponent(props = Map("one" -> 1, "two" -> "2", "foo" -> "bar"))()

    val mounted = ReactTestUtils.renderIntoDocument(testComponent)
    assert(mounted.getDOMNode().querySelector("#props").textContent === "{one->1,two->2,foo->bar}")
  }


  test("value class object properties") {
    case class TestComponent(id: js.UndefOr[String] = js.undefined, className: js.UndefOr[String] = js.undefined,
                             ref: js.UndefOr[String] = js.undefined, key: js.UndefOr[Any] = js.undefined,
                             props: js.UndefOr[NameType] = js.undefined) extends ReactBridgeComponent

    val testComponent: ReactElement = TestComponent(props = new NameType("dude"))()

    val mounted = ReactTestUtils.renderIntoDocument(testComponent)
    assert(mounted.getDOMNode().querySelector("#props").textContent === "dude")
  }


  test("non value class object properties") {
    case class TestComponent(id: js.UndefOr[String] = js.undefined, className: js.UndefOr[String] = js.undefined,
                             ref: js.UndefOr[String] = js.undefined, key: js.UndefOr[Any] = js.undefined,
                             props: js.UndefOr[Person] = js.undefined) extends ReactBridgeComponent

    val testComponent: ReactElement = TestComponent(props = Person("Krishna", 10))()

    val mounted = ReactTestUtils.renderIntoDocument(testComponent)
    assert(mounted.getDOMNode().querySelector("#props").textContent === "{name->Krishna,age->10}")
  }

  test("seq of object properties") {
    case class TestComponent(id: js.UndefOr[String] = js.undefined, className: js.UndefOr[String] = js.undefined,
                             ref: js.UndefOr[String] = js.undefined, key: js.UndefOr[Any] = js.undefined,
                             props: js.UndefOr[Seq[Person]] = js.undefined) extends ReactBridgeComponent

    val testComponent: ReactElement = TestComponent(props = Seq(Person("First", 10), Person("Second", 11)))()
    val mounted = ReactTestUtils.renderIntoDocument(testComponent)
    assert(mounted.getDOMNode().querySelector("#props").textContent === "[{name->First,age->10},{name->Second,age->11}]")
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

    val testComponent: ReactElement = TestComponent(onSomething1 = change1 _, onSomething2 = change2 _, onSomething3 = change3 _)()

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

  test("function properties that return Callback") {
    case class TestComponent(id: js.UndefOr[String] = js.undefined, className: js.UndefOr[String] = js.undefined,
                             ref: js.UndefOr[String] = js.undefined, key: js.UndefOr[Any] = js.undefined,
                             onSomething1: js.UndefOr[Int => Callback] = js.undefined,
                             onSomething2: js.UndefOr[(Int, String) => Callback] = js.undefined,
                             onSomething3: js.UndefOr[(Int, String, js.Array[Any]) => Callback] = js.undefined) extends ReactBridgeComponent
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

    def change3(i: Int, s: String, a: js.Array[Any]): Callback = Callback {
      something3 = true
      assert(i === 1)
      assert(s === "two")
      assert(a.toArray === Array(3, "four"))
    }

    val testComponent: ReactElement = TestComponent(onSomething1 = change1 _, onSomething2 = change2 _, onSomething3 = change3 _)()

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


  test("properties without js.UndefOr container") {
    case class TestComponent(id: js.UndefOr[String] = js.undefined, className: js.UndefOr[String] = js.undefined,
                             ref: js.UndefOr[String] = js.undefined, key: js.UndefOr[Any] = js.undefined,
                             name: String, age: Int) extends ReactBridgeComponent
    val testComponent: ReactElement = TestComponent(name = "foo", age = 25)()

    val mounted = ReactTestUtils.renderIntoDocument(testComponent)
    assert(mounted.getDOMNode().querySelector("#name").textContent === "foo")
    assert(mounted.getDOMNode().querySelector("#age").textContent === "25")
  }
}
