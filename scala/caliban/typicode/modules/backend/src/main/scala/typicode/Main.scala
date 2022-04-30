package typicode

import zio.*
import caliban.CalibanError.ValidationError
import caliban.GraphQL.graphQL
import caliban.RootResolver

import services.*
import resolvers.*

object Main extends ZIOAppDefault:

  val query =
    """
      |{
      |  user(id: 1) {
      |      username
      |      email
      |      todos {
      |        title
      |        completed
      |      }
      |  }
      |}
      |""".stripMargin

  override def run =
    for
      typicodeService <- TypicodeService.live
      resolver         = Todos.resolver(typicodeService)
      api              = graphQL(RootResolver(resolver))
      interpreter     <- api.interpreter
      response        <- interpreter.execute(query)
      _               <- IO.debug(response.data)
    yield ()
