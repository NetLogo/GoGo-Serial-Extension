scalaVersion := "2.9.2"

scalaSource in Compile <<= baseDirectory(_ / "src")

javaSource in Compile <<= baseDirectory(_ / "src")

scalacOptions ++= Seq("-deprecation", "-unchecked", "-Xlint", "-Xfatal-warnings",
                      "-encoding", "us-ascii")

javacOptions ++= Seq("-g", "-deprecation", "-Xlint:all", "-Xlint:-serial", "-Xlint:-path",
                     "-encoding", "us-ascii",
                     "-source", "1.5", "-target", "1.5")

libraryDependencies ++= Seq(
  "org.nlogo" % "NetLogo" % "5.0.4" from
    "http://ccl.northwestern.edu/netlogo/5.0.4/NetLogo.jar",
  "jssc" % "jssc" % "2.6.0" from
    "http://ccl.northwestern.edu/devel/jssc-2.6.0.jar"
)

name := "gogo-lite"

NetLogoExtension.settings

NetLogoExtension.classManager := "org.nlogo.extensions.gogolite.GoGoLiteExtension"
