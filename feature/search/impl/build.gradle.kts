plugins {
    id("movies.android.feature")
    id("movies.hilt")
    id("movies.serialization")
}

android { namespace = "com.tetiana.movies.feature.search.impl" }

dependencies {
    constraints {
        implementation(libs.androidx.annotation.experimental) {
            because("Fixes false opt-in diagnostics on generated Kotlin serializers")
        }
    }
    implementation(libs.retrofit)
    implementation(libs.okhttp)
    implementation(libs.retrofit.serialization)
    implementation(libs.serialization.json)
    implementation(project(":feature:favorites:domain"))
    implementation(project(":feature:search:domain"))
    implementation(project(":sdk"))
    implementation(libs.androidx.hilt.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.compose.icons)
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)
    implementation(libs.paging.runtime)
    implementation(libs.paging.compose)
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
