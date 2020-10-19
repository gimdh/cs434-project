package com.gimdh.project.slave

import java.net.InetAddress

import akka.actor.Status.Failure
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.grpc.GrpcClientSettings
import akka.http.scaladsl.model.StatusCodes.Success
import com.gimdh.project.common.rpc.{ACK, ProjectServiceClient, SampleMean}
import org.apache.logging.log4j.scala.Logging

import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.runtime.universe.Try
import scala.sys.exit

object Main extends Logging {
  val usage = """Usage: slave [master_ip_address:master_port_number]"""

  def main(args: Array[String]): Unit = {
    implicit val sys: ActorSystem[_] = ActorSystem(Behaviors.empty, "ProjectClient")
    implicit val ec: ExecutionContext = sys.executionContext

    if (args.size != 1) {
      invalidArgument
    }

    logger.info("Argument format seems fine.")

    val ipRegex = "(.+):(.+)".r
    val ipRegex(ipAddress, portNumber) = args(0)

    logger.info(s"Master is ${ipAddress}:${portNumber.toInt}")

    val clientSettings = GrpcClientSettings.connectToServiceAt("127.0.0.1", 18182).withTls(false)
    logger.info("Created clientSettings")

    val client = ProjectServiceClient(clientSettings)

    logger.info("Created ProjectServiceClient")

    val reply = client.sendSampleMean(SampleMean(ip = InetAddress.getLocalHost.getHostAddress, sampleMean = 100))
    reply.onComplete{
      case util.Success(i) => logger.info(s"Successfully sent sampling information and received ACK: ${i}")
      case util.Failure(e) => logger.info(s"Failed to sent sampling information:${e.getMessage}")
    }
  }

  def invalidArgument(): Unit = {
    logger.error("Invalid argument has been given")
    println(usage)
    exit(-1)
  }
}
