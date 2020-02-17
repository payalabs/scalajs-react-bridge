enablePlugins(ScalaJSPlugin)

organization := "com.payalabs"
name := "scalajs-react-bridge-example"

libraryDependencies ++= Seq(
  "org.scala-lang" % "scala-reflect" % scalaVersion.value,
  "org.scala-js" %%% "scalajs-dom" % Versions.scalaJsDom,
  "com.github.japgolly.scalajs-react" %%% "core" % Versions.scalaJsReact,
  "com.github.japgolly.scalajs-react" %%% "extra" % Versions.scalaJsReact
)

resolvers += Resolver.sonatypeRepo("snapshots")
