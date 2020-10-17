package com.gimdh.project.common.rpc

import org.apache.logging.log4j.scala.Logging

import scala.concurrent.{ExecutionContext, Future, future}
import akka.NotUsed
import akka.actor.typed.ActorSystem
import akka.http.scaladsl.model.AttributeKeys
import akka.http.scaladsl.server.Directives.extractClientIP
import com.gimdh.project.common.actors.SampleCounterActor

import scala.collection.mutable.ListBuffer

class ProjectServiceImpl(system: ActorSystem[_]) extends ProjectService with Logging {
  private implicit val sys: ActorSystem[_] = system
  private implicit val ec: ExecutionContext = system.executionContext

  def sendSampleMean(in: SampleMean): Future[ACK] = {
    logger.info(s"Master received: From ${in.ip}, Mean: ${in.sampleMean}.")
    Future.successful(ACK())
  }
}



