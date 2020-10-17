package com.gimdh.project.common.rpc

import org.apache.logging.log4j.scala.Logging

import scala.concurrent.{Future, future}
import akka.NotUsed
import akka.actor.typed.ActorSystem

class ProjectServiceImpl(system: ActorSystem[_]) extends ProjectService with Logging {
  private implicit val sys: ActorSystem[_] = system

  def sendSampleMean(in: SampleMean): Future[ACK] = {
    logger.info(s"Master received: Samplemean elements.")
    Future.successful(ACK())
  }
}



