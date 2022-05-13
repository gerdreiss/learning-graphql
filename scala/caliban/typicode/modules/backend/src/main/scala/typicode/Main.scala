package typicode

import caliban.GraphQL
import caliban.GraphQLResponse
import caliban.RootResolver

import sttp.client3.httpclient.zio.*
import zio.*

import services.*
import resolvers.*

object Main extends ZIOAppDefault:

  val userInterpreter = GraphQL
    .graphQL[SttpClient & TypicodeService, Queries, Unit, Unit](
      RootResolver(Queries(user => UserView.resolve(user.id)))
    )
    .interpreter

  val program =
    for
      interpreter <- userInterpreter
      response    <- interpreter.execute(Queries.user)
      _           <- response match
                       case GraphQLResponse(data, Nil, _) => ZIO.debug(data)
                       case GraphQLResponse(_, es, _)     => ZIO.foreach(es)(e => ZIO.debug(e))
    yield ()

  override def run = program
    .provide(HttpClientZioBackend.layer(), TypicodeService.live)
