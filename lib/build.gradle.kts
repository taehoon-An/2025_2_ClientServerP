import org.gradle.api.tasks.compile.JavaCompile

plugins {
    java
    application
    id("com.google.protobuf") version "0.9.4"
}

repositories {
    mavenCentral()
}

val grpcVersion = "1.64.0"
val protobufVersion = "3.25.3"

dependencies {
    implementation("io.grpc:grpc-netty-shaded:$grpcVersion")
    implementation("io.grpc:grpc-protobuf:$grpcVersion")
    implementation("io.grpc:grpc-stub:$grpcVersion")
    implementation("com.google.protobuf:protobuf-java:$protobufVersion")
    implementation("javax.annotation:javax.annotation-api:1.3.2")
    implementation("org.springframework.security:spring-security-crypto:6.2.0")
    implementation("commons-logging:commons-logging:1.2")
    implementation("io.jsonwebtoken:jjwt-api:0.12.3")
    implementation("io.grpc:grpc-services:$grpcVersion")
    

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.2")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.3")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.3")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:$protobufVersion"
    }
    plugins {
        create("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:$grpcVersion"
        }
    }
    generateProtoTasks {
        all().forEach {
            it.plugins.create("grpc") {}
        }
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

application {
    mainClass.set("com.example.grpc.NameServer")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}	

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}