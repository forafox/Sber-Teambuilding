pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "hackaton-sber-prosto-2025"

include("telegram-bot")
findProject(":telegram-bot")?.name = "telegram"
include("mail-sender")
findProject(":mail-sender")?.name = "mail"
