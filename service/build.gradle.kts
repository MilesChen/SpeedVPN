plugins {
    kotlin("android")
    id("kotlinx-serialization")
    id("com.android.library")
    id("com.google.devtools.ksp")
}
android {
    namespace = "com.github.kr328.clash.service"
}
dependencies {
    implementation(project(":core"))
    implementation(project(":common"))

    ksp(libs.kaidl.compiler)
    ksp(libs.androidx.room.compiler)

    implementation(libs.kotlin.coroutine)
    implementation(libs.kotlin.serialization.json)
    implementation(libs.androidx.core)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    implementation(libs.kaidl.runtime)
    implementation(libs.rikkax.multiprocess)
    api(platform("com.squareup.okhttp3:okhttp-bom:4.10.0"))

    // define any required OkHttp artifacts without version
    api("com.squareup.okhttp3:okhttp")
    api("com.squareup.okhttp3:logging-interceptor")
}

afterEvaluate {
    android {
        libraryVariants.forEach {
            sourceSets[it.name].kotlin.srcDir(buildDir.resolve("generated/ksp/${it.name}/kotlin"))
            sourceSets[it.name].java.srcDir(buildDir.resolve("generated/ksp/${it.name}/java"))
        }
    }
}