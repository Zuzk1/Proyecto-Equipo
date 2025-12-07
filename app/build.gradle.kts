plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.proyectoaula"
    compileSdk = 34 // La versión estándar y recomendada por Google

    defaultConfig {
        applicationId = "com.example.proyectoaula"
        minSdk = 26 // Mínimo para que funcionen las APIs de tiempo sin problemas
        targetSdk = 34 // La versión estándar
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
    // ¡¡ESTA LÍNEA ES IMPORTANTE!!
    // Le decimos a Gradle que no queremos fallar si hay conflictos de versión menores,
    // sino que resuelva usando la versión más nueva que encuentre.
    packagingOptions {
        resources.excludes.add("/META-INF/{AL2.0,LGPL2.1}")
    }
}

dependencies {
    // Librería del calendario que sí permite colorear
    implementation("com.github.prolificinteractive:material-calendarview:2.0.1")

    // Librerías de AndroidX súper comunes y estables
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.activity:activity:1.8.0")
    implementation("androidx.core:core-splashscreen:1.0.1")

    // Dependencias de prueba
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
    