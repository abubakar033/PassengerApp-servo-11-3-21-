package com.JResponseClasses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BannerData {


    @SerializedName("vCategory")
    @Expose
    private String vCategory;
    @SerializedName("tCategoryDesc")
    @Expose
    private String tCategoryDesc;
    @SerializedName("eCatType")
    @Expose
    private String eCatType;
    @SerializedName("iServiceId")
    @Expose
    private String iServiceId;
    @SerializedName("tBannerButtonText")
    @Expose
    private String tBannerButtonText;
    @SerializedName("eDeliveryType")
    @Expose
    private String eDeliveryType;
    @SerializedName("iVehicleCategoryId")
    @Expose
    private String iVehicleCategoryId;
    @SerializedName("flagName")
    @Expose
    private String flagName;
    @SerializedName("isCompany")
    @Expose
    private String isCompany;
    @SerializedName("vImage")
    @Expose
    private String vImage;
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
    @SerializedName("Restaurant_OrderPrepareTime")
    @Expose
    private String restaurantOrderPrepareTime;
    @SerializedName("ispriceshow")
    @Expose
    private String ispriceshow;
    @SerializedName("vAvgRating")
    @Expose
    private Integer vAvgRating;
    @SerializedName("deliveryPrice")
    @Expose
    private Integer deliveryPrice;
    @SerializedName("Restaurant_Cuisine")
    @Expose
    private String restaurantCuisine;
    @SerializedName("Restaurant_Status")
    @Expose
    private String restaurantStatus;

    public String getVCategory() {
        return vCategory;
    }

    public void setVCategory(String vCategory) {
        this.vCategory = vCategory;
    }

    public String getTCategoryDesc() {
        return tCategoryDesc;
    }

    public void setTCategoryDesc(String tCategoryDesc) {
        this.tCategoryDesc = tCategoryDesc;
    }

    public String getECatType() {
        return eCatType;
    }

    public void setECatType(String eCatType) {
        this.eCatType = eCatType;
    }

    public String getIServiceId() {
        return iServiceId;
    }

    public void setIServiceId(String iServiceId) {
        this.iServiceId = iServiceId;
    }

    public String getTBannerButtonText() {
        return tBannerButtonText;
    }

    public void setTBannerButtonText(String tBannerButtonText) {
        this.tBannerButtonText = tBannerButtonText;
    }

    public String getEDeliveryType() {
        return eDeliveryType;
    }

    public void setEDeliveryType(String eDeliveryType) {
        this.eDeliveryType = eDeliveryType;
    }

    public String getIVehicleCategoryId() {
        return iVehicleCategoryId;
    }

    public void setIVehicleCategoryId(String iVehicleCategoryId) {
        this.iVehicleCategoryId = iVehicleCategoryId;
    }

    public String getFlagName() {
        return flagName;
    }

    public void setFlagName(String flagName) {
        this.flagName = flagName;
    }

    public String getIsCompany() {
        return isCompany;
    }

    public void setIsCompany(String isCompany) {
        this.isCompany = isCompany;
    }

    public String getVImage() {
        return vImage;
    }

    public void setVImage(String vImage) {
        this.vImage = vImage;
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

    public Integer getDeliveryPrice() {
        return deliveryPrice;
    }

    public void setDeliveryPrice(Integer deliveryPrice) {
        this.deliveryPrice = deliveryPrice;
    }

    public String getRestaurantCuisine() {
        return restaurantCuisine;
    }

    public void setRestaurantCuisine(String restaurantCuisine) {
        this.restaurantCuisine = restaurantCuisine;
    }

    public String getRestaurantStatus() {
        return restaurantStatus;
    }

    public void setRestaurantStatus(String restaurantStatus) {
        this.restaurantStatus = restaurantStatus;
    }



}

  /*  @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("thumbnail")
    @Expose
    private String thumbnail;

    @SerializedName("type")
    @Expose
    private String type;

    @SerializedName("created_at")
    @Expose
    private String created_at;

    @SerializedName("updated_at")
    @Expose
    private String updated_at;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }*/

