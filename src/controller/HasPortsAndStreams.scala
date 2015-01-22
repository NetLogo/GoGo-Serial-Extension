package org.nlogo.extensions.gogoserial.controller

import jssc.SerialPort

private[controller] trait HasPortsAndStreams {
  protected val portName: String
  protected var portOpt: Option[SerialPort] = None
}
