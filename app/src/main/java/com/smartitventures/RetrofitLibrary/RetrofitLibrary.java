package com.smartitventures.RetrofitLibrary;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.smartitventures.Response.PhoneNoSuccess.PhoneNoSuccess;
import com.smartitventures.Response.UpdateLatLng.UpdateLatLong;
import com.smartitventures.Response.ValidateOtp.ValidateOTPSuccess;

import java.util.HashMap;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

public class RetrofitLibrary {


    private static GitApiInterface gitApiInterface;

    // http://employeelive.com/kwiqmall/API/public/getRestaurants

    private static String baseUrl = "http://employeelive.com/kwiqmall/DeliveryAPI/public/";

    public static GitApiInterface getClient() {


        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        //The logging interceptor will be added to the http client

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        //The Retrofit builder will have the client attached, in order to get connection logs
        Retrofit retrofit = new Retrofit.Builder()
                .client(httpClient.build())

                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(baseUrl)
                .build();


        gitApiInterface = retrofit.create(GitApiInterface.class);

        return gitApiInterface;


    }

    public interface GitApiInterface {



       //------------------------1
        @POST("isPhoneNoExist")
        Call<PhoneNoSuccess> isPhoneNoExist(@Body HashMap<String, String> hashMap);


        //------------------------10
        @POST("validateOTP")
        Call<ValidateOTPSuccess> validateOTP(@Body HashMap<String, String> hashMap);




        @POST("updateLatLng")
        Call<UpdateLatLong> updateLatLng(@Body HashMap<String, String> hashMap);





    }
}