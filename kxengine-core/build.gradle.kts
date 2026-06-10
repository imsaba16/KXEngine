plugins {
    kotlin("jvm") version "2.2.20"
    `java-library`
}

group = "com.developersyndicate.kxengine.core"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}
