package org.nlogo.extensions.gogolite.prim

import
  org.nlogo.{ api, extensions },
    api.{ Argument, Context, DefaultReporter, LogoList, Syntax },
    extensions.gogolite.util.{ fetchPorts, rethrowingSafely }

class ListPorts extends DefaultReporter {
  override def getSyntax = Syntax.reporterSyntax(Syntax.ListType)
  override def report(args: Array[Argument], context: Context) = {
    rethrowingSafely(classOf[NoClassDefFoundError] -> "Could not initialize GoGo Extension.") {
      LogoList.fromIterator(fetchPorts().toIterator)
    }
  }
}
