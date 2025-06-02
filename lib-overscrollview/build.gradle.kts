import com.vanniktech.maven.publish.SonatypeHost

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("maven-publish")
    id("signing")
    id("com.vanniktech.maven.publish") version "0.29.0"
}

group = "io.github.hanjunkwon"
version = "0.0.3"

android {
    namespace = "com.kwon.overscrollview"
    compileSdk = 35

    defaultConfig {
        minSdk = 23

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

    signAllPublications()

    // 프로젝트의 그룹 ID, 아티팩트 ID, 버전 설정
    coordinates("io.github.hanjunkwon", "overscrollview", "0.0.3")

    // POM 정보 설정
    pom {
        name.set("OverScrollView")  // 라이브러리 이름
        description.set("OverScrollView for Android")  // 라이브러리 설명
        url.set("https://github.com/HanJunKwon/OverScrollView")  // 프로젝트 URL
        inceptionYear.set("2025")  // 프로젝트 시작 연도

        // 라이선스 정보 설정
        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }

        // 개발자 정보 설정
        developers {
            developer {
                id.set("copybara")                          // 개발자 ID
                name.set("HanjunKwon")                      // 개발자 이름
                url.set("https://github.com/HanJunKwon")    // 개발자 URL
                email.set("skclaqks11@naver.com")           // 개발자 e-mail
            }
        }

        // 소스 코드 관리(SCM) 정보 설정
        scm {
            url.set("https://github.com/HanJunKwon/OverScrollView")
            connection.set("scm:git:https://github.com/HanJunKwon/OverScrollView.git")
            developerConnection.set("scm:git:https://github.com/HanJunKwon/OverScrollView.git")
        }
    }
}

signing {
    useInMemoryPgpKeys(
        findProperty("signingKey") as String?,
        findProperty("signingPassword") as String?
    )
}