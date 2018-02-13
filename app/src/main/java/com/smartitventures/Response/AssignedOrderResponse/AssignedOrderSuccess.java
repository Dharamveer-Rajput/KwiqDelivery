package com.smartitventures.Response.AssignedOrderResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by dharamveer on 30/1/18.
 */

public class AssignedOrderSuccess {


    @Expose
    @SerializedName("payload")
    private List<AssignedOrderPayload> payload;
    @Expose
    @SerializedName("message")
    private String message;
    @Expose
    @SerializedName("isError")
    private boolean isError;
    @Expose
    @SerializedName("isSuccess")
    private boolean isSuccess;

    public List<AssignedOrderPayload> getPayload() {
        return payload;
    }

    public void setPayload(List<AssignedOrderPayload> payload) {
        this.payload = payload;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean getIsError() {
        return isError;
    }

    public void setIsError(boolean isError) {
        this.isError = isError;
    }

    public boolean getIsSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }
}
