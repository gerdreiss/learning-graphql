package typicode

import zio.*

trait TypicodeService:
  def getUser(userId: UserId): ZIO[Any, Throwable, User]
  def getTodos(userId: UserId): ZIO[Any, Throwable, List[Todo]]

object TypicodeService:
  def live: ZIO[Any, Nothing, TypicodeService] =
    ZIO.succeed {
      new:
        def getUser(userId: UserId): ZIO[Any, Throwable, User]        =
          ZIO.succeed {
            User(
              id = 1,
              name = "John Doe",
              username = "doe",
              email = "john.doe@mail.com",
              address = Address(
                street = "Main St",
                suite = "Apt 1",
                city = "New York",
                zipcode = "10001",
                geo = Geo(
                  lat = 40.7128,
                  lng = -74.0060
                )
              ),
              company = Company(
                name = "Company",
                catchPhrase = "Company catch phrase",
                bs = "Company bs"
              ),
              phone = "1234567890",
              website = "company.com"
            )
          }
        def getTodos(userId: UserId): ZIO[Any, Throwable, List[Todo]] =
          ZIO.succeed {
            List(
              Todo(userId, 1, "Todo 1", false),
              Todo(userId, 2, "Todo 2", false),
              Todo(userId, 3, "Todo 3", false)
            )
          }
    }
