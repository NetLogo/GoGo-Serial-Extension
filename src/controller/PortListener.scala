package org.nlogo.extensions.gogoserial.controller

import jssc.{ SerialPortEvent, SerialPortEventListener }

import org.nlogo.api.ExtensionException

trait PortListener {

  self: HasPortsAndStreams with SerialPortEventListener =>

  override def serialEvent(event: SerialPortEvent) {

    val str = portOpt map {
      port => Option(port.readHexString()) getOrElse ""
    } getOrElse {
      throw new ExtensionException("Cannot read from closed port")
    }

    CacheManager.write(str)

  }

}
