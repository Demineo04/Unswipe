// settings.gradle.kts

pluginManagement {
    repositories {
        google() // Google's Maven repository (for Android plugins)
        mavenCentral() // Central repository
        gradlePluginPortal() // Gradle's own plugin portal
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS) // Good practice
    repositories {
        google()
        mavenCentral()
        // Add other repositories like JitPack if needed later
        // maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "Unswipe" // Sets your project name
include(":app") // Tells Gradle to include your 'app' module