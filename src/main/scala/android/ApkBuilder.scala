package android

import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.io.File
import java.io.PrintStream
import sbt._


abstract class ApkBuilder {
  val apkFile: File
  val resFile: File
  val dexFile: File
  // val log: Logger
  val cl: ClassLoader

  def create() = {
    val builder = cl.loadClass(ApkBuilder.className).getDeclaredConstructor(
      // Array(classOf[File], classOf[File], classOf[File], classOf[PrivateKey], classOf[X509Certificate], classOf[PrintStream]))
      Array(classOf[File], classOf[File], classOf[File], classOf[String], classOf[PrintStream]))
      .newInstance(apkFile, resFile, dexFile, "/home/acsia/.android/debug.keystore", null)

    val javaApkBuilder = None
    None
  }
}
//
//case class DebugApk extends ApkBuilder
//case class CertifiedApk() extends ApkBuilder

object ApkBuilder {

  val className = "com.android.sdklib.build.ApkBuilder"

  def apply(androidProject: android.Project): ApkBuilder =
    new ApkBuilder {
      import sbt.Path
      val apkFile = androidProject.apk.asInstanceOf[sbt.Path].asFile
      val resFile = androidProject.res.asInstanceOf[sbt.Path].asFile
      val dexFile = androidProject.dexD.asInstanceOf[sbt.Path].asFile
      def key = None
      def certificate = None
      //val log = None
      val cl = ClasspathUtilities.toLoader(androidProject.sdk.sdkLib)
    }

  /**
   *
   */
  def apply(androidProject: android.Project, certificate: Path, certPassword: String, alias: String, aliasPassword: String): ApkBuilder = {

    import java.io._
    import java.security._
    import java.security.KeyStore.PasswordProtection

    val in = new FileInputStream(certificate.asFile)
    val keystore = KeyStore.getInstance(KeyStore.getDefaultType())
    keystore.load(in, certPassword.toCharArray)
    in.close

    val password = new KeyStore.PasswordProtection(aliasPassword.toCharArray)
    val entry = keystore.getEntry(alias, password)
    val privateKey = entry.asInstanceOf[KeyStore.PrivateKeyEntry].getPrivateKey()

    new ApkBuilder {
      import sbt.Path
      val apkFile = androidProject.apk.asInstanceOf[sbt.Path].asFile
      val resFile = androidProject.res.asInstanceOf[sbt.Path].asFile
      val dexFile = androidProject.dexD.asInstanceOf[sbt.Path].asFile
      def key = None
      def certificate = None
      //val log = None
      val cl = ClasspathUtilities.toLoader(androidProject.sdk.sdkLib)
    }
  }
}