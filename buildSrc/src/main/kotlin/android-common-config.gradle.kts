import com.android.build.gradle.BaseExtension

plugins {
    kotlin("android")
}

extensions.getByType<BaseExtension>().apply {
    compileSdkVersion(32)

    defaultConfig {
        minSdk = 23
        targetSdk = 32
    }


    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

}