package com.example.fitapp;

import android.graphics.Bitmap;

/**
 * Represents a single meal entry logged by the user.
 * Tracks nutritional information like calories, proteins, carbs, and fats.
 */
public class MealEntry {
    /** Unique identifier for the meal entry. */
    private String mealId;
    /** Short description of the meal. */
    private String description;
    /** Total calories in the meal. */
    private int calories;
    /** Amount of protein in grams. */
    private double proteinGrams;
    /** Amount of carbohydrates in grams. */
    private double carbsGrams;
    /** Amount of fat in grams. */
    private double fatGrams;
    /** Image of the meal (optional). */
    private Bitmap image;

    /**
     * Default constructor for Firebase serialization.
     */
    public MealEntry() {
    }

    /**
     * Constructs a new MealEntry with nutritional details.
     * @param mealId Unique ID.
     * @param description Meal description.
     * @param calories Total calories.
     * @param proteinGrams Protein content.
     * @param carbsGrams Carbohydrate content.
     * @param fatGrams Fat content.
     * @param image Associated image.
     */
    public MealEntry(String mealId, String description, int calories, double proteinGrams, double carbsGrams, double fatGrams, Bitmap image) {
        this.mealId = mealId;
        this.description = description;
        this.calories = calories;
        this.proteinGrams = proteinGrams;
        this.carbsGrams = carbsGrams;
        this.fatGrams = fatGrams;
        this.image = image;
    }

    /** @return Meal unique ID. */
    public String getMealId() { return mealId; }
    /** @param mealId The ID to set. */
    public void setMealId(String mealId) { this.mealId = mealId; }

    /** @return Meal description. */
    public String getDescription() { return description; }
    /** @param description The description to set. */
    public void setDescription(String description) { this.description = description; }

    /** @return Total calories. */
    public int getCalories() { return calories; }
    /** @param calories The calories to set. */
    public void setCalories(int calories) { this.calories = calories; }

    /** @return Protein in grams. */
    public double getProteinGrams() { return proteinGrams; }
    /** @param proteinGrams The protein to set. */
    public void setProteinGrams(double proteinGrams) { this.proteinGrams = proteinGrams; }

    /** @return Carbs in grams. */
    public double getCarbsGrams() { return carbsGrams; }
    /** @param carbsGrams The carbs to set. */
    public void setCarbsGrams(double carbsGrams) { this.carbsGrams = carbsGrams; }

    /** @return Fat in grams. */
    public double getFatGrams() { return fatGrams; }
    /** @param fatGrams The fat to set. */
    public void setFatGrams(double fatGrams) { this.fatGrams = fatGrams; }

    /** @return Meal image. */
    public Bitmap getImage() { return image; }
    /** @param image The image to set. */
    public void setImage(Bitmap image) { this.image = image; }
}
