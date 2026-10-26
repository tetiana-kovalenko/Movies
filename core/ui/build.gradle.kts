plugins {
    id("movies.android.library")
    id("movies.android.compose")
    id("movies.hilt")
}

android { namespace = "com.tetiana.movies.core.ui" }

dependencies {
    implementation(project(":core:domain"))
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.material3)
    implementation(libs.compose.foundation)
    implementation(libs.compose.icons)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
