plugins {
    id("com.android.application")
    `android-common-config`
    kotlin("kapt")
    id("com.google.devtools.ksp")
    id("dagger.hilt.android.plugin")
    id("kotlin-parcelize")
}

android {
    namespace = "com.nemesis.sunrise.ui"

    defaultConfig {
        applicationId = "com.nemesis.sunrise"
        versionCode = 1
        versionName = "1"

    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = Dependencies.Android.Compose.COMPILER_VERSION
    }

    buildTypes.getByName("release") {
        isMinifyEnabled = true
        isShrinkResources = true
        proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }

    applicationVariants.all {
        kotlin.sourceSets {
            getByName(name) {
                kotlin.srcDir("build/generated/ksp/$name/kotlin")
            }
        }
    }

    kotlinOptions {
        jvmTarget = "11"
    }

}

dependencies {
    implementation(project(":domain"))
    implementation(project(":data"))

    implementation(Dependencies.Kotlin.COROUTINES_ANDROID)
    implementation(Dependencies.Android.CORE_KTX)

    implementation(Dependencies.Android.Compose.ACTIVITY)
    implementation(Dependencies.Android.Compose.MATERIAL3)
    implementation(Dependencies.Android.Compose.PAGING)
    implementation(Dependencies.Android.Compose.ICONS_EXTENDED)
    implementation(Dependencies.Android.Compose.PREVIEW)
    androidTestImplementation(Dependencies.Android.Compose.TESTING)

    implementation(Dependencies.Android.Compose.Accompanist.PAGER)
    implementation(Dependencies.Android.Compose.Accompanist.PAGER_INDICATOR)
    implementation(Dependencies.Android.Compose.Accompanist.SYSTEM_UI_CONTROLLER)

    implementation(Dependencies.Android.Paging.RUNTIME)

    implementation(Dependencies.Android.SplashScreen.SPLASHSCREEN)

    kapt(Dependencies.Android.Lifecycle.COMPILER)
    implementation(Dependencies.Android.Lifecycle.VIEWMODEL_COMPOSE)
    implementation(Dependencies.Android.Lifecycle.VIEWMODEL_KTX)


    ksp(Dependencies.ComposeDestinations.COMPILER)
    implementation(Dependencies.ComposeDestinations.CORE)


    kapt(Dependencies.Hilt.COMPILER)
    implementation(Dependencies.Hilt.HILT)
    implementation(Dependencies.Hilt.NAVIGATION_COMPOSE)

    implementation(Dependencies.OsmDroid.OSMDROID)

    implementation(Dependencies.MaterialDialogs.DATETIME)

    coreLibraryDesugaring(Dependencies.Android.CORE_LIBRARY_DESUGARING)

    testImplementation(kotlin("test"))
}