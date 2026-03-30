package com.example.fitapp.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.fitapp.R;
import com.example.fitapp.Gemini_helpers.GeminiCallback;
import com.example.fitapp.Gemini_helpers.GeminiManager;
import com.example.fitapp.classes.FBRef;
import com.example.fitapp.classes.MealEntry;
import com.example.fitapp.classes.Prompts;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Fragment responsible for tracking the user's daily calorie intake.
 * Users can log meals by scanning food photos using Gemini AI.
 */
public class CalorieTrackingFragment extends Fragment {

    private TextView foodNameDisplay, caloriesDisplay, proteinDisplay, carbsDisplay, fatsDisplay;
    private Button addMealBtn, scanFoodBtn;
    private final String TAG = "CalorieTrackingFragment";

    private ActivityResultLauncher<String> selectImagesLauncher;

    public CalorieTrackingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Initialize the image picker launcher
        selectImagesLauncher = registerForActivityResult(
                new ActivityResultContracts.GetMultipleContents(),
                uris -> {
                    if (uris != null && !uris.isEmpty()) {
                        processSelectedImages(uris);
                    }
                }
        );
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calorie_tracking, container, false);

        // Initialize UI components
        foodNameDisplay = view.findViewById(R.id.foodNameDisplay);
        caloriesDisplay = view.findViewById(R.id.caloriesDisplay);
        proteinDisplay = view.findViewById(R.id.proteinDisplay);
        carbsDisplay = view.findViewById(R.id.carbsDisplay);
        fatsDisplay = view.findViewById(R.id.fatsDisplay);
        addMealBtn = view.findViewById(R.id.addMealBtn);
        scanFoodBtn = view.findViewById(R.id.scanFoodBtn);

        addMealBtn.setOnClickListener(v -> saveMealEntry());

        scanFoodBtn.setOnClickListener(v -> {
            // Launch image picker to select several pictures of food
            selectImagesLauncher.launch("image/*");
        });

        return view;
    }

    /**
     * Converts selected Uris to Bitmaps and sends them to Gemini for analysis.
     */
    private void processSelectedImages(List<Uri> uris) {
        ArrayList<Bitmap> bitmaps = new ArrayList<>();
        try {
            for (Uri uri : uris) {
                InputStream inputStream = getContext().getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                if (bitmap != null) {
                    bitmaps.add(bitmap);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading images", e);
            Toast.makeText(getContext(), "Error loading images", Toast.LENGTH_SHORT).show();
            return;
        }

        if (bitmaps.isEmpty()) return;

        Toast.makeText(getContext(), "Analyzing food photos...", Toast.LENGTH_LONG).show();
        scanFoodBtn.setEnabled(false);

        // Use GeminiManager to analyze the photos
        GeminiManager.getInstance().sendTextWithPhotosPrompt(Prompts.PHOTOS_PROMPT, bitmaps, new GeminiCallback() {
            @Override
            public void onSuccess(String result) {
                if (getActivity() == null) return;
                
                getActivity().runOnUiThread(() -> {
                    scanFoodBtn.setEnabled(true);
                    parseAndPopulateNutrition(result);
                });
            }

            @Override
            public void onFailure(Throwable error) {
                if (getActivity() == null) return;

                getActivity().runOnUiThread(() -> {
                    scanFoodBtn.setEnabled(true);
                    Log.e(TAG, "Gemini analysis failed", error);
                    Toast.makeText(getContext(), "Failed to analyze food: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    /**
     * Parses the JSON result from Gemini and populates the display fields.
     */
    private void parseAndPopulateNutrition(String result) {
        try {
            // Clean the result (Gemini sometimes wraps JSON in markdown)
            String cleanJson = result;
            if (result.contains("```json")) {
                cleanJson = result.substring(result.indexOf("```json") + 7, result.lastIndexOf("```"));
            } else if (result.contains("```")) {
                cleanJson = result.substring(result.indexOf("```") + 3, result.lastIndexOf("```"));
            }
            cleanJson = cleanJson.trim();

            JSONArray jsonArray = new JSONArray(cleanJson);
            
            double totalCalories = 0;
            double totalProtein = 0;
            double totalCarbs = 0;
            double totalFats = 0;
            StringBuilder foodNames = new StringBuilder();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);
                totalCalories += item.optDouble("calories", 0);
                totalProtein += item.optDouble("protein", 0);
                totalCarbs += item.optDouble("carbohydrates", 0);
                totalFats += item.optDouble("fat", 0);
                
                if (i > 0) foodNames.append(", ");
                foodNames.append(item.optString("name", ""));
            }

            // Populate the fields
            foodNameDisplay.setText(foodNames.toString());
            caloriesDisplay.setText(String.valueOf((int) totalCalories));
            proteinDisplay.setText(String.format(Locale.US, "%.1f", totalProtein));
            carbsDisplay.setText(String.format(Locale.US, "%.1f", totalCarbs));
            fatsDisplay.setText(String.format(Locale.US, "%.1f", totalFats));

            Toast.makeText(getContext(), "Nutrition details updated!", Toast.LENGTH_SHORT).show();

        } catch (JSONException e) {
            Log.e(TAG, "Error parsing Gemini response", e);
            Toast.makeText(getContext(), "Could not parse nutrition data. Please check the image or try again.", Toast.LENGTH_LONG).show();
            Log.d(TAG, "Raw Gemini result: " + result);
        }
    }

    private void saveMealEntry() {
        String foodName = foodNameDisplay.getText().toString().trim();
        String caloriesStr = caloriesDisplay.getText().toString().trim();
        String proteinStr = proteinDisplay.getText().toString().trim();
        String carbsStr = carbsDisplay.getText().toString().trim();
        String fatsStr = fatsDisplay.getText().toString().trim();

        if (TextUtils.isEmpty(foodName) || TextUtils.isEmpty(caloriesStr)) {
            Toast.makeText(getContext(), "Please scan food first to get nutrition details", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int calories = Integer.parseInt(caloriesStr);
            double protein = TextUtils.isEmpty(proteinStr) ? 0 : Double.parseDouble(proteinStr);
            double carbs = TextUtils.isEmpty(carbsStr) ? 0 : Double.parseDouble(carbsStr);
            double fats = TextUtils.isEmpty(fatsStr) ? 0 : Double.parseDouble(fatsStr);

            String mealId = FBRef.refMealEntries.push().getKey();
            MealEntry mealEntry = new MealEntry(mealId, foodName, calories, protein, carbs, fats, null);

            String userId = FBRef.refAuth.getCurrentUser().getUid();
            if (userId != null) {
                FBRef.refUsers.child(userId).child("meals").child(mealId).setValue(mealEntry)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(getContext(), "Meal added successfully!", Toast.LENGTH_SHORT).show();
                                clearDisplays();
                            } else {
                                Toast.makeText(getContext(), "Failed to add meal.", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Invalid nutrition data found", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearDisplays() {
        foodNameDisplay.setText("");
        caloriesDisplay.setText("");
        proteinDisplay.setText("");
        carbsDisplay.setText("");
        fatsDisplay.setText("");
    }
}
