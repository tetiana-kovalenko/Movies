plugins {
    id("movies.android.feature")
    id("movies.hilt")
}

ksp { arg("room.schemaLocation", "$projectDir/schemas") }

android { namespace = "com.tetiana.movies.feature.favorites.impl" }

dependencies {
    implementation(libs.room.runtime)
    ksp(libs.room.compiler)
    implementation(project(":feature:favorites:domain"))
    implementation(libs.androidx.hilt.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.coroutines.core)
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
