enablePlugins(ScalaJSPlugin)

val reactV = "16.5.1"

organization := "com.payalabs"
name := "scalajs-react-bridge"

version := "0.8.1"

crossScalaVersions := Seq("2.12.8", "2.11.12")
scalaVersion := crossScalaVersions.value.head

scalacOptions := Seq("-unchecked", "-deprecation", "-feature")

dependencyOverrides += "org.webjars.npm" % "js-tokens" % "3.0.2"

libraryDependencies ++= {
  val scalaJsDomV = "0.9.6"
  val scalaJsReactV = "1.4.2"
  val scalatestV = "3.0.1"

  Seq(
    "org.scala-lang" % "scala-reflect" % scalaVersion.value % Provided,
    "org.scala-js" %%% "scalajs-dom" % scalaJsDomV % Provided,
    "com.github.japgolly.scalajs-react" %%% "core" % scalaJsReactV % Provided,
    "org.scalatest" %%% "scalatest" % scalatestV % Test,
    "com.github.japgolly.scalajs-react" %%% "test" % scalaJsReactV % Test
  )
}

jsEnv := new org.scalajs.jsenv.jsdomnodejs.JSDOMNodeJSEnv

jsDependencies += "org.webjars.npm" % "react" % reactV % Test / "umd/react.development.js" minified "umd/react.production.min.js" commonJSName "React"
jsDependencies += "org.webjars.npm" % "react-dom" % reactV % Test / "umd/react-dom.development.js" minified "umd/react-dom.production.min.js" commonJSName "ReactDOM" dependsOn "umd/react.development.js"
jsDependencies in Test += ProvidedJS / "test-component.js" dependsOn "umd/react.development.js"
jsDependencies in Test += "org.webjars.npm" % "react-dom" % reactV % Test / "umd/react-dom-test-utils.development.js" minified "umd/react-dom-test-utils.production.min.js" commonJSName "ReactTestUtils" dependsOn "umd/react-dom.development.js"

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
