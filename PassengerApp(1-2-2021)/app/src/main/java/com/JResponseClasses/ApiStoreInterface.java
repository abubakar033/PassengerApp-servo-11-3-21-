package com.JResponseClasses;

import com.adapter.ResBannerResponce;

import retrofit2.Call;
import retrofit2.http.POST;

public interface ApiStoreInterface {


    // fixme Abubabakr foo
    @POST("api/food_delivery/food_delivery.php")
    Call<ResBannerResponce> get_Resturent_banners();
}
