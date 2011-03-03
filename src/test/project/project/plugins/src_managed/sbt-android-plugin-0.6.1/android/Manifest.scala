package android

import scala.xml._
import sbt._

object Manifest {

  val SCHEMA = "http://schemas.android.com/apk/res/android"
  val MIN_SDK_VERSION_TAG = "minSdkVersion"
  val TARGET_VERSION_TAG = "targetSdkVersion"
  val MAX_SDK_VERSION_TAG = "maxSdkVersion"

  def apply() = new Manifest(XML.load("AndroidManifest.xml"))
  def apply(project: AndroidProject) = new Manifest(XML.load(project.manifestPath.absolutePath))
  def apply(manifest: Elem) = new Manifest(manifest)
}

class Manifest(manifest: Elem) {

  val minSdkVersion: Option[Int] = getSDKVersion(Manifest.MIN_SDK_VERSION_TAG)
  val maxSdkVersion: Option[Int] = getSDKVersion(Manifest.MAX_SDK_VERSION_TAG)
  val targetSdkVersion: Option[Int] = getSDKVersion(Manifest.TARGET_VERSION_TAG)

  private[Manifest] def getSDKVersion(key: String): Option[Int] = if ((manifest \\ "uses-sdk").isEmpty) None else (manifest \\ "uses-sdk")(0).attribute(Manifest.SCHEMA, key).map { _.text.toInt }

  /*
   * Gets the target SDK version as we probably work against that one. If not set, check minimum SDK and return 1 if none selected.
   */
  lazy val sdkVersion = targetSdkVersion.getOrElse(minSdkVersion.getOrElse(1))

  lazy val versionCode = ((manifest \\ "manifest") \ "@{%s}versionCode".format(Manifest.SCHEMA) text).toInt
  lazy val versionName = (manifest \\ "manifest") \ "@{%s}versionName".format(Manifest.SCHEMA) text

  def versionnedName(): String = versionnedName("", defaultNammingStrategy)
  def versionnedName(name: String): String = versionnedName(name, defaultNammingStrategy)

  def versionnedName(name: String, namingStrategy: (String, Int, String) ⇒ String): String = namingStrategy(name, versionCode, versionName)
  def defaultNammingStrategy(name: String, versionCode: Int, versionName: String): String = name + "_" + versionName + "_v" + versionCode
}