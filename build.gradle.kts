plugins {
  idea
  java
  jacoco
  `maven-publish`

  id("org.jlleitschuh.gradle.ktlint") version "10.0.0"
  id("io.gitlab.arturbosch.detekt") version "1.16.0-RC2"
  id("com.github.ben-manes.versions") version "0.36.0"
  kotlin("jvm") version "1.4.31"
  kotlin("kapt") version "1.4.31"
}

object Versions {
  const val JAVA = "11"
  const val AUTO_SERVICE = "1.0-rc7"
  const val HANDLEBARS = "4.2.0"
  const val JACKSON = "2.12.2"
  const val KTLINT = "0.40.0"
}

group = "com.ukonnra.wonderland"
version = "0.0.1-SNAPSHOT"

repositories {
  jcenter()
  mavenLocal()
  mavenCentral()
  maven { url = uri("https://jitpack.io") }
}

ktlint {
  version.set(Versions.KTLINT)
}

detekt {
  failFast = true
  config = files("$rootDir/detekt.yml")
  autoCorrect = true
  buildUponDefaultConfig = true
  reports {
    xml.enabled = true
    html.enabled = true
    txt.enabled = false
  }
}

tasks.detekt {
  jvmTarget = Versions.JAVA
}

tasks.compileKotlin {
  kotlinOptions {
    jvmTarget = Versions.JAVA
    freeCompilerArgs = listOf("-Xjsr305=strict")
  }
}

tasks.compileTestKotlin {
  kotlinOptions {
    jvmTarget = Versions.JAVA
    freeCompilerArgs = listOf("-Xjsr305=strict")
  }
}

tasks.jacocoTestReport {
  dependsOn(tasks.test)
  reports {
    xml.isEnabled = true
    html.isEnabled = true
    csv.isEnabled = false
  }
}

tasks.test {
  finalizedBy(tasks.jacocoTestReport)
  useJUnitPlatform()
}

dependencies {

  implementation("com.google.auto.service:auto-service:${Versions.AUTO_SERVICE}")
  kapt("com.google.auto.service:auto-service:${Versions.AUTO_SERVICE}")

  implementation("com.github.jknack:handlebars:${Versions.HANDLEBARS}")
  api("com.fasterxml.jackson.core:jackson-databind:${Versions.JACKSON}")
}

java {
  sourceCompatibility = JavaVersion.VERSION_11
  targetCompatibility = JavaVersion.VERSION_11

  withSourcesJar()
  withJavadocJar()
}

publishing.publications.register("release", MavenPublication::class) {
  from(components["java"])
  pom {
    licenses {
      license {
        name.set("The Apache License, Version 2.0")
        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
      }
    }
  }
}
