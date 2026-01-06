// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {

    id("com.android.application") version "8.9.2" apply false // Version anpassen
    id("org.jetbrains.kotlin.android") version "2.0.21" apply false // Version anpassen
    id("com.google.dagger.hilt.android") version "2.51.1" apply false // Version anpassen (muss zur dep passen)
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.21" apply false // Version anpassen
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.21" apply false // <-- DEINE KOTLIN VERSION

}