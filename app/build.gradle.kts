// Module-level build.gradle.kts (app)
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt) // Hilt Dependency Injection
    id("kotlin-kapt")

}

android {
    namespace = "com.example.mobilelogbook"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.mobilelogbook"
        minSdk = 26
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        compose = true // Активиране на Jetpack Compose
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.4"
    }
}

dependencies {
    // AndroidX Core и UI компоненти
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)

    // Навигация (Navigation Component)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // Lifecycle ViewModel & LiveData (Управление на UI данни)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)

    // Room Database (SQLite)
    implementation(libs.room.runtime)
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.androidx.foundation.android)
    implementation(libs.androidx.ui.android)
    implementation(libs.androidx.material3.android)
    kapt(libs.room.compiler)
    implementation(libs.room.ktx)

    // Retrofit (REST API за PostgreSQL или Supabase)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)

    // WorkManager (синхронизация)
    implementation(libs.workmanager)

    // Dependency Injection (Hilt)
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    // Jetpack Compose UI
    implementation(libs.compose.ui)
    implementation(libs.compose.material)
    implementation("androidx.compose.material3:material3:1.1.2")
    implementation(libs.compose.tooling.preview)
    implementation(libs.activity.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.datastore.preferences)
    implementation("androidx.compose.material:material-icons-extended")



    // Тестове
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.androidx.material3)

}
