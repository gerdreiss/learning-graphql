package pugs

import java.net.URL

enum Color:
  case FAWN, BLACK, OTHER

case class Pug(
    name: String,
    nicknames: List[String],
    pictureUrl: Option[URL],
    color: Color
)
