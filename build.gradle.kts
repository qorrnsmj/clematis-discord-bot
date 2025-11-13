plugins {
    kotlin("jvm") version "2.2.20"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "qorrnsmj"
version = "1.1.5"

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation("net.dv8tion:JDA:5.6.1")
    implementation("io.github.kaktushose:jda-commands:v4.0.0-beta.3")

    implementation("ch.qos.logback:logback-classic:1.5.19")
    implementation("com.google.code.gson:gson:2.13.2")
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
