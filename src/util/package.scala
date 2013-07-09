package org.nlogo.extensions.gogolite

import jssc.SerialPortList

package object util {
  def fetchPorts(): Seq[String] = SerialPortList.getPortNames()
}
