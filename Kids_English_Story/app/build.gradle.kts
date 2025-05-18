plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "vn.phamtranthuyvy.kids_english_story"
    compileSdk = 35 // Bạn có thể cân nhắc hạ xuống 34 nếu gặp vấn đề tương thích với các thư viện khác, 35 là khá mới

    defaultConfig {
        applicationId = "vn.phamtranthuyvy.kids_english_story"
        minSdk = 24
        targetSdk = 35 // Tương tự compileSdk
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures {
        viewBinding = true  // Bật ViewBinding để dễ truy cập View trong code Java
        buildConfig = true   // Bật BuildConfig để sau này lưu API Key (nếu cần)
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.credentials)
    implementation(libs.credentials.play.services.auth)
    implementation(libs.googleid)

    // 1. Glide (Để tải và hiển thị hình ảnh hiệu quả)
    val glideVersion = "4.16.0" // Kiểm tra phiên bản Glide mới nhất
    implementation("com.github.bumptech.glide:glide:$glideVersion")
    annotationProcessor("com.github.bumptech.glide:compiler:$glideVersion")

    // 2. ML Kit Translate (Để dịch trên thiết bị, không cần server riêng)
    implementation("com.google.mlkit:translate:17.0.2") // Kiểm tra phiên bản ML Kit Translate mới nhất

    // 3. Gemini API (Nếu bạn muốn dùng AI tạo truyện - có thể thêm sau nếu chưa cần ngay)
    implementation("com.google.ai.client.generativeai:generativeai:0.6.0")

    // 4. Guava (Thường cần cho Gemini API bản Java - thêm nếu dùng Gemini)
    implementation("com.google.guava:guava:32.1.3-android")

    // 5. Room (Nếu bạn muốn lưu từ vựng/lịch sử - có thể thêm sau)
    val roomVersion = "2.6.1" // Kiểm tra phiên bản Room mới nhất
    implementation("androidx.room:room-runtime:$roomVersion")
    annotationProcessor("androidx.room:room-compiler:$roomVersion")

    // Firebase BoM (Bill of Materials) - Giúp quản lý phiên bản các thư viện Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.1.1")) // Kiểm tra bản BoM mới nhất

    // Thư viện Firebase Authentication
    implementation("com.google.firebase:firebase-auth")

    // THÊM DÒNG NÀY VÀO: Thư viện Firebase Cloud Firestore
    implementation("com.google.firebase:firebase-firestore")

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
