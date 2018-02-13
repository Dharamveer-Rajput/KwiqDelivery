package com.smartitventures.Response.CompletedOrPendingOrder;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CompletedPendingPayload {
    @Expose
    @SerializedName("distance")
    private double distance;
    @Expose
    @SerializedName("lng")
    private String lng;
    @Expose
    @SerializedName("lat")
    private String lat;
    @Expose
    @SerializedName("phone_no")
    private String phone_no;
    @Expose
    @SerializedName("name")
    private String name;
    @Expose
    @SerializedName("total")
    private int total;
    @Expose
    @SerializedName("bussinessName")
    private String bussinessName;
    @Expose
    @SerializedName("userLng")
    private String userLng;
    @Expose
    @SerializedName("userLat")
    private String userLat;
    @Expose
    @SerializedName("bussinessId")
    private int bussinessId;
    @Expose
    @SerializedName("userId")
    private int userId;
    @Expose
    @SerializedName("orderNo")
    private String orderNo;

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getPhone_no() {
        return phone_no;
    }

    public void setPhone_no(String phone_no) {
        this.phone_no = phone_no;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getBussinessName() {
        return bussinessName;
    }

    public void setBussinessName(String bussinessName) {
        this.bussinessName = bussinessName;
    }

    public String getUserLng() {
        return userLng;
    }

    public void setUserLng(String userLng) {
        this.userLng = userLng;
    }

    public String getUserLat() {
        return userLat;
    }

    public void setUserLat(String userLat) {
        this.userLat = userLat;
    }

    public int getBussinessId() {
        return bussinessId;
    }

    public void setBussinessId(int bussinessId) {
        this.bussinessId = bussinessId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }
}
