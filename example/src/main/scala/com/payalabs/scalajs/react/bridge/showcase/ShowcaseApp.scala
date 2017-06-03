package com.payalabs.scalajs.react.bridge.showcase

import com.payalabs.scalajs.react.bridge.elements.{ReactMediumEditor, FormControl, Button, TagsInput}
import japgolly.scalajs.react.vdom.all.{onChange => _,_}
import japgolly.scalajs.react._
import org.scalajs.dom

import scala.scalajs.js

object ShowcaseApp extends js.JSApp {
  def main(): Unit = {

    def printSequence(value: js.Array[String]): Callback = Callback.log(s"Value is ${value.mkString(",")}")
    def validateLength(value: String) = CallbackTo { value.length <= 7 }

    val component = ScalaComponent.builder[Unit]("ShowcaseApp").render { _ =>
      div(className := "col-sm-10 col-sm-offset-1")(
        h1()(
          "Example components created with ",
          a(href := "https://github.com/payalabs/scalajs-react-bridge")("scalajs-react-bridge")
        ),
        div(className := "well")(
          b("Tags input (open Inspect Element to see callback being called as you make changes). Values with size > 7 considered invalid"),
          TagsInput(value = Seq("some", "default", "values"), onChange = printSequence _, validate = validateLength _)
        ),
        div(className := "well")(
          ReactMediumEditor(text =
            """
            | <h1>Medium Editor</h1>
            |
            | <p>Click here and start editing.</p>
            |
            | <b>Select some text to see the editor toolbar pop up.</b>
            """.stripMargin, options = Map("buttons" -> js.Array("bold", "italic", "underline", "anchor", "header1", "header2", "quote", "orderedlist", "unorderedlist")))
        ),
        div(className := "well")(
          b("Bootstrap"),
          FormControl(placeholder = "This is a bootstrap input", `type` = "text"),
          Button().apply("Bootstrap Button")
        )
      )
    }.build.apply()

    component.renderIntoDOM(dom.document.getElementById("app-container"))
  }
}
