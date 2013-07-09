package org.nlogo.extensions.gogolite.controller

import jssc.SerialPortException

import org.nlogo.api.ExtensionException

import Constants.{ OutHeader1, OutHeader2 }

trait CommandWriter {

  self: HasPortsAndStreams =>

  protected def write(commands: Byte*) {
    portOpt map {
      port =>
        try port.writeBytes(Array(OutHeader1, OutHeader2) ++ commands)
        catch {
          case e: SerialPortException => throw new ExtensionException("Unknown error in communicating to GoGo board", e)
        }
    } getOrElse (throw new ExtensionException("Cannot write to unopened port"))
  }

}
