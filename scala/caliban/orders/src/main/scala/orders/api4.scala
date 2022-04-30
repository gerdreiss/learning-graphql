package orders

import zio.*
import zio.query.*

/** 3 DB Hits!!! */
object api4:

  type ZQ[+A] = ZQuery[Any, Nothing, A]

  case class QueryArgs(count: Int)
  case class Query(orders: QueryArgs => ZQ[List[OrderView]])

  case class OrderView(id: OrderId, customer: ZQ[Customer], products: List[ProductOrderView])
  case class ProductOrderView(id: ProductId, details: ZQ[ProductDetailsView], quantity: Int)
  case class ProductDetailsView(name: String, description: String, brand: ZQ[Brand])

  def resolver(dbService: DBService): Query =

    case class GetCustomer(id: CustomerId) extends Request[Nothing, Customer]
    val CustomerDataSource: DataSource[Any, GetCustomer] =
      DataSource.fromFunctionBatchedZIO("CustomerDataSource")(requests =>
        dbService.getCustomers(requests.map(_.id).toList).map(Chunk.fromIterable)
      )
    def getCustomer(id: CustomerId): ZQ[Customer]        =
      ZQuery.fromRequest(GetCustomer(id))(CustomerDataSource)

    case class GetProduct(id: ProductId) extends Request[Nothing, Product]
    val ProductDataSource: DataSource[Any, GetProduct] =
      DataSource.fromFunctionBatchedZIO("ProductDataSource")(requests =>
        dbService.getProducts(requests.map(_.id).toList).map(Chunk.fromIterable)
      )
    def getProduct(id: ProductId): ZQ[Product]         =
      ZQuery.fromRequest(GetProduct(id))(ProductDataSource)

    case class GetBrand(id: BrandId) extends Request[Nothing, Brand]
    val BrandDataSource: DataSource[Any, GetBrand] =
      DataSource.fromFunctionBatchedZIO("BrandDataSource")(requests =>
        dbService.getBrands(requests.map(_.id).toList).map(Chunk.fromIterable)
      )
    def getBrand(id: BrandId): ZQ[Brand]           =
      ZQuery.fromRequest(GetBrand(id))(BrandDataSource)

    def getOrders(count: Int): ZQ[List[OrderView]] =
      ZQuery
        .fromZIO(dbService.getLastOrders(count))
        .map(_.map { order =>
          OrderView(order.id, getCustomer(order.customerId), getProducts(order.products))
        })

    def getProducts(products: List[(ProductId, Int)]): List[ProductOrderView] =
      products
        .map { case (productId, quantity) =>
          ProductOrderView(
            productId,
            getProduct(productId).map { product =>
              ProductDetailsView(product.name, product.description, getBrand(product.brandId))
            },
            quantity
          )
        }

    Query(args => getOrders(args.count))
