plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    buildFeatures {
        buildConfig = true
    }
    namespace = "com.abwaalaa.monabehbasma"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.abwaalaa.monabehbasma"
        minSdk = 26
        targetSdk = 35
        versionCode = 142
        versionName = "2.4.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            val keyPath = System.getenv("ANDROID_KEYSTORE_PATH") ?: "release.keystore"
            val keyFile = file(keyPath)
            if (keyFile.exists()) {
                storeFile = keyFile
                storePassword = System.getenv("ANDROID_STORE_PASSWORD") ?: "basma12345"
                keyAlias = System.getenv("ANDROID_KEY_ALIAS") ?: "basma"
                keyPassword = System.getenv("ANDROID_KEY_PASSWORD") ?: "basma12345"
            }
        }
    }

    buildTypes {
        release {
            val releaseSigning = signingConfigs.getByName("release")
            if (releaseSigning.storeFile != null && releaseSigning.storeFile!!.exists()) {
                signingConfig = releaseSigning
            }
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        debug {
            applicationIdSuffix = ".debug"
            isDebuggable = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
}
