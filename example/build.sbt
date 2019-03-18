enablePlugins(ScalaJSPlugin)

organization := "com.payalabs"
name := "scalajs-react-bridge-example"
version := "0.8.1-SNAPSHOT"

crossScalaVersions := Seq("2.12.8", "2.11.12")
scalaVersion := crossScalaVersions.value.head

libraryDependencies ++= {
  val scalaJsDom = "0.9.6"
  val scalaJsReact = "1.4.0"

  Seq(
    "org.scala-lang" % "scala-reflect" % scalaVersion.value,
    "org.scala-js" %%% "scalajs-dom" % scalaJsDom,
    "com.github.japgolly.scalajs-react" %%% "core" % scalaJsReact,
    "com.github.japgolly.scalajs-react" %%% "extra" % scalaJsReact
  )
}

resolvers += Resolver.sonatypeRepo("snapshots")
