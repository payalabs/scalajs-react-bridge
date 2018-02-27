import sbt.Keys.publishLocal

enablePlugins(ScalaJSPlugin)

val core = project in file("core")

crossScalaVersions := Seq("2.12.2", "2.11.12")
scalaVersion := crossScalaVersions.value.head

version := "0.5.0"

val example = (project in file("example")).dependsOn(core).settings(
  publish := (),
  publishLocal := (),
  publishArtifact := false
)

val scalaJsReactBridge = (project in file(".")).aggregate(
  core,
  example
).settings(
  publish := (),
  publishLocal := ()
)
