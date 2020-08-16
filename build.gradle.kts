plugins {
    id("org.springframework.boot")        version "2.3.1.RELEASE"
    id("io.spring.dependency-management") version "1.0.9.RELEASE"
    id("idea")
    id("groovy")
    id("org.asciidoctor.convert") version "1.6.0"
    kotlin("jvm")             version "1.3.72"
    kotlin("kapt")            version "1.3.72"
    kotlin("plugin.spring")   version "1.3.72"
    kotlin("plugin.jpa")      version "1.3.72"
    kotlin("plugin.allopen")  version "1.3.72"
    kotlin("plugin.noarg")    version "1.3.72"
}

group = "com.taskforce"
version = "SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

val queryDsl = "4.3.1"
ext {
    set("snippetsDir", file("build/generated-snippets"))
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.session:spring-session-data-redis")

    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation("io.jsonwebtoken:jjwt:0.9.1")
    implementation("org.apache.httpcomponents:httpclient")

    // JPA
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("com.querydsl:querydsl-jpa:${queryDsl}")
    kapt("com.querydsl:querydsl-apt:${queryDsl}:jpa")
    kaptTest("com.querydsl:querydsl-apt:${queryDsl}:jpa")

    runtimeOnly("mysql:mysql-connector-java")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }

    testImplementation("org.mockito:mockito-core:3.4.6")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.spockframework:spock-core:1.1-groovy-2.4")   // Spock 의존성 추가
    testImplementation("org.spockframework:spock-spring:1.1-groovy-2.4") // Spock 의존성 추가
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")

}

kapt {
    useBuildCache = true
    correctErrorTypes = true
}

tasks {

    test {
        useJUnitPlatform()
        outputs.dir(ext.get("snippetsDir") as File)
    }

    asciidoctor {
        dependsOn(test)
        inputs.dir(ext.get("snippetsDir") as File)

        doFirst {
            println("===== START asciidoctor GENERATE=======")
            delete(file("src/main/resources/static/docs"))
        }

        doLast {
            println("===== END asciidoctor GENERATE =======")
        }
    }

    register("copyDocument", Copy::class) {
        dependsOn(asciidoctor)
        from("build/asciidoc/html5")
        into("src/main/resources/static/docs")

        // 서버 가동 시 <host>/docs/index.html로 restDocs 조회
    }

    build {
        dependsOn(":copyDocument")
    }

    bootJar {

        dependsOn(asciidoctor)
        from ("${asciidoctor.get().outputDir}/html5") {
            into("BOOT-INF/classes/static/docs")
        }
    }

    compileKotlin {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "1.8"
        }
    }

    compileTestKotlin {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "1.8"
        }
    }
}
