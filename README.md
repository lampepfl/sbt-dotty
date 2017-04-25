sbt-dotty
=========

[![Build Status](https://travis-ci.org/lampepfl/sbt-dotty.svg?branch=master)](https://travis-ci.org/lampepfl/sbt-dotty)
[![Maven Central](https://img.shields.io/maven-central/v/ch.epfl.lamp/sbt-dotty.svg)]()

This plugin allows you to easily create an sbt dotty project using the
standard structure:

```
.
├── build.sbt
├── project
│   ├── build.properties
│   └── plugins.sbt
└── src
```

Please see the
[example](https://github.com/felixmulder/sbt-dotty/tree/master/example)
project.

build.sbt
---------
```
name := "application"
version := "0.1"

scalaVersion := "0.1.1-bin-20170501-b19d1fb-NIGHTLY"
```

New builds of dotty are published nightly, scroll to the end of
https://repo1.maven.org/maven2/ch/epfl/lamp/dotty_0.1 to find the latest version
number. Alternatively, you can set `scalaVersion := dottyLatestNightlyBuild.get`
to automatically use the latest nightly build of dotty.

If you want to migrate an existing library, it might be a good idea to start
out with the compatibility mode by adding:

```
scalacOptions ++= Seq("-language:Scala2")
```

to `build.sbt`, note that this affects typechecking and thus may prevent some
valid Dotty code from compiling.

A tool to port code from Scala 2.x to Dotty is currently in development at
https://github.com/scalacenter/scalafix

build.properties
----------------
```
sbt.version=0.13.15
```

Older versions of sbt are not supported.

plugins.sbt
-----------
```
addSbtPlugin("ch.epfl.lamp" % "sbt-dotty" % "0.1.0-RC3")
```
