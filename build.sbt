name := "Akka-rest"

version := "0.1"

scalaVersion := "2.12.4"

scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked", "-Xlint")

libraryDependencies ++= {
  val akkaVersion = "2.5.13"
  val akkaHttpVersion = "10.1.1"
  val scalatestVersion = "3.0.5"
  val slickVersion = "3.2.3"
  val mysqlConnectorVersion = "8.0.11"
  Seq(
    "com.typesafe.akka" %% "akka-actor"      % akkaVersion,
    "com.typesafe.akka" %% "akka-http-core"  % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http"       % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-stream"     % akkaVersion,
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
    "com.typesafe.slick" %% "slick" % slickVersion,
    "mysql" % "mysql-connector-java" % mysqlConnectorVersion,
    "com.typesafe.akka" %% "akka-testkit"    % akkaVersion  % "test",
    "org.scalatest"     %% "scalatest"       % scalatestVersion % "test"
  )
}