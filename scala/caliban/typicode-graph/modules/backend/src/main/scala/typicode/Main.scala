package typicode

import caliban.GraphQL
import caliban.GraphQLResponse
import caliban.RootResolver
import caliban.ZHttpAdapter

import io.netty.handler.codec.http.HttpHeaderNames
import io.netty.handler.codec.http.HttpHeaderValues

import sttp.client3.httpclient.zio.*

import zhttp.http.*
import zhttp.service.Server

import zio.Clock
import zio.ZIO
import zio.ZIOAppDefault
import zio.stream.ZStream

import services.TypicodeService
import resolvers.*

object Main extends ZIOAppDefault:

  val graphiql = Http.fromStream(ZStream.fromResource("graphiql.html"))

  val program =
    for
      interpreter <- GraphQL
                       .graphQL[SttpClient & TypicodeService, Queries, Unit, Unit](
                         RootResolver(Queries(user => UserView.resolve(user.id)))
                       )
                       .interpreter
      // response    <- interpreter.execute(Queries.user)
      // _           <- response match
      //                  case GraphQLResponse(data, Nil, _) => ZIO.debug(data)
      //                  case GraphQLResponse(_, es, _)     => ZIO.foreach(es)(e => ZIO.debug(e))
      // this doesn't compile: 'java.lang.AssertionError: assertion failed'
      _           <- Server
                       .start(
                         8088,
                         Http.route[Request] {
                           case _ -> !! / "api" / "graphql" => ZHttpAdapter.makeHttpService(interpreter)
                           case _ -> !! / "ws" / "graphql"  => ZHttpAdapter.makeWebSocketService(interpreter)
                           case _ -> !! / "graphiql"        => graphiql
                         }
                       )
                       .forever
    yield ()

  override def run = program
    .provide(HttpClientZioBackend.layer(), TypicodeService.live, Clock.live)
