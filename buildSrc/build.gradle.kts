plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    google()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation("com.android.tools.build:gradle:7.3.0")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.20")
    implementation("com.squareup:javapoet:1.13.0")
}