package vn.phamtranthuyvy.kids_english_story;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class StoryListActivity extends AppCompatActivity {

    private TextView textViewTitle;
    private RecyclerView recyclerViewStories;

    private FirebaseFirestore db;
    private List<Story> storyList;
    private StoryAdapter storyAdapter; // Khai báo StoryAdapter

    private String currentThemeId;
    private String currentAgeGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_list);

        textViewTitle = findViewById(R.id.textViewStoryListTitle);
        recyclerViewStories = findViewById(R.id.recyclerViewStories);

        // Nhận Intent và cập nhật tiêu đề (code như cũ)
        Intent intent = getIntent();
        if (intent != null) {
            currentThemeId = intent.getStringExtra("SELECTED_THEME_ID");
            if (intent.hasExtra("SELECTED_AGE_GROUP")) {
                currentAgeGroup = intent.getStringExtra("SELECTED_AGE_GROUP");
            } else {
                currentAgeGroup = HomeActivity.AGE_GROUP_ALL;
            }
        }

        Log.d("StoryListActivity", "Theme ID: " + currentThemeId);
        Log.d("StoryListActivity", "Age Group: " + currentAgeGroup);

        if (currentThemeId != null) {
            String titleText = "Truyện chủ đề: " + formatThemeIdForDisplay(currentThemeId);
            if (currentAgeGroup != null && !currentAgeGroup.equals(HomeActivity.AGE_GROUP_ALL)) {
                titleText += " (Tuổi: " + currentAgeGroup + ")";
            }
            textViewTitle.setText(titleText);
        } else {
            textViewTitle.setText("Danh sách truyện");
        }

        // Khởi tạo Firestore và danh sách truyện
        db = FirebaseFirestore.getInstance();
        storyList = new ArrayList<>();

        // Thiết lập RecyclerView và Adapter
        setupRecyclerView(); // Gọi hàm thiết lập

        // Tải truyện từ Firestore
        if (currentThemeId != null) {
            loadStoriesFromFirestore();
        } else {
            Log.e("StoryListActivity", "Theme ID is null, cannot load stories.");
            Toast.makeText(this, "Không thể tải truyện, thiếu thông tin chủ đề.", Toast.LENGTH_LONG).show();
        }
    }

    private void setupRecyclerView() {
        recyclerViewStories.setLayoutManager(new LinearLayoutManager(this));
        // Khởi tạo StoryAdapter với context và danh sách truyện (ban đầu rỗng)
        storyAdapter = new StoryAdapter(this, storyList);
        // Gán adapter cho RecyclerView
        recyclerViewStories.setAdapter(storyAdapter);
        Log.d("StoryListActivity", "RecyclerView and StoryAdapter setup complete.");
    }

    private void loadStoriesFromFirestore() {
        if (currentThemeId == null) {
            Log.e("StoryListActivity", "Theme ID is null in loadStoriesFromFirestore. Cannot query.");
            Toast.makeText(this, "Lỗi: Không có thông tin chủ đề để tải truyện.", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("StoryListActivity", "Loading stories for Theme: " + currentThemeId + ", Age: " + currentAgeGroup);

        Query query = db.collection("stories").whereEqualTo("themeId", currentThemeId);

        if (currentAgeGroup != null && !currentAgeGroup.equals(HomeActivity.AGE_GROUP_ALL)) {
            query = query.whereEqualTo("age_group", currentAgeGroup);
        }
        query = query.orderBy("title_en", Query.Direction.ASCENDING);


        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // Không clear storyList ở đây nữa, vì hàm updateData của adapter sẽ làm điều đó
                    // storyList.clear();
                    List<Story> fetchedStories = new ArrayList<>(); // Tạo danh sách tạm để chứa truyện mới
                    if (task.getResult() != null && !task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Story story = document.toObject(Story.class);
                            story.setStoryId(document.getId());
                            fetchedStories.add(story); // Thêm vào danh sách tạm
                            Log.d("StoryListActivity", "Fetched story: " + story.getTitle_en() + " (ID: " + story.getStoryId() + ")");
                        }
                    } else {
                        Log.d("StoryListActivity", "No stories found for this criteria.");
                    }

                    // Sử dụng hàm updateData của adapter để cập nhật danh sách và giao diện
                    if (storyAdapter != null) {
                        storyAdapter.updateData(fetchedStories); // Truyền danh sách truyện mới lấy được
                    } else {
                        Log.e("StoryListActivity", "StoryAdapter is null, cannot update data.");
                    }

                    Log.d("StoryListActivity", "Number of stories displayed: " + fetchedStories.size());
                    if (fetchedStories.isEmpty()){
                        Toast.makeText(StoryListActivity.this, "Không tìm thấy truyện nào cho lựa chọn này.", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Log.e("StoryListActivity", "Error getting documents: ", task.getException());
                    Toast.makeText(StoryListActivity.this, "Lỗi khi tải truyện: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private String formatThemeIdForDisplay(String themeId) {
        if (themeId == null) return "Không rõ";
        switch (themeId) {
            case "FAMILY": return "Gia Đình";
            case "ANIMALS": return "Động Vật";
            case "BODY": return "Cơ Thể";
            case "HOME": return "Nhà Cửa";
            case "TOYS": return "Đồ Chơi";
            case "CLOTHES": return "Quần Áo";
            case "FOOD": return "Đồ Ăn";
            case "DAILY_ROUTINES": return "Hoạt Động Hàng Ngày";
            default: return themeId;
        }
    }
}
