// build.gradle.kts (Project: Unswipe) - ROOT LEVEL
plugins {
    id("com.android.application") version "8.9.1" apply false // Reverted
    id("org.jetbrains.kotlin.android") version "2.1.20" apply false // Reverted
    id("com.google.devtools.ksp") version "2.1.20-1.0.32" apply false // Reverted
    id("com.google.dagger.hilt.android") version "2.51.1" apply false // Kept stable Hilt
    id("com.google.gms.google-services") version "4.4.2" apply false // Kept Google Services
    // Use the correct serialization plugin ID and match Kotlin version
    id("org.jetbrains.kotlin.plugin.serialization") version "2.1.20" apply false // Reverted
    // Match Kotlin version
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.20" apply false // Reverted
}