package typicode

import caliban.CalibanError.ValidationError
import caliban.GraphQL.graphQL
import caliban.GraphQLResponse
import caliban.RootResolver
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

  override def run: ZIO[Any, Throwable, Unit] =
    for
      typicodeService <- TypicodeService.live
      resolver         = Todos.resolver(typicodeService)
      api              = graphQL(RootResolver(resolver))
      interpreter     <- api.interpreter
      response        <- interpreter.execute(query)
      _               <- response match
                           case GraphQLResponse(data, Nil, _) => ZIO.debug(data)
                           case GraphQLResponse(_, es, _)     => ZIO.foreach(es)(e => ZIO.debug(e))
    yield ()
