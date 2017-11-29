import sbt.Keys.publishLocal

enablePlugins(ScalaJSPlugin)

val core = project in file("core")

scalaVersion := "2.12.2"

version := "0.5.0-SNAPSHOT"

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


