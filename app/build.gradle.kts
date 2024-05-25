import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties()
keystoreProperties.load(FileInputStream(keystorePropertiesFile))

android {
    namespace = "com.libremobileos.clock"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.libremobileos.clock"
        minSdk = 34
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    signingConfigs {
        getByName("debug") {
            keyAlias = keystoreProperties["keyAlias"] as String
            keyPassword = keystoreProperties["keyPassword"] as String
            storeFile = file(keystoreProperties["storeFile"] as String)
            storePassword = keystoreProperties["storePassword"] as String
        }
    }
    configurations {
        all { // You should exclude one of them not both of them
            exclude(group = "com.google.errorprone", module = "error_prone_annotations")
        }
    }
}

dependencies {

    // System libs
    compileOnly(files("../system_libs/framework.jar"))
    implementation(files("../system_libs/SystemUISharedLib.jar"))
    implementation(files("../system_libs/SystemUI-statsd.jar"))
    implementation(project(":SystemUIAnimationLib"))
    implementation(project(":SystemUICustomizationLib"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
}