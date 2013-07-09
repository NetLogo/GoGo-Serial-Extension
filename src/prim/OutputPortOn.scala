package org.nlogo.extensions.gogolite.prim

import
  org.nlogo.{ api, extensions },
    api.{ Argument, Context, Syntax },
    extensions.gogolite.controller.{ Controller, ControllerManager }

class OutputPortOn(manager: ControllerManager) extends ManagedCommand(manager) {
  override def getSyntax = Syntax.commandSyntax
  override def managedPerform(args: Array[Argument], context: Context, controller: Controller) {
    controller.outputPortOn()
  }
}
