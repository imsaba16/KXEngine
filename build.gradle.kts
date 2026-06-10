plugins {
    kotlin("jvm") version "2.2.20"
    `java-library`
}

group = "com.developersyndicate.kxengine"
version = "0.0.1-SNAPSHOT"


repositories {
    mavenCentral()
}

val lwjgl = "3.3.6"

dependencies {
    api("org.lwjgl:lwjgl:$lwjgl")
    api("org.lwjgl:lwjgl-glfw:$lwjgl")
    api("org.lwjgl:lwjgl-opengl:$lwjgl")
    api("org.lwjgl:lwjgl-stb:$lwjgl")
    api("org.lwjgl:lwjgl-openal:$lwjgl")
    implementation("org.slf4j:slf4j-simple:2.0.17")

    runtimeOnly("org.lwjgl:lwjgl::natives-macos-arm64")
    runtimeOnly("org.lwjgl:lwjgl-glfw::natives-macos-arm64")
    runtimeOnly("org.lwjgl:lwjgl-opengl::natives-macos-arm64")
    runtimeOnly("org.lwjgl:lwjgl-stb::natives-macos-arm64")
    runtimeOnly("org.lwjgl:lwjgl-openal::natives-macos-arm64")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}