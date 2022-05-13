package typicode
package resolvers

import sttp.client3.httpclient.zio.*

import zio.query.*

import data.*
import services.*

case class PostView(
    title: String,
    body: String,
    comments: RQuery[SttpClient & TypicodeService, List[CommentView]]
)

object PostView:
  case class GetPosts(userId: UserId) extends Request[Throwable, Posts]

  val ds: DataSource[SttpClient & TypicodeService, GetPosts] =
    DataSource.fromFunctionZIO("PostsDataSource") { request =>
      TypicodeService.getPosts(request.userId)
    }

  def resolve(userId: UserId): RQuery[SttpClient & TypicodeService, List[PostView]] =
    ZQuery.fromRequest(GetPosts(userId))(ds).map {
      _.data.map { post =>
        PostView(
          post.title,
          post.body,
          CommentView.resolve(post.id)
        )
      }
    }
