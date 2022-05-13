package typicode
package resolvers

import sttp.client3.httpclient.zio.*

import zio.query.*

import data.*
import services.*

case class AlbumView(
    title: String,
    photos: RQuery[SttpClient & TypicodeService, List[PhotoView]]
)

object AlbumView:
  case class GetAlbums(userId: UserId) extends Request[Throwable, Albums]

  private val ds: DataSource[SttpClient & TypicodeService, GetAlbums] =
    DataSource.fromFunctionZIO("AlbumsDataSource") { request =>
      TypicodeService.getAlbums(request.userId)
    }

  def resolve(userId: UserId): RQuery[SttpClient & TypicodeService, List[AlbumView]] =
    ZQuery.fromRequest(GetAlbums(userId))(ds).map {
      _.data.map(album =>
        AlbumView(
          album.title,
          PhotoView.resolve(album.id)
        )
      )
    }
