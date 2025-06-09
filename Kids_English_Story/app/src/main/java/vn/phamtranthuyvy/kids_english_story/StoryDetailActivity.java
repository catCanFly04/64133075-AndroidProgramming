package vn.phamtranthuyvy.kids_english_story;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
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
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
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

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import vn.phamtranthuyvy.kids_english_story.databinding.ActivityStoryDetailBinding;

public class StoryDetailActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private static final String TTS_DEBUG_TAG = "TTS_DEBUG";
    private ActivityStoryDetailBinding binding;
    private FirebaseFirestore db;
    private String currentStoryId;
    private Story currentStoryObject;
    private boolean isShowingEnglish = true;
    private Translator englishVietnameseTranslator;
    private PopupWindow translationPopupWindow;

    private TextToSpeech textToSpeech;
    private boolean isTtsInitialized = false;
    private boolean isSpeaking = false;
    private final String UTTERANCE_ID = "KidsStoryUtteranceId";

    private static final Pattern IMAGE_URL_TAG_PATTERN =
            Pattern.compile("\\[\\s*IMAGE_URL\\s*:\\s*([^\\]]+?)\\s*\\]", Pattern.CASE_INSENSITIVE);


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

        initializeTranslator(); // Gọi hàm

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("SELECTED_STORY_ID")) {
            currentStoryId = intent.getStringExtra("SELECTED_STORY_ID");
            if (currentStoryId != null && !currentStoryId.isEmpty()) {
                loadStoryDetails(); // Gọi hàm
            } else {
                Toast.makeText(this, "Lỗi: Không nhận được ID truyện.", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(this, "Lỗi: Không có thông tin truyện.", Toast.LENGTH_SHORT).show();
            finish();
        }

        binding.buttonSwitchToEnglish.setOnClickListener(v -> {
            stopSpeaking();
            isShowingEnglish = true;
            renderStoryContent(); // Gọi hàm
            updateLanguageButtonUI(); // Gọi hàm
        });

        binding.buttonSwitchToVietnamese.setOnClickListener(v -> {
            stopSpeaking();
            isShowingEnglish = false;
            renderStoryContent(); // Gọi hàm
            updateLanguageButtonUI(); // Gọi hàm
        });
        updateLanguageButtonUI(); // Gọi hàm

        Log.d(TTS_DEBUG_TAG, "Initializing TTS...");
        textToSpeech = new TextToSpeech(this, this);

        binding.fabSpeakStory.setOnClickListener(v -> {
            Log.d(TTS_DEBUG_TAG, "FAB Speak button clicked. isSpeaking = " + isSpeaking);
            if (isSpeaking) {
                stopSpeaking();
            } else {
                speakOut();
            }
        });
    }

    @Override
    public void onInit(int status) {
        Log.d(TTS_DEBUG_TAG, "onInit called with status: " + status);
        if (status == TextToSpeech.SUCCESS) {
            isTtsInitialized = true;
            Log.d(TTS_DEBUG_TAG, "TTS Initialization successful.");
            int result = textToSpeech.setLanguage(Locale.US);
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e(TTS_DEBUG_TAG, "Default language (US English) is not supported!");
            }

            textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onStart(String utteranceId) {
                    runOnUiThread(() -> {
                        isSpeaking = true;
                        binding.fabSpeakStory.setImageResource(R.drawable.ic_stop_speaking);
                    });
                }
                @Override
                public void onDone(String utteranceId) {
                    runOnUiThread(() -> {
                        isSpeaking = false;
                        binding.fabSpeakStory.setImageResource(R.drawable.ic_speaker_on);
                    });
                }
                @Override
                public void onError(String utteranceId) {
                    runOnUiThread(() -> {
                        isSpeaking = false;
                        binding.fabSpeakStory.setImageResource(R.drawable.ic_speaker_on);
                        Toast.makeText(StoryDetailActivity.this, "Lỗi khi đọc truyện.", Toast.LENGTH_SHORT).show();
                    });
                }
            });

        } else {
            Log.e(TTS_DEBUG_TAG, "TTS Initialization Failed! Status code: " + status);
            isTtsInitialized = false;
            binding.fabSpeakStory.setEnabled(false);
            Toast.makeText(this, "Không thể khởi tạo chức năng đọc.", Toast.LENGTH_LONG).show();
        }
    }

    private void speakOut() {
        Log.d(TTS_DEBUG_TAG, "speakOut() called.");
        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int streamVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        if (streamVolume == 0) {
            Log.w(TTS_DEBUG_TAG, "Media volume is 0. Informing user.");
            Toast.makeText(this, "Âm lượng Media đang tắt, hãy tăng âm lượng để nghe.", Toast.LENGTH_SHORT).show();
        }

        if (!isTtsInitialized || currentStoryObject == null) {
            Toast.makeText(this, "Chức năng đọc chưa sẵn sàng.", Toast.LENGTH_SHORT).show();
            return;
        }

        String titleToRead = isShowingEnglish ? currentStoryObject.getTitle_en() : currentStoryObject.getTitle_vi();
        String contentToRead = isShowingEnglish ? currentStoryObject.getContent_en() : currentStoryObject.getContent_vi();
        Locale languageToSet = isShowingEnglish ? Locale.US : new Locale("vi", "VN");

        String textToRead = (titleToRead != null ? titleToRead : "") + ". " + (contentToRead != null ? contentToRead : "");
        if (textToRead.trim().equals(".")) {
            Toast.makeText(this, "Không có nội dung để đọc.", Toast.LENGTH_SHORT).show();
            return;
        }

        textToRead = textToRead.replaceAll("\\[\\s*IMAGE_URL\\s*:[^\\]]+?\\]", "");
        Log.d(TTS_DEBUG_TAG, "Cleaned text to read: \"" + textToRead + "\"");

        int result = textToSpeech.setLanguage(languageToSet);
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            Toast.makeText(this, "Giọng đọc " + languageToSet.getDisplayLanguage() + " chưa được cài đặt trên điện thoại này.", Toast.LENGTH_LONG).show();
            return;
        }

        textToSpeech.speak(textToRead, TextToSpeech.QUEUE_FLUSH, null, UTTERANCE_ID);
    }

    private void stopSpeaking() {
        Log.d(TTS_DEBUG_TAG, "stopSpeaking() called.");
        if (textToSpeech != null && textToSpeech.isSpeaking()) {
            textToSpeech.stop();
        }
        isSpeaking = false;
        binding.fabSpeakStory.setImageResource(R.drawable.ic_speaker_on);
    }

    @Override
    protected void onDestroy() {
        Log.d(TTS_DEBUG_TAG, "onDestroy() called. Shutting down TTS.");
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }

    // Định nghĩa lại các hàm bị thiếu
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
                        Toast.makeText(StoryDetailActivity.this, "Lỗi: Dữ liệu truyện không hợp lệ.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(StoryDetailActivity.this, "Lỗi: Không tìm thấy truyện.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(StoryDetailActivity.this, "Lỗi khi tải chi tiết truyện.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateInitialUI() {
        if (currentStoryObject == null) return;

        setDefaultContentBackground();

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
        renderStoryContent();
    }

    private void setDefaultContentBackground() {
        binding.linearLayoutStoryContentHolder.setBackgroundColor(Color.WHITE);
        if (binding.linearLayoutStoryContentHolder.getParent() instanceof View) {
            ((View)binding.linearLayoutStoryContentHolder.getParent()).setBackgroundColor(Color.WHITE);
        }
    }

    private void renderStoryContent() {
        if (currentStoryObject == null) { return; }
        String titleToShow = isShowingEnglish ? currentStoryObject.getTitle_en() : currentStoryObject.getTitle_vi();
        binding.textViewStoryDetailTitle.setText(titleToShow);
        String rawContent = isShowingEnglish ? currentStoryObject.getContent_en() : currentStoryObject.getContent_vi();
        binding.linearLayoutStoryContentHolder.removeAllViews();
        if (rawContent == null || rawContent.isEmpty()) {
            TextView emptyTextView = createTextView(isShowingEnglish ? "Content not available." : "Nội dung không có sẵn.");
            binding.linearLayoutStoryContentHolder.addView(emptyTextView);
            return;
        }
        Matcher matcher = IMAGE_URL_TAG_PATTERN.matcher(rawContent);
        int lastProcessedEnd = 0;
        while (matcher.find()) {
            if (matcher.start() > lastProcessedEnd) {
                addTextSegmentToLayout(rawContent.substring(lastProcessedEnd, matcher.start()));
            }
            String imageUrl = matcher.group(1).trim();
            if (!imageUrl.isEmpty()) { addImageToLayout(imageUrl); }
            lastProcessedEnd = matcher.end();
        }
        if (lastProcessedEnd < rawContent.length()) {
            addTextSegmentToLayout(rawContent.substring(lastProcessedEnd));
        } else if (lastProcessedEnd == 0 && !rawContent.isEmpty()) {
            addTextSegmentToLayout(rawContent);
        }
    }

    private TextView createTextView(String text) {
        TextView textView = new TextView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, (int) (8 * getResources().getDisplayMetrics().density));
        textView.setLayoutParams(params);
        textView.setTextSize(30f);
        textView.setTextColor(Color.parseColor("#F29705"));
        textView.setLineSpacing(8f, 1.1f);
        textView.setText(text);
        return textView;
    }

    private void addTextSegmentToLayout(String textSegment) {
        if (textSegment == null || textSegment.trim().isEmpty()) { return; }
        TextView textView = createTextView("");
        if (isShowingEnglish) {
            setupClickableTextForSegment(textView, textSegment.trim());
        } else {
            textView.setText(textSegment.trim());
            textView.setMovementMethod(null);
        }
        binding.linearLayoutStoryContentHolder.addView(textView);
    }

    private void addImageToLayout(String imageUrl) {
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
                .placeholder(R.drawable.anh1)
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
                Log.e("ClickableSpan", "Error setting span: " + e.getMessage());
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
        TextView tvTranslated = popupView.findViewById(R.id.textViewTranslatedWord);
        if (tvTranslated == null) {
            Toast.makeText(this, "Lỗi giao diện popup dịch.", Toast.LENGTH_SHORT).show();
            Toast.makeText(this, originalWord + ": " + translatedText, Toast.LENGTH_LONG).show();
            return;
        }
        tvTranslated.setText(translatedText);
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
