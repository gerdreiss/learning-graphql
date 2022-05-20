import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._
import sbt._

object Dependencies {
  object V         {
    val tyrian      = "0.3.2"
    val caliban     = "2.0.0-RC2"
    val sttp3       = "3.5.2"
    val zio         = "2.0.0-RC6"
    val `zio-query` = "0.3.0-RC5"
    val `zio-json`  = "0.3.0-RC8"
  }
  object Libraries {
    val tyrian                   = Def.setting("io.indigoengine" %%% "tyrian" % V.tyrian)
    val caliban                  = Def.setting("com.github.ghostdogpr" %%% "caliban" % V.caliban)
    val `caliban-client`         = Def.setting("com.github.ghostdogpr" %% "caliban-client" % V.caliban)
    val `caliban-zio-http`       = Def.setting("com.github.ghostdogpr" %% "caliban-zio-http" % V.caliban)
    val zio                      = Def.setting("dev.zio" %% "zio" % V.zio)
    val `zio-query`              = Def.setting("dev.zio" %% "zio-query" % V.`zio-query`)
    val `zio-json`               = Def.setting("dev.zio" %% "zio-json" % V.`zio-json`)
    val `httpclient-backend-zio` = Def.setting("com.softwaremill.sttp.client3" %% "httpclient-backend-zio" % V.sttp3)
  }
}
