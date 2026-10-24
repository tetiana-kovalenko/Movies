plugins {
    id("movies.android.library")
    id("movies.android.compose")
    id("movies.hilt")
}

android { namespace = "com.tetiana.movies.sdk.admob" }

dependencies {
    implementation(project(":sdk"))
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
    implementation(libs.compose.foundation)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.coroutines.android)
    implementation(libs.google.ads)
}
