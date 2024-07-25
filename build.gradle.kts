// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    kotlin("jvm") version "2.0.0"
    id("org.jetbrains.compose") version "1.6.20-dev1646"
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.0"
    id("com.google.dagger.hilt.android") version "2.48" apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
}
buildscript {
    //ext (kotlin_version="2.0.0")
    repositories {
        maven {
            url = uri("https://plugins.gradle.org/m2/")
        }
        google()
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.0.0")
        classpath("org.jetbrains.compose:compose-gradle-plugin:1.6.20-dev1646")
        classpath("com.android.tools.build:gradle:8.5.0")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.48")
        //classpath("com.google")
        classpath("com.google.gms:google-services:4.4.2")
    }
}