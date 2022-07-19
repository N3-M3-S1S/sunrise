plugins {
    id("com.diffplug.spotless") version "6.7.2"
    id("com.google.devtools.ksp") version "1.6.21-1.0.6"
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}

buildscript {
    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        classpath(Dependencies.Android.ANDROID_GRADLE_PLUGIN)
        classpath(Dependencies.Kotlin.KOTLIN_GRADLE_PLUGIN)
        classpath(Dependencies.Hilt.GRADLE_PLUGIN)
    }

}

subprojects {
    plugins.apply("com.diffplug.spotless")

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = "11"
            freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
        }
    }

    spotless {
        kotlin {
            ktlint("0.45.2").setUseExperimental(true)
        }
    }
}