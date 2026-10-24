plugins {
    id("movies.android.library")
    id("movies.serialization")
}

android { namespace = "com.tetiana.movies.feature.search.domain" }

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    api(project(":core:domain"))
    api(libs.navigation.runtime)
    implementation(libs.serialization.json)
}
