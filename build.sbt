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

artifactName := { (_, _, _) => "gogo-lite.jar" }

packageOptions +=
  Package.ManifestAttributes(
    ("Extension-Name", "gogo-lite"),
    ("Class-Manager", "org.nlogo.extensions.gogolite.GoGoLiteExtension"),
    ("NetLogo-Extension-API-Version", "5.0"))

packageBin in Compile <<= (packageBin in Compile, dependencyClasspath in Runtime, baseDirectory, streams) map {
  (jar, classpath, base, s) =>
    IO.copyFile(jar, base / "gogo-lite.jar")
    val libraryJarPaths =
      classpath.files.filter{path =>
        path.getName.endsWith(".jar") &&
        path.getName != "scala-library.jar" &&
        !path.getName.startsWith("NetLogo")}
    for(path <- libraryJarPaths)
      IO.copyFile(path, base / path.getName)
    if(Process("git diff --quiet --exit-code HEAD").! == 0) {
      Process("git archive -o gogo-lite.zip --prefix=gogo-lite/ HEAD").!!
      IO.createDirectory(base / "gogo-lite")
      val zipExtras =
        (libraryJarPaths.map(_.getName) :+ "gogo-lite.jar")
      for(extra <- zipExtras)
        IO.copyFile(base / extra, base / "gogo-lite" / extra)
      Process("zip -r gogo-lite.zip " + zipExtras.map("gogo-lite/" + _).mkString(" ")).!!
      IO.delete(base / "gogo-lite")
    }
    else {
      s.log.warn("working tree not clean; no zip archive made")
      IO.delete(base / "gogo-lite.zip")
    }
    jar
  }

cleanFiles <++= baseDirectory { base =>
  Seq(base / "gogo-lite.jar",
      base / "gogo-lite.zip") }
