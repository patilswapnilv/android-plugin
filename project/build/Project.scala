import sbt._

class AndroidPlugin(info: ProjectInfo) extends PluginProject(info) {
  val proguard = "net.sf.proguard" % "proguard" % "4.4"
  val eclipse = "de.element34" % "sbt-eclipsify" % "0.7.0"
}
