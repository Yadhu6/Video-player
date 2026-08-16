plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.chinnu.pocketplayer"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.chinnu.pocketplayer"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures { viewBinding = false }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
}

dependencies {
    implementation("androidx.recyclerview:recyclerview:1.4.0")
    implementation("androidx.media3:media3-exoplayer:1.11.0")
    implementation("androidx.media3:media3-ui:1.11.0")
}
