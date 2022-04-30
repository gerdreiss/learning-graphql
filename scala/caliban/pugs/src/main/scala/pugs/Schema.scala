package pugs

import caliban.CalibanError.ExecutionError
import caliban.schema.ArgBuilder
import caliban.schema.Schema
import zio.*

import java.net.URL
import scala.util.Try

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

given Schema[Any, URL] = Schema.stringSchema.contramap(_.toString)
given ArgBuilder[URL]  = ArgBuilder.string.flatMap(arg =>
  Try(new URL(arg)).fold(_ => Left(ExecutionError(s"Invalid URL: $arg")), Right(_))
)
