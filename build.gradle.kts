plugins {
    kotlin("jvm") version "2.2.20"
    application
}

application {
    mainClass.set("com.developersyndicate.kxengine.MainKt")
    applicationDefaultJvmArgs = listOf("-XstartOnFirstThread")
}

group = "com.developersyndicate.kxengine"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

val lwjgl = "3.3.6"

dependencies {
    implementation("org.lwjgl:lwjgl:$lwjgl")
    implementation("org.lwjgl:lwjgl-glfw:$lwjgl")
    implementation("org.lwjgl:lwjgl-opengl:$lwjgl")
    implementation("org.lwjgl:lwjgl-stb:$lwjgl")
    implementation("org.slf4j:slf4j-simple:2.0.17")

    runtimeOnly("org.lwjgl:lwjgl::natives-macos-arm64")
    runtimeOnly("org.lwjgl:lwjgl-glfw::natives-macos-arm64")
    runtimeOnly("org.lwjgl:lwjgl-opengl::natives-macos-arm64")
    runtimeOnly("org.lwjgl:lwjgl-stb::natives-macos-arm64")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}