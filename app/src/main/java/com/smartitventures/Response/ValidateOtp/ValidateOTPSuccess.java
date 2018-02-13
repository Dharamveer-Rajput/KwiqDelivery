package com.smartitventures.Response.ValidateOtp;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dharamveer on 18/1/18.
 */

public class ValidateOTPSuccess {
    @SerializedName("isSuccess")
    private boolean isSuccess;
    @SerializedName("isError")
    private boolean isError;
    @SerializedName("payload")
    private Payload payload;

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

    public Payload getPayload() {
        return payload;
    }

    public void setPayload(Payload payload) {
        this.payload = payload;
    }
}
