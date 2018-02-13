package com.smartitventures;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;

import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeSuccessDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.interfaces.Closure;
import com.kwiqdelivery.R;
import com.smartitventures.applicationclass.AppController;
import com.smartitventures.Network.ApiService;
import com.smartitventures.di.modules.SharedPrefsHelper;

import javax.inject.Inject;

import am.appwise.components.ni.NoInternetDialog;
import butterknife.Unbinder;

/**
 * Created by dharamveer on 30/1/18.
 */

public class BaseFragment extends Fragment{



    @Inject
    public ApiService apiService;


    protected ProgressDialog pDialog;

    protected android.app.AlertDialog alertDialog;

    @Inject
    public SharedPrefsHelper sharedPrefsHelper;

    protected Unbinder unbinder;
    public NoInternetDialog noInternetDialog;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AppController) getActivity().getApplication()).getComponent().inject(this);
    }


    public void alertLoading() {



        pDialog =new ProgressDialog(getActivity());
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        pDialog.setCanceledOnTouchOutside(true);


        android.app.AlertDialog.Builder alertDialogBuilder;
        alertDialogBuilder =new android.app.AlertDialog.Builder(getActivity());

        alertDialogBuilder.setCancelable(true);
        alertDialogBuilder.setPositiveButton(
                "Ok",
                (dialog,id)->dialog.cancel());
        alertDialog =alertDialogBuilder.create();

    }


    public void showSuccessDialog(String title){

        new AwesomeSuccessDialog(getActivity())
                .setTitle(title)
                .setMessage("Successfully")
                .setColoredCircle(R.color.green)
                .setDialogIconAndColor(R.drawable.checkwhite, R.color.white)
                .setCancelable(true)
                .setPositiveButtonText(getString(R.string.dialog_yes_button))
                .setPositiveButtonbackgroundColor(R.color.red)
                .setPositiveButtonTextColor(R.color.white)
                .setPositiveButtonClick(new Closure() {
                    @Override
                    public void exec() {


                    }
                }).show();

    }








    public void showAlertDialog(String action,String message){

        AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
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
