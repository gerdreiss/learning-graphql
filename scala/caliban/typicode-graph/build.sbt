ThisBuild / scalaVersion     := "3.1.2"
ThisBuild / version          := "0.1.0"
ThisBuild / organization     := "pro.reiss"
ThisBuild / organizationName := "reiss.pro"

Compile / run / fork := true

Global / onChangedBuildSource := ReloadOnSourceChanges
Global / semanticdbEnabled    := true // for metals

lazy val `typicode-graph` = project
  .in(file("."))
  .settings(name := "typicode-graph")
  .aggregate(frontend, backend, domain.jvm, domain.js)

lazy val frontend = project
  .in(file("modules/frontend"))
  .enablePlugins(ScalaJSPlugin)
  .settings(commonSettings)
  .settings(
    scalaJSUseMainModuleInitializer := true,
    scalaJSLinkerConfig ~= {
      _.withModuleKind(ModuleKind.ESModule)
    },
    scalaJSLinkerConfig ~= {
      _.withSourceMap(false)
    },
    libraryDependencies ++= Seq(
      "io.indigoengine"       %%% "tyrian"         % "0.3.2",
      "com.github.ghostdogpr" %%% "caliban-client" % "2.0.0-RC2"
    )
  )
  .dependsOn(domain.js)

lazy val backend = project
  .in(file("modules/backend"))
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Seq(
      "com.softwaremill.sttp.client3" %% "httpclient-backend-zio" % "3.5.2",
      "com.github.ghostdogpr"         %% "caliban"                % "1.4.0",
      "com.github.ghostdogpr"         %% "caliban-zio-http"       % "1.4.0",
      "dev.zio"                       %% "zio"                    % "2.0.0-RC6",
      "dev.zio"                       %% "zio-query"              % "0.3.0-RC5"
    ),
    excludeDependencies += "org.scala-lang.modules" % "scala-collection-compat_2.13"
  )
  .dependsOn(domain.jvm)

lazy val domain = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("modules/domain"))
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio-json" % "0.3.0-RC8"
    )
  )

val commonSettings = Seq(
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
