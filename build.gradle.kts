// build.gradle.kts (Project: Unswipe) - ROOT LEVEL
plugins {
    id("com.android.application") version "8.9.1" apply false // Keep stable AGP
    // Keep Kotlin RC version and align related plugins
    id("org.jetbrains.kotlin.android") version "2.2.0-Beta2" apply false
    // Use KSP built for Kotlin 2.0.0 (closest guess for compatibility)
    id("com.google.devtools.ksp") version "2.2.0-Beta2-2.0.1" apply false // <-- UPDATED KSP
    id("com.google.dagger.hilt.android") version "2.51.1" apply false // Keep stable Hilt
    id("com.google.gms.google-services") version "4.4.1" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "2.2.0-Beta2" apply false // Match Kotlin
    id("org.jetbrains.kotlin.plugin.compose") version "2.2.0-Beta2"  apply false // Match Kotlin
    id("org.jetbrains.kotlin.kapt") version "2.2.0-Beta2"  apply false // Match Kotlin
}