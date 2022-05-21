package typicode

import caliban.*

import org.http4s.blaze.server.BlazeServerBuilder

import sttp.client3.httpclient.zio.*

import zhttp.http.*
import zhttp.service.Server

import zio.*
import zio.stream.ZStream

import typicode.services.*
import typicode.resolvers.*

import caliban.Http4sAdapter
import cats.data.Kleisli
import org.http4s.StaticFile
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.implicits.*
import org.http4s.server.Router
import org.http4s.server.middleware.CORS
import zio.*
import zio.interop.catz.*

object Main extends ZIOAppDefault:
  type TypicodeEnv  = ZEnv & SttpClient & TypicodeService
  type TypicodeF[A] = RIO[TypicodeEnv, A]

  val program =
    // ZIO
    //   .runtime[TypicodeEnv]
    //   .flatMap(implicit runtime =>
    for
      interpreter <- GraphQL
                       .graphQL[SttpClient & TypicodeService, Queries, Unit, Unit](
                         RootResolver(Queries(user => UserView.resolve(user.id)))
                       )
                       .interpreter
      // this doesn't compile
      // _           <- Server
      //                  .start(
      //                    8088,
      //                    Http.route[Request] {
      //                      case _ -> !! / "api" / "graphql" => ZHttpAdapter.makeHttpService(interpreter)
      //                      case _ -> !! / "ws" / "graphql"  => ZHttpAdapter.makeWebSocketService(interpreter)
      //                      case _ -> !! / "graphiql"        => Http.fromStream(ZStream.fromResource("graphiql.html"))
      //                    },
      //                  )
      //                  .forever
      // this doesn't compile either
      // _           <- BlazeServerBuilder[TypicodeF]
      //                  .bindHttp(8088, "localhost")
      //                  .withHttpWebSocketApp(wsBuilder =>
      //                    Router[TypicodeF](
      //                      "/api/graphql" -> CORS.policy(Http4sAdapter.makeHttpService(interpreter)),
      //                      "/ws/graphql"  -> CORS.policy(Http4sAdapter.makeWebSocketService(wsBuilder, interpreter)),
      //                      "/graphiql"    -> Kleisli.liftF(StaticFile.fromResource("/graphiql.html", None)),
      //                    ).orNotFound
      //                  )
      //                  .resource
      //                  .toManagedZIO
      //                  .useForever
      // and this compiles, but fails to run with 'Caused by: java.lang.ClassNotFoundException: zio.IO$'
      result      <- interpreter.execute(Queries.user)
      _           <- Console.printLine(result.data.toString)
    yield ()
  // )

  override def run =
    program
      .provide(HttpClientZioBackend.layer(), TypicodeService.live)
