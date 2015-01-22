package org.nlogo.extensions.gogoserial.controller

import CacheManagerMessages._

import actors.{ Actor, TIMEOUT }

object CacheManager {

  private val actor = new CacheManagerActor

  // Tell `actor` to refresh every 50ms
  new Actor {
    start()
    def act() {
      loop {
        reactWithin(50) {
          case TIMEOUT => actor ! Refresh
        }
      }
    }
  }

  def write(str: String) {
    actor ! Write(str)
  }

  def readSensor(sensorNumber: Int): Int = {
    val Sensor(sensorValue) = actor !? Read(sensorNumber)
    sensorValue
  }

  def checkPing(): Boolean = {

    @annotation.tailrec
    def tryAgain(startTime: Long): Boolean = {

      val PingResponse(pingTime) = actor !? PingRequest
      val timedOut               = (System.currentTimeMillis() - startTime) >= 50
      val isFresh                = (pingTime                   - startTime) >= 0

      if (timedOut)
        false
      else
        if (isFresh)
          true
        else
          tryAgain(startTime)

    }

    tryAgain(System.currentTimeMillis())

  }

}

private class CacheManagerActor extends Actor {

  import collection.mutable.{ Map => MMap }
  import Constants._

  private var lastPingTime: Long           = 0
  private var stash:        String         = ""
  private val cache:        MMap[Int, Int] = MMap()

  private val FullOutHeader = """%02X%02X""".format(OutHeader1, OutHeader2)
  private val FullInHeader  = """%02X%02X""".format(InHeader1,  InHeader2)

  private val Dropper        = """.*?(%02X$|%s.*|$)""".       format(OutHeader1, FullOutHeader).r // Sometimes, we have to drop junk until next command
  private val Command        = """%s(.*?)%s(.*)""".           format(FullOutHeader, FullInHeader).r
  private val Ping           = """%s%02X%s(.*)""".            format(FullOutHeader, CmdPing, FullInHeader).r
  private val NormalSensor   = """%s(.{2})%s(.{4})(.*)""".    format(FullOutHeader, FullInHeader).r
  private val ExtendedSensor = """%s%02X(.{4})%s(.{4})(.*)""".format(FullOutHeader, CmdReadExtendedSensor, FullInHeader).r

  start()

  override def act() {
    loop {
      react {
        case Write(str) =>
          stash += str
        case Read(num) =>
          freshenCache()
          reply(Sensor(cache.getOrElse(num, -1)))
        case Refresh =>
          freshenCache()
        case PingRequest =>
          reply(PingResponse(lastPingTime))
      }
    }
  }

  private def freshenCache() {

    def convert(str: String): Int = Integer.valueOf(str, 16) // Reads hex string into `Int`

    @annotation.tailrec
    def accumulateUpdates(str: String, updates: Seq[(Int, Int)] = Seq()): Seq[(Int, Int)] = {
      str match {
        case NormalSensor(sensorNum, data, remaining) if (32 to 60 contains convert(sensorNum)) =>
          val num        = (convert(sensorNum) - CmdReadSensor) / 4
          val newUpdates = updates :+ num -> convert(data)
          accumulateUpdates(remaining, newUpdates)
        case ExtendedSensor(highLow, data, remaining) =>
          val num        = convert(highLow)
          val newUpdates = updates :+ num -> convert(data)
          accumulateUpdates(remaining, newUpdates)
        case Ping(Dropper(remaining)) =>
          lastPingTime = System.currentTimeMillis()
          accumulateUpdates(remaining, updates)
        case Command(cmd, Dropper(remaining)) => // Simply drop commands that aren't sensor readings
          accumulateUpdates(remaining, updates)
        case remaining =>
          stash = remaining
          updates
      }
    }

    val Dropper(retained) = stash.filterNot(_ == ' ')
    cache ++= accumulateUpdates(retained)

  }

}

private object CacheManagerMessages {
  case object PingRequest
  case class  PingResponse(time: Long)
  case object Refresh
  case class  Read  (num:   Int)
  case class  Sensor(value: Int)
  case class  Write (str:   String)
}
