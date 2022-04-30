package typicode
package data

import zio.json.DeriveJsonDecoder
import zio.json.JsonDecoder

case class Photo(
    albumId: AlbumId,
    id: PhotoId,
    title: String,
    url: String,
    thumbnailUrl: String
) extends TypicodeData

object Photo:
  given JsonDecoder[Photo] = DeriveJsonDecoder.gen[Photo]

case class Album(
    userId: UserId,
    id: AlbumId,
    title: String
) extends TypicodeData

object Album:
  given JsonDecoder[Album] = DeriveJsonDecoder.gen[Album]
