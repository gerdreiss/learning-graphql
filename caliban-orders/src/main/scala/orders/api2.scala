package orders

import zio.*
import zio.query.*

/** Using ZIO types reduces the number of calls to the DB (61 DB Hits) */
object api2:
  case class QueryArgs(count: Int)
  case class Query(orders: QueryArgs => UIO[List[OrderView]])

  case class OrderView(id: OrderId, customer: UIO[Customer], products: List[ProductOrderView])
  case class ProductOrderView(id: ProductId, details: UIO[ProductDetailsView], quantity: Int)
  case class ProductDetailsView(name: String, description: String, brand: UIO[Brand])

  def resolver(dbService: DBService): Query =

    def getOrders(count: Int): UIO[List[OrderView]] =
      dbService
        .getLastOrders(count)
        .map(_.map { order =>
          OrderView(order.id, dbService.getCustomer(order.customerId), getProducts(order.products))
        })

    def getProducts(products: List[(ProductId, Int)]): List[ProductOrderView] =
      products
        .map { case (productId, quantity) =>
          ProductOrderView(
            productId,
            getProduct(productId),
            quantity
          )
        }

    def getProduct(productId: ProductId): UIO[ProductDetailsView] =
      dbService
        .getProduct(productId)
        .map { product =>
          ProductDetailsView(product.name, product.description, dbService.getBrand(product.brandId))
        }

    Query(args => getOrders(args.count))
