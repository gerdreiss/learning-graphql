package typicode
package services

import sttp.client3.*
import sttp.client3.httpclient.zio.*
import sttp.model.*

import zio.*
import zio.json.JsonDecoder

import data.*

trait TypicodeService:
  def getUser(userId: UserId): RIO[SttpClient, User]
  def getTodos(userId: UserId): RIO[SttpClient, Todos]

object TypicodeService:
  def getUser(userId: UserId)  = ZIO.serviceWithZIO[TypicodeService](_.getUser(userId))
  def getTodos(userId: UserId) = ZIO.serviceWithZIO[TypicodeService](_.getTodos(userId))

  def live: ULayer[TypicodeService] = ZLayer.succeed {
    new:

      private val commonHeaders = Map("Content-Type" -> "application/json")

      private val baseUrl = "https://jsonplaceholder.typicode.com"

      private def getUserURI(userId: UserId): Uri      = uri"$baseUrl/users/$userId"
      private def getUserTodosURI(userId: UserId): Uri = uri"$baseUrl/users/$userId/todos"

      private def createRequest[T <: TypicodeData](uri: Uri, lastModified: Option[String] = None)(using
          D: JsonDecoder[T]
      ): Request[Either[String, T], Any] =
        val headers = lastModified.map("If-Modified-Since" -> _).foldLeft(commonHeaders)(_ + _)
        basicRequest
          .get(uri)
          .headers(headers)
          .mapResponse(_.flatMap(D.decodeJson))

      private def getObject[T <: TypicodeData](uri: Uri)(using D: JsonDecoder[T]): RIO[SttpClient, T] =
        send(createRequest[T](uri))
          .flatMap { response =>
            response.code match
              case StatusCode.Ok =>
                response.body match
                  case Left(error) => ZIO.fail(new Exception(error))
                  case Right(body) => ZIO.succeed(body)
              case code          =>
                ZIO.fail(new Exception(s"Unexpected response code: $code"))
          }

      def getUser(userId: UserId): ZIO[SttpClient, Throwable, User] =
        getObject[User](getUserURI(userId))

      def getTodos(userId: UserId): ZIO[SttpClient, Throwable, Todos] =
        getObject[Todos](getUserTodosURI(userId))
  }
