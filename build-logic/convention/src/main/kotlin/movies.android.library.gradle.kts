import com.tetiana.movies.buildlogic.AndroidConfig

plugins { id("com.android.library") }

android {
    compileSdk = AndroidConfig.COMPILE_SDK
    defaultConfig { minSdk = AndroidConfig.MIN_SDK }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin { jvmToolchain(17) }
