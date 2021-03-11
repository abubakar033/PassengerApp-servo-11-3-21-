package com.JResponseClasses;


import retrofit2.Call;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiInterface {

    @POST("api/banner/top_banner.php")
    Call<BannerResponse> get_j_banners();

}