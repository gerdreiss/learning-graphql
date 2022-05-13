package typicode

import caliban.GraphQL
import caliban.GraphQLResponse
import caliban.RootResolver
import caliban.schema.GenericSchema
import caliban.schema.Schema

import sttp.client3.httpclient.zio.*

import zio.*

import resolvers.*
import services.*

object Main extends ZIOAppDefault:

  val query: String =
    """
      |{
      |  user(id: 1) {
      |      name
      |      username
      |      email
      |      phone
      |      website
      |      todos {
      |        title
      |        completed
      |      }
      |  }
      |}
      |""".stripMargin

  val program =
    for
      interpreter <-
        GraphQL
          .graphQL[SttpClient & TypicodeService, UserTodos.Query, Unit, Unit](RootResolver(UserTodos.resolver))
          .interpreter
      response    <- interpreter.execute(query)
      _           <- response match
                       case GraphQLResponse(data, Nil, _) => ZIO.debug(data)
                       case GraphQLResponse(_, es, _)     => ZIO.foreach(es)(e => ZIO.debug(e))
    yield ()

  override def run = program
    .provide(HttpClientZioBackend.layer(), TypicodeService.live)
