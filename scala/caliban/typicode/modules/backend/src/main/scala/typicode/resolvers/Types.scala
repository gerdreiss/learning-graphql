package typicode
package resolvers

import sttp.client3.httpclient.zio.*

import zio.query.*

import services.*

type ZQ[A] = RQuery[SttpClient & TypicodeService, A]
type DS[A] = DataSource[SttpClient & TypicodeService, A]
