import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.room)
}

kotlin {

    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    jvmToolchain(17)

    jvm {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }
    sourceSets {

        commonMain.dependencies {
            // Compose
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(compose.materialIconsExtended)

            // Koin
            implementation(libs.koin.core)

            //data store
            implementation(libs.androidx.data.store.preferences.core)

            // Coroutines
            implementation(libs.kotlinx.coroutines.core)

            // Ktor for networking
            implementation(libs.ktor.core)
            implementation(libs.ktor.negotiation)
            implementation(libs.ktor.logging)
            implementation(libs.ktor.serialization.kotlinx.json)

            // Kotlinx Serialization
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.collections.immutable)

            // Room Database
            implementation(libs.room.runtime)
            implementation(libs.sqlite.bundled)
            implementation(libs.sqlite)

            // Decompose for navigation
            api(libs.decompose.decompose)
            api(libs.decompose.extensionsComposeJetbrains)
            api(libs.essenty.lifecycle)
            api(libs.essenty.lifecycle.coroutines)
        }
        commonTest.dependencies {
//            implementation(libs.kotlin.test)
        }

        androidMain.dependencies {
            //splash screen
            implementation(libs.koin.android)
            implementation(libs.androidx.activityCompose)
            implementation(libs.androidx.core.splashscreen)

            implementation(libs.kotlinx.coroutines.android)
            implementation(libs.room.runtime.android)
            implementation(libs.room.gradle.plugin)

            implementation(libs.ktor.client.okhttp)


        }

        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
            implementation(libs.ktor.client.okhttp)

        }
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}


android {
    namespace = libs.versions.nameSpace.get()
    compileSdk = libs.versions.compileSDK.get().toInt()

    defaultConfig {
        minSdk = 26
        versionCode = 1
        versionName = libs.versions.appVersion.get()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    sourceSets["main"].apply {
        //manifest.srcFile("src/androidMain/AndroidManifest.xml")
        res.srcDirs("src/androidMain/res")
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        debug {
            isMinifyEnabled = false
        }

    }
}



compose.desktop {
    application {
        mainClass = "${libs.versions.nameSpace.get()}.MainKt"
        jvmArgs += listOf(
            "--add-opens=java.base/sun.misc=ALL-UNNAMED",
            "--add-opens=java.base/java.lang=ALL-UNNAMED"
        )

        nativeDistributions {
            modules("jdk.unsupported") // Добавляем модуль с Unsafe
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "LlamaTool"
            packageVersion = "1.0.0"

            windows {
                iconFile.set(File("src/commonMain/composeResources/drawable/app_icon.ico"))
            }

        }


    }
}

dependencies {
    "kspJvm"(libs.room.compiler)
    "kspAndroid"(libs.room.compiler)
}
