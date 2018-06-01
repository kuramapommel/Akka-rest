name := "Akka-rest"

version := "0.1"

scalaVersion := "2.12.4"

scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked", "-Xlint")

libraryDependencies ++= {
    val akkaVersion = "2.5.13"
    val akkaHttpVersion = "10.1.1"
    val logbackVersion = "1.2.3"
    val scalatestVersion = "3.0.5"
    Seq(
        "com.typesafe.akka" %% "akka-actor"      % akkaVersion,
        "com.typesafe.akka" %% "akka-stream"     % akkaVersion,
        "com.typesafe.akka" %% "akka-http-core"  % akkaHttpVersion,
        "com.typesafe.akka" %% "akka-http"       % akkaHttpVersion,
        "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
        "com.typesafe.akka" %% "akka-slf4j"      % akkaVersion,
        "ch.qos.logback"    %  "logback-classic" % logbackVersion,
        "com.typesafe.akka" %% "akka-testkit"    % akkaVersion  % "test",
        "org.scalatest"     %% "scalatest"       % scalatestVersion % "test"
    )
}