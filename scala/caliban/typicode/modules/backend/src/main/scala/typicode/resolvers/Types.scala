package typicode
package resolvers

type ZQ[+A] = zio.query.ZQuery[Any, Throwable, A]
