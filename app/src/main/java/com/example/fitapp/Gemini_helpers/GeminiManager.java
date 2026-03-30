package com.example.fitapp.Gemini_helpers;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.fitapp.BuildConfig;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.type.BlobPart;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.ai.client.generativeai.type.ImagePart;
import com.google.ai.client.generativeai.type.Part;
import com.google.ai.client.generativeai.type.RequestOptions;
import com.google.ai.client.generativeai.type.TextPart;

import java.util.ArrayList;
import java.util.List;

import kotlin.Result;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;

/**
 * The {@code GeminiManager} class provides a simplified interface for interacting with the Gemini AI model.
 * It handles the initialization of the {@link GenerativeModel} and provides methods for sending text prompts
 * and prompts with images to the model.
 */
public class GeminiManager {
    private static GeminiManager instance;
    private GenerativeModel gemini;
    private final String TAG = "GeminiManager";

    /**
     * Private constructor to enforce the Singleton pattern.
     * Initializes the {@link GenerativeModel} with the specified model name and API key.
     */
    private GeminiManager() {
        String apiKey = BuildConfig.Gemini_API_Key;
        if (apiKey == null || apiKey.isEmpty() || apiKey.equals("YOUR_API_KEY")) {
            Log.e(TAG, "Gemini API Key is missing or invalid! Check local.properties");
        }
        
        // Use gemini-1.5-flash as the stable model for multimodal tasks.
        // Explicitly setting the API version to "v1" instead of "v1beta" to avoid 404 errors 
        // if v1beta is not supported for the specific model/region combination.
        RequestOptions requestOptions = new RequestOptions(null, "v1");
        
        gemini = new GenerativeModel(
                "gemini-2.5-flash",
                apiKey,
                null, // generationConfig
                null, // safetySettings
                requestOptions
        );
    }

    /**
     * Returns the singleton instance of {@code GeminiManager}.
     *
     * @return The singleton instance of {@code GeminiManager}.
     */
    public static GeminiManager getInstance() {
        if (instance == null) {
            instance = new GeminiManager();
        }
        return instance;
    }

    /**
     * Sends a text prompt to the Gemini model and receives a text response.
     *
     * @param prompt   The text prompt to send to the model.
     * @param callback The callback to receive the response or error.
     */
    public void sendTextPrompt(String prompt, GeminiCallback callback) {
        try {
            gemini.generateContent(prompt,
                    new Continuation<GenerateContentResponse>() {
                        @NonNull
                        @Override
                        public CoroutineContext getContext() {
                            return EmptyCoroutineContext.INSTANCE;
                        }

                        @Override
                        public void resumeWith(@NonNull Object result) {
                            try {
                                handleResponse(result, callback);
                            } catch (Exception e) {
                                Log.e(TAG, "Crash during response handling", e);
                                callback.onFailure(e);
                            }
                        }
                    });
        } catch (Exception e) {
            callback.onFailure(e);
        }
    }

    /**
     * Sends a text prompt along with a photo to the Gemini model and receives a text response.
     *
     * @param prompt   The text prompt to send to the model.
     * @param photo    The photo to send to the model.
     * @param callback The callback to receive the response or error.
     */
    public void sendTextWithPhotoPrompt(String prompt, Bitmap photo, GeminiCallback callback) {
        try {
            List<Part> parts = new ArrayList<>();
            parts.add(new TextPart(prompt));
            parts.add(new ImagePart(photo));

            Content[] content = new Content[1];
            content[0] = new Content(parts);

            gemini.generateContent(content,
                    new Continuation<GenerateContentResponse>() {
                        @NonNull
                        @Override
                        public CoroutineContext getContext() {
                            return EmptyCoroutineContext.INSTANCE;
                        }

                        @Override
                        public void resumeWith(@NonNull Object result) {
                            try {
                                handleResponse(result, callback);
                            } catch (Exception e) {
                                Log.e(TAG, "Crash during response handling", e);
                                callback.onFailure(e);
                            }
                        }
                    });
        } catch (Exception e) {
            callback.onFailure(e);
        }
    }

    /**
     * Sends a text prompt along with several photos to the Gemini model and receives a text response.
     *
     * @param prompt    The text prompt to send to the model.
     * @param photos    The files to send to the model.
     * @param callback  The callback to receive the response or error.
     */
    public void sendTextWithPhotosPrompt(String prompt, ArrayList<Bitmap> photos, GeminiCallback callback) {
        try {
            List<Part> parts = new ArrayList<>();
            parts.add(new TextPart(prompt));
            for (Bitmap photo : photos) {
                parts.add(new ImagePart(photo));
            }

            Content[] content = new Content[1];
            content[0] = new Content(parts);

            gemini.generateContent(content,
                    new Continuation<GenerateContentResponse>() {
                        @NonNull
                        @Override
                        public CoroutineContext getContext() {
                            return EmptyCoroutineContext.INSTANCE;
                        }

                        @Override
                        public void resumeWith(@NonNull Object result) {
                            try {
                                handleResponse(result, callback);
                            } catch (Exception e) {
                                Log.e(TAG, "Crash during response handling", e);
                                callback.onFailure(e);
                            }
                        }
                    });
        } catch (Exception e) {
            callback.onFailure(e);
        }
    }

    /**
     * Helper method to handle the response from the Gemini model.
     *
     * @param result   The result from the model.
     * @param callback The callback to receive the response or error.
     */
    private void handleResponse(Object result, GeminiCallback callback) {
        if (result instanceof Result.Failure) {
            Throwable exception = ((Result.Failure) result).exception;
            Log.e(TAG, "Gemini Error: " + exception.getMessage());
            callback.onFailure(exception);
        } else if (result instanceof GenerateContentResponse) {
            String text = ((GenerateContentResponse) result).getText();
            if (text != null) {
                callback.onSuccess(text);
            } else {
                callback.onFailure(new Exception("Response text is null. The request might have been blocked (safety filters) or no content was generated."));
            }
        } else {
            callback.onFailure(new Exception("Unexpected response type from Gemini SDK."));
        }
    }

    /**
     * Sends a text prompt along with a file to the Gemini model and receives a text response.
     *
     * @param prompt    The text prompt to send to the model.
     * @param bytes     The file to send to the model.
     * @param mimeType  The MIME type of the file.
     * @param callback  The callback to receive the response or error.
     */
    public void sendTextWithFilePrompt(String prompt, byte[] bytes, String mimeType, GeminiCallback callback) {
        try {
            List<Part> parts = new ArrayList<>();
            parts.add(new TextPart(prompt));
            parts.add(new BlobPart(mimeType, bytes));

            Content[] content = new Content[1];
            content[0] = new Content(parts);

            gemini.generateContent(content,
                    new Continuation<GenerateContentResponse>() {
                        @NonNull
                        @Override
                        public CoroutineContext getContext() {
                            return EmptyCoroutineContext.INSTANCE;
                        }

                        @Override
                        public void resumeWith(@NonNull Object result) {
                            try {
                                handleResponse(result, callback);
                            } catch (Exception e) {
                                Log.e(TAG, "Crash during response handling", e);
                                callback.onFailure(e);
                            }
                        }
                    });
        } catch (Exception e) {
            callback.onFailure(e);
        }
    }

    /**
     * Sends a text prompt along with several files to the Gemini model and receives a text response.
     *
     * @param prompt    The text prompt to send to the model.
     * @param filesBytes The files to send to the model.
     * @param mimeTypes  The MIME types of the files.
     * @param callback   The callback to receive the response or error.
     */
    public void sendTextWithFilesPrompt(String prompt, ArrayList<byte[]> filesBytes, ArrayList<String> mimeTypes, GeminiCallback callback) {
        try {
            List<Part> parts = new ArrayList<>();
            parts.add(new TextPart(prompt));
            for (int i = 0; i < filesBytes.size(); i++) {
                parts.add(new BlobPart(mimeTypes.get(i), filesBytes.get(i)));
            }

            Content[] content = new Content[1];
            content[0] = new Content(parts);

            gemini.generateContent(content,
                    new Continuation<GenerateContentResponse>() {
                        @NonNull
                        @Override
                        public CoroutineContext getContext() {
                            return EmptyCoroutineContext.INSTANCE;
                        }

                        @Override
                        public void resumeWith(@NonNull Object result) {
                            try {
                                handleResponse(result, callback);
                            } catch (Exception e) {
                                Log.e(TAG, "Crash during response handling", e);
                                callback.onFailure(e);
                            }
                        }
                    });
        } catch (Exception e) {
            callback.onFailure(e);
        }
    }
}
