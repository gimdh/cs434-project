package com.gimdh.project.master

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl._
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.ConnectionContext
import akka.http.scaladsl.Http
import akka.http.scaladsl.HttpsConnectionContext
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.model._
import akka.pki.pem.DERPrivateKeyLoader
import akka.pki.pem.PEMDecoder
import com.gimdh.project.common.rpc.{ProjectServiceHandler, ProjectServiceImpl}
import com.typesafe.config.ConfigFactory
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import org.apache.logging.log4j.scala.Logging

import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.util.Failure
import scala.util.Success
import scala.concurrent.duration._


object ProjectServer {
  def apply(): ProjectServer = {
    val conf = ConfigFactory.parseString("akka.http.server.preview.enable-http2 = on")
      .withFallback(ConfigFactory.defaultApplication())
    val system = ActorSystem[Nothing](Behaviors.empty, "SamplerServer", conf)
    new ProjectServer(system)
  }
}

class ProjectServer(system: ActorSystem[_]) extends Logging {
  def run(): Future[Http.ServerBinding] = {
    implicit val sys = system
    implicit val ec = sys.executionContext

    val service: HttpRequest => Future[HttpResponse] = {
      /* Use ServiceHandler.concatOrNotFound() for multiple services */
      ProjectServiceHandler(new ProjectServiceImpl(system))
    }

    val bound: Future[Http.ServerBinding] = Http.get(system)
      .newServerAt(interface = "0.0.0.0", port = 18180)
      .enableHttps(serverHttpContext)
      .bind(service)
      .map(_.addToCoordinatedShutdown(hardTerminationDeadline = 10.seconds))


    bound.onComplete {
      case Success(binding) =>
        val address = binding.localAddress
        logger.info(s"gRPC project server bound to ${address.getAddress}:${address.getPort}")
      case Failure(ex) =>
        logger.error("Failed to bind gRPC endpoint. Terminating system.", ex)
        sys.terminate()
    }

    bound
  }
}
