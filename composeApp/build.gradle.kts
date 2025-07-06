import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.room)
}

kotlin {
    jvmToolchain(17)

    jvm {
        compilerOptions {
//            jvmTarget.set(JvmTarget.JVM_17)
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
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
        }
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}


compose.desktop {
    application {
        mainClass = "ru.llama.tool.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "LlamaTool"
            packageVersion = "1.0.0"
        }
    }
}

dependencies {
    "kspJvm"(libs.room.compiler)
}
