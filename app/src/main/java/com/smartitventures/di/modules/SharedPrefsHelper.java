package com.smartitventures.di.modules;

import android.content.Context;
import android.content.SharedPreferences;

import com.smartitventures.applicationclass.AppController;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;


@Module
public class SharedPrefsHelper {

    public static String PREF_KEY_ACCESS_TOKEN = "access-token";


    private SharedPreferences mSharedPreferences;

    public SharedPrefsHelper(AppController appController) {
        mSharedPreferences = appController.getSharedPreferences("hello-car-prefs", Context.MODE_PRIVATE);
    }


    @Provides
    @Singleton
    public SharedPrefsHelper provideSharedPreferences() {
        return this;
    }

    public void put(String key, String value) {
        mSharedPreferences.edit().putString(key, value).apply();
    }

    public void put(String key, int value) {
        mSharedPreferences.edit().putInt(key, value).apply();
    }

    public void put(String key, float value) {
        mSharedPreferences.edit().putFloat(key, value).apply();
    }

    public void put(String key, boolean value) {
        mSharedPreferences.edit().putBoolean(key, value).apply();
    }

    public String get(String key, String defaultValue) {
        return mSharedPreferences.getString(key, defaultValue);
    }

    public Integer get(String key, int defaultValue) {
        return mSharedPreferences.getInt(key, defaultValue);
    }

    public Float get(String key, float defaultValue) {
        return mSharedPreferences.getFloat(key, defaultValue);
    }

    public Boolean get(String key, boolean defaultValue) {
        return mSharedPreferences.getBoolean(key, defaultValue);
    }

    public void deleteSavedData(String key) {
        mSharedPreferences.edit().remove(key).apply();
    }
}
