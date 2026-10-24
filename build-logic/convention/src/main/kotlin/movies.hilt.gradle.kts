import org.gradle.api.artifacts.VersionCatalogsExtension

plugins {
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
}

val libraries = extensions.getByType<VersionCatalogsExtension>().named("libs")

dependencies {
    add("implementation", libraries.findLibrary("hilt-android").get())
    add("ksp", libraries.findLibrary("hilt-compiler").get())
}
