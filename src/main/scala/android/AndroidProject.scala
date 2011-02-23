package android

import sbt._
import Process._
import Path._
import scala.xml._

trait Project extends DefaultProject {
  
  override def mainSourcePath = "."
  override def mainJavaSourcePath = "src"

  def libraryJarPath = androidJarPath +++ addonsJarPath
  override def unmanagedClasspath = super.unmanagedClasspath +++ libraryJarPath

  
  lazy val androidDefinedVersion = {
	  (XML.load("AndroidManifest.xml") \\ "uses-sdk") \ "@{http://schemas.android.com/apk/res/android}minSdkVersion"
  }
  
  lazy val androidManifestPath: Path = {
    "AndroidManifest.xml".get.toList.first
  }

  lazy val mainResPath: Path = {
    "res".get.toList.first
  }

  lazy val aaptPath: Path = {
    androidSdkPath / "tools" / "aapt"
  }

  lazy val androidSdkPath = {
    val envs = List("ANDROID_SDK_HOME", "ANDROID_SDK_ROOT", "ANDROID_HOME", "ANDROID_SDK")
    val paths = for { e ← envs; p = System.getenv(e); if p != null } yield p
    if (paths.isEmpty) error("You need to set " + envs.mkString(" or "))
    Path.fromFile(paths.first)
  }

  lazy val generatedRFile = {
    "gen".get.toList.first
  }

  lazy val androidJarPath = {
    androidSdkPath / "platforms" / "android-9" / "android.jar"
  }

  /*
   * Android Asset Packaging Tool, generating the R file
   */
  lazy val aapt = aaptGenerateAction
  def aaptGenerateAction = aaptGenerateTask describedAs ("Generating R file")
  def aaptGenerateTask = execTask {
    <x>{ aaptPath.absolutePath } package -m -M { androidManifestPath.absolutePath } -S { mainResPath.absolutePath } -I { androidJarPath.absolutePath } -J { generatedRFile.absolutePath }</x>
  } dependsOn directory("gen")

  lazy val aidl = aaptGenerateAction
  lazy val dex = aaptGenerateAction

  def directory(dir: Path) = fileTask(dir :: Nil) {
    FileUtilities.createDirectory(dir, log)
  }

  lazy val compileTask = task {
    "ls" ! log
    None
  } dependsOn (aapt, aidl)

  lazy val dexTask = task {
    None
  } dependsOn (compile)

  lazy val packageTask = task {
    None
  } dependsOn (dex)

  lazy val packageReleaseTask = task {
    None
  } dependsOn (packageTask)

}

trait LibraryProject extends android.Project {
}