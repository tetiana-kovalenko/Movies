plugins {
    `kotlin-dsl`
}

kotlin { jvmToolchain(17) }

dependencies {
    implementation(libs.android.gradlePlugin)
    implementation(libs.kotlin.gradlePlugin)
    implementation(libs.compose.gradlePlugin)
    implementation(libs.hilt.gradlePlugin)
    implementation(libs.ksp.gradlePlugin)
    implementation(libs.serialization.gradlePlugin)
}
