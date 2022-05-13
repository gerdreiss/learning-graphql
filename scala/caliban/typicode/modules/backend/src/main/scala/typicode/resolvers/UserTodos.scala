package typicode
package resolvers

import sttp.client3.httpclient.zio.*

import zio.*
import zio.query.*

import data.*
import services.*

object UserTodos:

  case class QueryArgs(id: UserId)
  case class Query(user: QueryArgs => RQuery[SttpClient & TypicodeService, UserView])

  case class TodoView(title: String, completed: Boolean)
  case class UserView(
      name: String,
      username: String,
      email: String,
      phone: String,
      website: String,
      address: Address,
      company: Company,
      todos: ZQuery[SttpClient & TypicodeService, Throwable, List[TodoView]]
  )

  def resolver: Query =
    case class GetTodos(userId: UserId) extends Request[Throwable, Todos]
    val TodosDataSource: DataSource[SttpClient & TypicodeService, GetTodos]   =
      DataSource.fromFunctionZIO("TodosDataSource") { request =>
        TypicodeService.getTodos(request.userId)
      }
    def getTodos(userId: UserId): RQuery[SttpClient & TypicodeService, Todos] =
      ZQuery.fromRequest(GetTodos(userId))(TodosDataSource)

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
            getTodos(user.id).map(_.data.map(todo => TodoView(todo.title, todo.completed)))
          )
        }

    Query(user => getUser(user.id))
