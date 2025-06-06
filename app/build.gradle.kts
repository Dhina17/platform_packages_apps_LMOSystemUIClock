/*
 * SPDX-FileCopyrightText: 2024-2025 The LibreMobileOS Foundation
 * SPDX-License-Identifier: Apache-2.0
 */

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
    compileSdk = 35

    defaultConfig {
        applicationId = "com.libremobileos.clock"
        minSdk = 34
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
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
    compileOnly(files("../system_libs/SystemUISharedLib.jar"))
    compileOnly(files("../system_libs/SystemUI-statsd.jar"))
    compileOnly(files("../system_libs/SystemUIPluginLib.jar"))
    implementation(files("../system_libs/PlatformAnimationLib.jar"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
}
