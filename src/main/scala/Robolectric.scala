import sbt._

import java.nio.charset.Charset._
import scala.xml.Node

import java.io.File

trait Robolectric extends Eclipse { this: AndroidProjectOld =>

  object Robolectric {
    val DefaultTestFolder = "tests"
    val DefaultAIDLFolder = "resources" / "aidl"
    val TempFolder = "tests" / "tmp"
    val version = "0.9.4"
    val robolectricUrl = "http://pivotal.github.com/robolectric/downloads/robolectric-%s-all.jar" format version
  }

  val junitInterface = "com.novocode" % "junit-interface" % "0.5" % "test"
  val robolectric = "com.xtremelabs" % "robolectric" % Robolectric.version % "test" from Robolectric.robolectricUrl
  val javaassit = "javassist" % "javassist" % "3.8.0.GA" % "test"
  val junit = "junit" % "junit" % "4.8.2" % "test"

  def googleMapLocation: String;
  val googlemap = Path.fromFile(googleMapLocation)

  override def unmanagedClasspath = super.unmanagedClasspath +++ googlemap
  override def includeTest(s: String) = { s.endsWith("Spec") || s.endsWith("Test") }

  override def testSourcePath = "tests"
  override def testJavaSourcePath = testSourcePath / "java"
  override def testResourcesPath = "tests" / "resources"

  override def testFrameworks = super.testFrameworks ++ List(new TestFramework("com.novocode.junit.JUnitFrameworkNoMarker"))

  // start eclipse
  lazy val roboEclipse = task {
    log.info("generating eclipse file for robolectric project...")
    FileUtilities.touch(projectFile, log) match {
      case Some(error) =>
        Some("Unable to write project file " + projectFile + ": " + error)
      case None =>
        FileUtilities.write(projectFile, projectFileXML toString, forName("UTF-8"), log)
    }
    log.info("generating robolectric eclipse classpath file...")
    FileUtilities.touch(classpathFile, log) match {
      case Some(error) =>
        Some("Unable to write classpath file " + projectFile + ": " + error)
      case None =>
        FileUtilities.write(classpathFile, classPathFileXML toString, forName("UTF-8"), log)
    }
    None
  }
//
//  override lazy val projectFile: File = testSourcePath / ".project" asFile
//
//  override lazy val classpathFile: File = testSourcePath / ".classpath" asFile

  override def eclipseAction = super.eclipseAction dependsOn (roboEclipse)

//  override def getLibraries = {
//    (dependencyPath * "*.jar" get).map { classpathEntry(_) }
//  }
//
//  override def getLibrarySources(project: Project) = {
//    (List[Node]() /: dependencies)((l, v) => linkedResourcesXML(v.asInstanceOf[AndroidProject]) :: l)
//  }
}