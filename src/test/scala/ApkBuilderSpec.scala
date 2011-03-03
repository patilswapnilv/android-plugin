package android

import org.specs.Specification
import org.specs.matcher._
import sbt._
import org.specs.matcher.Matcher
import org.specs.mock.Mockito

class ApkBuilderSpec extends Specification with Mockito {

  val sdkRoot = sbt.Path.fromFile(System.getenv("ANDROID_HOME"))
  val lib = sdkRoot / "tools" / "lib" / "sdklib.jar"
  val project = mock[AndroidProject]
  val sdk = mock[SDK]
  
  project.apkPath returns sbt.Path.fromFile("src/test/resources/test.apk")
  project.classesDexPath returns sbt.Path.fromFile("src/test/resources/classes.dex")
  project.tmpResApkPath returns sbt.Path.fromFile("src/test/resources/res.apk")
  project.sdk returns sdk
  sdk.sdkLib returns lib

  "Given the sdklib.jar is not know at runtime" should {
    "load the jar" in {
      ApkBuilder(project).create();
      "src/test/resources/test.apk" must beAnExistingPath
    }
  }
}