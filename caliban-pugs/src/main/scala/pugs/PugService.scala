package pugs

import zio.*
import java.net.URL

case class PugNotFound(name: String) extends Throwable

trait PugService:
  def findPug(name: String): IO[PugNotFound, Pug]
  def randomPugPicture: UIO[String]
  def addPug(pug: Pug): UIO[Unit]
  def editPugPicture(name: String, pictureUrl: URL): IO[PugNotFound, Unit]

object PugService:
  def make: PugService =
    new:
      def findPug(name: String): IO[PugNotFound, Pug] =
        IO.succeed(
          Pug(
            "Piggy",
            Nil,
            Some(new URL("https://images.unsplash.com/photo-1523626797181-8c5ae80d40c2")),
            Color.FAWN
          )
        )

      def randomPugPicture: UIO[String] =
        UIO.succeed("https://images.unsplash.com/photo-1523626797181-8c5ae80d40c2")

      def addPug(pug: Pug): UIO[Unit] =
        UIO.unit

      def editPugPicture(name: String, pictureUrl: URL): IO[PugNotFound, Unit] =
        IO.unit
