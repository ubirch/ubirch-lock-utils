// see http://www.scala-sbt.org/0.13/docs/Parallel-Execution.html for details
concurrentRestrictions in Global := Seq(
  Tags.limit(Tags.Test, 1)
)

/*
 * BASIC INFORMATION
 ********************************************************/

name := "ubirch-lock-utils"
version := "0.3.4"
description := "Simple Redis based locking utils"
organization := "com.ubirch.util"
homepage := Some(url("http://ubirch.com"))
scalaVersion := "2.11.12"
scalacOptions ++= Seq(
  "-feature"
)
scmInfo := Some(ScmInfo(
  url("https://github.com/ubirch/ubirch-lock-utils"),
  "https://github.com/ubirch/ubirch-lock-utils.git"
))

/*
 * CREDENTIALS
 ********************************************************/

(sys.env.get("CLOUDREPO_USER"), sys.env.get("CLOUDREPO_PW")) match {
  case (Some(username), Some(password)) =>
    println("USERNAME and/or PASSWORD found.")
    credentials += Credentials("ubirch.mycloudrepo.io", "ubirch.mycloudrepo.io", username, password)
  case _ =>
    println("USERNAME and/or PASSWORD is taken from /.sbt/.credentials")
    credentials += Credentials(Path.userHome / ".sbt" / ".credentials")
}


/*
 * RESOLVER
 ********************************************************/

val resolverUbirchUtils = "cloudrepo.io" at "https://ubirch.mycloudrepo.io/repositories/ubirch-utils-mvn"
val resolverElasticsearch = "elasticsearch-releases" at "https://artifacts.elastic.co/maven"

resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots"),
  resolverUbirchUtils,
  resolverElasticsearch)


/*
 * PUBLISHING
 ********************************************************/


publishTo := Some(resolverUbirchUtils)
publishMavenStyle := true


/*
 * DEPENDENCIES
 ********************************************************/

//version
val akkaV = "2.5.11"

//groups
val ubirchUtilGroup = "com.ubirch.util"
val akkaG = "com.typesafe.akka"

// Ubirch dependencies
val ubirchUtilUuid = ubirchUtilGroup %% "ubirch-uuid-utils" % "0.1.4"
val ubirchUtilRedisUtil = ubirchUtilGroup %% "ubirch-redis-utils" % "0.6.1"

// External dependencies
val scalaTest = "org.scalatest" %% "scalatest" % "3.0.5"
val akkaTestkit = "com.typesafe.akka" %% "akka-testkit" % akkaV
val redisson = "org.redisson" % "redisson" % "3.7.5"
val akkaActor = akkaG %% "akka-actor" % akkaV
val rediscala = "com.github.etaty" %% "rediscala" % "1.8.0" excludeAll ExclusionRule(organization = s"${akkaActor.organization}", name = s"${akkaActor.name}")
val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % "3.9.0"

val slf4j = "org.slf4j" % "slf4j-api" % "1.7.25"
val logbackClassic = "ch.qos.logback" % "logback-classic" % "1.2.3"

val logging = Seq(
  scalaLogging,
  slf4j,
  logbackClassic
)

libraryDependencies ++= Seq(
  ubirchUtilRedisUtil,
  ubirchUtilUuid % "test",
  redisson,
  rediscala,
  scalaTest % "test",
  akkaTestkit % "test"
) ++ logging
