pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // Jitpack for the calendar library
        maven { url = uri("https://jitpack.io") }
    }
}
rootProject.name = "PhotoStudio"
include(":app")
