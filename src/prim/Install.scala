package org.nlogo.extensions.gogoserial.prim

import
  org.nlogo.{ api, extensions },
    api.{ Argument, Context, DefaultCommand, Syntax },
    extensions.gogoserial.installer.WindowsInstaller

class Install extends DefaultCommand {
  override def getSyntax = Syntax.commandSyntax
  override def perform(args: Array[Argument], context: Context) {
    WindowsInstaller(false)
  }
}
