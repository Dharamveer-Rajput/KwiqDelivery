package com.smartitventures.applicationclass;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.facebook.stetho.Stetho;
import com.kwiqdelivery.R;
import com.smartitventures.RxBus;
import com.smartitventures.di.components.AppComponent;
import com.smartitventures.di.components.DaggerAppComponent;
import com.smartitventures.di.modules.HttpModule;
import com.smartitventures.di.modules.SharedPrefsHelper;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by dharamveer on 22/1/18.
 */

public class AppController extends Application {

    private RxBus bus;
    private AppComponent component;



    @Override
    public void onCreate() {
        super.onCreate();


        Stetho.initializeWithDefaults(this);

        bus = new RxBus();

        component = DaggerAppComponent.builder().sharedPrefsHelper(new SharedPrefsHelper(this))
                .httpModule(new HttpModule(this))
                .build();


        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

    }



    public RxBus bus() {
        return bus;
    }
    public AppComponent getComponent() {
        return component;
    }



    protected void attachBaseContext(Context base) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(base));
        MultiDex.install(this);
    }


}
