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
        // Repositorio para la librería del calendario
        maven { url = uri("https://jitpack.io") }
    }
}
rootProject.name = "ProyectoAula"
include(":app")
    