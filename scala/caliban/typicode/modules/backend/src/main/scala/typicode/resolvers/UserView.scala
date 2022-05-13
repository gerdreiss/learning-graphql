package typicode
package resolvers

import sttp.client3.httpclient.zio.*

import zio.query.*

import data.*
import services.*

case class UserView(
    name: String,
    username: String,
    email: String,
    phone: String,
    website: String,
    address: Address,
    company: Company,
    todos: RQuery[SttpClient & TypicodeService, List[TodoView]],
    posts: RQuery[SttpClient & TypicodeService, List[PostView]],
    albums: RQuery[SttpClient & TypicodeService, List[AlbumView]]
)

object UserView:
  def resolve(userId: UserId): RQuery[SttpClient & TypicodeService, UserView] =
    ZQuery
      .fromZIO(TypicodeService.getUser(userId))
      .map { user =>
        UserView(
          user.name,
          user.username,
          user.email,
          user.phone,
          user.website,
          user.address,
          user.company,
          TodoView.resolve(user.id),
          PostView.resolve(user.id),
          AlbumView.resolve(user.id)
        )
      }
