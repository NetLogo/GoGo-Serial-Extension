package org.nlogo.extensions.gogolite.controller

import Constants.{ CmdReadExtendedSensor, CmdReadSensor, SensorReadNormal }

import org.nlogo.api.ExtensionException

trait SensorReader {

  self: CommandWriter =>

  def readSensor(sensor: Int): Int = readSensorHelper(sensor, SensorReadNormal)

  private def readSensorHelper(sensor: Int, mode: Int): Int = {

    if (sensor < 1)
      throw new ExtensionException("Sensor number out of range: " + sensor)
    else {

      val (arr, sensorNum) =
        if (sensor > 8) {
          val highByte = ((sensor - 9) >> 8).toByte
          val lowByte  = ((sensor - 9) & 0xFF).toByte
          val num: Int = Integer.valueOf("%02X%02X".format(highByte, lowByte), 16)
          (Array(CmdReadExtendedSensor, highByte, lowByte), num)
        }
        else
          (Array((CmdReadSensor | ((sensor - 1) << 2) | mode).toByte), sensor - 1)

      write(arr: _*)
      Thread.sleep(10)
      CacheManager.readSensor(sensorNum)

    }

  }

}
