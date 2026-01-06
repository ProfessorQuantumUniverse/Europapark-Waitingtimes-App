// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {

    id("com.android.application") version "8.9.2" apply false // Version anpassen
    id("org.jetbrains.kotlin.android") version "2.1.20" apply false // Version anpassen
    id("com.google.dagger.hilt.android") version "2.56.2" apply false // Version anpassen (muss zur dep passen)
    id("org.jetbrains.kotlin.plugin.serialization") version "2.1.20" apply false // Version anpassen
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.20" apply false // <-- DEINE KOTLIN VERSION
    id("com.google.devtools.ksp") version "2.1.20-1.0.31" apply false // KSP für Kotlin 2.1.20

}