// app/build.gradle.kts

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    // Removed: id("kotlin-kapt")
    id("com.google.devtools.ksp") // <-- ADDED KSP Plugin
    id("com.google.dagger.hilt.android")
    id("com.google.gms.google-services") // Firebase
    id("kotlinx-serialization") // For DataStore (optional, if using with serialization)
    id("org.jetbrains.kotlin.plugin.compose")
    // id("androidx.navigation.safeargs.kotlin") // If using Safe Args with Navigation
}

android {
    namespace = "com.unswipe.android"
    compileSdk = 34 // Or latest stable

    defaultConfig {
        applicationId = "com.unswipe.android"
        minSdk = 24 // Required for UsageStatsManager effectively, Accessibility APIs improve
        targetSdk = 34
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
    buildFeatures {
        compose = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    // composeOptions block should be removed if using Kotlin Compose Plugin
}

dependencies {
    // Define versions consistently
    val hilt_version = "2.51.1" // Use the version defined in root build.gradle.kts
    val room_version = "2.6.1" // Use latest stable Room version
    val androidx_hilt_version = "1.2.0" // Use latest stable AndroidX Hilt extensions version
    val compose_bom_version = "2024.02.01" // Use latest stable Compose BOM version

    // Core Android & Kotlin
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")

    // Compose
    implementation(platform("androidx.compose:compose-bom:$compose_bom_version"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

    // Material Components (Provides Material 3 Themes)
    implementation("com.google.android.material:material:1.11.0") // Use latest stable version

    // Core Splash Screen API
    implementation("androidx.core:core-splashscreen:1.0.1") // Use latest stable version

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.7") // Use latest stable version

    // Hilt (Dependency Injection)
    implementation("com.google.dagger:hilt-android:$hilt_version")
    ksp("com.google.dagger:hilt-compiler:$hilt_version") // <-- Use ksp
    implementation("androidx.hilt:hilt-navigation-compose:$androidx_hilt_version")

    // Room (Local Database)
    implementation("androidx.room:room-runtime:$room_version")
    implementation("androidx.room:room-ktx:$room_version")
    ksp("androidx.room:room-compiler:$room_version") // <-- Use ksp

    // DataStore (Preferences)
    implementation("androidx.datastore:datastore-preferences:1.0.0") // Use latest stable version

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:32.8.0")) // Use latest BOM
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")

    // Google Play Billing
    implementation("com.android.billingclient:billing-ktx:6.1.0") // Use latest stable version

    // WorkManager (Background Tasks)
    implementation("androidx.work:work-runtime-ktx:2.9.0") // Use latest stable version
    implementation("androidx.hilt:hilt-work:$androidx_hilt_version")
    ksp("androidx.hilt:hilt-compiler:$androidx_hilt_version") // <-- Use ksp for androidx hilt compiler

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:$compose_bom_version"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    // Hilt Testing
    androidTestImplementation("com.google.dagger:hilt-android-testing:$hilt_version")
    kspAndroidTest("com.google.dagger:hilt-compiler:$hilt_version") // <-- Use kspAndroidTest
    // Turbine for Flow testing
    testImplementation("app.cash.turbine:turbine:1.0.0") // Or latest version
    // Mockito for mocking
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.2.1") // Or latest version
    testImplementation("org.mockito:mockito-inline:5.2.0") // Or latest version
}

// Remove the kapt { ... } block entirely