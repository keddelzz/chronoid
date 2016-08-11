
// ==================== SETTINGS ====================

lazy val baseSettings = Seq(
  EclipseKeys.withSource := true,
  scalaVersion := "2.11.8",
  libraryDependencies ++= Seq(
    "org.scalatest" %% "scalatest" % "3.0.0" % "test"
  )
)

lazy val customSettings = Seq(
  version := "0.0.1",
  name := "chronoid",
  mainClass in (Compile, packageBin) := Some("chronoid.Chronoid")
)

// ==================== PROJECTS ====================

lazy val proj = (project in file(".")).
  settings(baseSettings: _*).
  settings(customSettings: _*)
