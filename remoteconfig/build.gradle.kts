plugins {
    kotlin("jvm") version "1.4.0"
    id("maven-publish")
}

group = rootProject.ext.get("projectGroup") as String
version = rootProject.ext.get("projectVersion") as String

repositories {
    maven(url = "https://jitpack.io")
}

dependencies {
    api ("com.github.GiuseppeGiacoppo:RemoteConfigCore:1.0.0")
    implementation("com.squareup.okhttp3:okhttp:4.9.0")
    implementation("com.google.code.gson:gson:2.8.4")

    testImplementation("junit:junit:4.13.1")
    testImplementation("org.mockito:mockito-core:3.3.3")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.9.0")
}

val sourcesJar by tasks.creating(Jar::class) {
    archiveClassifier.set("sources")
    from(project.the<SourceSetContainer>()["main"].allSource)
}

publishing {
    publications {
        create<MavenPublication>(rootProject.ext.get("projectName") as String) {
            groupId = rootProject.ext.get("projectGroup") as String
            artifactId = rootProject.ext.get("projectArtifact") as String
            version = rootProject.ext.get("projectVersion") as String

            artifact("$buildDir/libs/$artifactId-$version.jar")
            artifact(sourcesJar)

            pom.withXml {
                asNode().apply {
                    appendNode("name", rootProject.ext.get("projectName"))
                    appendNode("description", rootProject.ext.get("projectDescription"))
                    appendNode("url", rootProject.ext.get("githubProjectLink"))
                    appendNode("developers").appendNode("developer").apply {
                        appendNode("id", rootProject.ext.get("githubName"))
                        appendNode("name", "Giuseppe Giacoppo")
                        appendNode("organizationUrl", rootProject.ext.get("githubLink"))
                    }
                }
            }
        }
    }
}

val deploy by tasks.creating(GradleBuild::class) {
    tasks = listOf("clean", "assemble")
}

buildscript {
    val projectName by rootProject.extra("RemoteConfig")
    val githubName by rootProject.extra("GiuseppeGiacoppo")
    val projectVersion by rootProject.extra("2.0.0")
    val projectGroup by rootProject.extra("com.github.$githubName")
    val projectArtifact by rootProject.extra("$projectName")

    val projectDescription by rootProject.extra("RemoteConfig is a Kotlin library that lets you manage all your remote configuration without requiring developers to manually download each configuration and integrate them into the Kotlin application.")
    val githubLink by rootProject.extra("https://github.com/$githubName")
    val githubProjectName by rootProject.extra("$projectName")
    val githubProjectLink by rootProject.extra("$githubLink/$githubProjectName")
    val gitProjectLink by rootProject.extra("$githubProjectLink.git")

    val licenseName by rootProject.extra("The Apache Software License, Version 2.0")
    val licenseUrl by rootProject.extra("http://www.apache.org/licenses/LICENSE-2.0.txt")
}
