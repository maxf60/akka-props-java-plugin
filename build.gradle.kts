plugins {
    id("org.jetbrains.intellij") version "0.4.21"
    kotlin("jvm") version "1.4-M1"
}

group = "com.max.gc"
version = "1.0.0"

repositories {
    maven("https://dl.bintray.com/kotlin/kotlin-eap")
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
}

intellij {
    version = "2020.1.1"
    setPlugins("java")
    updateSinceUntilBuild = false
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}