package typicode

import caliban.schema.Annotations.GQLDescription
import zio.query.*

import data.*
import resolvers.*

case class UserQueryArgs(id: UserId)
case class Queries(
    @GQLDescription("Return user data")
    user: UserQueryArgs => ZQ[UserView]
)

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
