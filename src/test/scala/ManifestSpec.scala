package android

import org.specs.Specification
import org.specs.mock.Mockito
import org.mockito.Matchers._

class AndroidPluginSpec extends Specification with Mockito {

  //  "Manifest object" should {
  //    "throw an exception if Manifest Not present in root of project" in {
  //      val project = mock[android.Project]
  //      Manifest(project) must throwA(new java.io.FileNotFoundException)
  //    }
  //  }

  "Manifest class" should {
    "give the current sdk version " in {
      val manifest = <manifest xmlns:android="http://schemas.android.com/apk/res/android"><uses-sdk android:minSdkVersion="5" /></manifest>
      new Manifest(manifest).sdkVersion mustEq (5)
    }

    "get the correct version" in {
      val manifest = <manifest xmlns:android="http://schemas.android.com/apk/res/android" android:versionCode="123" />
      new Manifest(manifest).versionCode must be matching ("123")
    }

    "set the correct version accordingly" in {
    }
  }

  "Within an android environment, the plugin" should {
    "point to an accurate android.jar file" in {
      new SDK().getAndroidJar.absolutePath must beAnExistingPath
    }
  }
}