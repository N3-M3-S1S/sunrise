plugins {
    kotlin("jvm")
    `java-library`
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
    api(Dependencies.Kotlin.DATETIME)
    implementation(Dependencies.Kotlin.COROUTINES)
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}