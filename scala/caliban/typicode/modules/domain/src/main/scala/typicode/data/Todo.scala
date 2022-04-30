package typicode.data

case class Todo(
    userId: UserId,
    id: PostId,
    title: String,
    completed: Boolean
)
