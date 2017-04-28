import sbt.ScriptedPlugin._
import sbt.Keys._
import sbt._

lazy val root = (project in file("."))
  .settings(
    commonSettings,
    publishingSettings,
    sbt.ScriptedPlugin.scriptedSettings,
    scriptedLaunchOpts += "-Dplugin.version=" + version.value,
    scriptedBufferLog := false
  )


lazy val commonSettings = Seq(
  organization := "ch.epfl.lamp",
  name := "sbt-dotty",
  version := "0.1.0-RC3",
  scalacOptions ++= Seq("-feature", "-deprecation", "-encoding", "utf8"),
  scalaVersion := "2.10.6",
  sbtPlugin := true
)

lazy val publishingSettings = Seq(
  publishMavenStyle := true,
  publishArtifact := true,
  isSnapshot := version.value.contains("SNAPSHOT"),
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value)
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("releases"  at nexus + "service/local/staging/deploy/maven2")
  },
  publishArtifact in Test := false,
  homepage := Some(url("https://github.com/lampepfl/sbt-dotty")),
  licenses += ("BSD New",
    url("https://github.com/lampepfl/sbt-dotty/blob/master/LICENSE")),
  scmInfo := Some(
    ScmInfo(
      url("https://github.com/lampepfl/sbt-dotty"),
      "scm:git:git@github.com:lampepfl/sbt-dotty.git"
    )
  ),
  developers += Developer(
    id = "felixmulder",
    name = "Felix Mulder",
    email = "felix.mulder@gmail.com",
    url = url("http://felixmulder.com")
  )
)
