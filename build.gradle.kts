plugins {
    id("com.android.application") version "8.2.2" apply false // Use your Android Gradle plugin version
    id("org.jetbrains.kotlin.android") version "1.9.10" apply false // Use your Kotlin version
    id("com.google.dagger.hilt.android") version "2.50" apply false // Match Hilt version
    id("com.google.gms.google-services") version "4.4.1" apply false // Use latest google-services version
    // id("androidx.navigation.safeargs.kotlin") version "2.7.7" apply false // If using Safe Args
} 