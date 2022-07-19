plugins {
    id("com.android.library")
    id("com.google.devtools.ksp")
    `android-common-config`
}

android {
    namespace = "com.nemesis.sunrise.data"

    defaultConfig {
        consumerProguardFiles("consumer-rules.pro")
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    ksp {
        arg("room.schemaLocation", "$projectDir/schemas")
        arg("room.incremental", true.toString())
    }
}


dependencies {
    implementation(project(":domain"))

    implementation(Dependencies.Kotlin.COROUTINES_ANDROID)
    implementation(Dependencies.Android.CORE)

    api(Dependencies.Room.RUNTIME)
    ksp(Dependencies.Room.COMPILER)
    implementation(Dependencies.Room.KTX)
}