val scala3Version = "3.1.2"

lazy val root = project
  .in(file("."))
  .aggregate(shared, backend, frontend)

lazy val backend = project
  .in(file("backend"))
  .settings(
    name         := "backend",
    version      := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    libraryDependencies ++= Seq(
      "com.github.ghostdogpr" %% "caliban" % "2.0.0-RC2"
    ),
    scalacOptions ++= commonOptions
  )

lazy val frontend = project
  .in(file("frontend"))
  .settings(
    name         := "frontend",
    version      := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    scalacOptions ++= commonOptions
  )

lazy val shared = project
  .in(file("shared"))
  .settings(
    name         := "shared",
    version      := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    scalacOptions ++= commonOptions
  )

val commonOptions = Seq(
  "-source:future",
  "-deprecation",
  // "-explain",
  "-feature",
  "-language:implicitConversions",
  "-unchecked",
  "-Xfatal-warnings",
  "-Xmax-inlines:64",
  "-Ykind-projector",
  "-rewrite",
  "-indent"
)
