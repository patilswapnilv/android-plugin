package android
import sbt._

class SDK(sdkHome: Path, apiLevel: Int) {

  import SDK._

  lazy val androidJar = sdkHome / "platforms" / apiLevelToName(apiLevel.toString) / "android.jar"
  lazy val aapt: Path = sdkHome / "platform-tools" / "aapt"
  lazy val dex = sdkHome / "platform-tools" / "dx"
  lazy val apkBuilder = sdkHome / "platform-tools" / "apkbuilder"
  lazy val sdkLib = sdkHome / "tools" / "lib" / "sdklib.jar"
}

object SDK {

  def platformName2ApiLevel(platformName: String): Int = {
    import scala.util.matching.Regex
    val RegexAndroid = new Regex("""android-([0-9]*)""")
    val RegexAndroidHoneycomb = new Regex("""android-([a-zA-Z]*)""")
    platformName match {
      case RegexAndroid(level: String) ⇒ level.toInt
      case RegexAndroidHoneycomb       ⇒ 13
      case _                           ⇒ 1
    }
  }

  def apiLevelToName(apiLevel: String) = {
    "android-%s".format(apiLevel)
  }

  def apply() = {
    val envs = List("ANDROID_SDK_HOME", "ANDROID_SDK_ROOT", "ANDROID_HOME", "ANDROID_SDK")
    val paths = for { e ← envs; p = System.getenv(e); if p != null } yield p
    if (paths.isEmpty) error("You need to set " + envs.mkString(" or "))
    new SDK(Path.fromFile(paths.first), 3)
  }

  def apply(apiVersion: Int) = {
    val envs = List("ANDROID_SDK_HOME", "ANDROID_SDK_ROOT", "ANDROID_HOME", "ANDROID_SDK")
    val paths = for { e ← envs; p = System.getenv(e); if p != null } yield p
    if (paths.isEmpty) error("You need to set " + envs.mkString(" or "))
    new SDK(Path.fromFile(paths.first), apiVersion)
  }

  def apply(root: Path) = new SDK(root, 3)
  def apply(root: Path, apiVersion: Int) = new SDK(root, apiVersion)
}