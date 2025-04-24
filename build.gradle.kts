// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()

        maven {
            url = uri("https://jitpack.io") // Correct way to specify the URL
        }
        jcenter()
        maven {url =  uri("https://zebratech.jfrog.io/artifactory/EMDK-Android/")}
    }
    dependencies {
        val nav_version = "2.5.0"
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$nav_version")
        classpath ("com.android.tools.build:gradle:8.0.0")  // Ensure this is the latest version




    }
}
plugins {
    id("com.android.application") version "8.1.2" apply false
    id("com.android.library") version "8.1.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
}