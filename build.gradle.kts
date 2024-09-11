buildscript {
    repositories {
        google()
    }
    dependencies {
        val nav_version = "2.8.0"
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$nav_version")
    }
}


// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id ("com.google.gms.google-services") version "4.4.2" apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.android.application) apply false
}