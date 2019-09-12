plugins {
    //https://docs.gradle.org/current/userguide/java_plugin.html
    java

    //https://docs.gradle.org/current/userguide/maven_plugin.html
    maven

    //https://github.com/leleliu008/BintrayUploadGradlePlugin
    //https://plugins.gradle.org/plugin/com.fpliu.bintray
    id("com.fpliu.bintray").version("1.0.7")

    //用于上传到jCenter中
    //https://github.com/bintray/gradle-bintray-plugin
    id("com.jfrog.bintray").version("1.7.3")
    
    //https://kotlinlang.org/docs/reference/using-gradle.html
    kotlin("jvm") version "1.3.50"
}

sourceSets["main"].java.srcDir("src/main/kotlin")
sourceSets["test"].java.srcDir("src/test/kotlin")

group = "com.fpliu"

version = "1.0.1"

val projectName = project.name

bintrayUploadExtension {
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
