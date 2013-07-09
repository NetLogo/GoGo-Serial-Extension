package org.nlogo.extensions.gogolite.prim

import
  org.nlogo.{ api, extensions },
    api.{ Argument, Context, DefaultCommand, Syntax },
    extensions.gogolite.controller.ControllerManager

class Open(manager: ControllerManager) extends DefaultCommand {
  override def getSyntax = Syntax.commandSyntax(Array(Syntax.StringType))
  override def perform(args: Array[Argument], context: Context) {

    import manager.{ getControllerOpt => controllerOpt }

    manager.close()

    try {
      manager.init(args(0).getString)
      controllerOpt foreach (_.openPort())
    }
    catch {
      case e: NoClassDefFoundError => throw new EE("Could not initialize GoGo Extension.  Full error message: %s : %s".format(args(0).getString, e.getLocalizedMessage))
      case e: Exception            => throw new EE("Could not open port %s : %s".format(args(0).getString, e.getLocalizedMessage))
    }

    controllerOpt map (_.ping())

  }
}
