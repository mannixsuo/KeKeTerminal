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

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

val testVersion = "5.4.2"
dependencies {
    implementation("org.jetbrains.pty4j:pty4j:0.12.7")
    implementation("ch.qos.logback:logback-classic:1.2.11")
    implementation("ch.qos.logback:logback-core:1.2.11")
    implementation("org.slf4j:slf4j-api:1.7.36")
    testImplementation("io.kotest:kotest-runner-junit5:${testVersion}")
    testImplementation("io.kotest:kotest-assertions-core:${testVersion}")
    testImplementation("io.kotest:kotest-property:${testVersion}")
}
