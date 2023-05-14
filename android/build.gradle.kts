plugins {
    id("org.jetbrains.compose") version "1.0.0"
    id("com.android.application")
    kotlin("android")
}

group = "me.andr4"
version = "1.0"

repositories {
    jcenter()
}

dependencies {
    implementation(project(":common"))
    implementation("androidx.activity:activity-compose:1.4.0")
    implementation("androidx.navigation:navigation-compose:2.4.0-rc01")
    val activity_version = "1.4.0"

    // Java language implementation
    implementation("androidx.activity:activity:$activity_version")
    // Kotlin
    implementation("androidx.activity:activity-ktx:$activity_version")
    implementation("androidx.fragment:fragment:1.4.0")
    implementation("io.coil-kt:coil:1.4.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
}

android {
    compileSdkVersion(31)
    defaultConfig {
        applicationId = "me.andr4.android"
        minSdkVersion(24)
        targetSdkVersion(31)
        versionCode = 1
        versionName = "1.0"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
}