package typicode
package resolvers

import sttp.client3.httpclient.zio.*

import zio.*
import zio.query.*

import data.*
import services.*

object UserSchema:

  case class TodoView(title: String, completed: Boolean)
  case class PostView(
      title: String,
      body: String,
      comments: RQuery[SttpClient & TypicodeService, List[CommentView]]
  )
  case class CommentView(name: String, email: String, body: String)
  case class AlbumView(
      title: String,
      photos: RQuery[SttpClient & TypicodeService, List[PhotoView]]
  )
  case class PhotoView(title: String, url: String, thumbnailUrl: String)
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

  case class QueryArgs(id: UserId)
  case class Queries(user: QueryArgs => RQuery[SttpClient & TypicodeService, UserView])

  def resolver: Queries =
    case class GetTodos(userId: UserId) extends Request[Throwable, Todos]
    val TodosDataSource: DataSource[SttpClient & TypicodeService, GetTodos]   =
      DataSource.fromFunctionZIO("TodosDataSource") { request =>
        TypicodeService.getTodos(request.userId)
      }
    def getTodos(userId: UserId): RQuery[SttpClient & TypicodeService, Todos] =
      ZQuery.fromRequest(GetTodos(userId))(TodosDataSource)

    case class GetPosts(userId: UserId) extends Request[Throwable, Posts]
    val PostsDataSource: DataSource[SttpClient & TypicodeService, GetPosts]   =
      DataSource.fromFunctionZIO("PostsDataSource") { request =>
        TypicodeService.getPosts(request.userId)
      }
    def getPosts(userId: UserId): RQuery[SttpClient & TypicodeService, Posts] =
      ZQuery.fromRequest(GetPosts(userId))(PostsDataSource)

    case class GetComments(postId: PostId) extends Request[Throwable, Comments]
    val CommentsDataSource: DataSource[SttpClient & TypicodeService, GetComments]   =
      DataSource.fromFunctionZIO("CommentsDataSource") { request =>
        TypicodeService.getComments(request.postId)
      }
    def getComments(postId: PostId): RQuery[SttpClient & TypicodeService, Comments] =
      ZQuery.fromRequest(GetComments(postId))(CommentsDataSource)

    case class GetAlbums(userId: UserId) extends Request[Throwable, Albums]
    val AlbumsDataSource: DataSource[SttpClient & TypicodeService, GetAlbums]   =
      DataSource.fromFunctionZIO("AlbumsDataSource") { request =>
        TypicodeService.getAlbums(request.userId)
      }
    def getAlbums(userId: UserId): RQuery[SttpClient & TypicodeService, Albums] =
      ZQuery.fromRequest(GetAlbums(userId))(AlbumsDataSource)

    case class GetPhotos(albumId: AlbumId) extends Request[Throwable, Photos]
    val PhotosDataSource: DataSource[SttpClient & TypicodeService, GetPhotos]     =
      DataSource.fromFunctionZIO("PhotosDataSource") { request =>
        TypicodeService.getPhotos(request.albumId)
      }
    def getPhotos(albumId: AlbumId): RQuery[SttpClient & TypicodeService, Photos] =
      ZQuery.fromRequest(GetPhotos(albumId))(PhotosDataSource)

    def getUser(userId: UserId): RQuery[SttpClient & TypicodeService, UserView] =
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
            getTodos(user.id).map {
              _.data.map { todo =>
                TodoView(todo.title, todo.completed)
              }
            },
            getPosts(user.id).map {
              _.data.map { post =>
                PostView(
                  post.title,
                  post.body,
                  getComments(post.id).map {
                    _.data.map { comment =>
                      CommentView(comment.name, comment.email, comment.body)
                    }
                  }
                )
              }
            },
            getAlbums(user.id).map {
              _.data.map(album =>
                AlbumView(
                  album.title,
                  getPhotos(album.id).map {
                    _.data.map { photo =>
                      PhotoView(photo.title, photo.url, photo.thumbnailUrl)
                    }
                  }
                )
              )
            }
          )
        }

    Queries(user => getUser(user.id))
