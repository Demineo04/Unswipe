plugins {
    // Try latest stable AGP (e.g., 8.4.0 or 8.4.1 if released and stable)
    id("com.android.application") version "8.9.1" apply false
    // Use latest stable Kotlin 1.9.x
    id("org.jetbrains.kotlin.android") version "2.1.20" apply false
    // Use KSP compatible with Kotlin 1.9.23
    id("com.google.devtools.ksp") version "2.1.20-1.0.32" apply false
    // Keep Hilt version
    id("com.google.dagger.hilt.android") version "2.50" apply false
    // Keep Google Services
    id("com.google.gms.google-services") version "4.4.1" apply false
    // Keep Serialization plugin matching Kotlin
    id("org.jetbrains.kotlin.plugin.serialization") version "2.1.20" apply false
    // Keep Compose plugin version matching Kotlin
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.20" apply false
}