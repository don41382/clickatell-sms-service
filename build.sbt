name := "sms-service"
version := "1.0"
scalaVersion := "2.11.8"


lazy val commonSettings = Seq(
  version := "0.1-SNAPSHOT",
  organization := "de.rocketsolutions",
  scalaVersion := "2.11.8"
)

resolvers += "Java.net Maven2 Repository" at "http://download.java.net/maven/2/"


enablePlugins(DockerPlugin)

// http client
libraryDependencies +=  "org.scalaj" %% "scalaj-http" % "2.3.0"

// web server
libraryDependencies ++= {
  val akkaV = "2.3.9"
  val sprayV = "1.3.3"
  Seq(
    "io.spray"            %%  "spray-can"     % sprayV,
    "io.spray"            %%  "spray-routing" % sprayV,
    "io.spray"            %%  "spray-testkit" % sprayV  % "test",
    "com.typesafe.akka"   %%  "akka-actor"    % akkaV,
    "com.typesafe.akka"   %%  "akka-testkit"  % akkaV   % "test",
    "org.specs2"          %%  "specs2-core"   % "3.8.4" % "test"
  )
}

// configuration
libraryDependencies += "com.iheart" %% "ficus" % "1.2.3"

// logging
libraryDependencies += "ch.qos.logback" %  "logback-classic" % "1.1.7"


// add conf to classpath
unmanagedClasspath in Runtime <+= (baseDirectory) map { bd => Attributed.blank(bd / "conf") }

// aws
region in aws := com.amazonaws.regions.Regions.AP_SOUTHEAST_2
ebBundleTargetFiles in aws <<= Def.task {
  val base = baseDirectory.value
  val packageJarFile = (packageBin in Compile).value
  Seq(
    (base / "target/docker/Dockerfile", "Dockerfile"),
    (base / "/conf/Dockerrun.aws.json", "Dockerrun.aws.json"),
    (packageJarFile, "/" + packageJarFile.name)
  )
}

// ebS3CreateBucket in aws := true

// remove application.test.conf from assembly
assemblyMergeStrategy in assembly := {
  case PathList("application.test.conf") => MergeStrategy.discard
  case other =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(other)
}


// docker builder
dockerfile in docker := {
  // The assembly task generates a fat JAR file
  val artifact: File = assembly.value
  val artifactTargetPath = s"/app/${artifact.name}"

  new Dockerfile {
    from("java")
    add(artifact, artifactTargetPath)
    expose(8080)
    entryPoint("java", "-jar", artifactTargetPath)
  }
}
