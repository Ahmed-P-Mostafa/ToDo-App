plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
    alias(libs.plugins.room)
    id("kotlin-parcelize")
    id("kotlin-kapt")
    id("androidx.navigation.safeargs")
    alias(libs.plugins.google.gms.google.services)
}

android {

    compileSdk = 34
    namespace = "com.polotika.todoapp"

    buildFeatures {
        dataBinding = true
    }

    defaultConfig {
        applicationId = "com.polotika.todoapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 6
        versionName = "1.1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        getByName("debug") {
            isDebuggable = true
        }

    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    room {
        schemaDirectory("$projectDir/schemas")
    }
}

kapt {
    correctErrorTypes = true
}

dependencies {

    implementation(libs.kotlin.stdlib)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.legacy.support.v4)
    implementation(libs.core.ktx)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.core)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Dagger - Hilt
    /*implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)
    kapt("androidx.hilt:hilt-compiler:2.42")
    implementation(libs.androidx.hilt.navigation.compose)*/
    implementation( libs.hilt.android)
    kapt( libs.hilt.android.compiler)



    // Room
    implementation(libs.room.runtime)
    annotationProcessor(libs.room.compiler)
    kapt(libs.room.compiler)
    implementation(libs.room.ktx)

    //Guide View
    implementation(libs.showcaseview)

    //DataStore
    implementation(libs.androidx.datastore.preferences)

    //Firebase
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.messaging)

    // Lifecycle components
    implementation(libs.androidx.lifecycle.extensions)
    implementation(libs.androidx.lifecycle.common.java8)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)

    //Navigation Component
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui)

}