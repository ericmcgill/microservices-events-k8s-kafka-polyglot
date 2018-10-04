name := """ms-waiter"""
organization := "com.msdemo"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.6"

libraryDependencies += guice
libraryDependencies ++= Seq(
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test,
  "org.apache.kafka" % "kafka_2.12" % "2.0.0",
  "net.codingwell" %% "scala-guice" % "4.2.1"
)
// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.msdemo.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.msdemo.binders._"
