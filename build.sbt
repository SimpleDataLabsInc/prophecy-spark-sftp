ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.8"

lazy val root = (project in file("."))
  .settings(
    name := "prophecy-spark-sftp",
    idePackagePrefix := Some("io.prophecy")
  )

val SparkVersion = "3.3.0"
val JschVersion = "0.1.55"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-sql" % SparkVersion,
  "com.jcraft" % "jsch" % JschVersion
)
