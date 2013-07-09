package org.nlogo.extensions.gogolite.controller

import jssc.{ SerialPort, SerialPortEventListener }

import Constants._

import org.nlogo.api.ExtensionException

class Controller(override protected val portName: String)
  extends HasPortsAndStreams
  with SerialPortEventListener
  with PortCloser
  with PortOpener
  with OutputPortController
  with CommandWriter
  with PortListener
  with SensorReader {

  def currentPortName = portOpt map (_.getPortName) getOrElse "INVALID"

  def currentPort: Option[SerialPort] = portOpt

  def beep() {
    write(CmdBeep, 0x00)
  }

  def ping() {
    write(CmdPing)
  }

  def led(on: Boolean) {
    write(if (on) CmdLedOn else CmdLedOff, 0x00)
  }

  def talkToOutputPorts(outputPortMask: Int) {
    write(CmdTalkToOutputPort, outputPortMask.toByte)
  }

  def setOutputPortPower(level: Int) {
    if ((level < 0) || (level > 7)) throw new ExtensionException("Power level out of range: " + level)
    write((CmdOutputPortPower | level << 2).toByte)
  }

  def setServoPosition(value: Int) {
    if ((value < 20) || (value > 40)) throw new ExtensionException("Requested servo position (%s) is out of safe range (20-40)".format(value))
    write(CmdPwmServo, value.toByte)
  }

}
