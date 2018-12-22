inThisBuild(
  List(
    organization := "com.kubukoz",
    homepage := Some(url("https://github.com/kubukoz/sup")),
    licenses := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
    developers := List(
      Developer(
        "kubukoz",
        "Jakub Kozłowski",
        "kubukoz@gmail.com",
        url("https://kubukoz.com")
      )
    )
  ))

val compilerPlugins = List(
  compilerPlugin("org.scalamacros" % "paradise" % "2.1.1").cross(CrossVersion.full),
  compilerPlugin("org.spire-math" %% "kind-projector" % "0.9.8")
)

val commonSettings = Seq(
  scalaVersion := "2.12.8",
  scalacOptions ++= Options.all,
  fork in Test := true,
  name := "sup",
  updateOptions := updateOptions.value.withGigahorse(false), //may fix publishing bug
  libraryDependencies ++= Seq(
    "org.typelevel"              %% "cats-tagless-laws"         % "0.2.0" % Test,
    "org.typelevel"              %% "cats-effect-laws"          % "1.1.0" % Test,
    "org.typelevel"              %% "cats-testkit"              % "1.5.0" % Test,
    "org.typelevel"              %% "cats-laws"                 % "1.5.0" % Test,
    "org.typelevel"              %% "cats-kernel-laws"          % "1.5.0" % Test,
    "com.github.alexarchambault" %% "scalacheck-shapeless_1.13" % "1.1.6" % Test,
    "org.scalatest"              %% "scalatest"                 % "3.0.4" % Test
  ) ++ compilerPlugins
)

def module(moduleName: String): Project =
  Project(moduleName, file(moduleName)).settings(commonSettings).settings(name += s"-$moduleName")

val core = module("core").settings(
  libraryDependencies ++= Seq(
    "com.github.mpilquist" %% "simulacrum"        % "0.14.0",
    "org.typelevel"        %% "cats-effect"       % "1.1.0",
    "org.typelevel"        %% "cats-tagless-core" % "0.2.0"
  )
)

val scalacache = module("scalacache")
  .settings(
    libraryDependencies ++= Seq(
      "com.github.cb372" %% "scalacache-core" % "0.27.0"
    )
  )
  .dependsOn(core)

val microsite = project.settings(
  micrositeName := "sup",
  micrositeDescription := "Functional healthchecks in Scala",
  micrositeUrl := "https://sup.kubukoz.com",
  micrositeAuthor := "Jakub Kozłowski",
  micrositeTwitterCreator := "@kubukoz",
  micrositeGithubOwner := "kubukoz",
  micrositeGithubRepo := "sup",
  micrositeGitterChannel := false,
  fork in tut := true,

).enablePlugins(MicrositesPlugin)

val sup =
  project
    .in(file("."))
    .settings(commonSettings)
    .settings(skip in publish := true)
    .dependsOn(core, scalacache, microsite)
    .aggregate(core, scalacache, microsite)
