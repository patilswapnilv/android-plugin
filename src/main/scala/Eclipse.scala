import sbt._
import java.io.File
import java.nio.charset.Charset._

trait Eclipse extends AndroidProject { this: AndroidProject =>

  lazy val eclipse = task {
    log.info("generating eclipse file...")

    FileUtilities.touch(projectFile, log) match {
      case Some(error) =>
        Some("Unable to write project file " + projectFile + ": " + error)
      case None =>
        FileUtilities.write(projectFile, projectFileXML toString, forName("UTF-8"), log)
    }

    FileUtilities.touch(classpathFile, log) match {
      case Some(error) =>
        Some("Unable to write classpath file " + projectFile + ": " + error)
      case None =>
        FileUtilities.write(classpathFile, classPathFileXML toString, forName("UTF-8"), log)
    }
  }

  lazy val projectFile: File = info.projectPath / ".project" asFile

  lazy val classpathFile: File = info.projectPath / ".classpath" asFile

  def classpathEntry(p: sbt.Path) =
    <classpathentry kind="lib" path={ p.relativePath }/>

  def getLibraries = {
    dependencyPath.descendentsExcept("*.jar", "test").get.map { classpathEntry(_) }
  }

  def getLibrarySources(project: Project) = {
    (List[Node]() /: dependencies)((l, v) => linkedResourcesXML(v.asInstanceOf[AndroidProject]) :: l)
  }

  lazy val classPathFileXML =
    <classpath>
      <classpathentry kind="src" path={ mainJavaSourcePath relativePath }/>
      <classpathentry kind="src" path={ generatedRFile relativePath }/>
      { getLibrarySources }
      { getLibraries }
      <classpathentry kind="con" path="com.android.ide.eclipse.adt.ANDROID_FRAMEWORK"/>
      depp
      <classpathentry kind="output" path={ outputPath relativePath }/>
    </classpath>

  val projectFileXML =
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
    </projectDescription>

  def linkedResourcesXML(project: AndroidProject) =
    <linkedResources>
      <link>
        <name>{ project.projectName.value + "_src" }</name>
        <type>2</type>
        <location>{ project.mainJavaSourcePath.absolutePath }</location>
      </link>
    </linkedResources>

}