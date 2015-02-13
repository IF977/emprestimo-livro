name := """breno-hiroshi"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  	javaEbean,
	javaJpa,
	"org.hibernate" % "hibernate-entitymanager" % "3.6.9.Final",
	"mysql" % "mysql-connector-java" % "5.1.18"
)