package vn.phamtranthuyvy.kids_english_story; // Đảm bảo đây là package name chính xác của bạn

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.AdapterView; // Import AdapterView
import android.widget.ArrayAdapter; // Import ArrayAdapter
import android.widget.Spinner;
import android.view.View;
import com.google.firebase.auth.FirebaseAuth;

// Import lớp View Binding được tạo tự động từ file layout activity_home.xml
import vn.phamtranthuyvy.kids_english_story.databinding.ActivityHomeBinding;

public class HomeActivity extends AppCompatActivity {

    // Khai báo đối tượng View Binding
    private ActivityHomeBinding binding;
    // Khai báo đối tượng Firebase Auth
    private FirebaseAuth mAuth;
    private String currentSelectedAgeGroup = null; // Biến lưu trữ giá trị tuổi thực tế (ví dụ: "ALL", "3-5")

    // Constants cho SharedPreferences và giá trị độ tuổi
    private static final String PREFS_NAME = "KidsStoryUserPrefs";
    private static final String KEY_SELECTED_AGE_GROUP = "selectedAgeGroup";
    public static final String AGE_GROUP_ALL = "ALL"; // Giá trị cho "Tất cả độ tuổi"
    public static final String AGE_GROUP_3_5 = "3-5";
    public static final String AGE_GROUP_6_8 = "6-8";
    private String[] ageGroupValues;

    // Định nghĩa các mã định danh cho chủ đề (Theme IDs)
    // Giúp quản lý và truyền dữ liệu một cách nhất quán
    private static final String THEME_ID_FAMILY = "FAMILY";
    private static final String THEME_ID_ANIMALS = "ANIMALS";
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

        // Khởi tạo mảng giá trị cho Spinner
        // Thứ tự phải khớp với mảng age_group_options_display trong strings.xml
        ageGroupValues = new String[]{AGE_GROUP_ALL, AGE_GROUP_3_5, AGE_GROUP_6_8};

        // Thiết lập Spinner
        setupAgeSpinner();
        loadAgePreferenceAndSetSpinner();


        // Gán sự kiện click cho các CardView chủ đề

        binding.cardViewFamily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openStoryList(THEME_ID_FAMILY);
            }
        });

        binding.cardViewAnimals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openStoryList(THEME_ID_ANIMALS);
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

    private void setupAgeSpinner() {
        // Lấy mảng các lựa chọn hiển thị từ strings.xml
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.age_group_options_display, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerAgeSelection.setAdapter(adapter); // spinnerAgeSelection là ID của Spinner trong XML

        binding.spinnerAgeSelection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Lấy giá trị logic tương ứng với lựa chọn hiển thị
                currentSelectedAgeGroup = ageGroupValues[position];
                saveAgePreference(currentSelectedAgeGroup);
                // Không cần update UI nút bấm nữa, Spinner tự hiển thị lựa chọn
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Có thể không cần xử lý gì ở đây
            }
        });
    }

    private void saveAgePreference(String ageGroup) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_SELECTED_AGE_GROUP, ageGroup);
        editor.apply();
    }

    private void loadAgePreferenceAndSetSpinner() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String savedAgeGroup = prefs.getString(KEY_SELECTED_AGE_GROUP, AGE_GROUP_ALL); // Mặc định là "ALL"
        currentSelectedAgeGroup = savedAgeGroup;

        // Tìm vị trí của savedAgeGroup trong mảng ageGroupValues để set selection cho Spinner
        int spinnerPosition = 0;
        for (int i = 0; i < ageGroupValues.length; i++) {
            if (ageGroupValues[i].equals(savedAgeGroup)) {
                spinnerPosition = i;
                break;
            }
        }
        binding.spinnerAgeSelection.setSelection(spinnerPosition);
    }

    private void openStoryListWithAge(String themeId) {
        Intent intent = new Intent(HomeActivity.this, StoryListActivity.class);
        intent.putExtra("SELECTED_THEME_ID", themeId);
        // currentSelectedAgeGroup đã được cập nhật bởi Spinner listener hoặc khi load preference
        if (currentSelectedAgeGroup != null && !currentSelectedAgeGroup.equals(AGE_GROUP_ALL)) {
            intent.putExtra("SELECTED_AGE_GROUP", currentSelectedAgeGroup);
        }
        // Nếu currentSelectedAgeGroup là AGE_GROUP_ALL hoặc null (ít khả năng nếu có default),
        // thì không gửi SELECTED_AGE_GROUP, StoryListActivity sẽ hiểu là lấy tất cả.
        startActivity(intent);
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