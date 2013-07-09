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
          case TIMEOUT  => actor ! Refresh
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

    private val Dropper = """.*?(%02X$|%02X%02X.*)""".format(OutHeader1, OutHeader1, OutHeader2).r
    private val Reading = """%02X%02X(.*?)%02X%02X(.*?)(%02X%02X.*)""".format(OutHeader1, OutHeader2, InHeader1, InHeader2, OutHeader1, OutHeader2).r

    private val NormalSensor   = """(.{2})""".r
    private val ExtendedSensor = """%02X(.{4})""".format(CmdReadExtendedSensor).r

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
           case Reading(cmd, data, remaining) =>
             val updateOpt = cmd match {
               case NormalSensor(inner) if (32 to 60 contains convert(inner)) =>
                 Option((convert(inner) - CmdReadSensor) / 4)
               case ExtendedSensor(highLow) =>
                 Option(convert(highLow))
               case x =>
                 None
             }
             val newUpdates = updateOpt map (_ -> convert(data)) map (updates :+ _) getOrElse updates
             accumulateUpdates(remaining, newUpdates)
           case _ =>
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
