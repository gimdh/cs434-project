package com.gimdh.project.master

import org.apache.logging.log4j.scala.Logging

import scala.sys.exit

object Main extends Logging {
  val usage = "Usage: master [number_of_slaves]"

  def main(args: Array[String]): Unit = {
    logger.info("Starting master application.")

    if (args.size != 1) {
      logger.error("Invalid argument has been given")
      println(usage)
      exit(-1)
    }
    val slaveCount = args(0).toInt
    logger.info(s"${slaveCount} slaves are about to attach.")

    ProjectServer().run()
  }
}
