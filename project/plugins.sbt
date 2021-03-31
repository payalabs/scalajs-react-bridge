val scalaJSVersion = Option(System.getenv("SCALAJS_VERSION")).getOrElse("1.5.1")
addSbtPlugin("org.scala-js" % "sbt-scalajs" % scalaJSVersion)

// For Node.js with jsdom
libraryDependencies ++= {
  if (scalaJSVersion.startsWith("0.6.")) Nil
  else Seq("org.scala-js" %% "scalajs-env-jsdom-nodejs" % "1.1.0")
}

// For jsDependencies
{
  if (scalaJSVersion.startsWith("0.6.")) Nil
  else Seq(addSbtPlugin("org.scala-js" % "sbt-jsdependencies" % "1.0.2"))
}
