import AssemblyKeys._

name := """wikipedia_categorizer"""

version := "1.0"

scalaVersion := "2.10.2"

mainClass in (Compile,run) := Some("jp.mwsoft.wikipedia.categorizer.Main")

resolvers += "maven-restlet" at "http://maven.restlet.org/"

libraryDependencies  ++= Seq(
  "com.typesafe.slick" %% "slick" % "1.0.1",
  "commons-logging" % "commons-logging" % "1.1.3",
  "joda-time" % "joda-time" % "2.3",
  "mysql" % "mysql-connector-java" % "5.1.27",
  "org.apache.solr" % "solr-analysis-extras" % "4.6.0",
  "org.apache.solr" % "solr-solrj" % "4.5.0",
  "org.apache.commons" % "commons-compress" % "1.6",
  "org.joda" % "joda-convert" % "1.5",
  "org.jsoup" % "jsoup" % "1.7.2",
  "junit" % "junit" % "4.11" % "test",
  "org.scalatest" %% "scalatest" % "1.9.1" % "test"
)

assemblySettings

test in assembly := {}

mergeStrategy in assembly <<= (mergeStrategy in assembly) { (old) =>
  {
    case "META-INF/MANIFEST.MF" => MergeStrategy.discard
    case x => MergeStrategy.first
  }
}

EclipseKeys.createSrc := EclipseCreateSrc.Default + EclipseCreateSrc.Resource

