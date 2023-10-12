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

lazy val sparkVersion = "3.4.0"
lazy val httpVersion = "4.5.14"
lazy val root = (project in file(".")).settings(
  name := "Spark",
  assembly / mainClass := Some("consumer.ConsumerApp"),
  assembly / assemblyJarName := "ConsumerApp.jar",
  libraryDependencies ++= Seq(
    "com.typesafe" % "config" % "1.4.2",
    "com.typesafe.scala-logging" %% "scala-logging" % "3.9.5",
    "org.apache.spark" %% "spark-core" % sparkVersion % "provided",
    "org.apache.spark" %% "spark-sql" % sparkVersion % "provided",
    "org.apache.spark" %% "spark-sql-kafka-0-10" % sparkVersion
      exclude("org.apache.hadoop", "hadoop-client-runtime")
      exclude("org.apache.httpcomponents", "httpclient"),
    "org.apache.httpcomponents" % "httpclient" % httpVersion,
    "org.apache.httpcomponents" % "httpmime" % httpVersion,
  )
)
