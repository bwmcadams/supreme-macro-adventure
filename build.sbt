
lazy val commonSettings = Seq(
  version := "1.0.0",
  scalaVersion := "2.11.7",
  scalacOptions ++= compileOptions,
  resolvers += Resolver.sonatypeRepo("releases"),
  resolvers += Resolver.sonatypeRepo("snapshots"),
  parallelExecution in Test := false,
  fork in Test := true,
  libraryDependencies ++= Seq(
    "org.scalatest" %% "scalatest" % "2.2.4" % "test",
    "org.scala-lang" % "scala-reflect" % "2.11.7"
  )
)

lazy val macros = (project in file("macros")).
  settings(commonSettings: _*).
  settings(
    name := "macros-talk-macros",
    libraryDependencies +=
      compilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)
  )

lazy val implementation = (project in file("implementation")).
  settings(commonSettings: _*).
  settings(
    name := "macros-talk-implementation"
  ).
  dependsOn(macros)


val compileOptions = Seq(
  "-deprecation",
  "-unchecked"
)
