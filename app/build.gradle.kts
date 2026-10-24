import java.util.Properties

plugins {
    id("movies.android.application")
    id("movies.android.compose")
    id("movies.hilt")
    id("movies.serialization")
}

val localConfiguration =
    Properties().apply {
        val file = rootProject.file("local.properties")
        if (file.exists()) file.inputStream().use { load(it) }
    }
val omdbApiKey = localConfiguration.getProperty("omdb.api.key", "").trim()

val adsConfiguration = Properties().apply {
    rootProject.file("ads.defaults.properties").inputStream().use { load(it) }
    putAll(localConfiguration)
}
val admobApplicationIdKey = "admob.application.id"
val admobNativeAdUnitIdKey = "admob.native.ad.unit.id"
val admobInterstitialAdUnitIdKey = "admob.interstitial.ad.unit.id"

fun Properties.buildConfigString(key: String): String {
    val value = getProperty(key).trim()
    return "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"") + "\""
}

android {
    namespace = "com.tetiana.movies"

    buildFeatures { buildConfig = true }

    defaultConfig {
        applicationId = "com.tetiana.movies"
        buildConfigField(
            "String",
            "OMDB_API_KEY",
            "\"" + omdbApiKey.replace("\\", "\\\\").replace("\"", "\\\"") + "\"",
        )
        buildConfigField("String", "ADMOB_APPLICATION_ID", adsConfiguration.buildConfigString(admobApplicationIdKey))
        buildConfigField("String", "ADMOB_NATIVE_AD_UNIT_ID", adsConfiguration.buildConfigString(admobNativeAdUnitIdKey))
        buildConfigField("String", "ADMOB_INTERSTITIAL_AD_UNIT_ID", adsConfiguration.buildConfigString(admobInterstitialAdUnitIdKey))
        versionCode = 1
        versionName = "1.0"

    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
        }
    }
}

dependencies {
    implementation(project(":feature:search:domain"))
    implementation(project(":feature:favorites:domain"))
    implementation(project(":feature:details:domain"))

    implementation(project(":feature:search:impl"))
    implementation(project(":feature:favorites:impl"))
    implementation(project(":feature:details:impl"))
    implementation(project(":core:domain"))
    implementation(project(":core:ui"))
    implementation(project(":sdk"))
    implementation(project(":sdk:admob"))
    implementation(project(":sdk:launch"))
    implementation(libs.androidx.startup.runtime)
    implementation(libs.navigation.runtime)
    implementation(libs.navigation.ui)
    implementation(libs.serialization.json)
    implementation(libs.compose.animation)
    implementation(libs.compose.icons)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.hilt.viewmodel.compose)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
