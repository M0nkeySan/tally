import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.sqldelight)
}

sqldelight {
    databases {
        create("TallyDatabase") {
            packageName.set("io.github.m0nkeysan.tally.database")
            generateAsync = true
        }
    }
}

kotlin {
    androidTarget()

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    @OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
    wasmJs {
        browser {
            commonWebpackConfig {
                cssSupport {
                    enabled.set(true)
                }
            }
        }
        binaries.executable()
    }

    compilerOptions {
        optIn.add("kotlin.uuid.ExperimentalUuidApi")
        optIn.add("androidx.compose.material3.ExperimentalMaterial3Api")
        optIn.add("androidx.compose.foundation.ExperimentalFoundationApi")
        optIn.add("androidx.compose.ui.ExperimentalComposeUiApi")
        optIn.add("kotlinx.cinterop.ExperimentalForeignApi")
        optIn.add("kotlin.js.ExperimentalWasmJsInterop")

        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    sourceSets {
        androidMain.dependencies {
            implementation(libs.androidx.navigationevent)
            implementation(libs.androidx.activity.compose)
            implementation(libs.koin.android)
            implementation(libs.sqldelight.android)
        }

        iosMain.dependencies {
            implementation(libs.androidx.navigationevent)
            implementation(libs.sqldelight.native)
        }

        wasmJsMain.dependencies {
            implementation(libs.sqldelight.webworker)
            implementation(npm("@sqlite.org/sqlite-wasm", "3.51.2-build5"))
            implementation(devNpm("copy-webpack-plugin", "13.0.1"))
        }

        commonMain.dependencies {
            api(libs.compose.runtime)
            api(libs.compose.foundation)
            api(libs.compose.ui)
            api(libs.compose.components)
            api(libs.compose.material3)
            api(libs.compose.materialIconsExtended)
            api(libs.compose.uiToolingPreview)

            implementation(libs.jetbrains.lifecycle.viewmodelCompose)
            implementation(libs.jetbrains.lifecycle.runtimeCompose)
            implementation(libs.jetbrains.navigation.compose)
            implementation(libs.jetbrains.compose.uiBackhandler)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.koin.core)
            implementation(libs.koin.compose)

            implementation(libs.sqldelight.runtime)
            implementation(libs.sqldelight.coroutines)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }

    targets
        .withType<KotlinNativeTarget>()
        .matching { it.konanTarget.family.isAppleFamily }
        .configureEach {
            binaries {
                framework {
                    baseName = "ComposeApp"
                    isStatic = true
                }
            }
        }
}

android {
    namespace = "io.github.m0nkeysan.tally"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "io.github.m0nkeysan.tally"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()

        val envVersionName = System.getenv("VERSION_TAG")?.removePrefix("v") ?: "1.0.0"
        val envVersionCode = System.getenv("GITHUB_RUN_NUMBER")?.toIntOrNull() ?: 1

        versionCode = envVersionCode
        versionName = envVersionName
    }

    signingConfigs {
        create("release") {
            val keystorePropertiesFile = file("keystore.properties")
            val keystoreProperties = Properties()
            if (keystorePropertiesFile.exists()) {
                keystoreProperties.load(FileInputStream(keystorePropertiesFile))
            }

            storeFile = file("release.jks")
            storePassword = System.getenv("KEYSTORE_PASSWORD")
                ?: keystoreProperties["storePassword"]?.toString()
            keyAlias = System.getenv("KEY_ALIAS") ?: keystoreProperties["keyAlias"]?.toString()
            keyPassword =
                System.getenv("KEY_PASSWORD") ?: keystoreProperties["keyPassword"]?.toString()
        }
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    debugImplementation(libs.compose.uiToolingPreview)
}

compose.resources {
    publicResClass = true
    packageOfResClass = "io.github.m0nkeysan.tally.generated.resources"
    generateResClass = always
}
