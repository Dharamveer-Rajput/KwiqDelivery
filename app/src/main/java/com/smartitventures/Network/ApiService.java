package com.smartitventures.Network;

import com.smartitventures.Response.AssignedOrderResponse.AssignedOrderSuccess;
import com.smartitventures.Response.CompletedOrPendingOrder.CompletedOrPendingOrderSuccess;
import com.smartitventures.Response.DeliveryStatusResponse.DeliveryStatusSuccess;
import com.smartitventures.Response.PhoneNoSuccess.PhoneNoSuccess;
import com.smartitventures.Response.UpdateLatLng.UpdateLatLong;
import com.smartitventures.Response.ValidateOtp.ValidateOTPSuccess;

import java.util.HashMap;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import io.reactivex.Observable;
import retrofit2.http.Part;

/**
 * Created by dharamveer on 30/1/18.
 */

public interface ApiService {


    //------------------------1
    @FormUrlEncoded
    @POST("isPhoneNoExist")
    Observable<PhoneNoSuccess> isPhoneNoExist(@Field("phoneNo") String Phone);


    //------------------------2
    @FormUrlEncoded
    @POST("validateOTP")
    Observable<ValidateOTPSuccess> validateOTP(@Field("phoneNo") String Phone,
                                               @Field("OTP") String OTP);



    //------------------------3
    @FormUrlEncoded
    @POST("updateLatLng")
    Observable<UpdateLatLong> updateLatLng(@Field("driverId") String DriverID,
                                           @Field("lat") String lat,
                                           @Field("lng") String lng);


    //------------------------4
    @FormUrlEncoded
    @POST("getAssignedOrder")
    Observable<AssignedOrderSuccess> getAssignedOrder(@Field("driverId") String driverId);



    //-----------------------5
    @Multipart
    @POST("deliveryStatus")
    Observable<DeliveryStatusSuccess> deliveryStatus(@Part("driverId") RequestBody driverId,
                                                    @Part("orderNo") RequestBody orderNo,
                                                    @Part("bussinessId") RequestBody bussinessId,
                                                     @Part MultipartBody.Part file);


    //------------------------4
    @FormUrlEncoded
    @POST("CompletedOrPendingOrder")
    Observable<CompletedOrPendingOrderSuccess> completedOrPendingOrder(@Field("driverId") String DriverID,
                                                                       @Field("deliveryStatus") String deliveryStatus,
                                                                       @Field("bussinessId") String bussinessId);







}
