package com;


import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiInterface {

    @FormUrlEncoded
    @POST("api/voucher/update_user_voucher.php")
    Call<MyResponse> update_voucher(
            @Field("voucher_id") String voucher_id,
            @Field("user_id") String user_i);


    @FormUrlEncoded
    @POST("api/voucher/update_user_voucher.php")
    Call<VoucherTesting> VoucherTEST(@Field("user_id") String user_id, @Field("voucher_id") String voucher_id);

}