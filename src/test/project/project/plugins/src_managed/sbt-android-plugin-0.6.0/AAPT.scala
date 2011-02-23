import Process._
import sbt._

trait AndroidEnvironment {
  val sdkHome: Path = {
    val envs = List("ANDROID_SDK_HOME", "ANDROID_SDK_ROOT", "ANDROID_HOME", "ANDROID_SDK")
    val paths = for { e <- envs; p = System.getenv(e); if p != null } yield p
    if (paths.isEmpty) error("You need to set " + envs.mkString(" or "))
    Path.fromFile(paths.first)
  }
}

object AAPT {
	val < = {
	}
}

class AAPT extends AndroidEnvironment {

}