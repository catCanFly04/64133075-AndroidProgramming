package vn.phamtranthuyvy.kids_english_story; // <-- THAY BẰNG PACKAGE CỦA BẠN

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

// Đảm bảo tên binding đúng với tên layout activity_home.xml
import vn.phamtranthuyvy.kids_english_story.databinding.ActivityHomeBinding; // <-- THAY BẰNG PACKAGE CỦA BẠN

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Tạm thời chưa cần xử lý gì thêm ở đây
        // Sau này sẽ thêm sự kiện click cho các nút chủ đề
    }
}