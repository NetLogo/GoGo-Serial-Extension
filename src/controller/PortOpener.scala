package org.nlogo.extensions.gogolite.controller

import
  jssc.{ SerialPort, SerialPortEventListener },
    SerialPort._

import org.nlogo.extensions.gogolite.util.rethrowingJSSCSafely

private[controller] trait PortOpener {

  self: HasPortsAndStreams with SerialPortEventListener =>

  def openPort() {
    portOpt = portOpt orElse Option(initializePort())
  }

  private def initializePort(): SerialPort = {
    rethrowingJSSCSafely("Unable to open port: " + portName) {
      val port = new SerialPort(portName)
      port.openPort()
      port.setParams(BAUDRATE_9600, DATABITS_8, STOPBITS_1, PARITY_NONE)
      port.purgePort(PURGE_RXCLEAR | PURGE_TXCLEAR)
      port.setEventsMask(MASK_RXCHAR)
      port.addEventListener(this)
      port
    }
  }

}
