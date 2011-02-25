package android
import sbt._

class SDK { 
	//this: Manifest ⇒

  lazy val getSdkHome: Path = {
    val envs = List("ANDROID_SDK_HOME", "ANDROID_SDK_ROOT", "ANDROID_HOME", "ANDROID_SDK")
    val paths = for { e ← envs; p = System.getenv(e); if p != null } yield p
    if (paths.isEmpty) error("You need to set " + envs.mkString(" or "))
    Path.fromFile(paths.first)
  }

  lazy val getAndroidJar: Path = {
    getSdkHome / "platforms" / "android-9" / "android.jar"
  }
  
  lazy val aapt:Path = {
	  getSdkHome / "platform-tools" / "aapt"
  }
  
  lazy val dex:Path = {
	  getSdkHome / "platform-tools" / "dx"
  }
  
}