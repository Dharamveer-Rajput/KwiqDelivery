package com.smartitventures;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.kwiqdelivery.R;
import com.smartitventures.applicationclass.AppController;
import com.smartitventures.Network.ApiService;
import com.smartitventures.di.modules.SharedPrefsHelper;
import com.taishi.flipprogressdialog.FlipProgressDialog;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import am.appwise.components.ni.NoInternetDialog;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by dharamveer on 30/1/18.
 */

public class BaseActivity extends AppCompatActivity
{



    @Inject
    public ApiService apiService;


    @Inject
    public SharedPrefsHelper sharedPrefsHelper;

    protected Unbinder unbinder;

    public FlipProgressDialog fpd;
    public NoInternetDialog noInternetDialog;

    @Override
    public void setContentView(int layoutRedID) {
        super.setContentView(layoutRedID);
        ((AppController) getApplication()).getComponent().inject(this);
        unbinder = ButterKnife.bind(this);

    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();

    }





    public void flipProgress(){

        // Set imageList
        List<Integer> imageList = new ArrayList<Integer>();
        imageList.add(R.drawable.loader);


        fpd = new FlipProgressDialog();

        fpd.setImageList(imageList);                              // *Set a imageList* [Have to. Transparent background png recommended]
        fpd.setCanceledOnTouchOutside(true);                      // If true, the dialog will be dismissed when user touch outside of the dialog. If false, the dialog won't be dismissed.
        fpd.setDimAmount(0.0f);                                   // Set a dim (How much dark outside of dialog)

        // About dialog shape, color
        fpd.setBackgroundColor(Color.parseColor("#e0e0e0"));      // Set a background color of dialog
        fpd.setBackgroundAlpha(0.2f);                             // Set a alpha color of dialog
        fpd.setBorderStroke(0);                                   // Set a width of border stroke of dialog
        fpd.setBorderColor(-1);                                   // Set a border stroke color of dialog
        fpd.setCornerRadius(16);                                  // Set a corner radius

        // About image
        fpd.setImageSize(200);                                    // Set an image size
        fpd.setImageMargin(10);                                   // Set a margin of image

        // About rotation
        fpd.setOrientation("rotationY");                          // Set a flipping rotation
        fpd.setDuration(600);                                     // Set a duration time of flipping ratation
        fpd.setStartAngle(0.0f);                                  // Set an angle when flipping ratation start
        fpd.setEndAngle(180.0f);                                  // Set an angle when flipping ratation end
        fpd.setMinAlpha(0.0f);                                    // Set an alpha when flipping ratation start and end
        fpd.setMaxAlpha(1.0f);                                    // Set an alpha while image is flipping


        fpd.show(getFragmentManager(),"");                        // Show flip-progress-dialg


    }


    public void navigateToNextActivity(Class cla){

        startActivity(new Intent(BaseActivity.this,cla));
        overridePendingTransition(R.anim.enter, R.anim.exit);

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ((AppController) getApplication()).getComponent().inject(this);
        super.onCreate(savedInstanceState);
        noInternetDialog =  new NoInternetDialog.Builder(BaseActivity.this).build();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
        noInternetDialog.onDestroy();
    }



    public void showAlertDialog(String action,String message){

        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage(message);
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                action,


                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();

                    }
                });


        AlertDialog alert11 = builder1.create();
        alert11.show();


    }





}
