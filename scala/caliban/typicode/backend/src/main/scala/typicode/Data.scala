package typicode

type UserId    = Int
type TodoId    = Int
type AlbumId   = Int
type PhotoId   = Int
type PostId    = Int
type CommentId = Int

case class Geo(
    lat: Double,
    lng: Double
)

case class Address(
    street: String,
    suite: String,
    city: String,
    zipcode: String,
    geo: Geo
)

case class Company(
    name: String,
    catchPhrase: String,
    bs: String
)

case class User(
    id: UserId,
    name: String,
    username: String,
    email: String,
    address: Address,
    company: Company,
    phone: String,
    website: String
)

case class Todo(
    userId: UserId,
    id: TodoId,
    title: String,
    completed: Boolean
)

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
