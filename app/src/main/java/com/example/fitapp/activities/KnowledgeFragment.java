package com.example.fitapp.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitapp.R;
import com.example.fitapp.classes.ArticlesAdapter;
import com.example.fitapp.classes.ContentArticle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Fragment that provides users with educational content and articles.
 * Helps users gain knowledge about fitness, nutrition, and wellness.
 */
public class KnowledgeFragment extends Fragment {

    private RecyclerView rvArticles;
    private ArticlesAdapter adapter;
    private List<ContentArticle> articleList;

    public KnowledgeFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_knowledge, container, false);
        
        rvArticles = view.findViewById(R.id.rvArticles);
        rvArticles.setLayoutManager(new LinearLayoutManager(getContext()));
        
        loadArticles();
        
        adapter = new ArticlesAdapter(articleList, getContext());
        rvArticles.setAdapter(adapter);
        
        return view;
    }

    private void loadArticles() {
        articleList = new ArrayList<>();
        
        // 1. Master Squat Form
        Map<String, Integer> cat1 = new HashMap<>();
        cat1.put("Training", 1);
        cat1.put("Form", 1);
        articleList.add(new ContentArticle(
                "1",
                "Mastering Squat Form",
                cat1,
                "The squat is the king of all exercises. Proper form is crucial: keep your chest up, sit back into your hips, and ensure your knees don't cave in.",
                "https://www.youtube.com/watch?v=gcNh17Ckjgg"
        ));

        // 2. Deadlift Basics
        Map<String, Integer> catDL = new HashMap<>();
        catDL.put("Training", 1);
        catDL.put("Form", 1);
        articleList.add(new ContentArticle(
                "2",
                "How to Deadlift Safely",
                catDL,
                "Deadlifting builds incredible total-body strength. Learn how to hinge at the hips and keep the bar close to your shins to protect your back.",
                "https://www.youtube.com/watch?v=Xs3-2E-Y_4M"
        ));

        // 3. Nutrition: Protein
        Map<String, Integer> cat2 = new HashMap<>();
        cat2.put("Nutrition", 1);
        articleList.add(new ContentArticle(
                "3",
                "Protein: The Building Block",
                cat2,
                "Muscle protein synthesis requires adequate intake. Aim for 1.6g to 2.2g of protein per kilogram of body weight for optimal growth.",
                "https://www.youtube.com/watch?v=wv4z9_Vn7S4"
        ));

        // 4. Progressive Overload
        Map<String, Integer> catPO = new HashMap<>();
        catPO.put("Principles", 1);
        catPO.put("Growth", 1);
        articleList.add(new ContentArticle(
                "4",
                "What is Progressive Overload?",
                catPO,
                "To keep growing, you must challenge your muscles. This means gradually increasing weight, reps, or sets over time.",
                "https://www.youtube.com/watch?v=iywsjH_0M-I"
        ));

        // 5. Sleep & Recovery
        Map<String, Integer> cat3 = new HashMap<>();
        cat3.put("Recovery", 1);
        articleList.add(new ContentArticle(
                "5",
                "Recovery: When the Magic Happens",
                cat3,
                "Your body repairs itself during deep sleep. Without rest, your performance will plateau and your risk of injury increases.",
                "https://www.youtube.com/watch?v=nm1TxQj9IsQ"
        ));

        // 6. Gym Etiquette
        Map<String, Integer> catEt = new HashMap<>();
        catEt.put("General", 1);
        articleList.add(new ContentArticle(
                "6",
                "Gym Etiquette for Beginners",
                catEt,
                "New to the gym? Learn the unwritten rules: re-rack your weights, wipe down equipment, and don't hoard machines.",
                "https://www.youtube.com/watch?v=6Yv9p_9n9qY"
        ));

        // 7. Breathing Techniques
        Map<String, Integer> catBr = new HashMap<>();
        catBr.put("Training", 1);
        catBr.put("Form", 1);
        articleList.add(new ContentArticle(
                "7",
                "Breathing During Lifts",
                catBr,
                "Exhale on the exertion, inhale on the way down. Mastering the Valsalva maneuver can help stabilize your core during heavy lifts.",
                "https://www.youtube.com/watch?v=PJX1CyjbMic"
        ));

        // 8. Pre-Workout Warmup
        Map<String, Integer> catWu = new HashMap<>();
        catWu.put("Training", 1);
        catWu.put("Safety", 1);
        articleList.add(new ContentArticle(
                "8",
                "The Ultimate Warm-up Routine",
                catWu,
                "Static stretching isn't for the start of your workout. Use dynamic movements to increase blood flow and prime your nervous system.",
                "https://www.youtube.com/watch?v=R0mMyV5OtcM"
        ));
    }
}
