import scala.reflect.Manifest
import org.specs.specification.Context
import org.specs.Specification
import org.specs.matcher.Matcher
import org.specs.mock.EasyMock
import org.easymock.EasyMock._
import sbt._
import android._

class ProjectSpec extends Specification with EasyMock {
  class P(info: ProjectInfo) extends AndroidProject(info)

  import sbt._
  val tmp: sbt.Path = sbt.Path.fromFile("/tmp")
  var p: AndroidProject = _

  val t = beforeContext(p = new P(mock[ProjectInfo]))
  val project = globalContext(createProject, deleteProject)

  val createProject = new Context {
    before(p = new P(mock[ProjectInfo]))
  }

  val deleteProject = new Context { after(println("hello")) }

  val projectWithLib = globalContext(createProject, deleteProject)

  "An android project" when project should {
    "get the correct library jar" in {

      //  ph = new P(niceMock[ProjectInfo])

      //   p.libraryJarPath.absolutePath must be matching ("/opt/android-sdk-linux_86/platforms/android-1/android.jar")
    }
  }

  "An android project" when projectWithLib should {
    "get the android.jar version from parent" in {
    }

    "include the res folder into the parent temporary apk" in {
    }

  }

  doLast { println("last") }
}