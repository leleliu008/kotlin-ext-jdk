plugins {
    java
    maven

    //用于上传到jCenter中
    //https://github.com/bintray/gradle-bintray-plugin
    id("com.jfrog.bintray").version("1.7.3")
    kotlin("jvm") version "1.3.50"
}

buildscript {
    repositories {
        jcenter { url = uri("https://maven.aliyun.com/repository/jcenter") }
    }
    dependencies {
        classpath("com.fpliu:BintrayUploadGradlePlugin:1.0.7")
    }
}

apply {
    plugin("com.fpliu.bintray")
}

sourceSets["main"].java.srcDir("src/main/kotlin")
sourceSets["test"].java.srcDir("src/test/kotlin")

group = "com.fpliu"

version = "1.0.1"

val projectName = project.name

(project.extensions.getByName("bintrayUploadExtension") as com.fpliu.gradle.BintrayUploadExtension).apply {
    developerName = "leleliu008"
    developerEmail = "leleliu008@gamil.com"

    projectSiteUrl = "https://github.com/$developerName/$projectName"
    projectGitUrl = "https://github.com/$developerName/$projectName"

    bintrayOrganizationName = "fpliu"
    bintrayRepositoryName = "newton"
}

repositories {
    jcenter { url = uri("https://maven.aliyun.com/repository/jcenter") }
    maven { url = uri("https://maven.aliyun.com/repository/public") }
}

dependencies {
    implementation(kotlin("stdlib"))
    testImplementation("junit:junit:4.12")
}
