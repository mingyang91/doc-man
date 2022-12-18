ThisBuild / scalaVersion     := "3.2.0"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.example"
ThisBuild / organizationName := "example"

enablePlugins(JavaAppPackaging)

lazy val root = (project in file("."))
  .settings(
    name := "doc-man",
    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-dsl" % "0.23.16",
      "org.http4s" %% "http4s-ember-server" % "0.23.16",
      "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % "1.1.3",
      "com.softwaremill.sttp.tapir" %% "tapir-aws-lambda" % "1.1.3",
      "com.softwaremill.sttp.tapir" %% "tapir-aws-sam" % "1.1.3",
      "com.amazonaws" % "aws-lambda-java-runtime-interface-client" % "2.1.1",
      "org.typelevel" %% "cats-effect-testing-specs2" % "1.4.0" % Test,
      "org.tpolecat" %% "doobie-core" % "1.0.0-RC2",
      "org.tpolecat" %% "doobie-postgres"  % "1.0.0-RC2",
      "com.github.jwt-scala" %% "jwt-circe" % "9.1.1",
      "org.typelevel" %% "log4cats-core"    % "2.5.0",
      "org.typelevel" %% "log4cats-slf4j"   % "2.5.0",
    ),
    scalacOptions ++= Seq("-Xmax-inlines", "64"),
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework"),
  )
  .dependsOn(template)

lazy val template = (project in file("templates"))
  .settings(
    name := "templates",
    scalaVersion := "3.2.0",
    TwirlKeys.templateImports += "dev.famer.document.datatypes._",
  )
  .enablePlugins(SbtTwirl)

dockerBaseImage := "eclipse-temurin:17-jre"
