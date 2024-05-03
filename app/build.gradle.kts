plugins {
    id("com.android.application")
    id("androidx.navigation.safeargs.kotlin") version "2.7.6" apply false
}

android {
    namespace = "com.example.escalaiv2"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.escalaiv2"
        minSdk = 33
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    dataBinding {
        enable = true
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation(files("libs\\jtds-1.3.1.jar"))
    implementation("androidx.databinding:databinding-runtime:8.3.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.legacy:legacy-support-core-utils:1.0.0")
    implementation("com.google.android.material:material:1.11.0")
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.3")
}