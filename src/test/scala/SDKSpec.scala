package android

import org.specs.Specification
import org.mockito.Matchers._

class SDKSpec extends Specification {
  "Given a correct SDK Path" should {
    val sdk = SDK()
    "the correct sdk version code should be returned" in {
      SDK.platformName2ApiLevel("android-10") mustEq (10)
    }
  }
}
