package com.adapter;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RsBannnerModelClass  {


    /*
    {"latitude":"32.1194242",
    "longitude":"20.0867909",
    "iCompanyId":"318",
    "vCompany":"test test",
    "deliveryPrice":"10",
    "ispriceshow":"combine",
    "Restaurant_Status":"open",
    "Restaurant_Cuisine":"74"}*/
    @SerializedName("thumbnail")
    @Expose
    private String thumbnail;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("latitude")
    @Expose
    private String latitude;
    @SerializedName("longitude")
    @Expose
    private String longitude;
    @SerializedName("iCompanyId")
    @Expose
    private String iCompanyId;
    @SerializedName("vCompany")
    @Expose
    private String vCompany;
    @SerializedName("deliveryPrice")
    @Expose
    private String deliveryPrice;
    @SerializedName("Restaurant_OrderPrepareTime")
    @Expose
    private String restaurantOrderPrepareTime;
    @SerializedName("ispriceshow")
    @Expose
    private String ispriceshow;
    @SerializedName("vAvgRating")
    @Expose
    private Integer vAvgRating;
    @SerializedName("Restaurant_Status")
    @Expose
    private String restaurantStatus;
    @SerializedName("Restaurant_Cuisine")
    @Expose
    private String restaurantCuisine;

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getICompanyId() {
        return iCompanyId;
    }

    public void setICompanyId(String iCompanyId) {
        this.iCompanyId = iCompanyId;
    }

    public String getVCompany() {
        return vCompany;
    }

    public void setVCompany(String vCompany) {
        this.vCompany = vCompany;
    }

    public String getDeliveryPrice() {
        return deliveryPrice;
    }

    public void setDeliveryPrice(String deliveryPrice) {
        this.deliveryPrice = deliveryPrice;
    }

    public String getRestaurantOrderPrepareTime() {
        return restaurantOrderPrepareTime;
    }

    public void setRestaurantOrderPrepareTime(String restaurantOrderPrepareTime) {
        this.restaurantOrderPrepareTime = restaurantOrderPrepareTime;
    }

    public String getIspriceshow() {
        return ispriceshow;
    }

    public void setIspriceshow(String ispriceshow) {
        this.ispriceshow = ispriceshow;
    }

    public Integer getVAvgRating() {
        return vAvgRating;
    }

    public void setVAvgRating(Integer vAvgRating) {
        this.vAvgRating = vAvgRating;
    }

    public String getRestaurantStatus() {
        return restaurantStatus;
    }

    public void setRestaurantStatus(String restaurantStatus) {
        this.restaurantStatus = restaurantStatus;
    }

    public String getRestaurantCuisine() {
        return restaurantCuisine;
    }

    public void setRestaurantCuisine(String restaurantCuisine) {
        this.restaurantCuisine = restaurantCuisine;
    }

}
