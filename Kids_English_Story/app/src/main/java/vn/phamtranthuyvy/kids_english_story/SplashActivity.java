package vn.phamtranthuyvy.kids_english_story; // <-- THAY BẰNG PACKAGE CỦA BẠN

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
// Import layout của bạn nếu cần (thường không cần trong Splash đơn giản)

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 2500; // 2.5 giây

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_splash); // Có thể bỏ qua nếu dùng theme NoActionBar và chỉ hiển thị logo mặc định của Android 12+, hoặc giữ lại nếu có layout tùy chỉnh

        // Đặt layout tùy chỉnh nếu bạn muốn hiển thị logo/text riêng
        setContentView(R.layout.activity_splash);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                finish(); // Kết thúc SplashActivity
            }
        }, SPLASH_DELAY);
    }
}