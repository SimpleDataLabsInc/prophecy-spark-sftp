ThisBuild / version := "0.1.1"
ThisBuild / scalaVersion := "2.12.10"

lazy val root = (project in file("."))
  .settings(
    name := "filetransfer",
  )
  .settings(addArtifact(Artifact("filetransfer", "assembly"), sbtassembly.AssemblyKeys.assembly))

val SparkVersion = "3.3.0"
val SshjVersion = "0.27.0"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-sql" % SparkVersion,
  "com.hierynomus" % "sshj" % SshjVersion
)

assemblyMergeStrategy in assembly := {
  case "application.conf" ⇒ MergeStrategy.concat
  case "reference.conf" ⇒ MergeStrategy.concat
  case x if Assembly.isConfigFile(x) ⇒
    MergeStrategy.concat
  case PathList(ps@_*) if Assembly.isReadme(ps.last) || Assembly.isLicenseFile(ps.last) ⇒
    MergeStrategy.rename
  case PathList("META-INF", xs@_*) ⇒
    xs.map(_.toLowerCase) match {
      case ("manifest.mf" :: Nil) | ("index.list" :: Nil) | ("dependencies" :: Nil) ⇒
        MergeStrategy.discard
      case ps@(x :: xs) if ps.last.endsWith(".sf") || ps.last.endsWith(".dsa") ⇒
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

ThisBuild / organization := "io.prophecy.spark"
ThisBuild / organizationName := "prophecy"
ThisBuild / organizationHomepage := Some(url("https://app.prophecy.io/"))

ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/SimpleDataLabsInc/prophecy-spark-sftp.git"),
    "scm:git@github.com:SimpleDataLabsInc/prophecy-spark-sftp.git"
  )
)
ThisBuild / developers := List(
  Developer(
    id    = "anshuman-91",
    name  = "Anshuman Agrawal",
    email = "anshuman@prophecy.io",
    url   = url("https://github.com/anshuman-91")
  )
)

ThisBuild / description := "Contains prophecy provided spark connectors"
ThisBuild / licenses := List("Apache 2" -> new URL("http://www.apache.org/licenses/LICENSE-2.0.txt"))
ThisBuild / homepage := Some(url("https://github.com/SimpleDataLabsInc/prophecy-spark-sftp.git"))

// Remove all additional repository other than Maven Central from POM
ThisBuild / pomIncludeRepository := { _ => false }
ThisBuild / publishTo := {
  val nexus = "https://s01.oss.sonatype.org/"
  if (isSnapshot.value) Some("snapshots" at nexus + "content/repositories/snapshots")
  else Some("releases" at nexus + "service/local/staging/deploy/maven2")
}
ThisBuild / publishMavenStyle := true