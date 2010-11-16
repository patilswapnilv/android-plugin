import org.scalatest.FlatSpec
import org.scalatest.mock.MockitoSugar
import org.scalatest.matchers.ShouldMatchers
import scala.collection.immutable.Stack
import sbt._

class AndroidLayoutSpec extends FlatSpec with ShouldMatchers with MockitoSugar {

  class MainProject(info: ProjectInfo) extends AndroidProject(info) {
def androidPlatformName = "android-8"
	}

  "An Android project" should "define its SDK directory in local.properties or ENV or fail" in {
	val project = new MainProject(new ProjectInfo)
	project.androidSdkPath should equal("/opt/android")
  }
}
