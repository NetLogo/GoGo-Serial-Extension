package org.nlogo.extensions.gogo.prim

import
  org.nlogo.{ api, extensions },
    api.{ Argument, Context, Syntax },
    extensions.gogo.controller.{ Controller, ControllerManager }

class GoGoOutputPortThatWay(manager: ControllerManager) extends ManagedCommand(manager) {
  override def getSyntax = Syntax.commandSyntax
  override def managedPerform(args: Array[Argument], context: Context, controller: Controller) {
    controller.outputPortThatWay()
  }
}
