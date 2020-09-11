name := "cs434-project"
version := "0.1"
scalaVersion := "2.13.3"

lazy val global = project
  .in(file("."))
  .aggregate(
    common,
    master,
    slave
  )

lazy val common = project
  .in(file("common"))

lazy val master = project
  .in(file("master"))
  .dependsOn(
    common
  )

lazy val slave = project
  .in(file("slave"))
  .dependsOn(
    common
  )