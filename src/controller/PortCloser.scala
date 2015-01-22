package org.nlogo.extensions.gogoserial.controller

import scala.util.control.Exception.ignoring

import jssc.SerialPortException

private[controller] trait PortCloser {

  self: HasPortsAndStreams =>

  def closePort() {
    portOpt foreach {
      port =>
        ignoring(classOf[SerialPortException]) {
          port.removeEventListener()
        }
        port.closePort()
        portOpt = None
    }
  }

}
