package org.nlogo.extensions.gogolite.controller

import CacheManagerMessages._

import actors.{ Actor, TIMEOUT }

object CacheManager {

  private val actor = new CacheManagerActor

  // Tell `actor` to refresh every 50ms
  new Actor {
    start()
    val FlushIntervalMs = 50
    def act() {
      loop {
        reactWithin(FlushIntervalMs) {
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

  class CacheManagerActor extends Actor {

    import collection.mutable.{ Map => MMap }
    import Constants._

    private var stash: String         = ""
    private val cache: MMap[Int, Int] = MMap()

    private val Dropper        = """.*?(%02X$|%02X%02X.*|$)""".format(OutHeader1, OutHeader1, OutHeader2).r
    private val Command        = """%02X%02X(.*?)%02X%02X(.*)""".format(OutHeader1, OutHeader2, InHeader1, InHeader2).r
    private val NormalSensor   = """%02X%02X(.{2})%02X%02X(.{4})(.*)""".format(OutHeader1, OutHeader2, InHeader1, InHeader2).r
    private val ExtendedSensor = """%02X%02X%02X(.{4})%02X%02X(.{4})(.*)""".format(OutHeader1, OutHeader2, CmdReadExtendedSensor, InHeader1, InHeader2).r

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
        }
      }
    }

    private def freshenCache() {

      def convert(str: String): Int = Integer.valueOf(str, 16)

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
           case Command(cmd, remaining) => // Simply drop commands that aren't sensor readings
             val Dropper(retained) = remaining
             accumulateUpdates(retained, updates)
           case remaining =>
             stash = remaining
             updates
        }
      }

      val Dropper(retained) = stash.filterNot(_ == ' ')
      val updates = accumulateUpdates(retained)
      updates foreach { case (num, value) => cache += num -> value }

    }

  }

}

object CacheManagerMessages {
  case object Refresh
  case class  Read  (num: Int)
  case class  Sensor(value: Int)
  case class  Write (str: String)
}
