package com.gimdh.project.common.actors

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import com.gimdh.project.common.rpc.SampleMean

import scala.collection.mutable.ListBuffer


object SampleCounterActor {
  def apply(slaveCount: Int): Behavior[SampleMean] =
    Behaviors.setup(context => new SampleCounterActor(context, slaveCount))
}

class SampleCounterActor(context: ActorContext[SampleMean], slaveCount: Int)
  extends AbstractBehavior[SampleMean](context) {

  var samples = ListBuffer[SampleMean]()

  override def onMessage(sampleMean: SampleMean): Behavior[SampleMean] = {
    samples.append(sampleMean)

    if (samples.size == slaveCount) {
      Behaviors.empty
    }
    else {
      SampleCounterActor(slaveCount + 1)
    }
  }
}