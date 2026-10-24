plugins {
    id("movies.android.library")
    id("movies.hilt")
}

android { namespace = "com.tetiana.movies.sdk.launch" }

dependencies {
    implementation(project(":sdk"))
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.coroutines.core)
}
