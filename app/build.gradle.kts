// app/build.gradle.kts
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp") // Keep KSP (for Room)
    id("kotlin-kapt")             // Keep KAPT (for Hilt)
    id("com.google.dagger.hilt.android")
    id("com.google.gms.google-services")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.unswipe.android"
    // Align compileSdk with AGP 8.4.1 tested version
    compileSdk = 34

    defaultConfig {
        applicationId = "com.unswipe.android"
        minSdk = 24
        // Align targetSdk with compileSdk
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        // KSP arguments for Room schema location
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Core Android & Kotlin
    implementation("androidx.core:core-ktx:1.13.1") // Downgraded from 1.16.0
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7") // Note: Very new
    implementation("androidx.activity:activity-compose:1.9.0") // Downgraded from 1.10.1

    // Compose
    implementation(platform("androidx.compose:compose-bom:2025.04.00")) // Note: Very new BOM
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7") // Note: Very new

    // Material Components
    implementation("com.google.android.material:material:1.12.0")

    // Core Splash Screen API
    implementation("androidx.core:core-splashscreen:1.0.1")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.8.9") // Note: Very new

    // Hilt (Dependency Injection)
    implementation("com.google.dagger:hilt-android:2.51.1")
    kapt("com.google.dagger:hilt-compiler:2.51.1") // <-- Using KAPT for Hilt
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // Room (Local Database)
    implementation("androidx.room:room-runtime:2.7.1") // Note: Very new, consider 2.6.1
    implementation("androidx.room:room-ktx:2.7.1")     // Note: Very new, consider 2.6.1
    ksp("androidx.room:room-compiler:2.7.1")           // <-- Using KSP for Room

    // DataStore (Preferences)
    implementation("androidx.datastore:datastore-preferences:1.1.4") // Note: Very new, consider 1.0.0 or 1.1.1

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.12.0")) // Note: Very new BOM
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")

    // Google Play Billing
    implementation("com.android.billingclient:billing-ktx:6.1.0")

    // WorkManager (Background Tasks)
    implementation("androidx.work:work-runtime-ktx:2.9.0") // Downgraded from 2.10.0
    implementation("androidx.hilt:hilt-work:1.2.0")

    // Hilt Navigation Compose
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // Number Picker for Time selection
    implementation("com.chargemap.compose:numberpicker:1.0.3")

    // Accompanist for Drawable Painting
    implementation("com.google.accompanist:accompanist-drawablepainter:0.34.0")

    // Material Design
    implementation("androidx.compose.material:material-icons-extended")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1") // Note: Very new
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1") // Note: Very new
    androidTestImplementation(platform("androidx.compose:compose-bom:2025.04.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    // Hilt Testing
    androidTestImplementation("com.google.dagger:hilt-android-testing:2.51.1")
    kaptAndroidTest("com.google.dagger:hilt-compiler:2.51.1") // <-- Using KAPT for Hilt Test
    // Turbine for Flow testing
    testImplementation("app.cash.turbine:turbine:1.0.0") // Consider updating if needed
    // Mockito for mocking
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.2.1")
    testImplementation("org.mockito:mockito-inline:5.2.0")
}

// Ensure no lingering kapt { ... } block exists elsewhere if not needed