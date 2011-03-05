import sbt._
class AndroidPlugin(info: ProjectInfo) extends PluginProject(info) {
  val proguard = "net.sf.proguard" % "proguard" % "4.4"
  val scalaspec = "org.scala-tools.testing" % "specs" % "1.6.2" % "test"
  val mockito = "org.mockito" % "mockito-core" % "1.8.5" % "test"
}
