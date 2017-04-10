package com.felixmulder.dotty.plugin

import sbt._
import sbt.Keys._

object DottyPlugin extends AutoPlugin {
  // http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22ch.epfl.lamp%22%20dotty
  val dottyVersion: String =
    sys.env.getOrElse("COMPILERVERSION", "0.1.1-20170214-606e36b-NIGHTLY")

  object autoImport {
    // NOTE:
    // - this is a def to support `scalaVersion := dottyLatestNightlyBuild`
    // - if this was a taskKey, then you couldn't do `scalaVersion := dottyLatestNightlyBuild`
    // - if this was a settingKey, then this would evaluate even if you don't use it.
    def dottyLatestNightlyBuild: Option[String] = {
      println("Fetching latest Dotty nightly version (requires an internet connection)...")
      val Version = """      <version>(0.1\..*)</version>""".r
      val latest = scala.io.Source
          .fromURL(
            "http://repo1.maven.org/maven2/ch/epfl/lamp/dotty_2.11/maven-metadata.xml")
          .getLines()
          .collect { case Version(version) => version }
          .toSeq
          .lastOption
      println(s"Latest Dotty nightly build version: $latest")
      latest
    }

    // Allows plugin users to enable Dotty in a single line:
    //   project.configure(dottyEnable)         # regular project
    //   crossProject.jvmConfigure(dottyEnable) # Scala.js cross-project
    // while keeping existing build setttings unchanged.
    def dottyEnable(project: Project): Project = dottyEnableWithVersion(project, dottyVersion)
    def dottyEnableWithVersion(project: Project, dottyVersion: String): Project =
      project
        .settings(
          scalaVersion := dottyVersion,
          // `scalacOption +=` keeps existing options such as -Xwarn-unused-import
          // which are invalid with Dotty.
          scalacOptions := Seq("-language:Scala2")
        )
        .enablePlugins(DottyPlugin)
  }
  override def requires: Plugins = plugins.JvmPlugin

  override def projectSettings: Seq[Setting[_]] =
    Seq(
      // Dotty version
      scalaVersion := dottyVersion,
      scalaOrganization := "ch.epfl.lamp",

      // Dotty is compatible with Scala 2.11, as such you can use 2.11
      // binaries. However, when publishing - this version number should be set
      // to 0.1 (the dotty version number)
      scalaBinaryVersion := "2.11",

      // bug in sbt 0.13.13: https://github.com/sbt/sbt/issues/2867
      // should be fixed in 0.13.14
      ivyScala ~= (_ map (_ copy (overrideScalaVersion = false))),

      // Compiler on tool path
      libraryDependencies += "ch.epfl.lamp" % "dotty_2.11" % scalaVersion.value % "scala-tool",

      // Bridge which allows REPL and compilation via dotty
      scalaCompilerBridgeSource := ("ch.epfl.lamp" % "dotty-sbt-bridge" % scalaVersion.value % "component").sources(),

      // dotty requires the latest version of the sbt compiler interface
      resolvers += Resolver.typesafeIvyRepo("releases")
    )
}
