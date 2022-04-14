import caliban.CalibanError.ExecutionError
import caliban.GraphQL.graphQL
import caliban.RootResolver
import caliban.schema.ArgBuilder
import caliban.schema.Schema
import zio.*

import java.net.URL
import scala.util.Try

/** MODEL */
enum Color:
  case FAWN, BLACK, OTHER

case class Pug(name: String, nicknames: List[String], pictureUrl: Option[URL], color: Color)

/** Service */
case class PugNotFound(name: String) extends Throwable

trait PugService:
  def findPug(name: String): IO[PugNotFound, Pug]
  def randomPugPicture: UIO[String]
  def addPug(pug: Pug): UIO[Unit]
  def editPugPicture(name: String, pictureUrl: URL): IO[PugNotFound, Unit]

val pugService: PugService =
  new:
    def findPug(name: String): IO[PugNotFound, Pug] =
      IO.succeed(
        Pug(
          "Piggy",
          Nil,
          Some(new URL("https://images.unsplash.com/photo-1523626797181-8c5ae80d40c2")),
          Color.FAWN
        )
      )

    def randomPugPicture: UIO[String] =
      UIO.succeed("https://images.unsplash.com/photo-1523626797181-8c5ae80d40c2")

    def addPug(pug: Pug): UIO[Unit] =
      UIO.unit

    def editPugPicture(name: String, pictureUrl: URL): IO[PugNotFound, Unit] =
      IO.unit

/** Schema */
case class FindPugArgs(name: String)
case class AddPugArgs(pug: Pug)
case class EditPugPictureArgs(name: String, pictureUrl: URL)

case class Queries(
    findPug: FindPugArgs => IO[PugNotFound, Pug],
    randomPugPicture: UIO[String]
)

case class Mutations(
    addPug: AddPugArgs => UIO[Unit],
    editPugPicture: EditPugPictureArgs => IO[PugNotFound, Unit]
)

/** Resolvers */
val queries = Queries(
  args => pugService.findPug(args.name),
  pugService.randomPugPicture
)

val mutations = Mutations(
  args => pugService.addPug(args.pug),
  args => pugService.editPugPicture(args.name, args.pictureUrl)
)

/** Custom Schemas */
given Schema[Any, URL] = Schema.stringSchema.contramap(_.toString)
given ArgBuilder[URL] = ArgBuilder.string.flatMap(arg =>
  Try(new URL(arg)).fold(_ => Left(ExecutionError(s"Invalid URL: $arg")), Right(_))
)

/** API */
val api = graphQL(RootResolver(queries, mutations))

/** Serving Requests */
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

object Main extends ZIOAppDefault:
  override def run =
    for
      interpreter <- api.interpreter
      result <- interpreter.execute(query)
      _ <- Console.putStrLn(result.data.toString)
    yield ()
