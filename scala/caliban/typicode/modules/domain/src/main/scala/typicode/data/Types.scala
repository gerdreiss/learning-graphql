package typicode
package data

import zio.json.DeriveJsonDecoder
import zio.json.JsonDecoder

type UserId    = Int
type TodoId    = Int
type AlbumId   = Int
type PhotoId   = Int
type PostId    = Int
type CommentId = Int

trait TypicodeData
