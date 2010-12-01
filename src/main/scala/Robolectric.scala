import sbt._

trait Robolectric extends AndroidProject {
  
  object Robolectric {
    val DefaultTestFolder = "tests" / "local"
    val DefaultAIDLFolder = ""
    val version = "0.9.2"
    val robolectricUrl = "http://pivotal.github.com/robolectric/downloads/robolectric-%s-all.jar" format version
  }
    
  val junitInterface = "com.novocode" % "junit-interface" % "0.5" % "test"
  val robolectric = "com.xtremelabs" % "robolectric" % "0.9.2" % "test" from "http://pivotal.github.com/robolectric/downloads/robolectric-0.9.2.jar"
  val javaassit = "javassist" % "javassist" %"3.8.0.GA" % "test"
  val mockito = "org.mockito" % "mockito-all" % "1.8.5" % "test"
  val junit = "junit" % "junit" % "4.8.2" % "test"
    
  def googleMapLocation:String;
  val googlemap =  Path.fromFile(googleMapLocation)
  
  override def unmanagedClasspath = super.unmanagedClasspath +++ googlemap
  override def includeTest(s: String) = { s.endsWith("Spec") || s.endsWith("Test")}
  override def testJavaSourcePath = "tests" / "java"
  override def testResourcesPath = "tests" / "resources"
  override def testFrameworks = super.testFrameworks ++ List(new TestFramework("com.novocode.junit.JUnitFrameworkNoMarker"))
}