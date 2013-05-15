import sbt._

object Dependencies {

  val resolutionRepos = Seq(
    "spray repo"            at "http://repo.spray.io/",
    "spray nightlies repo"  at "http://nightlies.spray.io"
  )

  val sprayCan        = "io.spray"                  %   "spray-can"             % "1.1-20130509"
  val sprayRouting    = "io.spray"                  %   "spray-routing"         % "1.1-20130509"
  val sprayTestkit    = "io.spray"                  %   "spray-testkit"         % "1.1-20130509"
  val sprayClient     = "io.spray"                  %   "spray-client"          % "1.1-20130509"
  val sprayServlet    = "io.spray"                  %   "spray-servlet"         % "1.1-20130509"
  val sprayJson       = "io.spray"                  %%  "spray-json"            % "1.2.3"
  val scalaReflect    = "org.scala-lang"            %   "scala-reflect"         % "2.10.1"
  val akkaActor       = "com.typesafe.akka"         %%  "akka-actor"            % "2.1.2"
  val akkaSlf4j       = "com.typesafe.akka"         %%  "akka-slf4j"            % "2.1.2"
  val akkaTestKit     = "com.typesafe.akka"         %%  "akka-testkit"          % "2.1.2"
  val scalatest       = "org.scalatest"             %%  "scalatest"             % "1.9.1"
  val logback         = "ch.qos.logback"            %   "logback-classic"       % "1.0.10"
  val aspectJ         = "org.aspectj"               %   "aspectjrt"             % "1.7.2"
  val metrics         = "com.yammer.metrics"        %   "metrics-core"          % "2.2.0"
  val metricsScala    = "com.yammer.metrics"        %   "metrics-scala_2.9.1"   % "2.2.0"
  val newrelic        = "com.newrelic.agent.java"   %   "newrelic-api"          % "2.17.2"
  val playJson        = "play"                      %   "play-json"             % "2.2-SNAPSHOT"


  def compile   (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "compile")
  def provided  (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "provided")
  def test      (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "test")
  def runtime   (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "runtime")
  def container (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "container")
}
