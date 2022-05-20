package typicode

import caliban.*

import sttp.client3.httpclient.zio.*

import zhttp.http.*
import zhttp.service.Server

import zio.*
import zio.stream.ZStream

import typicode.services.*
import typicode.resolvers.*

object Main extends ZIOAppDefault:

  val program =
    for
      interpreter <- GraphQL
                       .graphQL[SttpClient & TypicodeService, Queries, Unit, Unit](
                         RootResolver(Queries(user => UserView.resolve(user.id)))
                       )
                       .interpreter
      _           <- Server
                       .start(
                         8088,
                         Http.route[Request] {
                           case _ -> !! / "api" / "graphql" => ZHttpAdapter.makeHttpService(interpreter)
                           case _ -> !! / "ws" / "graphql"  => ZHttpAdapter.makeWebSocketService(interpreter)
                           case _ -> !! / "graphiql"        => Http.fromStream(ZStream.fromResource("graphiql.html"))
                         }
                       )
                       .forever
    yield ()

  override def run = program.provide(HttpClientZioBackend.layer(), TypicodeService.live, Clock.live)
