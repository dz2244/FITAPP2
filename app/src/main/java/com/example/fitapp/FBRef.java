package com.example.fitapp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Helper class that provides static references to Firebase services and database paths.
 * Centralizes all Firebase Authentication, Realtime Database, and Storage interactions.
 */
public class FBRef {
    /** Static reference to Firebase Authentication. */
    public static FirebaseAuth refAuth = FirebaseAuth.getInstance();

    /** Static reference to the Firebase Realtime Database instance. */
    public static FirebaseDatabase FBDB = FirebaseDatabase.getInstance();
    /** Root reference for general data in the Realtime Database. */
    public static DatabaseReference refData = FBDB.getReference("Data");
    
    /** Reference to the 'Users' node in the Realtime Database. */
    public static DatabaseReference refUsers = FBDB.getReference("Users");
    /** Reference to the 'WorkoutPrograms' node in the Realtime Database. */
    public static DatabaseReference refWorkoutPrograms = FBDB.getReference("WorkoutPrograms");
    /** Reference to the 'TrainingWeeks' node in the Realtime Database. */
    public static DatabaseReference refTrainingWeeks = FBDB.getReference("TrainingWeeks");
    /** Reference to the 'MealEntries' node in the Realtime Database. */
    public static DatabaseReference refMealEntries = FBDB.getReference("MealEntries");
    /** Reference to the 'SleepSessions' node in the Realtime Database. */
    public static DatabaseReference refSleepSessions = FBDB.getReference("SleepSessions");
    /** Reference to the 'ContentArticles' node in the Realtime Database. */
    public static DatabaseReference refContentArticles = FBDB.getReference("ContentArticles");

    /** Static reference to the Firebase Storage instance. */
    public static FirebaseStorage FBST = FirebaseStorage.getInstance();
    /** Root reference for Firebase Storage. */
    public static StorageReference refST = FBST.getReference();
}
