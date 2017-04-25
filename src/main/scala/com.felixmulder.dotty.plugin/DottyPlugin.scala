package com.felixmulder.dotty.plugin

import sbt._
import sbt.Keys._

object DottyPlugin extends AutoPlugin {
  object autoImport {
    val isDotty = settingKey[Boolean]("Is this project compiled with Dotty?")

    // NOTE:
    // - this is a def to support `scalaVersion := dottyLatestNightlyBuild`
    // - if this was a taskKey, then you couldn't do `scalaVersion := dottyLatestNightlyBuild`
    // - if this was a settingKey, then this would evaluate even if you don't use it.
    def dottyLatestNightlyBuild: Option[String] = {
      println("Fetching latest Dotty nightly version (requires an internet connection)...")
      val Version = """      <version>(0.1\..*-bin.*)</version>""".r
      val latest = scala.io.Source
          .fromURL(
            "http://repo1.maven.org/maven2/ch/epfl/lamp/dotty_0.1/maven-metadata.xml")
          .getLines()
          .collect { case Version(version) => version }
          .toSeq
          .lastOption
      println(s"Latest Dotty nightly build version: $latest")
      latest
    }
  }

  import autoImport._

  override def requires: Plugins = plugins.JvmPlugin
  override def trigger = allRequirements

  // Adapted from CrossVersionUtil#sbtApiVersion
  private def sbtFullVersion(v: String): Option[(Int, Int, Int)] =
  {
    val ReleaseV = """(\d+)\.(\d+)\.(\d+)(-\d+)?""".r
    val CandidateV = """(\d+)\.(\d+)\.(\d+)(-RC\d+)""".r
    val NonReleaseV = """(\d+)\.(\d+)\.(\d+)([-\w+]*)""".r
    v match {
      case ReleaseV(x, y, z, ht) => Some((x.toInt, y.toInt, z.toInt))
      case CandidateV(x, y, z, ht)  => Some((x.toInt, y.toInt, z.toInt))
      case NonReleaseV(x, y, z, ht) if z.toInt > 0 => Some((x.toInt, y.toInt, z.toInt))
      case _ => None
    }
  }


  override def projectSettings: Seq[Setting[_]] = {
    Seq(
      isDotty := {
        val log = sLog.value

        sbtFullVersion(sbtVersion.value) match {
          case Some((sbtMajor, sbtMinor, sbtPatch)) if sbtMajor == 0 && sbtMinor == 13 && sbtPatch < 15 =>
            log.error(s"The sbt-dotty plugin cannot work with this version of sbt (${sbtVersion.value}), sbt >= 0.13.15 is required.")
            false
          case _ =>
            scalaVersion.value.startsWith("0.")
        }
      },
      scalaOrganization := {
        if (isDotty.value)
          "ch.epfl.lamp"
        else
          scalaOrganization.value
      },

      scalaBinaryVersion := {
        if (isDotty.value)
          "0.1" // TODO: Fix sbt so that this isn't needed
        else
          scalaBinaryVersion.value
      },

      // Needed until https://github.com/sbt/sbt/issues/3012 is fixed
      resolvers ++= {
        if (isDotty.value)
          Seq(Resolver.typesafeIvyRepo("releases"))
        else
          Nil
      }
    )
  }
}
