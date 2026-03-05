package com.example.fitapp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FBRef {
    public static FirebaseAuth refAuth = FirebaseAuth.getInstance();

    public static FirebaseDatabase FBDB = FirebaseDatabase.getInstance();
    public static DatabaseReference refData = FBDB.getReference("Data");
    
    public static DatabaseReference refUsers = FBDB.getReference("Users");
    public static DatabaseReference refWorkoutPrograms = FBDB.getReference("WorkoutPrograms");
    public static DatabaseReference refTrainingWeeks = FBDB.getReference("TrainingWeeks");
    public static DatabaseReference refMealEntries = FBDB.getReference("MealEntries");
    public static DatabaseReference refSleepSessions = FBDB.getReference("SleepSessions");
    public static DatabaseReference refContentArticles = FBDB.getReference("ContentArticles");

    public static FirebaseStorage FBST = FirebaseStorage.getInstance();
    public static StorageReference refST = FBST.getReference();
}
