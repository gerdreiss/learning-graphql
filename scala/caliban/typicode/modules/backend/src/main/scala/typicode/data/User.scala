package typicode
package data

case class Geo(
    lat: Double,
    lng: Double
)

case class Address(
    street: String,
    suite: String,
    city: String,
    zipcode: String,
    geo: Geo
)

case class Company(
    name: String,
    catchPhrase: String,
    bs: String
)

case class User(
    id: UserId,
    name: String,
    username: String,
    email: String,
    address: Address,
    company: Company,
    phone: String,
    website: String
)
