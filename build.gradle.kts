import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    `java-library`
}

subprojects {
    group = "org.anvilpowered"
    version = "0.4.0-SNAPSHOT"
    repositories {
        mavenLocal()
        mavenCentral()
        maven("https://oss.sonatype.org/content/repositories/snapshots")
        maven("https://repo.spongepowered.org/repository/maven-public/")
        maven("https://packages.jetbrains.team/maven/p/xodus/xodus-daily")
        maven("https://papermc.io/repo/repository/maven-public/")
        maven("https://jetbrains.bintray.com/xodus")
    }
    project.findProperty("buildNumber")
        ?.takeIf { version.toString().contains("SNAPSHOT") }
        ?.also { version = version.toString().replace("SNAPSHOT", "RC$it") }
    tasks {
        withType<KotlinCompile> {
            kotlinOptions.jvmTarget = "17"
            kotlinOptions.freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
        }
        withType<JavaCompile> {
            targetCompatibility = "17"
            sourceCompatibility = "17"
        }
    }
}