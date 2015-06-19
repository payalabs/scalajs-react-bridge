enablePlugins(ScalaJSPlugin)

organization := "com.payalabs"
name := "scalajs-react-bridge"

version := "0.1.0"

scalaVersion := "2.11.6"

jsDependencies += RuntimeDOM

preLinkJSEnv := PhantomJSEnv().value
postLinkJSEnv := PhantomJSEnv().value

libraryDependencies ++= {
  val scalaJsDomV = "0.8.1"
  val scalaJsReactV = "0.9.0"
  val scalatestV = "3.0.0-M1"
  Seq(
    "org.scala-lang" % "scala-reflect" % scalaVersion.value % Provided,
    "org.scala-js" %%% "scalajs-dom" % scalaJsDomV % Provided,
    "com.github.japgolly.scalajs-react" %%% "core" % scalaJsReactV % Provided,
    "org.scalatest" %%% "scalatest" % scalatestV % Test,
    "com.github.japgolly.scalajs-react" %%% "test" % scalaJsReactV % Test
  )
}

jsDependencies += "org.webjars" % "react" % "0.13.3" % Test / "react-with-addons.js" minified "react-with-addons.min.js" commonJSName "React"
jsDependencies in Test += ProvidedJS / "test-component.js" dependsOn "react-with-addons.js"
