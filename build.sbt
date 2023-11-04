ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.1"

ThisBuild / organization := "com.rahulsinghai"

ThisBuild / githubWorkflowJavaVersions := Seq(JavaSpec.temurin("21")) // sbt-github-actions defaults to using JDK 8 for testing and publishing. Use JDK 21 instead.

lazy val akkaHttpV = "10.6.0-M1"
lazy val akkaV = "2.9.0-M2"
lazy val logbackV = "1.4.11"
lazy val scalaLoggingV = "3.9.5"
lazy val scalaTestV = "3.2.17"

lazy val root = (project in file("."))
  .enablePlugins(JavaAppPackaging)
  .settings(
    name := "scala-akka-cart",
    maintainer := "Rahul Singhai",

    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor-typed" % akkaV,
      "com.typesafe.akka" %% "akka-http" % akkaHttpV,
      "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpV,
      "com.typesafe.akka" %% "akka-stream" % akkaV,

      "ch.qos.logback"     % "logback-classic" % logbackV % Runtime,

      "org.scalatest"     %% "scalatest" % scalaTestV % Test,
      "com.typesafe.akka" %% "akka-actor-testkit-typed" % akkaV % Test,
      "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpV % Test,
      "com.typesafe.akka" %% "akka-stream-testkit" % akkaV % Test,
    ),

    Compile / console / scalacOptions --= Seq("-Ywarn-dead-code"),

    Test / scalacOptions --= Seq("-deprecation", "-Ywarn-dead-code"),
    Test / fork := true, // If you only want to run tests sequentially, but in a different JVM, you can achieve this
    Test / testForkedParallel := true, // If our tests are properly designed, and can run independently, we can execute all of them in parallel by adding an SBT definition file
  )
