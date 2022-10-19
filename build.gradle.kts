import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id(Dependencies.Spotless.SPOTLESS_PLUGIN) version Dependencies.Spotless.VERSION
    id(Dependencies.Kotlin.KSP_PLUGIN) version Dependencies.Kotlin.KSP_VERSION
    id(Dependencies.DependencyVersions.DEPENDENCY_VERSIONS_PLUGIN) version Dependencies.DependencyVersions.VERSION
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
    plugins.apply(Dependencies.Spotless.SPOTLESS_PLUGIN)

    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = "11"
            freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
        }
    }

    spotless {
        kotlin {
            ktlint(Dependencies.Spotless.KTLINT_VERSION).setUseExperimental(true)
            target("**/*.kt")
        }
    }
}