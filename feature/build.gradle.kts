plugins { base }

tasks.named("assemble") {
    dependsOn(
        ":feature:search:impl:assemble",
        ":feature:favorites:impl:assemble",
        ":feature:details:impl:assemble",
    )
}
