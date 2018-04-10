package com.smartitventures.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.kwiqdelivery.R;
import com.smartitventures.AppConstants;
import com.smartitventures.Utils.NetUtils;
import com.smartitventures.applicationclass.AppController;
import com.smartitventures.Network.ApiService;
import com.smartitventures.Response.ValidateOtp.ValidateOTPSuccess;
import com.smartitventures.SharedPreferences.UserDataUtility;
import com.smartitventures.di.modules.SharedPrefsHelper;
import com.smartitventures.quickdelivery.DashboardActivity;

import javax.inject.Inject;

import am.appwise.components.ni.NoInternetDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by dharamveer on 18/1/18.
 */

public class EnterOTPDialog extends Dialog {

    Context context;
    @BindView(R.id.edAccessCode)
    EditText edAccessCode;
    @BindView(R.id.btnSubmit)
    Button btnSubmit;

    @BindView(R.id.edPhoneNo)
    AppCompatEditText edPhoneNO;

    NoInternetDialog noInternetDialog;


    @Inject
    public ApiService apiService;


    @Inject
    public SharedPrefsHelper sharedPrefsHelper;

    @BindView(R.id.dialog_progress_bar)
    ProgressBar dialogProgressBar;

    private String phone;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public EnterOTPDialog(@NonNull Context context) {
        super(context);
        this.context = context;

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCanceledOnTouchOutside(false);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ((AppController) getContext().getApplicationContext()).getComponent().inject(EnterOTPDialog.this);


        setContentView(R.layout.dialog_otp);
        ButterKnife.bind(this);

        noInternetDialog = new NoInternetDialog.Builder(context).build();


        phone = sharedPrefsHelper.get(AppConstants.PHONE_NUMBER, "9779269052");

        edPhoneNO.setText(phone);


    }


    @OnClick(R.id.btnSubmit)
    public void onViewClicked() {


        if (TextUtils.isEmpty(edPhoneNO.getText().toString().trim()) || TextUtils.isEmpty(edAccessCode.getText().toString().trim())) {

            if (TextUtils.isEmpty(edPhoneNO.getText().toString())) {
                edPhoneNO.setError("Enter phone no");

            } else if (TextUtils.isEmpty(edAccessCode.getText().toString())) {
                edAccessCode.setError("Enter Access Code");
            }
        } else {

            dialogProgressBar.setVisibility(View.VISIBLE);

            String otp = edAccessCode.getText().toString().trim();


            if(NetUtils.hasConnectivity(getContext())){

                compositeDisposable.add(apiService.validateOTP(phone, otp)
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<ValidateOTPSuccess>() {
                            @Override
                            public void accept(ValidateOTPSuccess validateOTPSuccess) throws Exception {

                                if (validateOTPSuccess.getIsSuccess()) {


                                    dialogProgressBar.setVisibility(View.GONE);

                                    sharedPrefsHelper.put(AppConstants.DRIVER_ID, validateOTPSuccess.getPayload().getId());
                                    sharedPrefsHelper.put(AppConstants.USER_NAME, validateOTPSuccess.getPayload().getName());
                                    sharedPrefsHelper.put(AppConstants.PHONE_NUMBER, validateOTPSuccess.getPayload().getPhoneNo());
                                    sharedPrefsHelper.put(AppConstants.ADDRESS, validateOTPSuccess.getPayload().getAddress());


                                    getContext().startActivity(new Intent(context, DashboardActivity.class));
                                    UserDataUtility.setLogin(true, context);

                                } else {

                                    dialogProgressBar.setVisibility(View.GONE);

                                    edAccessCode.setText("");

                                    Toast.makeText(context,"OTP is not Valid",Toast.LENGTH_SHORT).show();

                                }

                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {

                                edAccessCode.setText("");
                                dialogProgressBar.setVisibility(View.GONE);
                                noInternetDialog.show();


                            }
                        }));
            }else {

                AlertDialog.Builder builder =new AlertDialog.Builder(context);
                builder.setTitle("No internet Connection");
                builder.setMessage("Please turn on internet connection to continue");
                builder.setNegativeButton("close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }



        }


    }


    @Override
    protected void onStop() {
        super.onStop();
        noInternetDialog.onDestroy();

    }

    public interface myOnClickListener {
        void onButtonClick();
    }


}
