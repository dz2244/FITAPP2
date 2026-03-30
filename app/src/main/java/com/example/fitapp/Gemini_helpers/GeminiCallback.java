package com.example.fitapp.Gemini_helpers;

public interface GeminiCallback {
    void onSuccess(String result);
    void onFailure(Throwable error);
}