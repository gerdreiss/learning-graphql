package typicode
package data

case class Photo(
    albumId: AlbumId,
    id: PhotoId,
    title: String,
    url: String,
    thumbnailUrl: String
)

case class Album(
    userId: UserId,
    id: AlbumId,
    title: String
)
