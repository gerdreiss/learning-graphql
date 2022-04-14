val scala3Version = "3.1.2"

lazy val root = project
  .in(file("."))
  .settings(
    name := "caliban-pugs",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    libraryDependencies += "com.github.ghostdogpr" %% "caliban" % "2.0.0-RC2"
  )
