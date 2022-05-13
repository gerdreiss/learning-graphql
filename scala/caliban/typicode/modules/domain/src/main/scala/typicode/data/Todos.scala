package typicode
package data

import zio.json.DeriveJsonDecoder
import zio.json.JsonDecoder

case class Todo(
    userId: UserId,
    id: PostId,
    title: String,
    completed: Boolean
)

case class Todos(data: List[Todo]) extends TypicodeData

object Todos:
  given JsonDecoder[Todos] = DeriveJsonDecoder.gen[Todos]
  given JsonDecoder[Todo]  = DeriveJsonDecoder.gen[Todo]
