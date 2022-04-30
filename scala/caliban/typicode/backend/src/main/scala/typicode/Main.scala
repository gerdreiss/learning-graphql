package typicode

import zio.*
import caliban.CalibanError.ValidationError
import caliban.GraphQL.graphQL
import caliban.RootResolver
import caliban.ResponseValue

object Main extends ZIOAppDefault:

  val query = """
              |{
              |  userTodosView(userId: 1) {
              |    username
              |  }
              |}
              |""".stripMargin

  val program: IO[ValidationError, ResponseValue] =
    for
      typicodeService <- TypicodeService.live
      resolver         = todos.resolver(typicodeService)
      api              = graphQL(RootResolver(resolver))
      interpreter     <- api.interpreter
      response        <- interpreter.execute(query)
    yield response.data

  override def run =
    for
      response <- program
      _        <- Console.printLine(response)
    yield ()
