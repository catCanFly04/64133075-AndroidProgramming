package vy.ex7_intentlogin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        TextView tvWelcome = findViewById(R.id.tvWelcome);
        ImageView imgWelcome = findViewById(R.id.imgWelcome);
        Button btnLogout = findViewById(R.id.btnLogout);

        // Nhận dữ liệu từ Intent
        Intent intent = getIntent();
        String email = intent.getStringExtra("email");
        String username = intent.getStringExtra("username");

        // Hiển thị chào mừng người dùng
        tvWelcome.setText("Chào mừng, " + username + "!\nEmail: " + email);


        // Xử lý sự kiện đăng xuất
        btnLogout.setOnClickListener(v -> finish());
    }
}