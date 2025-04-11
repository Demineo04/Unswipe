// build.gradle.kts (Project: Unswipe) - ROOT LEVEL

plugins {
    id("com.android.application") version "8.9.1" apply false
    id("org.jetbrains.kotlin.android") version "2.0.21" apply false
    id("com.google.dagger.hilt.android") version "2.50" apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
    // id("androidx.navigation.safeargs.kotlin") version "2.7.7" apply false

    // --- ADD THIS LINE BELOW ---
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.10" apply false
    // --- MAKE SURE THE VERSION "1.9.10" MATCHES YOUR KOTLIN VERSION ABOVE ---

}