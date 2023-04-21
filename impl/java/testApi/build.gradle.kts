dependencies {
    implementation(project(":ktor"))
    implementation(project(":server"))
    implementation(project(":core"))

    implementation("io.ktor:ktor-server-core:2.2.4")
    implementation("io.ktor:ktor-server-netty:2.2.4")
}