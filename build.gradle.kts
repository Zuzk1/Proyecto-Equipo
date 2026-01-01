// Top-level build file where you can add configuration options common to all sub-projects/modules.
// build.gradle.kts (Project: ProyectoAula)
// Archivo: build.gradle.kts (el de la raíz del proyecto)

plugins {
    // Versión estable para máxima compatibilidad con Java 17 (estándar actual)
    id("com.android.application") version "8.2.0" apply false

    // Versión de Kotlin compatible con el compilador moderno
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false

    // Versión de kapt para procesamiento de anotaciones (Room/Glide)
    id("org.jetbrains.kotlin.kapt") version "1.9.0" apply false
}
