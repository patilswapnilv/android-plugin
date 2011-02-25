package android

import sbt._
import Process._
import Path._
import scala.xml._

trait Project extends DefaultProject {

  override def mainSourcePath = "."
  override def mainJavaSourcePath = "src"
  override def unmanagedClasspath = super.unmanagedClasspath +++ libraryJarPath

  def libraryJarPath = sdk.getAndroidJar

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

  lazy val mainAssetsPath = {
    "assets".get.toList.first
  }

  /*
   * Android Asset Packaging Tool, generating the R file
   */
  lazy val aapt = aaptGenerateAction
  def aaptGenerateAction = aaptGenerateTask describedAs ("Generating R file")
  def aaptGenerateTask = execTask {
    log.info("generating the R file")
    <x>{ sdk.aapt.absolutePath } package -m -M { androidManifestPath.absolutePath } -S { mainResPath.absolutePath } -I { sdk.getAndroidJar } -J { generatedRFile.absolutePath }</x>
  } dependsOn directory("gen")

  lazy val aidl = aaptGenerateAction

  def directory(dir: Path) = fileTask(dir :: Nil) {
    FileUtilities.createDirectory(dir, log)
  }

  class DoubleEntrySource extends MainCompileConfig {
    override def sources = descendents(mainJavaSourcePath, "*.java") +++ descendents("gen", "R.java")
  }

  override def mainCompileConfiguration = new DoubleEntrySource

  /**
   * TASKS
   */
  override def compileAction = super.compileAction dependsOn (aapt, aidl)

  /**
   * DEX
   */
  def DefaultClassesMinJarName = "myandroidappproject_2.8.1-1.0.4.jar"
  def DefaultClassesDexName = "classes.dex".get.toList.first

  lazy val dex = dexAction
  def dexAction = dexTask describedAs ("Converting .class to dex files")
  def dexTask = execTask {
    <x> { sdk.dex.absolutePath } { "-JXmx512m" } --dex --output={ (outputDirectoryName / "classes.dex").absolutePath } { mainCompilePath.absolutePath }</x>
  } dependsOn (compile)

  /**
   * Package into APK
   */
  override def packageAction = super.packageAction dependsOn (aaptPackageAction)
  def aaptPackageAction = packageTask dependsOn (dex) describedAs ("Package resources and assets.")
  def packageTask = execTask {
    log.info("packaging into APK file")
    <x>{ sdk.aapt.absolutePath } package -f -M { androidManifestPath.absolutePath } -S { mainResPath.absolutePath } -A { mainAssetsPath.absolutePath } -I { sdk.getAndroidJar } -F { (mainCompilePath / "test.apk").absolutePath }</x>
  } dependsOn directory(mainAssetsPath)

  lazy val packageReleaseTask = task {
    None
  } dependsOn (packageTask)

}

trait LibraryProject extends android.Project {
}

object AndroidProject {
  val AaptDescription = "Generate resource file for the given project."
  val DexDescription = "Convert .class file to classes.dex."
  val AaptPackageDescription = "Package the project into deployable APK."
  val SignForReleaseDescription = "Signs the APK in order to release to market."
  val SignForDebugDescription = "Signs the APK with debug key located in ~/.android/debug.keystore."
}

trait AndroidProjectPaths {
  import AndroidProjectPaths._
}

object AndroidProjectPaths {
  val DefaultSourceDirectoryName = "src"
  val DefaultMainCompileDirectoryName = "classes"
  val DefaultTestCompileDirectoryName = "test-classes"
  val DefaultDocDirectoryName = "doc"
  val DefaultAPIDirectoryName = "api"
  val DefaultGraphDirectoryName = "graph"
  val DefaultMainAnalysisDirectoryName = "analysis"
  val DefaultTestAnalysisDirectoryName = "test-analysis"

  val DefaultMainDirectoryName = "main"
  val DefaultScalaDirectoryName = "scala"
  val DefaultResourcesDirectoryName = "resources"
  val DefaultTestDirectoryName = "test"

  // forwarders to new locations
  def BootDirectoryName = Project.BootDirectoryName
  def DefaultManagedDirectoryName = BasicDependencyPaths.DefaultManagedDirectoryName
  def DefaultDependencyDirectoryName = BasicDependencyPaths.DefaultDependencyDirectoryName
}