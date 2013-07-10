package org.nlogo.extensions.gogolite.controller

import jssc.SerialPort

import
  org.nlogo.{ api, extensions },
    api.ExtensionException,
    extensions.gogolite.util.rethrowingJSSCSafely

import Constants.{ OutHeader1, OutHeader2 }

trait CommandWriter {

  self: HasPortsAndStreams =>

  protected def write(commands: Byte*) {
    portOpt map rethrowingJSSCSafely("Unknown error in communicating to GoGo board") {
      (_: SerialPort).writeBytes(Array(OutHeader1, OutHeader2) ++ commands)
    } getOrElse {
      throw new ExtensionException("Cannot write to unopened port")
    }
  }

}
