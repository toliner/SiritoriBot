import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        gradlePluginPortal()
        jcenter()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-serialization:1.3.11")
    }
}

plugins {
    kotlin("jvm") version "1.3.11"
    id("kotlinx-serialization") version "1.3.11"
}

group = "toliner.discord"
version = "0.1.0"

repositories {
    mavenCentral()
    jcenter()
    maven(url = "https://kotlin.bintray.com/kotlinx")
    maven(url = "https://jitpack.io")
}

dependencies {
    compile(kotlin("stdlib-jdk8"))
    compile("net.dv8tion:JDA:3.8.1_448") {
        exclude(module = "opus-java")
    }
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.0.1")
    compile("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.9.1")
    compile(group = "com.nativelibs4java", name = "bridj", version = "0.7.0")
    implementation("com.github.takscape:cmecab-java:2.1.0")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}