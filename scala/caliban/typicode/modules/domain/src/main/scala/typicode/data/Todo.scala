package typicode
package data

case class Todo(
    userId: UserId,
    id: PostId,
    title: String,
    completed: Boolean
)
