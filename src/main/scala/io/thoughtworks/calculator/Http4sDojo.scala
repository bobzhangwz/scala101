package io.thoughtworks.calculator

import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder

import scala.concurrent.ExecutionContext.global

object Http4sDojo extends IOApp with Http4sDsl[IO] {

  // Set up a project with docker compose
  // connect to postgres db
  // scala cats http4s cats-effect doobie
  // deploy to aliyun
  // integrate with buildkite or other pipeline or github action
  // has transaction id to log request
  // log into a public log collect
  // expose metrics to watch
  // cirurse breaker and newrelic
  // set up a error-monitor, health check
  // has auth
  // test/ unit test/ integration test / pact test
  // deploy with release tag
  // check dependency when deploy to prod
  // encrypt password
  // auto scaling

  // setup nix ?

  // OptionT[IO, Response[IO]]
  // IO[Reader[Environment, Option[Response[IO]]] ]
  // AppConfig TransactionId HttpClient
  // SQS

  val helloWorldService = HttpRoutes.of[IO] {
    case GET -> Root / "hello" => Ok("hello")
  }

  val speakService = HttpRoutes.of[IO] {
    case GET -> Root / "say" => Ok("hello")
  }

  val service = helloWorldService <+> speakService

  override def run(args: List[String]): IO[ExitCode] = {
    val serverBuilder = BlazeServerBuilder[IO](global)
      .bindHttp(8080, "localhost")
      .withHttpApp(service.orNotFound)

    serverBuilder.resource.use(_ => IO.never)
  }

}
