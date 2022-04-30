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

case class TodoList(data: List[Todo]) extends TypicodeData

object Todo:
  given JsonDecoder[Todo] = DeriveJsonDecoder.gen[Todo]

object TodoList:
  given JsonDecoder[TodoList] = DeriveJsonDecoder.gen[TodoList]
