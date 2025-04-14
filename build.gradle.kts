// build.gradle.kts (Project: Unswipe) - ROOT LEVEL

plugins {
    // Use latest STABLE AGP (e.g., 8.3.2, check for the latest stable release)
    id("com.android.application") version "8.9.1" apply false
    // Use latest STABLE Kotlin before 2.0 (e.g., 1.9.22)
    id("org.jetbrains.kotlin.android") version "2.1.20" apply false
    id("com.google.devtools.ksp") version "2.1.20-1.0.32" apply false
    // Use latest STABLE Hilt (e.g., 2.51.1)
    id("com.google.dagger.hilt.android") version "2.51.1" apply false
    // Use latest STABLE Google Services (e.g., 4.4.1 or 4.4.2 if stable)
    id("com.google.gms.google-services") version "4.4.1" apply false
    // Match Kotlin version
    id("org.jetbrains.kotlin.plugin.serialization") version "2.1.20" apply false
    // Match Kotlin version
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.20" apply false
}