plugins {
    kotlin("jvm") version "2.2.20" apply false
    id("com.android.library") version "8.7.3" apply false
    id("com.android.application") version "8.7.3" apply false
    kotlin("android") version "2.2.20" apply false
}

allprojects {
    group = "com.developersyndicate.kxengine"
    version = "0.0.1-SNAPSHOT"

    repositories {
        google()
        mavenCentral()
    }
}