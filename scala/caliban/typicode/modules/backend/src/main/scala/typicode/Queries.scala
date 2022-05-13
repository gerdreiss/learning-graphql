package typicode

import sttp.client3.httpclient.zio.*
import zio.*
import zio.query.*

import data.*
import resolvers.*
import services.*

case class UserQueryArgs(id: UserId)
case class Queries(user: UserQueryArgs => RQuery[SttpClient & TypicodeService, UserView])

object Queries:
  val user: String =
    """
      |{
      |  user(id: 1) {
      |    name
      |    username
      |    email
      |    phone
      |    website
      |    todos {
      |      title
      |      completed
      |    }
      |    posts {
      |      title
      |      body
      |      comments {
      |        name
      |        email
      |        body
      |      }
      |    }
      |  }
      |}
      |""".stripMargin
