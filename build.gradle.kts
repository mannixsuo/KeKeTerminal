import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("java")
}

group = "com.mmsuo"
version = "1.0-SNAPSHOT"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven("https://jcenter.bintray.com/")
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
            }
        }
        val jvmTest by getting
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "KekeTerminal"
            packageVersion = "1.0.0"
        }
    }
}

dependencies {
    // https://mvnrepository.com/artifact/org.jetbrains.pty4j/pty4j
    implementation("org.jetbrains.pty4j:pty4j:0.12.7")
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.0")
}
