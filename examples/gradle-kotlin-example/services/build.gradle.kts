/*
 * Copyright 2023 Creek Contributors (https://github.com/creek-service)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

plugins {
    java
}

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

val log4jVersion = "2.19.0"           // https://mvnrepository.com/artifact/org.apache.logging.log4j/log4j-core
val junitVersion = "5.9.2"            // https://mvnrepository.com/artifact/org.junit.jupiter/junit-jupiter-api
val junitPioneerVersion = "2.0.0"     // https://mvnrepository.com/artifact/org.junit-pioneer/junit-pioneer
val mockitoVersion = "5.1.1"          // https://mvnrepository.com/artifact/org.mockito/mockito-junit-jupiter
val hamcrestVersion = "2.2"           // https://mvnrepository.com/artifact/org.hamcrest/hamcrest-core

// begin-snippet: dependencies
val creekVersion = "0.3.2"

dependencies {
    // Add dependency on core Creek service library:
    implementation("org.creekservice:creek-service-context:$creekVersion")
    // Add dependency on Kafka Streams extension to Creek:
    implementation("org.creekservice:creek-kafka-streams-extension:$creekVersion")
    // end-snippet

    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junitVersion")
    testImplementation("org.junit-pioneer:junit-pioneer:$junitPioneerVersion")
    testImplementation("org.mockito:mockito-junit-jupiter:$mockitoVersion")
    testImplementation("org.hamcrest:hamcrest-core:$hamcrestVersion")
    testImplementation("org.apache.logging.log4j:log4j-core:$log4jVersion")
    testImplementation("org.apache.logging.log4j:log4j-slf4j2-impl:$log4jVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
}

defaultTasks("check")
