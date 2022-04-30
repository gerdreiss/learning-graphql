package typicode
package data

import zio.json.DeriveJsonDecoder
import zio.json.JsonDecoder

case class Comment(
    postId: PostId,
    id: CommentId,
    name: String,
    email: String,
    body: String
) extends TypicodeData

object Comment:
  given JsonDecoder[Comment] = DeriveJsonDecoder.gen[Comment]

case class Post(
    userId: UserId,
    id: PostId,
    title: String,
    body: String
) extends TypicodeData

object Post:
  given JsonDecoder[Post] = DeriveJsonDecoder.gen[Post]
