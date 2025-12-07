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
        // ¡ESTA LÍNEA ES CRUCIAL PARA LA LIBRERÍA DEL CALENDARIO!
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "ProyectoAula"
include(":app")
    