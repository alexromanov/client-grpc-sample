name := "client-grpc-sample"

version := "0.1"

scalaVersion := "2.13.6"

enablePlugins(AkkaGrpcPlugin)

libraryDependencies ++= Seq(
  "com.thesamet.scalapb" %% "scalapb-runtime" % "0.11.3",
  "com.typesafe.akka" %% "akka-discovery" % "2.6.15",
  "com.typesafe.akka" %% "akka-http-core" % "10.2.4",
  "com.typesafe.akka" %% "akka-http" % "10.2.4",
  "com.typesafe.akka" %% "akka-stream" % "2.6.15",
  "io.grpc" % "grpc-core" % "1.38.0",
  "io.grpc" % "grpc-netty-shaded" % "1.38.0",
  "org.scala-lang" % "scala-library" % "2.13.6",
  "org.scalatest" %% "scalatest" % "3.3.0-SNAP2" % Test,
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
  "com.typesafe" % "config" % "1.4.1"
)