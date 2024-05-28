import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
}

android {
    namespace = "com.spozebra.zebranfcticketdemo"
    compileSdk = 34

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        applicationId = "com.spozebra.zebranfcticketdemo"
        minSdk = 30
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField ("String", "APPLEVAS_PRIVATE_KEY", getKeyFromConfig("APPLEVAS_PRIVATE_KEY"))
        buildConfigField ("String", "GOOGLESMARTAPP_PRIVATE_KEY", getKeyFromConfig("GOOGLESMARTAPP_PRIVATE_KEY"))
        buildConfigField ("String", "GOOGLESMARTAPP_KEY_VERSION", getKeyFromConfig("GOOGLESMARTAPP_KEY_VERSION"))
        buildConfigField ("String", "GOOGLESMARTAPP_COLLECTOR_ID", getKeyFromConfig("GOOGLESMARTAPP_COLLECTOR_ID"))
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
}

dependencies {

    implementation(files("./../libs/zebranfcvas-release-2.0.1.aar"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

fun getKeyFromConfig(key : String) : String {
    val propFile = rootProject.file("./local.properties")
    val properties = Properties()
    properties.load(FileInputStream(propFile))
    return properties.getProperty(key)
}