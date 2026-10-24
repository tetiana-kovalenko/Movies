import org.gradle.api.artifacts.VersionCatalogsExtension

plugins {
    id("movies.android.library")
    id("movies.android.compose")
}

val libraries = extensions.getByType<VersionCatalogsExtension>().named("libs")

dependencies {
    add("implementation", project(":core:domain"))
    add("implementation", project(":core:ui"))
    add("implementation", platform(libraries.findLibrary("androidx-compose-bom").get()))
    add("implementation", libraries.findLibrary("androidx-compose-material3").get())
    add("implementation", libraries.findLibrary("compose-foundation").get())
}
