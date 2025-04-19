// settings.gradle.kts (Root project level)

pluginManagement { // <-- START Check this block
    repositories {
        google()         // <-- NEEDED for google plugins/artifacts
        mavenCentral()    // <-- NEEDED for many plugins/artifacts
        gradlePluginPortal() // <-- NEEDED for gradle plugins by ID
    }
} // <-- END Check this block

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "Unswipe" // Or your project name
include(":app")