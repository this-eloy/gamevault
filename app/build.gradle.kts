import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
}
val localPropsFile = rootProject.file("local.properties")
val localProps = Properties().apply {
    if (localPropsFile.exists()) {
        localPropsFile.inputStream().use { load(it) }
    }
}

val requiredKeys = listOf("STEAM_API_KEY", "STEAM_ID")
val missingKeys = requiredKeys.filter { !localProps.containsKey(it) }
if (missingKeys.isNotEmpty() && localPropsFile.exists()) {
    val current = localPropsFile.readText()
    val prefix = if (current.isEmpty() || current.endsWith("\n")) "" else "\n"
    localPropsFile.appendText(prefix + missingKeys.joinToString("\n") { "$it=" } + "\n")
}

android {
    namespace = "com.example.gamevault"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.gamevault"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "STEAM_API_KEY", "\"${localProps.getProperty("STEAM_API_KEY", "").trim()}\"")
        buildConfigField("String", "STEAM_ID", "\"${localProps.getProperty("STEAM_ID", "").trim()}\"")
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
    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("io.coil-kt:coil:2.7.0")
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")
}
