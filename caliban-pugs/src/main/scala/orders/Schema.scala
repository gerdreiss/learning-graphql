package orders

import zio.*
import zio.query.*

/** The naive schema results in many unnecessary calls to the DB (101 DB Hits) */
object naive:
  case class QueryArgs(count: Int)
  case class Query(orders: QueryArgs => UIO[List[OrderView]])

  case class OrderView(id: OrderId, customer: Customer, products: List[ProductOrderView])
  case class ProductOrderView(id: ProductId, details: ProductDetailsView, quantity: Int)
  case class ProductDetailsView(name: String, description: String, brand: Brand)

/** Using ZIO types reduces the number of calls to the DB (61 DB Hits) */
object better:
  case class QueryArgs(count: Int)
  case class Query(orders: QueryArgs => UIO[List[OrderView]])

  case class OrderView(id: OrderId, customer: UIO[Customer], products: List[ProductOrderView])
  case class ProductOrderView(id: ProductId, details: UIO[ProductDetailsView], quantity: Int)
  case class ProductDetailsView(name: String, description: String, brand: UIO[Brand])

/** 9 DB Hits! */
object zq:
  type ZQ[+A] = ZQuery[Any, Nothing, A]

  case class QueryArgs(count: Int)
  case class Query(orders: QueryArgs => ZQ[List[OrderView]])

  case class OrderView(id: OrderId, customer: ZQ[Customer], products: List[ProductOrderView])
  case class ProductOrderView(id: ProductId, details: ZQ[ProductDetailsView], quantity: Int)
  case class ProductDetailsView(name: String, description: String, brand: ZQ[Brand])

  case class GetCustomer(id: CustomerId) extends Request[Nothing, Customer]
  val CustomerDataSource: DataSource[Any, GetCustomer] =
    DataSource.fromFunctionZIO("CustomerDataSource")(req =>
      ZIO.succeed(Customer(req.id, "Max", "Muster"))
    )
  def getCustomer(id: CustomerId): ZQ[Customer]        =
    ZQuery.fromRequest(GetCustomer(id))(CustomerDataSource)

/** 3 DB Hits!!! */
object zqbatch:
  type ZQ[+A] = ZQuery[Any, Nothing, A]

  case class QueryArgs(count: Int)
  case class Query(orders: QueryArgs => ZQ[List[OrderView]])

  case class OrderView(id: OrderId, customer: ZQ[Customer], products: List[ProductOrderView])
  case class ProductOrderView(id: ProductId, details: ZQ[ProductDetailsView], quantity: Int)
  case class ProductDetailsView(name: String, description: String, brand: ZQ[Brand])

  case class GetCustomer(id: CustomerId) extends Request[Nothing, Customer]
  val CustomerDataSource: DataSource[Any, GetCustomer] =
    DataSource.fromFunctionBatchedZIO("CustomerDataSource")(requests =>
      ZIO.succeed(requests.map(req => Customer(req.id, "Max", "Muster")))
    )
  def getCustomer(id: CustomerId): ZQ[Customer]        =
    ZQuery.fromRequest(GetCustomer(id))(CustomerDataSource)
