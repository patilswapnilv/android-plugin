import scala.reflect.Manifest
import org.specs.specification.Context
import org.specs.Specification
import org.specs.matcher.Matcher
import org.specs.mock.Mockito
import sbt._
import android._

class ProjectSpec extends Specification with Mockito {

  import sbt._
  val tmp: sbt.Path = sbt.Path.fromFile("/tmp")

  val project = globalContext(createProject, deleteProject)
  val createProject = new Context { before(println("hello")) }
  val deleteProject = new Context { after(println("hello")) }

  val projectWithLib = globalContext(createProject, deleteProject)

  "An android project" when project should {
    "get the correct" in {
      1 mustEq (1)
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