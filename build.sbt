enablePlugins(ScalaJSPlugin)

ThisBuild / version := "0.8.3-SNAPSHOT"

ThisBuild / crossScalaVersions := Seq("2.13.3", "2.12.12")
ThisBuild / scalaVersion := crossScalaVersions.value.head

publishTo in ThisBuild := Some("sonatype-staging" at "https://oss.sonatype.org/service/local/staging/deploy/maven2")

val core =
  project

val example =
  project
    .dependsOn(core)
    .settings(
      skip in publish := true
    )

val scalaJsReactBridge =
  (project in file("."))
    .aggregate(core, example)
    .settings(
      skip in publish := true,
      scalaJSUseMainModuleInitializer := true
    )
