// build.gradle.kts (Project: Unswipe) - ROOT LEVEL
plugins {
    id("com.android.application") version "8.4.1" apply false // Downgraded to stable AGP
    // Align all Kotlin related plugins to stable K2
    id("org.jetbrains.kotlin.android") version "2.0.0" apply false
    id("com.google.devtools.ksp") version "2.0.0-1.0.21" apply false // KSP version for Kotlin 2.0.0
    id("com.google.dagger.hilt.android") version "2.51.1" apply false // Keep stable Hilt
    id("com.google.gms.google-services") version "4.4.1" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.0" apply false // Match Kotlin
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.0" apply false // Match Kotlin
    id("org.jetbrains.kotlin.kapt") version "2.0.0" apply false // Match Kotlin
}