plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")  // Adicione este plugin
    id("kotlin-kapt")  // Adicione este para data binding
}

android {
    namespace = "com.ufrj.escalaiv2"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.ufrj.escalaiv2"
        minSdk = 33
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

    buildFeatures {
        dataBinding = true  // Forma correta de habilitar data binding no Kotlin DSL
        viewBinding = true  // Forma correta de habilitar view binding no Kotlin DSL
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    annotationProcessor("androidx.room:room-compiler:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.0")
    implementation(files("libs\\jtds-1.3.1.jar"))
    implementation("androidx.databinding:databinding-runtime:8.8.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    implementation("androidx.core:core-ktx:1.10.0")
    implementation("androidx.legacy:legacy-support-core-utils:1.0.0")
    implementation("com.google.android.material:material:1.12.0")
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.4")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.0")
    implementation("androidx.room:room-runtime:$roomVersion")
    annotationProcessor("androidx.room:room-compiler:$roomVersion")
    compileOnly("com.google.dagger:dagger:2.28.3")
    //annotationProcessor("android.arch.persistence.room:runtime:1.1.1")
    //annotationProcessor("android.arch.persistence.room:compiler:1.1.1")
    implementation("android.arch.lifecycle:extensions:1.1.1")
}