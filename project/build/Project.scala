import sbt._

class AndroidPlugin(info: ProjectInfo) extends PluginProject(info) 
{
	val scalatest = "org.scalatest" % "scalatest" % "1.0.1" % "test"
	val mockito = "org.mockito" % "mockito-all" % "1.8.1" % "test"
	val proguard = "net.sf.proguard" % "proguard" % "4.4"
}
