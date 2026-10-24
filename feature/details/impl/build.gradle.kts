plugins {
    id("movies.android.feature")
    id("movies.hilt")
}

android { namespace = "com.tetiana.movies.feature.details.impl" }


dependencies {
    implementation(project(":feature:favorites:domain"))
    implementation(project(":feature:details:domain"))
    implementation(libs.androidx.hilt.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.coroutines.core)

    implementation(libs.compose.icons)
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
