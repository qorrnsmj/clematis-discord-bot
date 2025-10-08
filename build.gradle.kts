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
    implementation("net.dv8tion:JDA:6.0.0")
    implementation("ch.qos.logback:logback-classic:1.5.19")
    implementation("io.github.kaktushose:jda-commands:v4.0.0-beta.3")
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
