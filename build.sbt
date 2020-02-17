import sbt.Keys.publishLocal

enablePlugins(ScalaJSPlugin)

val core = project in file("core")

crossScalaVersions := Seq("2.13.1", "2.12.10")
scalaVersion := crossScalaVersions.value.head

version := "0.8.2-SNAPSHOT"

publishTo in ThisBuild := Some("sonatype-staging" at "https://oss.sonatype.org/service/local/staging/deploy/maven2")

val example = (project in file("example")).dependsOn(core).settings(
  skip in publish := true
)

val scalaJsReactBridge = (project in file(".")).aggregate(
  core,
  example
).settings(
  skip in publish := true
)
