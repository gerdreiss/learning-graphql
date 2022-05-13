package typicode
package resolvers

import sttp.client3.httpclient.zio.*

import zio.query.*

import data.*
import services.*

case class CommentView(
    name: String,
    email: String,
    body: String
)

object CommentView:
  case class GetComments(postId: PostId) extends Request[Throwable, Comments]

  private val ds: DataSource[SttpClient & TypicodeService, GetComments] =
    DataSource.fromFunctionZIO("CommentsDataSource") { request =>
      TypicodeService.getComments(request.postId)
    }

  def resolve(postId: PostId): RQuery[SttpClient & TypicodeService, List[CommentView]] =
    ZQuery.fromRequest(GetComments(postId))(ds).map {
      _.data.map { comment =>
        CommentView(comment.name, comment.email, comment.body)
      }
    }
