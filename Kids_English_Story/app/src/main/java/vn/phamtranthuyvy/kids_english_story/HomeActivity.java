package vn.phamtranthuyvy.kids_english_story;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.view.View;
import com.google.firebase.auth.FirebaseAuth;

import vn.phamtranthuyvy.kids_english_story.databinding.ActivityHomeBinding;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;
    private FirebaseAuth mAuth;
    private String currentSelectedAgeGroup = null;

    private static final String PREFS_NAME = "KidsStoryUserPrefs";
    private static final String KEY_SELECTED_AGE_GROUP = "selectedAgeGroup";
    public static final String AGE_GROUP_ALL = "ALL";
    public static final String AGE_GROUP_3_5 = "3-5";
    public static final String AGE_GROUP_6_8 = "6-8";
    private String[] ageGroupValues;

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
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        binding.btnLogout.setOnClickListener(v -> logoutUser());

        ageGroupValues = new String[]{AGE_GROUP_ALL, AGE_GROUP_3_5, AGE_GROUP_6_8};

        setupAgeSpinner();
        loadAgePreferenceAndSetSpinner();

        binding.cardViewFamily.setOnClickListener(v -> openStoryListWithAge(THEME_ID_FAMILY));
        binding.cardViewAnimals.setOnClickListener(v -> openStoryListWithAge(THEME_ID_ANIMALS));
        binding.cardViewBody.setOnClickListener(v -> openStoryListWithAge(THEME_ID_BODY));
        binding.cardViewHome.setOnClickListener(v -> openStoryListWithAge(THEME_ID_HOME));
        binding.cardViewToys.setOnClickListener(v -> openStoryListWithAge(THEME_ID_TOYS));
        binding.cardViewClothes.setOnClickListener(v -> openStoryListWithAge(THEME_ID_CLOTHES));
        binding.cardViewFood.setOnClickListener(v -> openStoryListWithAge(THEME_ID_FOOD));
        binding.cardViewDailyRoutines.setOnClickListener(v -> openStoryListWithAge(THEME_ID_DAILY_ROUTINES));
    }

    private void setupAgeSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.age_group_options_display, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerAgeSelection.setAdapter(adapter);

        binding.spinnerAgeSelection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentSelectedAgeGroup = ageGroupValues[position];
                Log.d("HomeActivity", "Age group selected: " + currentSelectedAgeGroup);
                saveAgePreference(currentSelectedAgeGroup);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
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
        String savedAgeGroup = prefs.getString(KEY_SELECTED_AGE_GROUP, AGE_GROUP_ALL);
        currentSelectedAgeGroup = savedAgeGroup;

        int spinnerPosition = 0;
        for (int i = 0; i < ageGroupValues.length; i++) {
            if (ageGroupValues[i].equals(savedAgeGroup)) {
                spinnerPosition = i;
                break;
            }
        }
        binding.spinnerAgeSelection.setSelection(spinnerPosition);
    }

    // Hàm này sẽ được sử dụng để gửi đi cả themeId và ageGroup
    private void openStoryListWithAge(String themeId) {
        Intent intent = new Intent(HomeActivity.this, StoryListActivity.class);
        intent.putExtra("SELECTED_THEME_ID", themeId);

        Log.d("HomeActivity", "Opening StoryList with Theme: " + themeId + " and Age Group: " + currentSelectedAgeGroup);

        if (currentSelectedAgeGroup != null && !currentSelectedAgeGroup.equals(AGE_GROUP_ALL)) {
            intent.putExtra("SELECTED_AGE_GROUP", currentSelectedAgeGroup);
        }
        startActivity(intent);
    }

    private void logoutUser() {
        mAuth.signOut();
        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
