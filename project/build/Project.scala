import sbt._
class AndroidPlugin(info: ProjectInfo) extends PluginProject(info) {
  val proguard = "net.sf.proguard" % "proguard" % "4.4"
  val scalaspec = "org.scala-tools.testing" % "specs" % "1.6.2.2" % "test"
  val mockito = "org.mockito" % "mockito-core" % "1.8.5" % "test"
  val easymock = "org.easymock" % "easymock" % "2.5.1" % "test"
  val easymockext = "org.easymock" % "easymockclassextension" % "2.4" % "test"
  override def testOptions = super.testOptions ++ Seq(TestArgument(TestFrameworks.Specs, "-v"))
}
