package com.payalabs.scalajs.react.bridge.showcase

import com.payalabs.scalajs.react.bridge.elements.{Button, FormControl, ReactMediumEditor, TagsInput}
import japgolly.scalajs.react.vdom.all._
import japgolly.scalajs.react._
import org.scalajs.dom

import scala.scalajs.js

object ShowcaseApp extends js.JSApp {
  def main(): Unit = {
    ShowcaseComponent().renderIntoDOM(dom.document.getElementById("app-container"))
  }
}

object ShowcaseComponent {
  case class State(tags: Seq[String])
  type Props = Unit

  class Backend(scope: BackendScope[Props, State]) {
    def updateTags(value: js.Array[String]) = scope.modState(s =>
      s.copy(tags = value.toSeq)
    )

    def validateLength(value: String) = CallbackTo { value.length <= 7 }

    def render(state: State): VdomElement = {
      div(className := "col-sm-10 col-sm-offset-1")(
        h1()(
          "Example components created with ",
          a(href := "https://github.com/payalabs/scalajs-react-bridge")("scalajs-react-bridge")
        ),
        div(className := "well")(
          b("Tags input. Values with size > 7 considered invalid"),
          TagsInput(value = state.tags, onChange = updateTags _, validate = validateLength _)
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
          Button()("Bootstrap Button")
        )
      )

    }
  }

  val component =
    ScalaComponent.builder[Props](this.getClass.getSimpleName).
      initialState(State(tags = Seq("some", "default", "tags"))).
      renderBackend[Backend].
      build

  def apply(): VdomElement = component()

}