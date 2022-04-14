package orders

import zio.*

case class QueryArgs(count: Int)
case class Query(orders: QueryArgs => UIO[List[OrderView]])

case class OrderView(id: OrderId, customer: UIO[Customer], products: List[ProductOrderView])
case class ProductOrderView(id: ProductId, details: UIO[ProductDetailsView], quantity: Int)
case class ProductDetailsView(name: String, description: String, brand: UIO[Brand])
