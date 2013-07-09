package org.nlogo.extensions.gogolite.prim

import
  org.nlogo.{ api, extensions },
    api.{ Argument, Context, DefaultCommand, Syntax },
    extensions.gogolite.installer.WindowsInstaller

class Install extends DefaultCommand {
  override def getSyntax = Syntax.commandSyntax
  override def perform(args: Array[Argument], context: Context) {
    WindowsInstaller(false)
  }
}
