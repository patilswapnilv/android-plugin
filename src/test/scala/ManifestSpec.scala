package android

import org.specs.Specification
import org.mockito.Matchers._

class AndroidPluginSpec extends Specification {

  "Manifest class" should {
    "give the current sdk version" in {
      val manifest = <manifest xmlns:android="http://schemas.android.com/apk/res/android"><uses-sdk android:minSdkVersion="5" /></manifest>
      new Manifest(manifest).sdkVersion mustEq (5)
    }

    "give a minimum of 1 for SDK version if none specified" in {
      val manifest = <manifest xmlns:android="http://schemas.android.com/apk/res/android" />
      new Manifest(manifest).sdkVersion mustEq (1)
    }

    "get the correct version" in {
      val manifest = <manifest xmlns:android="http://schemas.android.com/apk/res/android" android:versionCode="123" />
      new Manifest(manifest).versionCode mustEq (123)
    }

    "get a human readable name" in {
      val manifest = <manifest xmlns:android="http://schemas.android.com/apk/res/android" android:versionCode="123" android:versionName="hercules" />
      val m = new Manifest(manifest)
      m.versionnedName must be matching ("hercules_v123")
      m.versionnedName("appName") must be matching ("appName_hercules_v123")

      def ownVersionning(name: String, versionCode: Int, versionName: String): String = name + "_#_" + versionName + "_#_" + versionCode
      m.versionnedName("appName", ownVersionning) must be matching ("appName_#_hercules_#_123")
    }

    //    "set the correct version accordingly" in {
    //    }
  }

  "Within an android environment, the plugin" should {
    "point to an accurate android.jar file" in {
      SDK().androidJar.absolutePath must beAnExistingPath
    }
  }
}