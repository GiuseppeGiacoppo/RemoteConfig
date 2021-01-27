plugins {
    kotlin("jvm") version "1.4.0"
}

repositories {
    maven(url = "https://jitpack.io")
}

dependencies {
    implementation(project(":remoteconfig"))
}
