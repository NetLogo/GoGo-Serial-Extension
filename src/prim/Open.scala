package org.nlogo.extensions.gogoserial.prim

import
  org.nlogo.{ api, extensions },
    api.{ Argument, Context, DefaultCommand, Syntax },
    extensions.gogoserial.controller.ControllerManager

class Open(manager: ControllerManager) extends DefaultCommand {
  override def getSyntax = Syntax.commandSyntax(Array(Syntax.StringType))
  override def perform(args: Array[Argument], context: Context) {
    manager.close()
    manager.init(args(0).getString)
    manager.getControllerOpt foreach {
      controller =>
        controller.openPort()
        controller.ping()
    }
  }
}
