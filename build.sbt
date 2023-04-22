ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.example"
ThisBuild / organizationName := "example"

val `root/Path` = file(".")
lazy val root   = project in `root/Path` dependsOn templates enablePlugins JavaAppPackaging

root / name         := "doc-man"
root / scalaVersion := "3.2.2"
root / scalacOptions ++= Seq("-Xmax-inlines", "64")
root / testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
root / scalafmtOnCompile := true

val rLib = root / libraryDependencies
rLib += "org.http4s"                  %% "http4s-dsl"                               % "0.23.16"
rLib += "org.http4s"                  %% "http4s-ember-server"                      % "0.23.16"
rLib += "com.softwaremill.sttp.tapir" %% "tapir-http4s-server"                      % "1.1.3"
rLib += "com.softwaremill.sttp.tapir" %% "tapir-aws-lambda"                         % "1.1.3"
rLib += "com.softwaremill.sttp.tapir" %% "tapir-aws-sam"                            % "1.1.3"
rLib += "com.amazonaws"                % "aws-lambda-java-runtime-interface-client" % "2.1.1"
rLib += "org.typelevel"               %% "cats-effect-testing-specs2"               % "1.4.0" % Test
rLib += "org.tpolecat"                %% "doobie-core"                              % "1.0.0-RC2"
rLib += "org.tpolecat"                %% "doobie-postgres"                          % "1.0.0-RC2"
rLib += "com.github.jwt-scala"        %% "jwt-circe"                                % "9.1.1"
rLib += "org.typelevel"               %% "log4cats-core"                            % "2.5.0"
rLib += "org.typelevel"               %% "log4cats-slf4j"                           % "2.5.0"

lazy val templates = project in `root/Path` / "templates" enablePlugins SbtTwirl

templates / name         := "templates"
templates / scalaVersion := "3.2.2"
templates / TwirlKeys.templateImports += "dev.famer.document.datatypes._"

dockerBaseImage := "eclipse-temurin:17-jre"

Global / onChangedBuildSource := ReloadOnSourceChanges
