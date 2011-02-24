package android

import sbt._
import Process._
import Path._
import scala.xml._

trait Project extends DefaultProject {

  override def mainSourcePath = "."
  override def mainJavaSourcePath = "src"

  def libraryJarPath = sdk.getAndroidJar

  override def unmanagedClasspath = super.unmanagedClasspath +++ libraryJarPath

  val manifest = new Manifest(_)

  val sdk = new SDK

  lazy val androidManifestPath: Path = {
    "AndroidManifest.xml".get.toList.first
  }

  lazy val mainResPath: Path = {
    "res".get.toList.first
  }

  lazy val generatedRFile = {
    "gen".get.toList.first
  }

  /*
   * Android Asset Packaging Tool, generating the R file
   */
  lazy val aapt = aaptGenerateAction
  def aaptGenerateAction = aaptGenerateTask describedAs ("Generating R file")
  def aaptGenerateTask = execTask {
    <x>{ sdk.aapt.absolutePath } package -m -M { androidManifestPath.absolutePath } -S { mainResPath.absolutePath } -I { sdk.getAndroidJar } -J { generatedRFile.absolutePath }</x>
  } dependsOn directory("gen")

  lazy val aidl = aaptGenerateAction
  lazy val dex = aaptGenerateAction

  def directory(dir: Path) = fileTask(dir :: Nil) {
    FileUtilities.createDirectory(dir, log)
  }

  /**
   * TASKS
   */
  override def compileAction = super.compileAction dependsOn (aapt, aidl)

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