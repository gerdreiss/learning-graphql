package typicode

import zio.*
import zio.query.*

type ZQ[+A] = ZQuery[Any, Throwable, A]

object todos:

  case class UserTodoListQueryArgs(userId: Int)
  case class TodoView(title: String, completed: Boolean)
  case class UserTodoListView(username: String, todoList: ZQ[List[TodoView]])
  case class UserTodoListQuery(userId: UserTodoListQueryArgs => ZQ[UserTodoListView])

  def resolver(typicodeService: TypicodeService): UserTodoListQuery =

    case class GetUser(userId: UserId) extends Request[Throwable, User]
    val UserDataSource: DataSource[Any, GetUser] =
      DataSource.fromFunctionZIO("UserDataSource") { request =>
        typicodeService.getUser(request.userId)
      }
    def getUser(userId: UserId): ZQ[User]        =
      ZQuery.fromRequest(GetUser(userId))(UserDataSource)

    case class GetTodos(userId: UserId) extends Request[Throwable, List[Todo]]
    val TodosDataSource: DataSource[Any, GetTodos] =
      DataSource.fromFunctionZIO("TodosDataSource") { request =>
        typicodeService.getTodos(request.userId)
      }
    def getTodos(userId: UserId): ZQ[List[Todo]]   =
      ZQuery.fromRequest(GetTodos(userId))(TodosDataSource)

    def getTodoViews(userId: UserId): ZQ[List[TodoView]] =
      getTodos(userId)
        .map(_.map(todo => TodoView(todo.title, todo.completed)))

    def getUserTodosView(userId: UserId): ZQ[UserTodoListView] =
      getUser(userId)
        .map(user => UserTodoListView(user.username, getTodoViews(user.id)))

    UserTodoListQuery(args => getUserTodosView(args.userId))
