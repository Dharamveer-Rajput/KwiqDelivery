package com.smartitventures.Response.UpdateLatLng;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dharamveer on 23/1/18.
 */

public class UpdateLatLong {


    @SerializedName("isSuccess")
    private boolean isSuccess;
    @SerializedName("isError")
    private boolean isError;
    @SerializedName("message")
    private String message;

    public boolean getIsSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public boolean getIsError() {
        return isError;
    }

    public void setIsError(boolean isError) {
        this.isError = isError;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
