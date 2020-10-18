package com.gimdh.project.master


import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import com.gimdh.project.common.rpc.{ProjectServiceHandler, ProjectServiceImpl}
import com.typesafe.config.ConfigFactory
import org.apache.logging.log4j.scala.Logging

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._
import scala.util.{Failure, Success}


object ProjectServer {
  def apply(): ProjectServer = {
    val conf = ConfigFactory.parseString("akka.http.server.preview.enable-http2 = on")
      .withFallback(ConfigFactory.defaultApplication())
    val system = ActorSystem[Nothing](Behaviors.empty, "ProjectServer", conf)
    new ProjectServer(system)
  }
}

class ProjectServer(system: ActorSystem[_]) extends Logging {
  def run(): Future[Http.ServerBinding] = {
    implicit val sys = system
    implicit val ec: ExecutionContext = system.executionContext

    val service: HttpRequest => Future[HttpResponse] = {
      /* Use ServiceHandler.concatOrNotFound() for multiple services */
      ProjectServiceHandler(new ProjectServiceImpl(system))
    }

    val bound: Future[Http.ServerBinding] = Http(system)
      .newServerAt(interface = "127.0.0.1", port = 18182)
      .bind(service)
      .map(_.addToCoordinatedShutdown(hardTerminationDeadline = 10.seconds))

    bound.onComplete {
      case Success(binding) =>
        val address = binding.localAddress
        logger.info(s"gRPC project server bound to ${address}")
      case Failure(ex) =>
        logger.error("Failed to bind gRPC endpoint. Terminating system.", ex)
        sys.terminate()
    }

    bound
  }
}
