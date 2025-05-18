package vn.phamtranthuyvy.kids_english_story; // Nhớ thay bằng package name của bạn

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color; // Import Color
import android.os.Bundle;
import android.text.SpannableString; // Import SpannableString
import android.text.Spanned; // Import Spanned
import android.text.TextPaint; // Import TextPaint
import android.text.method.LinkMovementMethod; // Import LinkMovementMethod
import android.text.style.ClickableSpan; // Import ClickableSpan
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
// Bỏ import Button và ImageView nếu không dùng trực tiếp trong Java mà qua binding
// import android.widget.Button;
// import android.widget.ImageView;
// import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener; // Import OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener; // Import OnSuccessListener
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
// Bỏ import FirebaseFirestoreException nếu không dùng trực tiếp
// import com.google.firebase.firestore.FirebaseFirestoreException;

// ML Kit Translate
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import vn.phamtranthuyvy.kids_english_story.databinding.ActivityStoryDetailBinding;

public class StoryDetailActivity extends AppCompatActivity {

    private ActivityStoryDetailBinding binding;
    private FirebaseFirestore db;
    private String currentStoryId;
    private Story currentStoryObject;

    private boolean isShowingEnglish = true;

    // Khai báo ML Kit Translator
    private Translator englishVietnameseTranslator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStoryDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();

        setSupportActionBar(binding.toolbarStoryDetail);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Khởi tạo ML Kit Translator
        initializeTranslator();

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("SELECTED_STORY_ID")) {
            currentStoryId = intent.getStringExtra("SELECTED_STORY_ID");
            Log.d("StoryDetailActivity", "Received Story ID: " + currentStoryId);
            if (currentStoryId != null && !currentStoryId.isEmpty()) {
                loadStoryDetails();
            } else {
                Toast.makeText(this, "Lỗi: Không nhận được ID truyện.", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(this, "Lỗi: Không có thông tin truyện.", Toast.LENGTH_SHORT).show();
            finish();
        }

        binding.buttonSwitchToEnglish.setOnClickListener(v -> {
            isShowingEnglish = true;
            displayStoryContentAndTitle();
            updateLanguageButtonUI();
        });

        binding.buttonSwitchToVietnamese.setOnClickListener(v -> {
            isShowingEnglish = false;
            displayStoryContentAndTitle();
            updateLanguageButtonUI();
        });
        updateLanguageButtonUI();
    }

    private void initializeTranslator() {
        // Tạo tùy chọn dịch từ Tiếng Anh sang Tiếng Việt
        TranslatorOptions options =
                new TranslatorOptions.Builder()
                        .setSourceLanguage(TranslateLanguage.ENGLISH)
                        .setTargetLanguage(TranslateLanguage.VIETNAMESE)
                        .build();
        englishVietnameseTranslator = Translation.getClient(options);

        // (Tùy chọn) Đảm bảo model dịch đã được tải xuống
        // Bạn có thể gọi hàm này ở SplashActivity hoặc ở đây để tải trước
        // hoặc để nó tự tải khi lần đầu sử dụng (có thể hơi chậm lần đầu)
        DownloadConditions conditions = new DownloadConditions.Builder()
                .requireWifi() // Ví dụ: chỉ tải model khi có Wifi
                .build();
        englishVietnameseTranslator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener(v -> Log.d("Translator", "English-Vietnamese model downloaded or already available."))
                .addOnFailureListener(e -> Log.e("Translator", "Error downloading English-Vietnamese model: " + e.getMessage()));

        // Quan trọng: Đăng ký vòng đời cho translator để tự động giải phóng tài nguyên
        getLifecycle().addObserver(englishVietnameseTranslator);
    }


    private void loadStoryDetails() {
        // ... (code loadStoryDetails như cũ) ...
        if (currentStoryId == null) return;
        DocumentReference storyRef = db.collection("stories").document(currentStoryId);
        storyRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    currentStoryObject = document.toObject(Story.class);
                    if (currentStoryObject != null) {
                        currentStoryObject.setStoryId(document.getId());
                        Log.d("StoryDetailActivity", "Story data fetched: " + currentStoryObject.getTitle_en());
                        populateInitialUI();
                    } else {
                        Log.e("StoryDetailActivity", "Failed to convert document to Story object.");
                        Toast.makeText(StoryDetailActivity.this, "Lỗi: Dữ liệu truyện không hợp lệ.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("StoryDetailActivity", "No such document with ID: " + currentStoryId);
                    Toast.makeText(StoryDetailActivity.this, "Lỗi: Không tìm thấy truyện.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e("StoryDetailActivity", "Error getting story details: ", task.getException());
                Toast.makeText(StoryDetailActivity.this, "Lỗi khi tải chi tiết truyện.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateInitialUI() {
        // ... (code populateInitialUI như cũ, không đổi) ...
        if (currentStoryObject == null) return;
        if (currentStoryObject.getCoverImageUrl() != null && !currentStoryObject.getCoverImageUrl().isEmpty()) {
            binding.imageViewStoryDetailCover.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(currentStoryObject.getCoverImageUrl())
                    .placeholder(R.drawable.anh1)
                    .error(R.drawable.anh1)
                    .into(binding.imageViewStoryDetailCover);
        } else {
            binding.imageViewStoryDetailCover.setVisibility(View.GONE);
        }
        displayStoryContentAndTitle();
    }

    private void displayStoryContentAndTitle() {
        if (currentStoryObject == null) return;

        String titleToShow;
        String contentToShow;

        if (isShowingEnglish) {
            titleToShow = (currentStoryObject.getTitle_en() != null && !currentStoryObject.getTitle_en().isEmpty())
                    ? currentStoryObject.getTitle_en()
                    : (currentStoryObject.getTitle_vi() != null && !currentStoryObject.getTitle_vi().isEmpty() ? currentStoryObject.getTitle_vi() : "Story Title");
            contentToShow = (currentStoryObject.getContent_en() != null && !currentStoryObject.getContent_en().isEmpty())
                    ? currentStoryObject.getContent_en()
                    : "Content not available in English.";
            Log.d("StoryDetailActivity", "Displaying English content and title.");
            // Áp dụng ClickableSpan cho nội dung tiếng Anh
            setupClickableEnglishText(contentToShow);

        } else { // isShowingVietnamese
            titleToShow = (currentStoryObject.getTitle_vi() != null && !currentStoryObject.getTitle_vi().isEmpty())
                    ? currentStoryObject.getTitle_vi()
                    : (currentStoryObject.getTitle_en() != null && !currentStoryObject.getTitle_en().isEmpty() ? currentStoryObject.getTitle_en() : "Tiêu Đề");
            contentToShow = (currentStoryObject.getContent_vi() != null && !currentStoryObject.getContent_vi().isEmpty())
                    ? currentStoryObject.getContent_vi()
                    : "Nội dung không có sẵn bằng Tiếng Việt.";
            Log.d("StoryDetailActivity", "Displaying Vietnamese content and title.");
            // Với nội dung tiếng Việt, chỉ hiển thị text bình thường
            binding.textViewStoryDetailContent.setText(contentToShow);
            binding.textViewStoryDetailContent.setMovementMethod(null); // Tắt khả năng click link nếu có
        }
        binding.textViewStoryDetailTitle.setText(titleToShow);
    }

    private void setupClickableEnglishText(String englishContent) {
        if (englishContent == null || englishContent.isEmpty()) {
            binding.textViewStoryDetailContent.setText(englishContent);
            return;
        }

        SpannableString spannableString = new SpannableString(englishContent);
        // Tách nội dung thành các từ (dựa trên khoảng trắng và dấu câu cơ bản)
        // Bạn có thể cần một biểu thức chính quy (regex) phức tạp hơn để xử lý dấu câu tốt hơn
        String[] words = englishContent.split("\\s+|(?=[.,!?;:])|(?<=[.,!?;:])");

        int startIndexOfWord = 0;
        for (final String word : words) {
            if (word.trim().isEmpty()) { // Bỏ qua các khoảng trắng hoặc dấu câu đứng riêng
                startIndexOfWord += word.length();
                continue;
            }

            // Tìm vị trí bắt đầu thực sự của từ trong chuỗi gốc (quan trọng nếu có nhiều khoảng trắng)
            int currentWordStartInOriginal = englishContent.indexOf(word, startIndexOfWord);
            if (currentWordStartInOriginal == -1) { // Không tìm thấy từ, có thể do xử lý split chưa hoàn hảo
                startIndexOfWord += word.length(); // Bỏ qua và tiếp tục
                continue;
            }

            int endIndexOfWord = currentWordStartInOriginal + word.length();

            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {
                    // Loại bỏ dấu câu ở cuối từ trước khi dịch (nếu có)
                    String cleanedWord = word.replaceAll("[.,!?;:]$", "");
                    if (!cleanedWord.trim().isEmpty()) {
                        Log.d("ClickableSpan", "Clicked on word: " + cleanedWord);
                        translateWord(cleanedWord);
                    }
                }

                @Override
                public void updateDrawState(@NonNull TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setUnderlineText(false); // Bỏ gạch chân (tùy chọn)
                    // ds.setColor(Color.BLUE); // Đổi màu chữ nếu muốn (tùy chọn)
                }
            };

            spannableString.setSpan(clickableSpan, currentWordStartInOriginal, endIndexOfWord, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            startIndexOfWord = endIndexOfWord; // Cập nhật vị trí bắt đầu cho từ tiếp theo
        }

        binding.textViewStoryDetailContent.setText(spannableString);
        binding.textViewStoryDetailContent.setMovementMethod(LinkMovementMethod.getInstance()); // BẮT BUỘC để ClickableSpan hoạt động
        binding.textViewStoryDetailContent.setHighlightColor(Color.TRANSPARENT); // Bỏ màu highlight khi nhấn (tùy chọn)
    }

    private void translateWord(final String wordToTranslate) {
        if (englishVietnameseTranslator == null) {
            Toast.makeText(this, "Bộ dịch chưa sẵn sàng.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Hiển thị Toast báo đang dịch (tùy chọn)
        Toast.makeText(this, "Đang dịch '" + wordToTranslate + "'...", Toast.LENGTH_SHORT).show();

        englishVietnameseTranslator.translate(wordToTranslate)
                .addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(@NonNull String translatedText) {
                        Log.d("Translator", "Original: " + wordToTranslate + " -> Translated: " + translatedText);
                        // Hiển thị bản dịch, ví dụ bằng Toast
                        // Bạn có thể làm một Dialog nhỏ xinh để hiển thị đẹp hơn
                        Toast.makeText(StoryDetailActivity.this,
                                wordToTranslate + ": " + translatedText,
                                Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Translator", "Error translating word '" + wordToTranslate + "': " + e.getMessage());
                        Toast.makeText(StoryDetailActivity.this,
                                "Lỗi khi dịch từ: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void updateLanguageButtonUI() {
        // ... (code updateLanguageButtonUI như cũ) ...
        if (isShowingEnglish) {
            binding.buttonSwitchToEnglish.setSelected(true);
            binding.buttonSwitchToVietnamese.setSelected(false);
        } else {
            binding.buttonSwitchToEnglish.setSelected(false);
            binding.buttonSwitchToVietnamese.setSelected(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // ... (code onOptionsItemSelected như cũ) ...
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // (Tùy chọn) Nhớ giải phóng translator khi Activity bị hủy nếu không dùng getLifecycle().addObserver()
    // @Override
    // protected void onDestroy() {
    //     super.onDestroy();
    //     if (englishVietnameseTranslator != null) {
    //         englishVietnameseTranslator.close();
    //     }
    // }
}
