package typicode
package data

case class Comment(
    postId: PostId,
    id: CommentId,
    name: String,
    email: String,
    body: String
)

case class Post(
    userId: UserId,
    id: PostId,
    title: String,
    body: String
)
