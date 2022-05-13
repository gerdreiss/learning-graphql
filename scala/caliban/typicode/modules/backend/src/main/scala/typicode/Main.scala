package typicode

import caliban.GraphQL
import caliban.GraphQLResponse
import caliban.RootResolver
import caliban.schema.GenericSchema
import caliban.schema.Schema

import sttp.client3.httpclient.zio.*
import zio.*
import zio.query.*

import data.*
import services.*
import resolvers.*

object Main extends ZIOAppDefault:

  val query: String =
    """
      |{
      |  user(id: 1) {
      |    name
      |    username
      |    email
      |    phone
      |    website
      |    todos {
      |      title
      |      completed
      |    }
      |    posts {
      |      title
      |      body
      |      comments {
      |        name
      |        email
      |        body
      |      }
      |    }
      |  }
      |}
      |""".stripMargin

  case class QueryArgs(id: UserId)
  case class Queries(user: QueryArgs => RQuery[SttpClient & TypicodeService, UserView])

  val userInterpreter = GraphQL
    .graphQL[SttpClient & TypicodeService, Queries, Unit, Unit](
      RootResolver(Queries(args => UserView.resolve(args.id)))
    )
    .interpreter

  val program =
    for
      interpreter <- userInterpreter
      response    <- interpreter.execute(query)
      _           <- response match
                       case GraphQLResponse(data, Nil, _) => ZIO.debug(data)
                       case GraphQLResponse(_, es, _)     => ZIO.foreach(es)(e => ZIO.debug(e))
    yield ()

  override def run = program
    .provide(HttpClientZioBackend.layer(), TypicodeService.live)
