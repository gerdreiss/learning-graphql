package typicode
package resolvers

import sttp.client3.httpclient.zio.*

import zio.query.*

import data.*
import services.*

case class TodoView(
    title: String,
    completed: Boolean
)

object TodoView:
  case class GetTodos(userId: UserId) extends Request[Throwable, Todos]

  val ds: DataSource[SttpClient & TypicodeService, GetTodos] =
    DataSource.fromFunctionZIO("TodosDataSource") { request =>
      TypicodeService.getTodos(request.userId)
    }

  def resolve(userId: UserId): RQuery[SttpClient & TypicodeService, List[TodoView]] =
    ZQuery.fromRequest(GetTodos(userId))(ds).map {
      _.data.map { todo =>
        TodoView(todo.title, todo.completed)
      }
    }
