object Dependencies {

    object Android {
        private const val ANDROID_GRADLE_PLUGIN_VERSION = "7.2.1"
        private const val CORE_VERSION = "1.8.0"
        private const val CORE_LIBRARY_DESUGARING_VERSION = "1.1.5"

        const val ANDROID_GRADLE_PLUGIN =
            "com.android.tools.build:gradle:$ANDROID_GRADLE_PLUGIN_VERSION"
        const val CORE = "androidx.core:core-ktx:$CORE_VERSION"
        const val CORE_KTX = "androidx.core:core-ktx:$CORE_VERSION"
        const val CORE_LIBRARY_DESUGARING = "com.android.tools:desugar_jdk_libs:$CORE_LIBRARY_DESUGARING_VERSION"

        object Compose {

            object Accompanist {
                private const val VERSION = "0.23.1"

                const val PAGER = "com.google.accompanist:accompanist-pager:$VERSION"
                const val PAGER_INDICATOR =
                    "com.google.accompanist:accompanist-pager-indicators:$VERSION"
                const val SYSTEM_UI_CONTROLLER =
                    "com.google.accompanist:accompanist-systemuicontroller:$VERSION"
            }

            private const val COMPOSE_VERSION = "1.2.0-rc02"
            private const val MATERIAL_VERSION = "1.0.0-alpha13"
            private const val ACTIVITY_VERSION = "1.4.0"
            private const val PAGING_VERSION = "1.0.0-alpha15"
            const val COMPILER_VERSION = "1.2.0-rc02"

            const val MATERIAL3 = "androidx.compose.material3:material3:$MATERIAL_VERSION"
            const val ACTIVITY = "androidx.activity:activity-compose:$ACTIVITY_VERSION"
            const val PAGING = "androidx.paging:paging-compose:$PAGING_VERSION"
            const val PREVIEW = "androidx.compose.ui:ui-tooling-preview:$COMPOSE_VERSION"
            const val ICONS_EXTENDED =
                "androidx.compose.material:material-icons-extended:$COMPOSE_VERSION"
            const val TESTING = "androidx.compose.ui:ui-test-junit4:$COMPOSE_VERSION"
        }

        object Lifecycle {
            private const val VERSION = "2.5.0"

            const val COMPILER = "androidx.lifecycle:lifecycle-compiler:$VERSION"
            const val VIEWMODEL_KTX = "androidx.lifecycle:lifecycle-viewmodel-ktx:$VERSION"
            const val VIEWMODEL_COMPOSE = "androidx.lifecycle:lifecycle-viewmodel-compose:$VERSION"
        }

        object Paging {
            private const val VERSION = "3.1.1"

            const val RUNTIME = "androidx.paging:paging-runtime:$VERSION"
        }

        object SplashScreen {
            private const val VERSION = "1.0.0-rc01"

            const val SPLASHSCREEN = "androidx.core:core-splashscreen:$VERSION"
        }
    }

    object ComposeDestinations {
        private const val VERSION = "1.5.12-beta"

        const val COMPILER = "io.github.raamcosta.compose-destinations:ksp:$VERSION"
        const val CORE = "io.github.raamcosta.compose-destinations:core:$VERSION"
    }

    object Hilt {
        private const val HILT_VERSION = "2.42"
        private const val HILT_NAVIGATION_COMPOSE_VERSION = "1.0.0"

        const val GRADLE_PLUGIN = "com.google.dagger:hilt-android-gradle-plugin:$HILT_VERSION"
        const val HILT = "com.google.dagger:hilt-android:$HILT_VERSION"
        const val COMPILER = "com.google.dagger:hilt-android-compiler:$HILT_VERSION"
        const val NAVIGATION_COMPOSE =
            "androidx.hilt:hilt-navigation-compose:$HILT_NAVIGATION_COMPOSE_VERSION"
    }

    object Room {
        private const val VERSION = "2.4.2"

        const val RUNTIME = "androidx.room:room-runtime:$VERSION"
        const val COMPILER = "androidx.room:room-compiler:$VERSION"
        const val KTX = "androidx.room:room-ktx:$VERSION"
    }

    object Kotlin {
        private const val KOTLIN_GRADLE_PLUGIN_VERSION = "1.6.21"
        private const val DATETIME_VERSION = "0.4.0"
        private const val COROUTINES_VERSION = "1.6.3"

        const val KOTLIN_GRADLE_PLUGIN =
            "org.jetbrains.kotlin:kotlin-gradle-plugin:$KOTLIN_GRADLE_PLUGIN_VERSION"
        const val DATETIME = "org.jetbrains.kotlinx:kotlinx-datetime:$DATETIME_VERSION"
        const val COROUTINES = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$COROUTINES_VERSION"
        const val COROUTINES_ANDROID =
            "org.jetbrains.kotlinx:kotlinx-coroutines-core:$COROUTINES_VERSION"
    }

    object OsmDroid {
        private const val VERSION = "6.1.13"

        const val OSMDROID = "org.osmdroid:osmdroid-android:$VERSION"
    }

    object MaterialDialogs {
        const val DATETIME =
            "com.github.N3-M3-S1S.compose-material-dialogs:datetime:jitpack-SNAPSHOT"
    }
}