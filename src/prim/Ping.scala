package org.nlogo.extensions.gogoserial.prim

import
  org.nlogo.{ api, extensions, nvm },
    api.{ Argument, Context, ExtensionException, Syntax },
    extensions.gogoserial.controller.{ Controller, ControllerManager },
    nvm.{ ExtensionContext, Workspace }

class Ping(manager: ControllerManager) extends ManagedReporter(manager) {
  override def getSyntax = Syntax.reporterSyntax(Syntax.BooleanType)
  override def managedReport(args: Array[Argument], context: Context, controller: Controller) = {

    context match {
      case c: ExtensionContext =>
        val result = {
          try controller.ping()
          catch {
            case ex: ExtensionException =>
              c.workspace.outputObject("Failed to ping GoGo board: " + ex.getMessage, c.getAgent, true, true, Workspace.OutputDestination.NORMAL)
              false
          }
        }
        Boolean.box(result)
      case _ =>
        throw new ExtensionException("Somehow, this extension isn't running in an `ExtensionContext`")
    }

  }
}
