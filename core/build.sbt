enablePlugins(ScalaJSPlugin)

val reactV = "15.5.3"

organization := "com.payalabs"
name := "scalajs-react-bridge"

version := "0.5.0-SNAPSHOT"

crossScalaVersions := Seq("2.12.2", "2.11.12")

scalacOptions := Seq("-unchecked", "-deprecation", "-feature")

libraryDependencies ++= {
  val scalaJsDomV = "0.9.3"
  val scalaJsReactV = "1.1.0"
  val scalatestV = "3.0.1"

  Seq(
    "org.scala-lang" % "scala-reflect" % scalaVersion.value % Provided,
    "org.scala-js" %%% "scalajs-dom" % scalaJsDomV % Provided,
    "com.github.japgolly.scalajs-react" %%% "core" % scalaJsReactV % Provided,
    "org.scalatest" %%% "scalatest" % scalatestV % Test,
    "com.github.japgolly.scalajs-react" %%% "test" % scalaJsReactV % Test
  )
}

jsDependencies += RuntimeDOM

jsEnv := PhantomJSEnv().value

jsDependencies += "org.webjars.npm" % "react" % reactV % Test / "react-with-addons.js" minified "react-with-addons.min.js" commonJSName "React"
jsDependencies += "org.webjars.npm" % "react-dom" % reactV % Test / "react-dom.js" minified "react-dom.min.js" commonJSName "ReactDOM" dependsOn "react-with-addons.js"
jsDependencies in Test += ProvidedJS / "test-component.js" dependsOn "react-with-addons.js"

licenses := Seq("The MIT License (MIT)" -> url("https://opensource.org/licenses/MIT"))
homepage := Some(url("https://github.com/payalabs/scalajs-react-bridge"))
scmInfo := Some(
  ScmInfo(
    url("https://github.com/payalabs/scalajs-react-bridge"),
    "scm:git@github.com:payalabs/scalajs-react-bridge.git"
  )
)
developers := List(
  Developer(
    id    = "ramnivas",
    name  = "Ramnivas Laddad",
    email = "",
    url   = url("https://github.com/payalabs")
  )
)

sourceGenerators in Compile += Def.task {
  val dir = (sourceManaged in Compile).value
  val file = dir / "com" / "payalabs" / "scalajs" / "react" / "bridge" / "GeneratedImplicits.scala"

  val functions = (0 to 22).map { arity =>
    val indices = 1 to arity
    val types = indices.map(i => s"T$i") :+ "R"
    val tParams = types.mkString(", ")
    val params = indices.map(i => s"x$i: T$i").mkString(", ")
    val args = indices.map(i => s"x$i").mkString(", ")
    s"""
       |  implicit def function${arity}Writer[$tParams](implicit writerR: JsWriter[R]): JsWriter[Function$arity[$tParams]] = {
       |    new JsWriter[Function$arity[$tParams]] {
       |      override def toJs(value: Function$arity[$tParams]): js.Any =
       |        fromFunction$arity(($params) => writerR.toJs(value($args)))
       |    }
       | }""".stripMargin
  }.mkString("\n")


  IO.write(file, s"""
                    |package com.payalabs.scalajs.react.bridge
                    |
                    |import scala.scalajs.js
                    |import scala.scalajs.js.Any._
                    |import japgolly.scalajs.react.CallbackTo
                    |
                    |trait GeneratedImplicits {
                    |  $functions
                    |}
        """.stripMargin.trim
  )

  Seq(file)
}.taskValue
