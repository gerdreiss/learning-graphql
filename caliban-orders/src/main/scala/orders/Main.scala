package orders

import zio.*
import caliban.CalibanError.ValidationError
import caliban.GraphQL.graphQL
import caliban.RootResolver

object Main extends ZIOAppDefault:

  val test1: IO[ValidationError, Int] =
    for
      dbService   <- DBService.make
      resolver     = api1.resolver(dbService)
      api          = graphQL(RootResolver(resolver))
      interpreter <- api.interpreter
      _           <- interpreter.execute(Query.orders)
      dbHits      <- dbService.hits
      _           <- IO.debug(s"Naive Approach - DB Hits: $dbHits")
    yield dbHits

  val test2: IO[ValidationError, Int] =
    for
      dbService   <- DBService.make
      resolver     = api2.resolver(dbService)
      api          = graphQL(RootResolver(resolver))
      interpreter <- api.interpreter
      _           <- interpreter.execute(Query.orders)
      dbHits      <- dbService.hits
      _           <- IO.debug(s"Nested Effects - DB Hits: $dbHits")
    yield dbHits

  val test3: IO[ValidationError, Int] =
    for
      dbService   <- DBService.make
      resolver     = api3.resolver(dbService)
      api          = graphQL(RootResolver(resolver))
      interpreter <- api.interpreter
      _           <- interpreter.execute(Query.orders)
      dbHits      <- dbService.hits
      _           <- IO.debug(s"ZQuery - DB Hits: $dbHits")
    yield dbHits

  val test4: IO[ValidationError, Int] =
    for
      dbService   <- DBService.make
      resolver     = api4.resolver(dbService)
      api          = graphQL(RootResolver(resolver))
      interpreter <- api.interpreter
      _           <- interpreter.execute(Query.orders)
      dbHits      <- dbService.hits
      _           <- IO.debug(s"ZQuery with Batch - DB Hits: $dbHits")
    yield dbHits

  override def run = test1 *> test2 *> test3 *> test4
