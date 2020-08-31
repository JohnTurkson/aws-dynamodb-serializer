plugins {
    kotlin("jvm") version "1.4.0"
}

group = "com.johnturkson.awstools"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.0.0-RC")
}
