plugins {
    id("movies.android.library")
    id("movies.android.compose")
    id("movies.hilt")
}

android { namespace = "com.tetiana.movies.sdk" }

dependencies {
    api(platform(libs.androidx.compose.bom))
    api(libs.androidx.compose.ui)
    api(libs.coroutines.core)
    implementation(libs.coroutines.android)
}
