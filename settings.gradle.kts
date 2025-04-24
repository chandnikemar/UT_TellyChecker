pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven {
            url = uri("https://jitpack.io")}
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://jitpack.io")}
        jcenter()
        maven {url =  uri("https://zebratech.jfrog.io/artifactory/EMDK-Android/")}
    }
}

rootProject.name = "UTTellyChecker"
include(":app")
 