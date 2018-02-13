package com.smartitventures.quickdelivery;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Toast;

import com.kwiqdelivery.R;
import com.rilixtech.Country;
import com.rilixtech.CountryCodePicker;
import com.smartitventures.AppConstants;
import com.smartitventures.BaseActivity;
import com.smartitventures.Dialog.EnterOTPDialog;
import com.smartitventures.Response.PhoneNoSuccess.PhoneNoSuccess;
import com.taishi.flipprogressdialog.FlipProgressDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import am.appwise.components.ni.NoInternetDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class LoginActivity extends BaseActivity {


    @BindView(R.id.btnSignIn)
    Button btnSignIn;

    String phoneNo;


    Context context;

    FlipProgressDialog fpd;
    NoInternetDialog noInternetDialog;
    @BindView(R.id.ccp)
    CountryCodePicker ccp;
    @BindView(R.id.edPhoneNo)
    AppCompatEditText edPhoneNo;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        context = this;

        noInternetDialog = new NoInternetDialog.Builder(context).build();


        ccp.registerPhoneNumberTextView(edPhoneNo);


        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected(Country country) {


            }
        });

    }


    public void flipProgress() {

        // Set imageList
        List<Integer> imageList = new ArrayList<Integer>();
        imageList.add(R.drawable.foodorderfill);


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


        fpd.show(getFragmentManager(), "");                        // Show flip-progress-dialg


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        noInternetDialog.onDestroy();
        compositeDisposable.dispose();
    }


    @OnClick(R.id.btnSignIn)
    public void onViewClicked() {


        flipProgress();

        if(TextUtils.isEmpty(edPhoneNo.getText().toString()))

        {
            if (TextUtils.isEmpty(edPhoneNo.getText().toString().trim()))
            {
                fpd.dismiss();
                edPhoneNo.setError("Enter Mobile No");

            }
        }
        else {

            phoneNo = edPhoneNo.getText().toString().trim();

            String codeWithPh = ccp.getFullNumberWithPlus();

            compositeDisposable.add(apiService.isPhoneNoExist(codeWithPh)
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<PhoneNoSuccess>() {
                        @Override
                        public void accept(PhoneNoSuccess phoneNoSuccess) throws Exception {

                            if (phoneNoSuccess.getIsSuccess()) {

                                fpd.dismiss();

                                sharedPrefsHelper.put(AppConstants.PHONE_NUMBER, codeWithPh);

                                EnterOTPDialog enterOTPDialog = new EnterOTPDialog(LoginActivity.this);
                                enterOTPDialog.show();

                            } else {

                                fpd.dismiss();
                                showAlertDialog("Retry", phoneNoSuccess.getMessage());
                                edPhoneNo.setText("");

                            }

                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {

                            fpd.dismiss();
                            showAlertDialog("Retry", throwable.getMessage());

                        }
                    }));



        }
    }


}
