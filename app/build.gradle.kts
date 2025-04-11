plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt") // For Room/Hilt annotation processing
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
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3" // Check for latest compatible version
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Core Android & Kotlin
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2024.02.01")) // Use latest BOM
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // Hilt (Dependency Injection)
    implementation("com.google.dagger:hilt-android:2.50") // Check latest Hilt version
    kapt("com.google.dagger:hilt-compiler:2.50")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0") // Hilt integration for Compose Navigation

    // Room (Local Database)
    implementation("androidx.room:room-runtime:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1") // Coroutines support

    // DataStore (Preferences / Proto)
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    // implementation("androidx.datastore:datastore-core:1.0.0") // If using Proto DataStore
    // implementation("com.google.protobuf:protobuf-javalite:3.25.1") // If using Proto DataStore
    // implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2") // If using DataStore with Kotlinx Serialization

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:32.7.4")) // Use latest BOM
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")

    // Google Play Billing
    implementation("com.android.billingclient:billing-ktx:6.1.0") // Check latest version

    // WorkManager (Background Tasks)
    implementation("androidx.work:work-runtime-ktx:2.9.0")
    implementation("androidx.hilt:hilt-work:1.1.0") // Hilt integration for WorkManager
    kapt("androidx.hilt:hilt-compiler:1.1.0")

    // Charting (Optional - consider Compose alternatives or libraries like MPAndroidChart with Compose wrappers)
    // implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.02.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    // Hilt Testing
    androidTestImplementation("com.google.dagger:hilt-android-testing:2.50")
    kaptAndroidTest("com.google.dagger:hilt-compiler:2.50")
    // Turbine for Flow testing
    testImplementation("app.cash.turbine:turbine:1.0.0")
     // Mockito for mocking
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.2.1")
    testImplementation("org.mockito:mockito-inline:5.2.0") // For mocking final classes/methods
}

// Allow Hilt to process annotations in generated sources
kapt {
    correctErrorTypes = true
} 