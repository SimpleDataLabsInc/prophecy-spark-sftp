ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "2.12.10"

mainClass in (Compile, run) := Some("Main")

lazy val root = (project in file("."))
  .settings(
    name := "prophecy-spark-sftp",
  )

val SparkVersion = "3.3.0"
val JschVersion = "0.1.55"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-sql" % SparkVersion,
  "com.jcraft" % "jsch" % JschVersion
)

assemblyMergeStrategy in assembly := {
  case "application.conf" ⇒ MergeStrategy.concat
  case "reference.conf"   ⇒ MergeStrategy.concat
  case x if Assembly.isConfigFile(x) ⇒
    MergeStrategy.concat
  case PathList(ps @ _*) if Assembly.isReadme(ps.last) || Assembly.isLicenseFile(ps.last) ⇒
    MergeStrategy.rename
  case PathList("META-INF", xs @ _*) ⇒
    xs.map(_.toLowerCase) match {
      case ("manifest.mf" :: Nil) | ("index.list" :: Nil) | ("dependencies" :: Nil) ⇒
        MergeStrategy.discard
      case ps @ (x :: xs) if ps.last.endsWith(".sf") || ps.last.endsWith(".dsa") ⇒
        MergeStrategy.discard
      case "plexus" :: xs ⇒
        MergeStrategy.discard
      case "services" :: xs ⇒
        MergeStrategy.filterDistinctLines
      case ("spring.schemas" :: Nil) | ("spring.handlers" :: Nil) ⇒
        MergeStrategy.filterDistinctLines
      case _ ⇒ MergeStrategy.first
    }
  case _ ⇒ MergeStrategy.first
}