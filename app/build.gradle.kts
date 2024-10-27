plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.test2"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.test2"
        minSdk = 30
        targetSdk = 34
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
    buildFeatures {
        viewBinding = true
        dataBinding = true
    }



}

dependencies {

    implementation(platform("com.google.firebase:firebase-bom:33.4.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("androidx.appcompat:appcompat:1.4.1") // AppCompat
    implementation("com.google.android.material:material:1.8.0") // Material Components
    implementation("androidx.constraintlayout:constraintlayout:2.1.4") // ConstraintLayout 추가
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.4.1") // LiveData
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.4.1") // ViewModel
    implementation("androidx.navigation:navigation-fragment-ktx:2.4.1") // Navigation Fragment
    implementation("androidx.navigation:navigation-ui-ktx:2.4.1") // Navigation UI
    testImplementation("junit:junit:4.13.2") // JUnit
    androidTestImplementation("androidx.test.ext:junit:1.1.3") // Android JUnit
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")



}

