enablePlugins(ScalaJSPlugin)

organization := "com.payalabs"
name := "scalajs-react-bridge"

version := "0.3.0-SNAPSHOT"

scalaVersion := "2.11.7"

jsDependencies += RuntimeDOM

jsEnv := PhantomJSEnv().value

libraryDependencies ++= {
  val scalaJsDomV = "0.8.2"
  val scalaJsReactV = "0.10.0"
  val scalatestV = "3.0.0-M10"
  Seq(
    "org.scala-lang" % "scala-reflect" % scalaVersion.value % Provided,
    "org.scala-js" %%% "scalajs-dom" % scalaJsDomV % Provided,
    "com.github.japgolly.scalajs-react" %%% "core" % scalaJsReactV % Provided,
    "org.scalatest" %%% "scalatest" % scalatestV % Test,
    "com.github.japgolly.scalajs-react" %%% "test" % scalaJsReactV % Test
  )
}

jsDependencies += "org.webjars.npm" % "react" % "0.14.0" % Test / "react-with-addons.js" minified "react-with-addons.min.js" commonJSName "React"
jsDependencies += "org.webjars.npm" % "react-dom" % "0.14.0" % Test / "react-dom.js" minified "react-dom.min.js" commonJSName "ReactDOM"
jsDependencies in Test += ProvidedJS / "test-component.js" dependsOn "react-with-addons.js"

sourceGenerators in Compile <+= sourceManaged in Compile map { dir =>
  val file = dir / "com" / "payalabs" / "scalajs" / "react" / "bridge" / "GeneratedImplicits.scala"

  val f0_22 = (0 to 22).map { arity =>
    val argsParam = (1 to arity).map(i => s"T$i").mkString(",")
    val params = if (argsParam.isEmpty) "R" else s"$argsParam,R"
    s"""
      |implicit def function${arity}Writer[$params]: JsWriter[Function$arity[$params]] = {
      |  new JsWriter[Function$arity[$params]] {
      |    override def toJs(value: Function$arity[$params]): js.Any = fromFunction$arity(value)
      |  }
      |}""".stripMargin
  }.mkString("\n")

  IO.write(file, s"""
    |package com.payalabs.scalajs.react.bridge
    |
    |import scala.scalajs.js
    |import scala.scalajs.js.Any._
    |
    |trait GeneratedImplicits {
    |  $f0_22
    |}
    """.stripMargin
  )

  Seq(file)
}

pomExtra in Global := {
  <url>https://github.com/payalabs/scalajs-react-bridge</url>
  <licenses>
    <license>
      <name>The MIT License (MIT)</name>
      <url>https://opensource.org/licenses/MIT</url>
    </license>
  </licenses>
  <scm>
    <connection>scm:git:github.com/payalabs/scalajs-react-bridge</connection>
    <developerConnection>scm:git:git@github.com:payalabs/scalajs-react-bridge</developerConnection>
    <url>github.com/payalabs/scalajs-react-bridge</url>
  </scm>
  <developers>
    <developer>
      <id>ramnivas</id>
      <name>Ramnivas Laddad</name>
    </developer>
  </developers>
}
