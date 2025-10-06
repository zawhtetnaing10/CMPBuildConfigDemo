import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import com.codingfeline.buildkonfig.compiler.FieldSpec
import java.util.regex.Pattern


plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)

    // Apply Build Config Plugin
    alias(libs.plugins.buildkonfig)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
    
    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "org.cmpbuildconfig.demo"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "org.cmpbuildconfig.demo"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    flavorDimensions.add("variants")

    productFlavors{
        create("grab"){
            dimension = "variants"
            isDefault = true
            applicationIdSuffix = ".grab"
            resValue("string", "app_name", "Grab CMP")
        }

        create("foodpanda"){
            dimension = "variants"
            applicationIdSuffix = ".foodpanda"
            resValue("string", "app_name", "Foodpanda CMP")
        }
    }
}

// Set buildkonfig flavor according to build variant
project.extra.set("buildkonfig.flavor", currentBuildVariant())

fun Project.getAndroidBuildVariantOrNull(): String? {
    val variants = setOf("grab", "foodpanda")
    val taskRequestsStr = gradle.startParameter.taskRequests.toString()
    val pattern: Pattern = if (taskRequestsStr.contains("assemble")) {
        Pattern.compile("assemble(\\w+)(Release|Debug)")
    } else {
        Pattern.compile("bundle(\\w+)(Release|Debug)")
    }

    val matcher = pattern.matcher(taskRequestsStr)
    val variant = if (matcher.find()) matcher.group(1).lowercase() else null
    return if (variant in variants) {
        variant
    } else {
        null
    }
}

private fun Project.currentBuildVariant(): String {
    // ioS
    val iosEnvMap = hashMapOf<String, String>(
        "Grab Debug" to "grab",
        "Grab Release" to "grab",
        "Foodpanda Debug" to "foodpanda",
        "Foodpanda Release" to "foodpanda",
    )
    val iosEnv = iosEnvMap[System.getenv()["CONFIGURATION"]] ?: "grab"

    return getAndroidBuildVariantOrNull() ?: iosEnv
}

dependencies {
    implementation(libs.androidx.tools.core)
    debugImplementation(compose.uiTooling)
}

buildkonfig {

    packageName = "org.cmpbuildconfig.demo"

    defaultConfigs {
        buildConfigField(FieldSpec.Type.STRING, "variant", "grab")
        buildConfigField(FieldSpec.Type.STRING, "apiEndPoint", "https://api.grab.com")
    }

    defaultConfigs("grab"){
        buildConfigField(FieldSpec.Type.STRING, "variant", "grab")
        buildConfigField(FieldSpec.Type.STRING, "apiEndPoint", "https://api.grab.com")
    }

    defaultConfigs("foodpanda"){
        buildConfigField(FieldSpec.Type.STRING, "variant", "foodpanda")
        buildConfigField(FieldSpec.Type.STRING, "apiEndPoint", "https://api.foodpanda.com")
    }
}

