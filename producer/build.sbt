ThisBuild / organization := "poc"
ThisBuild / scalaVersion := "2.12.17"
ThisBuild / version := "1.0"

ThisBuild / assemblyMergeStrategy := {
  case PathList("javax", "servlet", xs @ _*)          => MergeStrategy.first
  case PathList(ps @ _*) if ps.last endsWith ".html"  => MergeStrategy.first
  case PathList(ps @ _*) if ps.last endsWith ".class" => MergeStrategy.first
  case "application.conf"                             => MergeStrategy.concat
  case "unwanted.txt"                                 => MergeStrategy.discard
  case x =>
    val oldStrategy = (ThisBuild / assemblyMergeStrategy).value
    oldStrategy(x)
}

lazy val root = (project in file(".")).settings(
  name := "Producer",
  assembly / mainClass := Some("producer.ProducerApp"),
  assembly / assemblyJarName := "ProducerApp.jar",
  libraryDependencies ++= Seq(
    "com.typesafe" % "config" % "1.4.2",
    "org.apache.kafka" % "kafka-clients" % "3.4.0",
    "org.slf4j" % "slf4j-simple" % "2.0.9",
  )
)
