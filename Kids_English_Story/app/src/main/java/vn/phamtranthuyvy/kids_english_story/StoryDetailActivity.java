package vn.phamtranthuyvy.kids_english_story;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import vn.phamtranthuyvy.kids_english_story.databinding.ActivityStoryDetailBinding;

public class StoryDetailActivity extends AppCompatActivity {

    private ActivityStoryDetailBinding binding;
    private FirebaseFirestore db;
    private String currentStoryId;
    private Story currentStoryObject;
    private boolean isShowingEnglish = true;
    private Translator englishVietnameseTranslator;
    private PopupWindow translationPopupWindow;

    // Pattern để tìm thẻ IMAGE_URL, không phân biệt chữ hoa thường và cho phép khoảng trắng
    private static final Pattern IMAGE_URL_TAG_PATTERN =
            Pattern.compile("\\[\\s*IMAGE_URL\\s*:\\s*([^\\]]+?)\\s*\\]", Pattern.CASE_INSENSITIVE);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStoryDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        // Đảm bảo ID toolbarStoryDetail có trong XML của bạn
        setSupportActionBar(binding.toolbarStoryDetail);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        initializeTranslator();

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("SELECTED_STORY_ID")) {
            currentStoryId = intent.getStringExtra("SELECTED_STORY_ID");
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

        // Đảm bảo ID các nút này có trong XML của bạn
        binding.buttonSwitchToEnglish.setOnClickListener(v -> {
            isShowingEnglish = true;
            renderStoryContent();
            updateLanguageButtonUI();
        });

        binding.buttonSwitchToVietnamese.setOnClickListener(v -> {
            isShowingEnglish = false;
            renderStoryContent();
            updateLanguageButtonUI();
        });
        updateLanguageButtonUI();
    }

    private void initializeTranslator() {
        TranslatorOptions options =
                new TranslatorOptions.Builder()
                        .setSourceLanguage(TranslateLanguage.ENGLISH)
                        .setTargetLanguage(TranslateLanguage.VIETNAMESE)
                        .build();
        englishVietnameseTranslator = Translation.getClient(options);
        getLifecycle().addObserver(englishVietnameseTranslator);
        englishVietnameseTranslator.downloadModelIfNeeded()
                .addOnSuccessListener(v -> Log.d("Translator", "Model downloaded or available."))
                .addOnFailureListener(e -> Log.e("Translator", "Model download failed: " + e.getMessage()));
    }

    private void loadStoryDetails() {
        if (currentStoryId == null) return;
        DocumentReference storyRef = db.collection("stories").document(currentStoryId);
        storyRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    currentStoryObject = document.toObject(Story.class);
                    if (currentStoryObject != null) {
                        currentStoryObject.setStoryId(document.getId());
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
        if (currentStoryObject == null) return;
        // Đảm bảo ID imageViewStoryDetailCover có trong XML
        if (currentStoryObject.getCoverImageUrl() != null && !currentStoryObject.getCoverImageUrl().isEmpty()) {
            binding.imageViewStoryDetailCover.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(currentStoryObject.getCoverImageUrl())
                    .placeholder(R.drawable.anh1) // Hoặc ic_book_placeholder
                    .error(R.drawable.anh1)     // Hoặc ic_book_placeholder
                    .into(binding.imageViewStoryDetailCover);
        } else {
            binding.imageViewStoryDetailCover.setVisibility(View.GONE);
        }
        renderStoryContent();
    }

    // Hàm renderStoryContent đã được cập nhật để làm việc với linearLayoutStoryContentHolder
    private void renderStoryContent() {
        if (currentStoryObject == null) {
            Log.e("RenderContent", "currentStoryObject is null. Cannot render content.");
            return;
        }

        String titleToShow;
        String rawContent;

        if (isShowingEnglish) {
            titleToShow = (currentStoryObject.getTitle_en() != null && !currentStoryObject.getTitle_en().isEmpty())
                    ? currentStoryObject.getTitle_en()
                    : (currentStoryObject.getTitle_vi() != null && !currentStoryObject.getTitle_vi().isEmpty() ? currentStoryObject.getTitle_vi() : "Story Title");
            rawContent = currentStoryObject.getContent_en();
            Log.d("StoryDetailActivity", "Rendering English content and title.");
        } else {
            titleToShow = (currentStoryObject.getTitle_vi() != null && !currentStoryObject.getTitle_vi().isEmpty())
                    ? currentStoryObject.getTitle_vi()
                    : (currentStoryObject.getTitle_en() != null && !currentStoryObject.getTitle_en().isEmpty() ? currentStoryObject.getTitle_en() : "Tiêu Đề");
            rawContent = currentStoryObject.getContent_vi();
            Log.d("StoryDetailActivity", "Rendering Vietnamese content and title.");
        }
        // Đảm bảo ID textViewStoryDetailTitle có trong XML
        binding.textViewStoryDetailTitle.setText(titleToShow);

        // Xóa các view cũ trong linearLayoutStoryContentHolder trước khi thêm mới
        // Đảm bảo ID linearLayoutStoryContentHolder có trong XML
        binding.linearLayoutStoryContentHolder.removeAllViews();

        if (rawContent == null || rawContent.isEmpty()) {
            TextView emptyTextView = createTextView(isShowingEnglish ? "Content not available in English." : "Nội dung không có sẵn bằng Tiếng Việt.");
            binding.linearLayoutStoryContentHolder.addView(emptyTextView);
            Log.d("RenderContent", "Raw content is empty or null.");
            return;
        }

        Log.d("RenderContent", "Raw content to parse: \"" + rawContent + "\"");

        Matcher matcher = IMAGE_URL_TAG_PATTERN.matcher(rawContent);
        int lastProcessedEnd = 0;

        while (matcher.find()) {
            if (matcher.start() > lastProcessedEnd) {
                String textSegment = rawContent.substring(lastProcessedEnd, matcher.start());
                Log.d("RenderContent", "Text segment before image: \"" + textSegment + "\"");
                addTextSegmentToLayout(textSegment);
            }
            String imageUrl = matcher.group(1).trim();
            Log.d("RenderContent", "Found image URL: \"" + imageUrl + "\" from tag: \"" + matcher.group(0) + "\"");
            if (!imageUrl.isEmpty()) {
                addImageToLayout(imageUrl);
            }
            lastProcessedEnd = matcher.end();
        }

        if (lastProcessedEnd < rawContent.length()) {
            String remainingTextSegment = rawContent.substring(lastProcessedEnd);
            Log.d("RenderContent", "Remaining text segment after last image: \"" + remainingTextSegment + "\"");
            addTextSegmentToLayout(remainingTextSegment);
        } else if (lastProcessedEnd == 0 && !rawContent.isEmpty()) { // Không có thẻ ảnh nào
            Log.d("RenderContent", "No image tags found. Displaying all as text.");
            addTextSegmentToLayout(rawContent);
        }
    }

    private TextView createTextView(String text) { // Tham số text không còn dùng ở đây
        TextView textView = new TextView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, (int) (8 * getResources().getDisplayMetrics().density));
        textView.setLayoutParams(params);
        textView.setTextSize(18f);
        textView.setTextColor(Color.parseColor("#333333"));
        textView.setLineSpacing(6f, 1.0f);
        return textView;
    }

    private void addTextSegmentToLayout(String textSegment) {
        if (textSegment == null || textSegment.trim().isEmpty()) {
            Log.d("AddText", "Text segment is empty or null, skipping.");
            return;
        }
        Log.d("AddText", "Processing text segment for display: \"" + textSegment.trim() + "\"");
        TextView textView = createTextView(""); // Tạo TextView
        if (isShowingEnglish) {
            setupClickableTextForSegment(textView, textSegment.trim());
        } else {
            textView.setText(textSegment.trim());
            textView.setMovementMethod(null);
        }
        binding.linearLayoutStoryContentHolder.addView(textView);
    }

    private void addImageToLayout(String imageUrl) {
        Log.d("AddImage", "Attempting to add image from URL: " + imageUrl);
        ImageView imageView = new ImageView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, (int) (8 * getResources().getDisplayMetrics().density), 0, (int) (8 * getResources().getDisplayMetrics().density));
        imageView.setLayoutParams(params);
        imageView.setAdjustViewBounds(true);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.anh1) // Bạn cần tạo drawable này
                .error(R.drawable.anh1)
                .into(imageView);
        binding.linearLayoutStoryContentHolder.addView(imageView);
    }

    private void setupClickableTextForSegment(TextView targetTextView, String englishSegment) {
        if (englishSegment == null || englishSegment.isEmpty()) {
            targetTextView.setText(englishSegment);
            return;
        }
        SpannableString spannableString = new SpannableString(englishSegment);
        String[] words = englishSegment.split("\\s+|(?=[.,!?;:])|(?<=[.,!?;:])");
        int startIndexOfWordInOriginal = 0;
        for (final String word : words) {
            if (word.trim().isEmpty()) {
                startIndexOfWordInOriginal += word.length();
                continue;
            }
            int currentWordStart = englishSegment.indexOf(word, startIndexOfWordInOriginal);
            if (currentWordStart == -1) {
                Log.w("ClickableSpan", "Word not found in segment: \"" + word + "\" within \"" + englishSegment + "\" starting from " + startIndexOfWordInOriginal);
                startIndexOfWordInOriginal += word.length();
                continue;
            }
            int currentWordEnd = currentWordStart + word.length();
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {
                    String cleanedWord = word.replaceAll("[.,!?;:]$", "");
                    if (!cleanedWord.trim().isEmpty()) {
                        translateWordAndShowPopup(cleanedWord, (TextView) widget, currentWordStart, currentWordEnd);
                    }
                }
                @Override
                public void updateDrawState(@NonNull TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setUnderlineText(false);
                }
            };
            try {
                spannableString.setSpan(clickableSpan, currentWordStart, currentWordEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } catch (IndexOutOfBoundsException e) {
                Log.e("ClickableSpan", "Error setting span for word: '" + word + "' at [" + currentWordStart + "," + currentWordEnd + "] in segment: '" + englishSegment + "'", e);
            }
            startIndexOfWordInOriginal = currentWordEnd;
        }
        targetTextView.setText(spannableString);
        targetTextView.setMovementMethod(LinkMovementMethod.getInstance());
        targetTextView.setHighlightColor(Color.TRANSPARENT);
    }

    private void translateWordAndShowPopup(final String wordToTranslate, final TextView anchorTextView, final int wordStartOffset, final int wordEndOffset) {
        if (englishVietnameseTranslator == null) { return; }
        englishVietnameseTranslator.translate(wordToTranslate)
                .addOnSuccessListener(translatedText ->
                        showTranslationPopup(anchorTextView, wordStartOffset, wordEndOffset, translatedText, wordToTranslate)
                )
                .addOnFailureListener(e -> Toast.makeText(StoryDetailActivity.this, "Lỗi dịch: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void showTranslationPopup(TextView anchorView, int startOffset, int endOffset, String translatedText, String originalWord) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_translation, null);

        // Đảm bảo ID này khớp với ID trong popup_translation.xml
        TextView tvTranslated = popupView.findViewById(R.id.textViewTranslatedWord); // Hoặc textViewTranslatedWordPopup tùy bạn đặt
        if (tvTranslated == null) {
            Log.e("PopupError", "TextView for translation not found in popup_translation.xml");
            Toast.makeText(this, "Lỗi giao diện popup dịch.", Toast.LENGTH_SHORT).show();
            Toast.makeText(this, originalWord + ": " + translatedText, Toast.LENGTH_LONG).show();
            return;
        }
        String displayText = originalWord + ": " + translatedText;
        tvTranslated.setText(displayText);

        if (translationPopupWindow != null && translationPopupWindow.isShowing()) {
            translationPopupWindow.dismiss();
        }
        translationPopupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        translationPopupWindow.setTouchable(true);
        translationPopupWindow.setFocusable(true);
        translationPopupWindow.setOutsideTouchable(true);
        translationPopupWindow.setBackgroundDrawable(new BitmapDrawable());

        android.text.Layout layout = anchorView.getLayout();
        if (layout == null) {
            translationPopupWindow.showAtLocation(anchorView, Gravity.BOTTOM, 0, anchorView.getHeight());
            return;
        }
        int line = layout.getLineForOffset(startOffset);
        float startX = layout.getPrimaryHorizontal(startOffset);
        float endX = layout.getPrimaryHorizontal(endOffset);
        int lineBaseline = layout.getLineBaseline(line);
        int lineHeight = layout.getLineBottom(line) - layout.getLineTop(line);
        int[] anchorViewLocation = new int[2];
        anchorView.getLocationOnScreen(anchorViewLocation);
        int popupX = anchorViewLocation[0] + (int) ((startX + endX) / 2);
        int popupY = anchorViewLocation[1] + lineBaseline + (lineHeight / 2) ;
        popupView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int popupWidth = popupView.getMeasuredWidth();
        popupX -= (popupWidth / 2);
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        if (popupX < 0) popupX = 0;
        else if (popupX + popupWidth > screenWidth) popupX = screenWidth - popupWidth;
        try {
            translationPopupWindow.showAtLocation(anchorView, Gravity.NO_GRAVITY, popupX, popupY);
        } catch (Exception e) {
            Log.e("PopupWindowError", "Error showing popup: " + e.getMessage());
            Toast.makeText(this, "Dịch '" + originalWord + "': " + translatedText, Toast.LENGTH_LONG).show();
        }
    }

    private void updateLanguageButtonUI() {
        binding.buttonSwitchToEnglish.setSelected(isShowingEnglish);
        binding.buttonSwitchToVietnamese.setSelected(!isShowingEnglish);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
