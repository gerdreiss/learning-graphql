val scala3Version = "3.1.2"

lazy val root = project
  .in(file("."))
  .settings(
    name         := "caliban-orders",
    version      := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    libraryDependencies ++= Seq(
      "com.github.ghostdogpr" %% "caliban" % "2.0.0-RC2"
    ),
    scalacOptions ++= Seq(
      "-source:future",
      "-deprecation",
      "-explain",
      "-feature",
      "-language:implicitConversions",
      "-unchecked",
      "-Xfatal-warnings",
      "-Xmax-inlines:64",
      "-Ykind-projector",
      "-rewrite",
      "-indent"
    )
  )
