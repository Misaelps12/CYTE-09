// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false

    //Idependecia para conectar los servicios de google a la app
    id("com.google.gms.google-services") version "4.4.4" apply false
}