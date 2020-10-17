name := "project"
organization := "com.gimdh"
version := "0.1"
scalaVersion := "2.13.3"

ThisBuild / useCoursier := false

lazy val root = project
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