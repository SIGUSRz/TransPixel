package com.sz1358.transpixel;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.ArrayList;

public class SharedPrefManager {
    private static final String SHARED_PREF_NAME = "pref";
    private static final String KEY_USERNAME = "key_username";
    private static final String KEY_EMAIL = "key_email";
    private static final String KEY_ID = "key_id";
    private static final String KEY_LANG = "key_lang";
    private static final String KEY_LANGSTRING = "key_langstring";
    private static final String KEY_DICT = "key_dict";

    private static SharedPrefManager prefManager;
    private static Context ctx;

    private SharedPrefManager(Context context) {
        ctx = context.getApplicationContext();
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (prefManager == null) {
            prefManager = new SharedPrefManager(context.getApplicationContext());
        }
        return prefManager;
    }

    // Let user login and share user information among session
    public void userLogin(User user) {
        SharedPreferences preferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(KEY_ID, user.getId());
        editor.putString(KEY_USERNAME, user.getUsername());
        editor.putString(KEY_EMAIL, user.getEmail());
        editor.putInt(KEY_LANG, user.getLang());
        editor.apply();
    }

    public void setLang(int lang, String langString) {
        SharedPreferences preferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(KEY_LANG, lang);
        editor.putString(KEY_LANGSTRING, langString);
        editor.apply();
    }

    public void updateDict(String word, String uri, String language) {
        SharedPreferences preferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        Tuple tuple = new Tuple(word, uri, language);
        String dict = preferences.getString(KEY_DICT, null);
        System.out.println(dict);
        if (dict == null) { dict = "[]"; }
        dict = new StringBuilder(dict).insert(dict.length() - 1, gson.toJson(tuple)).toString();
        editor.putString(KEY_DICT, dict);
        editor.apply();
    }

    public void setDict(String dict) {
        SharedPreferences preferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_DICT, dict);
        editor.apply();
    }

    public boolean isLogged() {
        SharedPreferences preferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return preferences.getString(KEY_USERNAME, null) != null;
    }

    public User getLoggedUser() {
        SharedPreferences preferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return new User(
                preferences.getInt(KEY_ID, -1),
                preferences.getString(KEY_USERNAME, null),
                preferences.getString(KEY_EMAIL, null),
                preferences.getInt(KEY_LANG, 0)
        );
    }

    public String getLanString() {
        SharedPreferences preferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return preferences.getString(KEY_LANGSTRING, null);
    }

    public String getDict() {
        SharedPreferences preferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return preferences.getString(KEY_DICT, null);
    }

    public void logout() {
        SharedPreferences preferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }

}
