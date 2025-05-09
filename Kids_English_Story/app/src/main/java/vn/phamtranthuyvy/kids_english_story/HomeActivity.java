package vn.phamtranthuyvy.kids_english_story; // Đảm bảo đây là package name chính xác của bạn

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import com.google.firebase.auth.FirebaseAuth;

// Import lớp View Binding được tạo tự động từ file layout activity_home.xml
import vn.phamtranthuyvy.kids_english_story.databinding.ActivityHomeBinding;

public class HomeActivity extends AppCompatActivity {

    // Khai báo đối tượng View Binding
    private ActivityHomeBinding binding;
    // Khai báo đối tượng Firebase Auth
    private FirebaseAuth mAuth;

    // Định nghĩa các mã định danh cho chủ đề (Theme IDs)
    // Giúp quản lý và truyền dữ liệu một cách nhất quán
    private static final String THEME_ID_FAMILY = "FAMILY";
    private static final String THEME_ID_BODY = "BODY";
    private static final String THEME_ID_HOME = "HOME";
    private static final String THEME_ID_TOYS = "TOYS";
    private static final String THEME_ID_CLOTHES = "CLOTHES";
    private static final String THEME_ID_FOOD = "FOOD";
    private static final String THEME_ID_DAILY_ROUTINES = "DAILY_ROUTINES";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Khởi tạo View Binding
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        // Thiết lập layout cho Activity
        setContentView(binding.getRoot());

        // Khởi tạo Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Gán sự kiện click cho nút Đăng xuất (btnLogout là ID bạn đã dùng)
        binding.btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser(); // Gọi hàm đăng xuất
            }
        });

        // Gán sự kiện click cho các CardView chủ đề

        binding.cardViewFamily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openStoryList(THEME_ID_FAMILY);
            }
        });

        binding.cardViewBody.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openStoryList(THEME_ID_BODY);
            }
        });

        binding.cardViewHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openStoryList(THEME_ID_HOME);
            }
        });

        binding.cardViewToys.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openStoryList(THEME_ID_TOYS);
            }
        });

        binding.cardViewClothes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openStoryList(THEME_ID_CLOTHES);
            }
        });

        binding.cardViewFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openStoryList(THEME_ID_FOOD);
            }
        });

        binding.cardViewDailyRoutines.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openStoryList(THEME_ID_DAILY_ROUTINES);
            }
        });
    }

    /**
     * Xử lý sự kiện đăng xuất người dùng
     */
    private void logoutUser() {
        // Gọi phương thức signOut của Firebase Authentication
        mAuth.signOut();

        // Tạo Intent để quay trở lại màn hình LoginActivity
        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);

        // QUAN TRỌNG: Thêm flags để xóa hết các Activity cũ khỏi stack
        // và tạo một Task mới cho LoginActivity. Điều này ngăn người dùng
        // nhấn nút Back để quay lại màn hình Home sau khi đã đăng xuất.
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // Bắt đầu LoginActivity
        startActivity(intent);

        // Đóng HomeActivity hiện tại
        finish();
    }

    /**
     * Mở màn hình danh sách truyện (StoryListActivity) với chủ đề được chọn
     * @param themeId Mã định danh của chủ đề (ví dụ: "FAMILY", "BODY",...)
     */
    private void openStoryList(String themeId) {
        // Tạo Intent để chuyển sang StoryListActivity (Bạn cần tạo Activity này)
        Intent intent = new Intent(HomeActivity.this, StoryListActivity.class);
        // Đính kèm mã chủ đề vào Intent để StoryListActivity biết cần hiển thị truyện nào
        intent.putExtra("SELECTED_THEME_ID", themeId);
        // Bắt đầu StoryListActivity
        startActivity(intent);
    }
}