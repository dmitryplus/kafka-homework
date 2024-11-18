plugins {
    id("java")
}

group = "ru.otus"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {

    implementation("org.apache.kafka:kafka-clients:3.4.0")
    implementation("org.apache.kafka:kafka-streams:3.4.0")

    implementation("org.slf4j:slf4j-api:2.0.7") // or the latest version
    implementation("ch.qos.logback:logback-classic:1.4.8")
    implementation("com.github.javafaker:javafaker:0.12")
    implementation("com.google.code.gson:gson:2.10.1")





    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

}

tasks.test {
    useJUnitPlatform()
}