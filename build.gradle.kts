// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    val kotlinVersion by extra("2.2.0")
    repositories {
        google()
        maven("https://jcenter.bintray.com")
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.11.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    }
}

allprojects {
    repositories {
        google()
        maven("https://jcenter.bintray.com")
        mavenCentral()
        maven { url = uri("https://maven.zohodl.com/") }
    }
}

tasks.register<Delete>("clean") {
    delete(fileTree(rootProject.buildDir) {
        exclude("ant.properties", "build.xml", "library.xml", "build.gradle", "lens_android.conf", "settings.xml")
    })
}
