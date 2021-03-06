package org.nlogo.extensions.gogoserial.prim

import
  org.nlogo.{ api, extensions },
    api.{ Argument, Context, Syntax },
    extensions.gogoserial.controller.{ Controller, ControllerManager }

class Sensor(manager: ControllerManager) extends ManagedReporter(manager) {
  override def getSyntax = Syntax.reporterSyntax(Array(Syntax.NumberType), Syntax.NumberType)
  override def managedReport(args: Array[Argument], context: Context, controller: Controller) =
    Double.box(controller.readSensor(args(0).getIntValue))
}
