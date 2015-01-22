package org.nlogo.extensions.gogoserial

import collection.mutable.{ LinkedHashMap => LinkedMap }

import jssc.{ SerialPortException, SerialPortList }

import org.nlogo.api.ExtensionException

package object util {

  def fetchPorts(): Seq[String] = SerialPortList.getPortNames()

  def rethrowingJSSCSafely[T](msg: String)(block: => T): T = rethrowingSafely(classOf[SerialPortException] -> msg)(block)

  def rethrowingSafely[T, U <: Throwable](classMsgPairs: (Class[U], String)*)(block: => T): T = {

    val map = LinkedMap(classMsgPairs: _*) // Use a linked map, since the matching order of cases is important

    val pf = new PartialFunction[Throwable, Nothing] {
      override def apply      (x: Throwable) = throw new ExtensionException(map.find(_._1 isAssignableFrom x.getClass).get._2, x.asInstanceOf[Exception])
      override def isDefinedAt(x: Throwable) = map.keys exists (_ isAssignableFrom x.getClass)
    }

    try
      block
    catch
      pf

  }

}
