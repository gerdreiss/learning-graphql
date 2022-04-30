package typicode
package resolvers

import zio.*
import zio.query.*

import data.*
import services.*

object Todos:

  case class QueryArgs(id: UserId)
  case class Query(user: QueryArgs => ZQ[UserView])

  case class TodoView(title: String, completed: Boolean)
  case class UserView(
      name: String,
      username: String,
      email: String,
      phone: String,
      website: String,
      address: Address,
      company: Company,
      todos: ZQ[List[TodoView]]
  )

  def resolver(typicodeService: TypicodeService): Query =

    case class GetTodos(userId: UserId) extends Request[Nothing, List[Todo]]
    val TodosDataSource: DataSource[Any, GetTodos] =
      DataSource.fromFunctionZIO("TodosDataSource") { request =>
        typicodeService.getTodos(request.userId)
      }
    def getTodos(userId: UserId): ZQ[List[Todo]]   =
      ZQuery.fromRequest(GetTodos(userId))(TodosDataSource)

    def getUser(userId: UserId): ZQ[UserView] =
      ZQuery
        .fromZIO(typicodeService.getUser(userId))
        .map { user =>
          UserView(
            user.name,
            user.username,
            user.email,
            user.phone,
            user.website,
            user.address,
            user.company,
            getTodos(user.id).map(_.map(todo => TodoView(todo.title, todo.completed)))
          )
        }

    Query(user => getUser(user.id))
