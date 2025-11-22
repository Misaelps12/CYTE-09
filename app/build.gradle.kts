plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.cyte_09.ctye_09"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.cyte_09.ctye_09"
        minSdk = 31
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
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }

    dependencies {

        // Dependecias bases de android
        implementation(libs.appcompat)
        implementation(libs.material)
        implementation(libs.activity)
        implementation(libs.constraintlayout)

        // Testing
        testImplementation(libs.junit)
        androidTestImplementation(libs.ext.junit)
        androidTestImplementation(libs.espresso.core)

        //  Google Identity Services
        implementation("androidx.credentials:credentials:1.6.0-beta03")
        implementation("androidx.credentials:credentials-play-services-auth:1.6.0-beta03")
        implementation("com.google.android.libraries.identity.googleid:googleid:1.1.0")

        //  Jetpack Compose
        implementation("androidx.compose.ui:ui:1.9.4")
        implementation("androidx.compose.material3:material3:1.3.1")
        implementation("androidx.compose.ui:ui-tooling-preview:1.9.4")
        implementation("androidx.activity:activity-compose:1.11.0")

        // para modo debug
        debugImplementation("androidx.compose.ui:ui-tooling:1.9.4")
        debugImplementation("androidx.compose.ui:ui-test-manifest:1.9.4")

        //Dependecias fire Base
        implementation(platform("com.google.firebase:firebase-bom:34.6.0"))
        implementation("com.google.firebase:firebase-analytics")

    }
}
dependencies {
    implementation(libs.firebase.firestore)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
}
