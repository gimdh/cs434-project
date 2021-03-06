name := "project"
organization := "com.gimdh"
version := "0.1"
scalaVersion := "2.13.2"


lazy val root = project
  .in(file("."))
  .aggregate(
    common,
    master,
    slave
  )

lazy val common: Project = project
  .in(file("common"))


lazy val master: Project = project
  .in(file("master"))
  .dependsOn(
    common
  )

lazy val slave: Project = project
  .in(file("slave"))
  .dependsOn(
    common
  )
