import sbt._

class AndroidPlugin(info: ProjectInfo) extends PluginProject(info) 
{
	val proguard = "net.sf.proguard" % "proguard" % "4.4"
}
