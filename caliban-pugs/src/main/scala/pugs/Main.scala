package pugs

import caliban.CalibanError.ExecutionError
import caliban.GraphQL.graphQL
import caliban.RootResolver
import caliban.schema.ArgBuilder
import caliban.schema.Schema
import zio.*

import java.net.URL
import scala.util.Try

object Main extends ZIOAppDefault:

  val api = graphQL(RootResolver(queries, mutations))

  val query =
    """
      |{
      |  findPug(name: "Piggy") {
      |    name
      |    nicknames
      |    pictureUrl
      |    color
      |  }
      |  randomPugPicture
      |}
    """.stripMargin

  override def run =
    for
      interpreter <- api.interpreter
      result      <- interpreter.execute(query)
      _           <- Console.printLine(result.data.toString)
    yield ()
