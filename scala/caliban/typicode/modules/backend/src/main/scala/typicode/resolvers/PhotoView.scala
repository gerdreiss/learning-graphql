package typicode
package resolvers

import sttp.client3.httpclient.zio.*

import zio.query.*

import data.*
import services.*

case class PhotoView(
    title: String,
    url: String,
    thumbnailUrl: String
)

object PhotoView:
  case class GetPhotos(albumId: AlbumId) extends Request[Throwable, Photos]

  val ds: DataSource[SttpClient & TypicodeService, GetPhotos] =
    DataSource.fromFunctionZIO("PhotosDataSource") { request =>
      TypicodeService.getPhotos(request.albumId)
    }

  def resolve(albumId: AlbumId): RQuery[SttpClient & TypicodeService, List[PhotoView]] =
    ZQuery.fromRequest(GetPhotos(albumId))(ds).map {
      _.data.map { photo =>
        PhotoView(photo.title, photo.url, photo.thumbnailUrl)
      }
    }
