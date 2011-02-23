import sbt._
import java.io.File

class Project(info: ProjectInfo) extends ParentProject(info) {

  //override def androidPlatformName = "android-2.1"

  // or preferably set the ANDROID_SDK_HOME environment variable
  //override lazy val androidSdkPath = Path.fromFile(new File("/usr/local/Cellar/android-sdk/r5"))

  // set to the keystore alias you used when creating your keychain
  //val keyalias = "my_keys"

  // set to the location of your keystore
  //override def keystorePath = Path.userHome / ".keystore" / "mykeys.keystore"

  override def shouldCheckOutputDirectories = false

  lazy val main = project("MyAndroidAppProject", "MyAndroidAppProject", new MainProject(_))

  class MainProject(info: ProjectInfo) extends DefaultProject(info) with android.Project {
  }

}
