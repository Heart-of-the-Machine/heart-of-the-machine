// This is to suppress false warnings generated by a bug in IntelliJ
@file:Suppress("DSL_SCOPE_VIOLATION", "MISSING_DEPENDENCY_CLASS", "FUNCTION_CALL_EXPECTED", "PropertyName")

import com.matthewprenger.cursegradle.CurseProject
import com.matthewprenger.cursegradle.CurseRelation
import com.matthewprenger.cursegradle.CurseUploadTask
import com.matthewprenger.cursegradle.Options
import com.modrinth.minotaur.dependencies.DependencyType
import com.modrinth.minotaur.dependencies.ModDependency
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    `maven-publish`

    alias(libs.plugins.kotlin)
    alias(libs.plugins.quilt.loom)
    alias(libs.plugins.minotaur)
    alias(libs.plugins.cursegradle)
}

val releaseTag: String? = System.getenv("RELEASE_TAG")
if (releaseTag != null) {
    version = releaseTag.substring(1)
    println("Detected Release Version: $version")
} else {
    val local_version: String by project
    version = local_version
    println("Detected Local Version: $version")
}

val modid: String by project

val archives_base_name: String by project
base.archivesName.set(archives_base_name)

val javaVersion = 17

val genResDir = file("src/main/resources-generated")

sourceSets {
    main {
        resources {
            srcDir(genResDir)
        }
    }
}

loom {
    accessWidenerPath.set(file("src/main/resources/hotm.accesswidener"))

    runs {
        getByName("client") {
            programArgs("--width", "1920", "--height", "1080")
        }

        create("datagenClient") {
            inherit(getByName("client"))
            name("Data Generation")
            vmArg("-Dfabric-api.datagen")
            vmArg("-Dfabric-api.datagen.output-dir=${genResDir}")
            vmArg("-Dfabric-api.datagen.modid=${modid}")

            runDir("build/datagen")
        }
    }
}

repositories {
    maven("https://kneelawk.com/maven/") { name = "Kneelawk" }
    maven("https://maven.terraformersmc.com/releases/") { name = "TerraformersMC" }
    maven("https://maven.cafeteria.dev/releases/") {
        name = "Cafeteria"
        content {
            includeGroup("me.luligabi")
        }
    }
}

dependencies {
    minecraft(libs.minecraft)
    mappings(variantOf(libs.quilt.mappings) { classifier("intermediary-v2") })

    // Prevent this dependency from being commuted to our dependants, because it can cause version conflicts
    modCompileOnly(libs.quilt.loader)
    modLocalRuntime(libs.quilt.loader)

    // Quilted Fabric API will automatically pull in the correct QSL version.
    modCompileOnly(libs.bundles.libs.base)
    modLocalRuntime(libs.bundles.libs.base)

    // Exclude QSL from QKL
    modCompileOnly(libs.bundles.libs.kotlin) {
        exclude(group = "org.quiltmc.qsl")
    }
    modLocalRuntime(libs.bundles.libs.kotlin) {
        exclude(group = "org.quiltmc.qsl")
    }

    modImplementation(libs.bundles.libs.impl)
    include(libs.bundles.libs.impl)

    modLocalRuntime(libs.bundles.runtime.local) {
        exclude(group = "net.fabricmc")
        exclude(group = "net.fabricmc.fabric-api")
    }
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = javaVersion.toString()
            // languageVersion: A.B of the kotlin plugin version A.B.C
            languageVersion = libs.plugins.kotlin.get().version.requiredVersion.substringBeforeLast('.')
        }
    }

    withType<JavaCompile>.configureEach {
        options.encoding = "UTF-8"
        options.isDeprecation = true
        options.release.set(javaVersion)
    }

    processResources {
        filteringCharset = "UTF-8"
        inputs.property("version", project.version)

        filesMatching("quilt.mod.json") {
            expand(
                mapOf(
                    "version" to project.version
                )
            )
        }
    }

    javadoc {
        options.encoding = "UTF-8"
    }

    // Run `./gradlew wrapper --gradle-version <newVersion>` or `gradle wrapper --gradle-version <newVersion>` to update gradle scripts
    // BIN distribution should be sufficient for the majority of mods
    wrapper {
        distributionType = Wrapper.DistributionType.BIN
    }

    jar {
        from("LICENSE") {
            rename { "LICENSE_${archives_base_name}" }
        }
    }

    afterEvaluate {
        named("genSources") {
            setDependsOn(listOf("genSourcesWithQuiltflower"))
        }
    }
}

val targetJavaVersion = JavaVersion.toVersion(javaVersion)
if (JavaVersion.current() < targetJavaVersion) {
    kotlin.jvmToolchain(javaVersion)

    java.toolchain {
        languageVersion.set(JavaLanguageVersion.of(javaVersion))
    }
}

java {
    withSourcesJar()

    // Still required by IDEs such as Eclipse and VSC
    sourceCompatibility = targetJavaVersion
    targetCompatibility = targetJavaVersion
}

val commaRegex = Regex("\\s*,\\s*")

modrinth {
    token.set(System.getenv("MODRINTH_TOKEN"))
    val mr_project_id: String by project
    projectId.set(mr_project_id)
    versionNumber.set(project.version.toString())
    val mr_version_type: String by project
    versionType.set(mr_version_type)
    val changelogFile = file("changelogs/changelog-v${project.version}.md")
    if (changelogFile.exists()) {
        changelog.set(changelogFile.readText())
    }
    uploadFile.set(tasks.getByName("remapJar"))
    additionalFiles.add(tasks.getByName("sourcesJar"))
    val mr_game_versions: String by project
    gameVersions.addAll(mr_game_versions.split(commaRegex))
    val mr_loaders: String by project
    loaders.addAll(mr_loaders.split(commaRegex))
    val mr_dependencies: String by project
    dependencies.addAll(mr_dependencies.split(commaRegex).map { ModDependency(it, DependencyType.REQUIRED) })
    syncBodyFrom.set(file("README.md").readText())
}

val curseApiKey = System.getenv("CURSE_API_KEY")
if (curseApiKey != null) {
    val cf_project_id: String by project
    curseforge {
        apiKey = curseApiKey
        project(closureOf<CurseProject> {
            id = cf_project_id
            changelogType = "markdown"
            changelog = file("changelogs/changelog-v${project.version}.md")
            val cf_release_type: String by project
            releaseType = cf_release_type
            val cf_minecraft_versions: String by project
            for (version in cf_minecraft_versions.split(commaRegex)) {
                addGameVersion(version)
            }
            mainArtifact(tasks.getByName("remapJar"))
            addArtifact(tasks.getByName("sourcesJar"))
            val cf_dependencies: String by project
            relations(closureOf<CurseRelation> {
                for (dependency in cf_dependencies.split(commaRegex)) {
                    requiredDependency(dependency)
                }
            })
        })
        options(closureOf<Options> {
            forgeGradleIntegration = false
        })
    }

    afterEvaluate {
        tasks.named("curseforge$cf_project_id", CurseUploadTask::class) {
            dependsOn(tasks.getByName("remapJar"))
            doLast {
                file("curse-file-id.txt").writeText(mainArtifact.fileID.toString())
            }
        }
    }
}

// Configure the maven publication
publishing {
    publications {
        register<MavenPublication>("Maven") {
            from(components.getByName("java"))
        }
    }

    repositories {
        val publishRepo: String? = System.getenv("PUBLISH_REPO")
        if (publishRepo != null) {
            maven {
                name = "publishRepo"
                url = uri(publishRepo)
            }
        }
    }
}
