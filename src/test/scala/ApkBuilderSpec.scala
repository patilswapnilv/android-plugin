package android

import org.specs.Specification
import org.specs.matcher._
import sbt._
import org.specs.matcher.Matcher
import org.specs.mock.Mockito

class ApkBuilderSpec extends Specification with Mockito {

  val sdkRoot = sbt.Path.fromFile(System.getenv("ANDROID_HOME"))
  val lib = sdkRoot / "tools" / "lib" / "sdklib.jar"
  val project = mock[android.Project]
  val sdk = mock[SDK]
  
  project.apk returns sbt.Path.fromFile("src/test/resources/test.apk")
  project.dexD returns sbt.Path.fromFile("src/test/resources/classes.dex")
  project.res returns sbt.Path.fromFile("src/test/resources/res.apk")
  project.sdk returns sdk
  sdk.sdkLib returns sbt.Path.fromFile("/opt/android-sdk-linux_x86/tools/lib/sdklib.jar")

  "Given the sdklib.jar is not know at runtime" should {
    "load the jar" in {
      ApkBuilder(project).create();
      "src/test/resources/test.apk" must beAnExistingPath
    }
  }
}