ThisBuild / scalaVersion     := "3.1.2"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.example"
ThisBuild / organizationName := "example"

lazy val root = (project in file("."))
  .settings(
    name := "doc-man",
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio" % "2.0.0-RC5",
      "dev.zio" %% "zio-test" % "2.0.0-RC5" % Test,
      "dev.zio" %% "zio-nio" % "2.0.0-RC5",
      "dev.zio" %% "zio-json" % "0.3.0-RC7",
      "org.http4s" %% "http4s-dsl" % "0.23.11",
      "org.http4s" %% "http4s-ember-server" % "0.23.11",
      "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % "1.0.0-M7",
      "com.softwaremill.sttp.tapir" %% "tapir-aws-lambda" % "1.0.0-M7",
      "com.softwaremill.sttp.tapir" %% "tapir-aws-sam" % "1.0.0-M7",
      "com.amazonaws" % "aws-lambda-java-runtime-interface-client" % "2.1.1"
    ),
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework"),
    excludeDependencies +="org.scala-lang.modules" % "scala-collection-compat_2.13",
  )
  .dependsOn(template)

lazy val template = (project in file("templates"))
  .settings(
    name := "templates",
    scalaVersion := "2.13.6",
    TwirlKeys.templateImports += "dev.famer.document.datatypes._",
  )
  .enablePlugins(SbtTwirl)
