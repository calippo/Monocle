import sbt._
import Keys._

import xerial.sbt.Sonatype._
import xerial.sbt.Sonatype.SonatypeKeys._

object BuildSettings {
  import ScalaLensPublishing._
  val buildScalaVersion = "2.10.3"

  val buildSettings = Defaults.defaultSettings ++ Seq(
    organization      := "com.github.julien-truffaut",
    version           := "0.1-SNAPSHOT",
    scalaVersion      := buildScalaVersion,
    scalacOptions     ++= Seq("-deprecation", "-unchecked", "-feature", "-language:higherKinds", "-language:implicitConversions"),
    resolvers         += Resolver.sonatypeRepo("releases"),
    resolvers         += Resolver.sonatypeRepo("snapshots"),
    addCompilerPlugin("org.scalamacros" % "paradise" % "2.0.0-M3" cross CrossVersion.full)
  )  ++ publishSettings
}

object Dependencies {
  val scalaz       = "org.scalaz"      %% "scalaz-core"               % "7.0.5"
  val scalaCheck   = "org.scalacheck"  %% "scalacheck"                % "1.10.1"
  val scalaCheckBinding = "org.scalaz" %% "scalaz-scalacheck-binding" % "7.0.5"        % "test"
  val specs2       = "org.specs2"      %% "specs2"                    % "1.12.3"       % "test"
  val scalazSpec2  = "org.typelevel"   %% "scalaz-specs2"             % "0.1.5"        % "test"
  val scalaReflect = "org.scala-lang"  %  "scala-reflect"             % BuildSettings.buildScalaVersion
  val quasiquotes  = "org.scalamacros" % "quasiquotes"                % "2.0.0-M3" cross CrossVersion.full
  val tests        = Seq(scalaCheck, scalaCheckBinding, specs2, scalazSpec2)
}

object ScalaLensBuild extends Build {
  import BuildSettings._
  import Dependencies._


  lazy val root: Project = Project(
    "monocle",
    file("."),
    settings = buildSettings ++ Seq(
      publishArtifact := false,
      run <<= run in Compile in macros) ++ sonatypeSettings
  ) aggregate(macros, core, examples)

  lazy val macros: Project = Project(
    "monocle-macros",
    file("macros"),
    settings = buildSettings ++ Seq(
      libraryDependencies ++= Seq(scalaReflect, quasiquotes, scalaz) ++ tests
    )
  ) dependsOn(core)

  lazy val core: Project = Project(
    "monocle-core",
    file("core"),
    settings = buildSettings ++ Seq(
      libraryDependencies ++= Seq(scalaz) ++ tests
    )
  )

  lazy val examples: Project = Project(
    "monocle-examples",
    file("examples"),
    settings = buildSettings ++ Seq(
      publishArtifact := false,
      libraryDependencies ++= Seq(scalaz)
    )
  ) dependsOn(macros, core)
}

object ScalaLensPublishing  {

  lazy val publishSettings: Seq[Setting[_]] = Seq(
    pomExtra := {
      <url>https://github.com/julien-truffaut/Monocle</url>
        <licenses>
          <license>
            <name>MIT</name>
            <url>http://opensource.org/licenses/MIT</url>
          </license>
        </licenses>
        <scm>
          <connection>scm:git:github.com/julien-truffaut/Monocle</connection>
          <developerConnection>scm:git:git@github.com:julien-truffaut/Monocle.git</developerConnection>
          <url>github.com:julien-truffaut/Monocle.git</url>
        </scm>
        <developers>
          <developer>
            <id>julien-truffaut</id>
            <name>Julien Truffaut</name>
          </developer>
        </developers>
    }
  ) ++ sonatypeSettings

}
