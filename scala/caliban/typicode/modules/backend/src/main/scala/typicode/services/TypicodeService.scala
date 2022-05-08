package typicode
package services

import sttp.client3.*
import sttp.client3.httpclient.zio.*

import zio.json.JsonDecoder

import data.*
import sttp.model.*
import zio.*

trait TypicodeService:
  def getUser(userId: UserId): Task[User]
  def getTodos(userId: UserId): Task[Todos]

case class TypicodeServiceLive() extends TypicodeService:

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

  private def getObject[T <: TypicodeData](uri: Uri)(using D: JsonDecoder[T]): ZIO[SttpClient, Throwable, T] =
    send(createRequest[T](uri))
      .flatMap { response =>
        response.code match
          case StatusCode.Ok =>
            response.body match
              case Left(error) => ZIO.fail(new Exception(error))
              case Right(body) => ZIO.succeed(body)
          case _             =>
            ZIO.fail(new Exception(s"Unexpected response code: ${response.code}"))
      }

  def getUser(userId: UserId): Task[User] =
    getObject[User](getUserURI(userId)).provide(HttpClientZioBackend.layer())

  def getTodos(userId: UserId): Task[Todos] =
    getObject[Todos](getUserTodosURI(userId)).provide(HttpClientZioBackend.layer())

object TypicodeService:
  def live: UIO[TypicodeService] = ZIO.succeed(TypicodeServiceLive())
