package org.nlogo.extensions.gogoserial

package object prim {
  private[prim] def buildMask(xs: Seq[_], maskMap: Map[Char, Int]): Int =
    xs.map(_.toString.toLowerCase.head).distinct.map(maskMap).foldLeft(0){ case (acc, x) => acc | x }
}
