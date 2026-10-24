val androidModules =
    listOf(
        ":app",
        ":feature:search:domain",
        ":feature:search:impl",
        ":feature:favorites:domain",
        ":feature:favorites:impl",
        ":feature:details:domain",
        ":feature:details:impl",
        ":core:ui",
        ":sdk",
        ":sdk:admob",
        ":sdk:launch",
    )

tasks.register("checkQuality") {
    group = "verification"
    description = "Runs Android lint across production Android modules."
    dependsOn(androidModules.map { "$it:lintDebug" })
}

tasks.register("verifyProject") {
    group = "verification"
    dependsOn("checkQuality", ":app:assembleDebug", ":app:assembleRelease")
}
