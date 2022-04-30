package typicode.resolvers

import zio.query.ZQuery

type ZQ[+A] = ZQuery[Any, Throwable, A]
