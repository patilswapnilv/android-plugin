package android

import sbt._
import Process._
import Path._
import scala.xml._

//case class AndroidProject(info: ProjectInfo) extends DefaultProject(info) with android.Project
//case class LibraryProject(info: ProjectInfo) extends DefaultProject(info) with android.Library
//case class InstrumentationProject(info: ProjectInfo) extends DefaultProject(info) with android.Library

trait Project extends DefaultProject {
  import AndroidProjectPaths._

  override def mainSourcePath = "."
  override def mainJavaSourcePath = "src"
  override def unmanagedClasspath = super.unmanagedClasspath +++ libraryJarPath

  def libraryJarPath = sdk.androidJar

  val manifest = Manifest(this.asInstanceOf[android.Project])
  val sdk = SDK(manifest.sdkVersion)

  lazy val apk:Path = mainCompilePath / manifest.versionnedName(this.name) + ".apk"
  lazy val res:Path = mainResPath
  lazy val dexD:Path = DefaultMainCompileDirectoryName / "classes.dex"

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

  lazy val packageApkPath = apk
  lazy val resourcesApkPath = mainResPath
  lazy val classesDexPath = DefaultMainCompileDirectoryName / "classes.dex"

  /**
   * Gets assets folder for each of the plugin on top of the current project
   */
  lazy val assetDirectories: PathFinder = {
    (mainAssetsPath.asInstanceOf[sbt.PathFinder] /: this.dependencies) { (pf, project) ⇒
      if (project.isInstanceOf[android.Library]) pf +++ project.asInstanceOf[android.Project].assetDirectories else pf
    }
  }

  /**
   * Gets resource folder for each of the plugin on top of the current project
   */
  lazy val resDirectories: PathFinder = {
    (mainResPath.asInstanceOf[sbt.PathFinder] /: this.dependencies) { (pf, project) ⇒
      if (project.isInstanceOf[android.Library]) pf +++ project.asInstanceOf[android.Project].resDirectories else pf
    }
  }

  /*
   * Android Asset Packaging Tool, generating the R file
   */
  lazy val aapt = aaptGenerateAction
  def aaptGenerateAction = aaptGenerateTask describedAs ("Generating R file")
  def aaptGenerateTask = execTask {
    log.info("generating the R file")
    <x>{ sdk.aapt.absolutePath } package -m -M { androidManifestPath.absolutePath } -S { mainResPath.absolutePath } -I { sdk.androidJar } -J { generatedRFile.absolutePath }</x>
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
    <x> { sdk.dex.absolutePath } { "-JXmx512m" } --dex --output={ classesDexPath.absolutePath } { mainCompilePath.absolutePath }</x>
  } dependsOn (compile)

  /**
   * Package into APK
   */
  override def packageAction = super.packageAction dependsOn (packageIntoApkAction)

  def aaptPackageAction = aaptPackageTask dependsOn (dex) describedAs ("Package resources and assets.")
  def aaptPackageTask = execTask {
    log.info("packaging into APK file")
    log.debug("the following asset folders will be included: %s".format(assetDirectories.getPaths.mkString(",")))
    <x>{ sdk.aapt.absolutePath } package --auto-add-overlay -f -M { androidManifestPath.absolutePath } -S { resDirectories.getPaths.mkString(" -S ") } -A { assetDirectories.getPaths.mkString(" -A ") } -I { sdk.androidJar } -F { apk.absolutePath + ".apk" }</x>
  } dependsOn directory(mainAssetsPath)
  //
  //  lazy val packageReleaseTask = task {
  //    None
  //  } dependsOn (packageTask)
  //
  //  lazy val packageDebug = packageDebugAction
  //  def packageDebugAction = packageTask(true) dependsOn (packageAction) describedAs ("Package and sign with a debug key.")
  //
  //  lazy val packageRelease = packageReleaseAction
  //  def packageReleaseAction = packageTask(false) dependsOn (packageAction) describedAs ("Package without signing.")
  //
  //  

  lazy val cleanApk = cleanTask(packageApkPath) describedAs ("Remove apk package")

  def packageIntoApkAction = packageApkTask(false) dependsOn (aaptPackageAction)

  def packageApkTask(signPackage: Boolean) = execTask {
    <x>{ sdk.apkBuilder.absolutePath } { packageApkPath.absolutePath } { if (signPackage) "-d" else "-u" } -z { resourcesApkPath.absolutePath } -f { classesDexPath.absolutePath } </x>
  } dependsOn (cleanApk)
}

trait Library extends android.Project {

  /**
   * Use the android.jar from Parent.
   */
  //  override def libraryJarPath = info.parent.get match {
  //	  case android.Project => _.libraryJarPath
  //	  case android.Library => _.libraryJarPath
  //  }
  def doNothing = task { None }
  override def packageAction = doNothing
  override def publishLocalAction = doNothing
  override def deliverLocalAction = doNothing
  override def publishAction = doNothing
  override def deliverAction = doNothing
}

object AndroidProject {
  val AaptDescription = "Generate resource file for the given project."
  val DexDescription = "Convert .class file to classes.dex."
  val AaptPackageDescription = "Package the project into deployable APK."
  val SignForReleaseDescription = "Signs the APK in order to release to market."
  val SignForDebugDescription = "Signs the APK with debug key located in ~/.android/debug.keystore."
}

/**
 * Default Paths for
 */
object AndroidProjectPaths {
  import sbt.Path._

  val DefaultSourceDirectoryName = "src"
  val DefaultMainCompileDirectoryName = "bin"
  val DefaultResourcesDirectoryName = "res"
  val DefaultGeneratedDirectoryName = "gen"
  val DefaultAssetsDirectoryName = "assets"

  val DefaultTestDirectoryName = "tests"
  //  val DefaultLocalTestDirectoryName = DefaultTestDirectoryName / "local"
  //  val DefaultInstrumentationTestDirectoryName = DefaultTestDirectoryName / "instrumentation"
  //  val DefaultMonkeyTestDirectoryName = DefaultTestDirectoryName / "monkey"
}