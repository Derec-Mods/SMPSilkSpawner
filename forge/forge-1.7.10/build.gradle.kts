plugins {
    id("com.gtnewhorizons.gtnhconvention")
}

tasks.matching { it.name.startsWith("spotless") }.configureEach {
    enabled = false
}

tasks.withType<AbstractArchiveTask>().configureEach {
    archiveBaseName.set(project.property("archives_base_name") as String)
    archiveVersion.set("${project.property("mod_version")}-${project.property("minecraft_version")}-${project.property("modloader")}")
}
