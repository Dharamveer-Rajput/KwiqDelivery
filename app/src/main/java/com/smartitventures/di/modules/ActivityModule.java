package com.smartitventures.di.modules;

import android.app.Activity;
import android.content.Context;


import com.smartitventures.di.scopes.ActivityContext;
import com.smartitventures.di.scopes.PerActivity;

import dagger.Module;
import dagger.Provides;

@Module
public class ActivityModule {

    private Activity activity;

    public ActivityModule(Activity activity) {
        this.activity = activity;
    }

    @PerActivity
    @Provides
    @ActivityContext
    public Context provideContext() {
        return activity;
    }
}
