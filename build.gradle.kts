plugins {
    kotlin("jvm") version "2.0.21"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "qorrnsmj"
version = "1.0.0"

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation("com.github.dv8fromtheworld:JDA:5.6.1")
    implementation("ch.qos.logback:logback-classic:1.5.19")
    implementation("com.github.Kaktushose:jda-commands:4.0.0-beta.8")
}

tasks {
    shadowJar {
        archiveBaseName = "Clematis"
        archiveVersion = "${rootProject.version}"
        archiveClassifier = ""
        manifest {
            attributes["Main-Class"] = "qorrnsmj.clematis.Clematis"
        }
    }
}
