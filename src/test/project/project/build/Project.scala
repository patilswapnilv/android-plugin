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

  lazy val httpservice = project("MyAndroidAppProject" / "libs" / "RESTProvider" / "libs" / "HttpService",
    "HttpService", new HttpLibraryProject(_));

  lazy val sqliteprovider = project("MyAndroidAppProject" / "libs" / "RESTProvider" / "libs" / "SQLiteProvider",
    "SQLiteProvider", new LibraryProject(_));

  lazy val restprovider = project("MyAndroidAppProject" / "libs" / "RESTProvider",
    "RESTProvider",
    new LibraryProject(_),
    httpservice, sqliteprovider)

  lazy val main = project("MyAndroidAppProject", "MyAndroidAppProject", new MainProject(_), restprovider)

  class MainProject(info: ProjectInfo) extends android.AndroidProject(info)
  class LibraryProject(info: ProjectInfo) extends android.AndroidProject(info) with android.Library
  class HttpLibraryProject(info: ProjectInfo) extends android.AndroidProject(info) with android.Library {
    val jacksoncore = "org.codehaus.jackson" % "jackson-core-asl" % "1.6.2" % "compile"
    val jacksonmapper = "org.codehaus.jackson" % "jackson-mapper-asl" % "1.6.2" % "compile"
    val roboelectric = "org.robolectric" % "robolectric" % "0.9.4" % "test" from "http://pivotal.github.com/robolectric/downloads/robolectric-0.9.4-all.jar"
    val mockito_io = "org.mockito" % "mockito-all" % "1.8.5" % "test"
    val signpostcore = "oauth.signpost" % "signpost-core" % "1.2.1" % "compile"
    val signpostcommons = "oauth.signpost" % "signpost-commonshttp4" % "1.2.1" % "compile"
  }
}
