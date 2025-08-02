@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    // Apply the Kotlin JVM plugin to add support for Kotlin on the JVM.
    kotlin("multiplatform") version "2.2.0"
}

group = "best.william"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    google()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/kotlinx-coroutines/maven") }
    maven { url = uri("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/dev") }
}

kotlin {
    jvm() {
        binaries {
            // Configures a JavaExec task named "runJvm" and a Gradle distribution for the "main" compilation in this target
            executable {
                mainClass.set("best.william.kgb.AppKt")
            }
        }
    }
    mingwX64("native") {
        binaries {
            executable()
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {

                // Use the Kotlin JDK 8 standard library.
                implementation(kotlin("stdlib"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
                implementation("co.touchlab:kermit:2.0.0")
            }
        }
        val commonTest by getting {
            dependencies {

                // Use the Kotlin test library.
                implementation("org.jetbrains.kotlin:kotlin-test")
            }
        }
        val jvmMain by getting
        val jvmTest by getting
        val nativeMain by getting
        val nativeTest by getting
    }
}