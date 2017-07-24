import sbt.Keys.publishLocal

enablePlugins(ScalaJSPlugin)

val core = project in file("core")

val example = (project in file("example")).dependsOn(core).settings(
  publish := (),
  publishLocal := ()
)

val scalaJsReactBridge = (project in file(".")).aggregate(
  core,
  example
).settings(
  publish := (),
  publishLocal := ()
)


