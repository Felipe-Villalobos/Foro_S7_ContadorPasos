
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose.compiler)

}

android {
    namespace = "com.nrc3319.foro_s7_contadorpasos"
    compileSdk = 36


    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1" // Esta versión es importante
    }



    defaultConfig {
        applicationId = "com.nrc3319.foro_s7_contadorpasos"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // --- DEPENDENCIAS DE JETPACK COMPOSE ---
    val composeBom = platform("androidx.compose:compose-bom:2024.06.00") // El "Bill of Materials" gestiona las versiones
    implementation(composeBom)
    androidTestImplementation(composeBom)

    // Dependencias fundamentales de Compose para UI y Material Design 3
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")

    // Integración de Compose con la Actividad
    implementation("androidx.activity:activity-compose:1.8.2")

    // Para que las vistas previas funcionen en Android Studio
    debugImplementation("androidx.compose.ui:ui-tooling")

}