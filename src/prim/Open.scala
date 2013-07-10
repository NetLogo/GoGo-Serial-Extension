package org.nlogo.extensions.gogolite.prim

import
  org.nlogo.{ api, extensions },
    api.{ Argument, Context, DefaultCommand, Syntax },
    extensions.gogolite.controller.ControllerManager

class Open(manager: ControllerManager) extends DefaultCommand {
  override def getSyntax = Syntax.commandSyntax(Array(Syntax.StringType))
  override def perform(args: Array[Argument], context: Context) {
    manager.close()
    manager.init(args(0).getString)
    manager.withController {
      controller =>
        controller.openPort()
        controller.ping()
    }
  }
}
