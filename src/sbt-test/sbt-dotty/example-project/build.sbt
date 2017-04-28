enablePlugins(DottyPlugin)
scalaVersion := dottyLatestNightlyBuild.get

lazy val a = project
  .settings(
    scalaVersion := "2.12.1", // user doesn't need to remove/comment existing settings.
    scalacOptions ++= Seq(
      "-Xfatal-warnings" // invalid dotc option.
    )
  )
  .configure(dottyEnable) // can be conveniently commented out to get original build.
