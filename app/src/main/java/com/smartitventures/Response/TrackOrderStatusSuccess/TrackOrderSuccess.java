package com.smartitventures.Response.TrackOrderStatusSuccess;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by dharamveer on 19/2/18.
 */

public class TrackOrderSuccess {


    @Expose
    @SerializedName("message")
    private String message;
    @Expose
    @SerializedName("isError")
    private boolean isError;
    @Expose
    @SerializedName("isSuccess")
    private boolean isSuccess;

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
