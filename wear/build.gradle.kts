plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.wear"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.happykidneys"
        minSdk = 30
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

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
    buildFeatures {
        compose = false
    }
}

dependencies {

    implementation(libs.play.services.wearable)

    implementation(libs.androidx.wear.tooling.preview)
    implementation(libs.androidx.core.splashscreen)




    implementation("com.google.android.gms:play-services-wearable:18.1.0")
// Add these for the watch UI
    implementation("androidx.wear:wear:1.3.0")
    implementation("com.google.android.material:material:1.11.0")
}