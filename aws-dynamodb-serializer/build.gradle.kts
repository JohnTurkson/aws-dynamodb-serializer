plugins {
    kotlin("jvm")
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

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    
    jar {
        configurations.runtimeClasspath.get().forEach { file -> from(zipTree(file.absoluteFile)) }
    }
}
