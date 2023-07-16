import java.io.FileInputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Properties
import java.util.TimeZone


plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}


fun releaseTime(): String {
    val dateFormat = SimpleDateFormat("yy.MMddHH")
    dateFormat.timeZone = TimeZone.getTimeZone("GMT+8")
    return dateFormat.format(Date())
}

// 秒时间戳
fun buildTime(): Long {
    return Date().time / 1000
}

fun executeCommand(command: String): String {
    val process = Runtime.getRuntime().exec(command)
    process.waitFor()
    val output = process.inputStream.bufferedReader().use { it.readText() }
    return output.trim()
}

//val name = "editor"
val version = "1.${releaseTime()}"
val gitCommits: Int = executeCommand("git rev-list HEAD --count").trim().toInt()

android {
    namespace = "com.github.jing332.text_searcher"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.github.jing332.text_searcher"
        minSdk = 21
        targetSdk = 33
        versionCode = gitCommits
        versionName = version

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        val pro = Properties()
        val input = FileInputStream(project.rootProject.file("local.properties"))
        pro.load(input)

        create("release") {
            storeFile = file(pro.getProperty("KEY_PATH"))
            storePassword = pro.getProperty("KEY_PASSWORD")
            keyAlias = pro.getProperty("ALIAS_NAME")
            keyPassword = pro.getProperty("ALIAS_PASSWORD")
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        debug {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = false
            applicationIdSuffix = ".debug"
            versionNameSuffix = "_debug"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // OpenAI
    implementation("com.aallam.openai:openai-client:3.3.1")
    runtimeOnly("io.ktor:ktor-client-okhttp:2.3.2")

    // 数据持久化
    implementation("com.github.FunnySaltyFish.ComposeDataSaver:data-saver:v1.1.6")

    val accompanistVersion = "0.31.3-beta"
    implementation("com.google.accompanist:accompanist-systemuicontroller:${accompanistVersion}")
    implementation("com.google.accompanist:accompanist-navigation-animation:${accompanistVersion}")

    implementation("androidx.savedstate:savedstate:1.2.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.activity:activity-ktx:1.7.2")

    implementation("androidx.webkit:webkit:1.7.0")
    implementation("com.google.accompanist:accompanist-webview:0.31.5-beta")

    implementation("com.louiscad.splitties:splitties-systemservices:3.0.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.2")
    implementation("com.squareup.okhttp3:okhttp:4.10.0") // 要求OkHttp4以上
    implementation("com.github.liangjingkanji:Net:3.5.8")

    implementation("androidx.core:core-ktx:1.10.1")
//    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")

    implementation("androidx.compose.material:material-icons-core")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.material3:material3-window-size-class:1.1.1")

    implementation("androidx.compose.runtime:runtime:1.4.3")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1")

    implementation("androidx.activity:activity-compose:1.7.2")
    implementation(platform("androidx.compose:compose-bom:2023.06.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3:1.1.1")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.06.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}