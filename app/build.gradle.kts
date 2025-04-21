// app/build.gradle.kts

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id("com.google.gms.google-services")
    // Use the correct plugin ID corresponding to the root definition
    id("org.jetbrains.kotlin.plugin.serialization") // Correct ID
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.unswipe.android"
    compileSdk = 35
    // Or latest stable

    defaultConfig {
        applicationId = "com.unswipe.android"
        minSdk = 24 // Required for UsageStatsManager effectively, Accessibility APIs improve
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        // KSP arguments for Room schema location (Optional but recommended)
        // This tells Room where to export the schema file during builds
        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17 // Use Java 17+
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Versions are now hardcoded below

    // Core Android & Kotlin
    implementation("androidx.core:core-ktx:1.16.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.activity:activity-compose:1.10.1")

    // Compose
    implementation(platform("androidx.compose:compose-bom:2025.04.00")) // Compose BOM version hardcoded
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")

    // Material Components (Provides Material 3 Themes)
    implementation("com.google.android.material:material:1.12.0")

    // Core Splash Screen API
    implementation("androidx.core:core-splashscreen:1.0.1")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.8.9")

    // Hilt (Dependency Injection)
    implementation("com.google.dagger:hilt-android:2.51.1")         // Hilt version hardcoded
    ksp("com.google.dagger:hilt-compiler:2.51.1")                   // Hilt version hardcoded
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0") // AndroidX Hilt version hardcoded

    // Room (Local Database)
    implementation("androidx.room:room-runtime:2.7.0")              // Room version hardcoded
    implementation("androidx.room:room-ktx:2.7.0")                  // Room version hardcoded
    ksp("androidx.room:room-compiler:2.7.0")                        // Room version hardcoded

    // DataStore (Preferences)
    implementation("androidx.datastore:datastore-preferences:1.1.4")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.12.0"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")

    // Google Play Billing
    implementation("com.android.billingclient:billing-ktx:6.1.0")

    // WorkManager (Background Tasks)
    implementation("androidx.work:work-runtime-ktx:2.10.0")
    implementation("androidx.hilt:hilt-work:1.2.0")                 // AndroidX Hilt version hardcoded

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2025.04.00")) // Compose BOM version hardcoded
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    // Hilt Testing
    androidTestImplementation("com.google.dagger:hilt-android-testing:2.51.1") // Hilt version hardcoded
    kspAndroidTest("com.google.dagger:hilt-compiler:2.51.1")                  // Hilt version hardcoded
    // Turbine for Flow testing
    testImplementation("app.cash.turbine:turbine:1.0.0")
    // Mockito for mocking
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.2.1")
    testImplementation("org.mockito:mockito-inline:5.2.0")
}

// Ensure no lingering kapt { ... } block exists