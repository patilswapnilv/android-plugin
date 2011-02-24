package android

import scala.xml._

object Manifest {

  val SCHEMA = "http://schemas.android.com/apk/res/android"
  val MIN_SDK_VERSION_TAG = "minSdkVersion"
  val TARGET_VERSION_TAG = "targetSdkVersion"
  val MAX_SDK_VERSION_TAG = "maxSdkVersion"

  def apply() = new Manifest(XML.load("."))
  def apply(project: android.Project) = new Manifest(XML.load(project.path("AndroidManifest.xml").absolutePath))
}

class Manifest (manifest: Elem) {

  lazy val minSdkVersion: Option[Int] = {
    (manifest \\ "uses-sdk")(0).attribute(Manifest.SCHEMA, Manifest.MIN_SDK_VERSION_TAG).map { _.text.toInt }
  }

  lazy val maxSdkVersion: Option[Int] = {
    (manifest \\ "uses-sdk")(0).attribute(Manifest.SCHEMA, Manifest.MAX_SDK_VERSION_TAG).map { _.text.toInt }
  }

  lazy val targetSdkVersion: Option[Int] = {
    (manifest \\ "uses-sdk")(0).attribute(Manifest.SCHEMA, Manifest.TARGET_VERSION_TAG).map { _.text.toInt }
  }

  /*
   * Gets the target SDK version as we probably work against that one. If not set, check minimum SDK and retrun 1 if none selected.
   */
  lazy val sdkVersion = {
    targetSdkVersion.getOrElse(
      minSdkVersion.getOrElse(1))
  }
  
  lazy val versionCode = {
	  (manifest \\ "manifest") \ "@{%s}versionCode".format(Manifest.SCHEMA) text
  }
}