package com.smartitventures.quickdelivery;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.kwiqdelivery.R;
import com.smartitventures.SharedPreferences.UserDataUtility;

import am.appwise.components.ni.NoInternetDialog;

public class SplashActivity extends AppCompatActivity {


    // Splash screen timer
    private static int SPLASH_TIME_OUT = 1000;

    Context context;

    NoInternetDialog noInternetDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //setContentView(R.layout.activity_splash);

        context  = this;

         noInternetDialog = new NoInternetDialog.Builder(context).build();


        new Handler().postDelayed(new Runnable() {


            @Override
            public void run() {



                if(UserDataUtility.getLogin(SplashActivity.this)){  //true

                    Intent intent = new Intent(context, DashboardActivity.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                    return;
                }
                else {  //false

                    Intent intent = new Intent(context, LoginActivity.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }






            }
        }, SPLASH_TIME_OUT);



    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        noInternetDialog.onDestroy();
    }

}
