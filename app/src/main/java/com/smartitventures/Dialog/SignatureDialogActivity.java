package com.smartitventures.Dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeSuccessDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.interfaces.Closure;
import com.kwiqdelivery.R;
import com.kyanogen.signatureview.SignatureView;
import com.smartitventures.AppConstants;
import com.smartitventures.BaseActivity;
import com.smartitventures.Network.ApiService;
import com.smartitventures.Response.DeliveryStatusResponse.DeliveryStatusSuccess;
import com.smartitventures.Utils.FileUtils;
import com.smartitventures.applicationclass.AppController;
import com.smartitventures.di.modules.SharedPrefsHelper;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import javax.inject.Inject;

import am.appwise.components.ni.NoInternetDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.zelory.compressor.Compressor;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by dharamveer on 7/2/18.
 */

public class SignatureDialogActivity extends Activity {


    NoInternetDialog noInternetDialog;
    Context context;


    @Inject
    public ApiService apiService;


    @Inject
    public SharedPrefsHelper sharedPrefsHelper;


    @BindView(R.id.signature_view)
    SignatureView signature_view;
    @BindView(R.id.signature_pad_container)
    RelativeLayout signaturePadContainer;
    @BindView(R.id.save_button)
    Button saveButton;

    @BindView(R.id.clear_button)
    Button clearButton;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;


    private CompositeDisposable compositeDisposable = new CompositeDisposable();





    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        ((AppController) getApplicationContext()).getComponent().inject(SignatureDialogActivity.this);

        setContentView(R.layout.signature_layout);
        ButterKnife.bind(this);


        context   = SignatureDialogActivity.this;

        noInternetDialog = new NoInternetDialog.Builder(context).build();

        int colorPrimary = ContextCompat.getColor(context, R.color.colorAccent);
        signature_view.setPenColor(colorPrimary);




    }


    @OnClick({R.id.save_button,  R.id.clear_button})

    public void onViewClicked(View view) {

        switch (view.getId()) {

            case R.id.save_button:


                progressBar.setVisibility(View.VISIBLE);

                Bitmap bitmap = signature_view.getSignatureBitmap();

                Uri imagePath = getImageUri1(getApplicationContext(),bitmap);

                String driverID1 = String.valueOf(sharedPrefsHelper.get(AppConstants.DRIVER_ID,0));
                String orderNo1 = sharedPrefsHelper.get(AppConstants.ORDER_NO,"");
                String bussinessId1 = String.valueOf(sharedPrefsHelper.get(AppConstants.BUSINESS_ID,0));

                RequestBody driverID = RequestBody.create(MediaType.parse("text/plain"), driverID1);
                RequestBody orderNo = RequestBody.create(MediaType.parse("text/plain"), orderNo1);
                RequestBody bussinessId = RequestBody.create(MediaType.parse("text/plain"), bussinessId1);


                compositeDisposable.add(apiService.deliveryStatus(driverID,orderNo,bussinessId,imagePath == null ? null : prepareFilePart("file",imagePath))
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<DeliveryStatusSuccess>() {
                            @Override
                            public void accept(DeliveryStatusSuccess deliveryStatusSuccess) throws Exception {


                                if(deliveryStatusSuccess.getIsSuccess()){


                                    progressBar.setVisibility(View.GONE);

                                    Toast.makeText(getApplicationContext(),deliveryStatusSuccess.getMessage(),Toast.LENGTH_SHORT).show();

                                    finish();
                                }
                                else {

                                    Toast.makeText(getApplicationContext(),deliveryStatusSuccess.getMessage(),Toast.LENGTH_SHORT).show();

                                }

                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {

                                compositeDisposable.dispose();

                                Toast.makeText(getApplicationContext(),throwable.getMessage(),Toast.LENGTH_SHORT).show();

                            }
                        }));







                break;

            case R.id.clear_button:

                signature_view.clearCanvas();


                break;
        }
    }




    public Uri getImageUri1(Context inContext, Bitmap inImage) {

        Uri uri = null;
        try {
            final BitmapFactory.Options options = new BitmapFactory.Options();
// Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, 100, 100);

// Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            Bitmap newBitmap = Bitmap.createScaledBitmap(inImage, 200, 200,
                    true);
            File file = new File(inContext.getFilesDir(), "Image"
                    + new Random().nextInt() + ".jpeg");
            FileOutputStream out =inContext.openFileOutput(file.getName(),
                    Context.MODE_PRIVATE);
            newBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        //get absolute path
            String realPath = file.getAbsolutePath();
            File f = new File(realPath);
            uri = Uri.fromFile(f);

        } catch (Exception e) {
            Log.e("Your Error Message", e.getMessage());
        }
        return uri;

    }



    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height/ (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio; } final float totalPixels = width * height; final float totalReqPixelsCap = reqWidth * reqHeight * 2; while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }


    @NonNull
    private MultipartBody.Part prepareFilePart(String partName, Uri fileUri) {
        // https://github.com/iPaulPro/aFileChooser/blob/master/aFileChooser/src/com/ipaulpro/afilechooser/utils/FileUtils.java
        // use the FileUtils to get the actual file by uri
        File file = FileUtils.getFile(context, fileUri);

        File compressedImageFile = null;
        try {
            compressedImageFile = new Compressor(context).compressToFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // create RequestBody instance from file
        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"),
                        compressedImageFile
                );

        // MultipartBody.Part is used to send also the actual file name
        return MultipartBody.Part.createFormData(partName, compressedImageFile.getName(), requestFile);
    }




}
