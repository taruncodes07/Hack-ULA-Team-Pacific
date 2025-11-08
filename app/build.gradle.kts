plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.myapplication2"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.myapplication2"
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
        isCoreLibraryDesugaringEnabled = true
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }

    // Explicitly specify jniLibs directory
    sourceSets {
        getByName("main") {
            jniLibs.srcDirs("src/main/jniLibs")
        }
    }

    // Disable APK splitting to ensure native libraries are included
    splits {
        abi {
            isEnable = false
        }
        density {
            isEnable = false
        }
    }

    androidResources {
        noCompress += listOf("gguf")
    }

    // Packaging options to handle duplicate files
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            pickFirst("META-INF/versions/9/OSGI-INF/MANIFEST.MF")
        }
        jniLibs {
            useLegacyPackaging = true
        }
    }
}

// Force Ktor version resolution
configurations.all {
    resolutionStrategy {
        force("io.ktor:ktor-client-core:3.0.1")
        force("io.ktor:ktor-client-okhttp:3.0.1")
        force("io.ktor:ktor-client-content-negotiation:3.0.1")
        force("io.ktor:ktor-client-logging:3.0.1")
        force("io.ktor:ktor-serialization-kotlinx-json:3.0.1")
        force("io.ktor:ktor-utils:3.0.1")
        force("io.ktor:ktor-http:3.0.1")
        force("io.ktor:ktor-io:3.0.1")
    }
}

dependencies {
    // RunAnywhere SDK - Local AARs
    implementation(files("libs/RunAnywhereKotlinSDK-release.aar"))
    implementation(files("libs/runanywhere-llm-llamacpp-release.aar"))

    // Required SDK dependencies
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.5.0")

    // Ktor for networking - Trying 3.0.1 for SDK compatibility
    implementation("io.ktor:ktor-client-core:3.0.1")
    implementation("io.ktor:ktor-client-okhttp:3.0.1")
    implementation("io.ktor:ktor-client-content-negotiation:3.0.1")
    implementation("io.ktor:ktor-client-logging:3.0.1")
    implementation("io.ktor:ktor-serialization-kotlinx-json:3.0.1")
    implementation("io.ktor:ktor-client-serialization:3.0.1")

    // OkHttp
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // Gson (already included, updating version)
    implementation("com.google.code.gson:gson:2.10.1")

    // Okio
    implementation("com.squareup.okio:okio:3.7.0")

    // AndroidX WorkManager
    implementation("androidx.work:work-runtime-ktx:2.9.0")

    // AndroidX Room
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")

    // AndroidX Security
    implementation("androidx.security:security-crypto:1.1.0-alpha06")

    // Existing dependencies
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation("androidx.navigation:navigation-compose:2.7.5")
    implementation("androidx.compose.material:material-icons-extended:1.5.4")
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}