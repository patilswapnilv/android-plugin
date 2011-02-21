import sbt._
import java.io.File
import java.nio.charset.Charset._
import scala.xml.Node

trait Eclipse extends AndroidProject { this: AndroidProject =>

  lazy val eclipse = eclipseAction
  def eclipseAction = eclipseTask describedAs "Create eclipse .project/.classpath for Android based projets"
  def eclipseTask: Task = task {
    log.info("generating eclipse project file...")
    FileUtilities.touch(projectFile, log) match {
      case Some(error) =>
        Some("Unable to write project file " + projectFile + ": " + error)
      case None =>
        FileUtilities.write(projectFile, projectFileXML toString, forName("UTF-8"), log)
    }
    log.info("generating eclipse classpath file...")
    FileUtilities.touch(classpathFile, log) match {
      case Some(error) =>
        Some("Unable to write classpath file " + projectFile + ": " + error)
      case None =>
        FileUtilities.write(classpathFile, classPathFileXML toString, forName("UTF-8"), log)
    }
  }

  lazy val projectFile: File = info.projectPath / ".project" asFile

  lazy val classpathFile: File = info.projectPath / ".classpath" asFile

  // Depending on the type, will generate the correct classpath entry
  def classpathEntry(p: Any): Node = p match {
    case p: sbt.Project => <classpathentry kind="src" path={ p.projectName.value + "_src" }/>
    case p: sbt.Path => <classpathentry kind="lib" path={ p.relativePath }/>
    case p: String => <classpathentry kind="con" path={ p }/>
  }

  def getLibraries = {
    dependencyPath.descendentsExcept("*.jar", "test").get.map { classpathEntry(_) }
  }

  def getDependenciesLibrary = {
    (List[Node]() /: dependencies)((l, v) =>
      v.asInstanceOf[AndroidProject].dependencyPath.descendentsExcept("*.jar", "test").get.map { p: Path =>
        classpathEntry(p.absolutePath)
      }.toList ++ l)
  }

  def getSrcForDependencies = {
    (List[Node]() /: dependencies)((l, v) => classpathEntry(v) :: l)
  }

  def getLibrarySources(project: Project) = {
    (List[Node]() /: dependencies)((l, v) => linkedResourcesXML(v.asInstanceOf[AndroidProject]) :: l)
  }

  def linkedResourcesXML(project: AndroidProject) =
      <link>
        <name>{ project.projectName.value + "_src" }</name>
        <type>2</type>
        <location>{ project.mainJavaSourcePath.absolutePath }</location>
      </link>

  lazy val classPathFileXML =
    <classpath>
      <classpathentry kind="src" path={ mainJavaSourcePath relativePath }/>
      <classpathentry kind="src" path={ generatedRFile relativePath }/>
      { getLibraries }
      { getDependenciesLibrary }
      { getSrcForDependencies }
      <classpathentry kind="output" path={ outputPath relativePath }/>
      <classpathentry kind="con" path="com.android.ide.eclipse.adt.ANDROID_FRAMEWORK"/>
    </classpath>

  lazy val projectFileXML =
    <projectDescription>
      <name>{ projectName.value }</name>
      <comment></comment>
      <projects>
      </projects>
      <buildSpec>
        <buildCommand>
          <name>com.android.ide.eclipse.adt.ResourceManagerBuilder</name>
          <arguments>
          </arguments>
        </buildCommand>
        <buildCommand>
          <name>com.android.ide.eclipse.adt.PreCompilerBuilder</name>
          <arguments>
          </arguments>
        </buildCommand>
        <buildCommand>
          <name>org.eclipse.jdt.core.javabuilder</name>
          <arguments>
          </arguments>
        </buildCommand>
        <buildCommand>
          <name>com.android.ide.eclipse.adt.ApkBuilder</name>
          <arguments>
          </arguments>
        </buildCommand>
      </buildSpec>
      <natures>
        <nature>com.android.ide.eclipse.adt.AndroidNature</nature>
        <nature>org.eclipse.jdt.core.javanature</nature>
      </natures>
  	  <linkedResources>
      { getLibrarySources(this) }
      </linkedResources>
    </projectDescription>

}