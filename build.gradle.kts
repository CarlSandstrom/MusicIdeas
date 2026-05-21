import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
    kotlin("plugin.serialization") version "2.1.0"
}

group = "com.musicideas"
version = "1.0.0-beta"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

// build.gradle.kts
dependencies {
    implementation(compose.desktop.currentOs)
    implementation(compose.material3)
    implementation(compose.material)
    implementation(compose.materialIconsExtended)
    implementation(compose.runtime)
    implementation(compose.foundation)
    implementation(compose.ui)
    implementation("br.com.devsrsouza.compose.icons:feather:1.1.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    implementation("io.insert-koin:koin-core:3.5.0")
    implementation("io.insert-koin:koin-compose:1.1.0")
    // JavaCV + FFmpeg: only include natives for the target platform.
    // Add classifiers for other platforms when needed:
    //   macOS Intel:  "org.bytedeco:ffmpeg:6.0-1.5.9:macosx-x86_64"
    //   macOS ARM:    "org.bytedeco:ffmpeg:6.0-1.5.9:macosx-arm64"
    //   Linux x64:    "org.bytedeco:ffmpeg:6.0-1.5.9:linux-x86_64"
    implementation("org.bytedeco:javacv:1.5.9")
    implementation("org.bytedeco:ffmpeg:6.0-1.5.9")
    implementation("org.bytedeco:ffmpeg:6.0-1.5.9:windows-x86_64")
}

compose.desktop {
    application {
        mainClass = "com.musicideas.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "MusicIdeas"
            packageVersion = "1.0.0"

            windows {
                iconFile.set(project.file("src/main/resources/MusicIdeas_icon.ico"))
                menuGroup = "MusicIdeas"
                upgradeUuid = "B3A7F2C1-4D8E-4F9A-A2B5-6C0D1E3F8A7B"
            }
            linux {
                iconFile.set(project.file("src/main/resources/MusicIdeas_icon.png"))
            }
        }
    }
}