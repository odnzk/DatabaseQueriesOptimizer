import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.jvm)
    id(libs.plugins.antlr.get().pluginId)
    id("org.jetbrains.intellij") version "1.16.1" // TODO()
}

group = "kpfu.itis.odenezhkina"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

intellij {
    version.set("2023.1.5")
    type.set("IC")
    plugins.set(listOf(/* Plugin Dependencies */))
}

tasks {
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }

    patchPluginXml {
        sinceBuild.set("231")
        untilBuild.set("241.*")
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}

dependencies {
    implementation(libs.kotlin)
    implementation(libs.antlr.runtime)
    antlr(libs.antlr.tool)
}

tasks.generateGrammarSource {
    arguments = listOf("-visitor", "-long-messages")
}

tasks.withType<KotlinCompile>().configureEach {
    inputs.files(tasks.named("generateGrammarSource"))
}
