package orders

import zio.*
import zio.query.*

/** The naive schema results in many unnecessary calls to the DB (101 DB Hits) */
object api1:
  case class QueryArgs(count: Int)
  case class Query(orders: QueryArgs => UIO[List[OrderView]])

  case class OrderView(id: OrderId, customer: Customer, products: List[ProductOrderView])
  case class ProductOrderView(id: ProductId, details: ProductDetailsView, quantity: Int)
  case class ProductDetailsView(name: String, description: String, brand: Brand)

  def resolver(dbService: DBService): Query =
    def getOrders(count: Int): UIO[List[OrderView]] =
      dbService
        .getLastOrders(count)
        .flatMap { orders =>
          UIO.foreach(orders) { order =>
            for
              customer <- dbService.getCustomer(order.customerId)
              products <- getProducts(order.products)
            yield OrderView(order.id, customer, products)
          }
        }

    def getProducts(products: List[(ProductId, Int)]): UIO[List[ProductOrderView]] =
      UIO.foreach(products) { case (productId, quantity) =>
        dbService
          .getProduct(productId)
          .flatMap { product =>
            dbService
              .getBrand(product.brandId)
              .map(brand => ProductDetailsView(product.name, product.description, brand))
          }
          .map(details => ProductOrderView(productId, details, quantity))
      }

    Query(args => getOrders(args.count))
