package com.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.JResponseClasses.BannerData;
import com.JResponseClasses.TopBannerInterfce;
import com.general.files.GeneralFunctions;
import com.general.files.StartActProcess;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.junaid_condition_sets.SharedPreference;
import com.servo.user.MainActivity;
import com.servo.user.R;
import com.servo.user.deliverAll.FoodDeliveryHomeActivity;
import com.servo.user.deliverAll.RestaurantAllDetailsNewActivity;
import com.squareup.picasso.Picasso;
import com.utils.Utils;
import android.widget.TextView;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JDemoAdapter extends RecyclerView.Adapter<JDemoAdapter.MyViewHolder> {


    SharedPreference instant_banner_data;
    Context mContext;
    ArrayList<HashMap<String, String>> list_item;
    int dimension;
    int bannerHeight;
    int bannerWidth;
    int width;
    int margin;
    int grid;
    List<BannerData> bannerData;
    String CAT_TYPE_MODE = "1";
    newIntercaeBannerClick listner;

    Activity activity;
    HashMap<String, String> stringHashMap;
    String latitude, longitude, adres;
    TopBannerInterfce topBannerInterfce;
    GeneralFunctions generalFunctions;


    public JDemoAdapter(Context context, Activity activity, List<BannerData> bannerData, String latitude, String longitude, String adres,
                        newIntercaeBannerClick listner, TopBannerInterfce topBannerInterfce, GeneralFunctions generalFunctions) {

        mContext = context;
        this.generalFunctions= generalFunctions;
        this.activity = activity;
        this.bannerData = bannerData;
        this.listner = listner;
        instant_banner_data = new SharedPreference(context, "instant_banner_data");
        String data = instant_banner_data.getStringValue("list_item");
        Type listType = new TypeToken<ArrayList<HashMap<String, String>>>() {
        }.getType();
        list_item = new Gson().fromJson(data, listType);
        //  this.stringHashMap= hashMap;
//
        dimension = mContext.getResources().getDimensionPixelSize(R.dimen.category_grid_size);
        margin = mContext.getResources().getDimensionPixelSize(R.dimen.category_banner_left_right_margin);
        grid = mContext.getResources().getDimensionPixelSize(R.dimen.category_grid_size);
        width = margin * 2;
        bannerWidth = Utils.getWidthOfBanner(mContext, width);
        bannerHeight = Utils.getHeightOfBanner(mContext, width, "16:9");
        this.latitude = latitude;
        this.longitude = longitude;
        this.adres = adres;
        this.topBannerInterfce = topBannerInterfce;


//        Toast.makeText(context,"Nobita: "+bannerData.size(),Toast.LENGTH_LONG).show();

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.j_banner_layout, viewGroup, false);
        return new JDemoAdapter.MyViewHolder(view);
    }


//    public void setOnItemClickList(newIntercaeBannerClick onItemClickList) {
//        this.newIntercaeBannerClick = onItemClickList;
//    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int i) {

        BannerData banner = bannerData.get(i);


        //   Log.d("***********",""+banner.getType());

        holder.bannerImgView.setOnClickListener(view -> {
            if (this.listner != null) {
                this.listner.onItemClickBanner(i);
            }
        });


//        HashMap<String, String> item = list_item.get(i);
//
//        String vCategory = item.get("vCategory");
//        String vLogo_image = item.get("vLogo_image");
        // fixme j banner data in adapter
        String vBannerImage = "https://www.servo.com.ly/admin/images/" + banner.getVImage();

//
//        String vCategoryBanner = item.get("vCategoryBanner");
//
        String imageURL = Utils.getResizeImgURL(mContext, vBannerImage, bannerWidth, bannerHeight);
//
        Picasso.with(mContext).load(imageURL).fit().placeholder(R.mipmap.ic_no_icon).into(holder.bannerImgView);
        holder.serviceNameTxt.setText(banner.getVCategory());
        // holder.bookNowTxt.setText(banner.getTBannerButtonText());

//        Picasso.with(mContext).load("").placeholder(R.mipmap.ic_no_icon).into(holder.bannerImgView, new Callback() {
//            @Override
//            public void onSuccess() { }
//
//            @Override
//            public void onError() {
//
//                if (!vBannerImage.contains("http") && !vBannerImage.equals("")) {
//                    Picasso.with(mContext).load(GeneralFunctions.parseIntegerValue(0, vBannerImage)).placeholder(R.mipmap.ic_no_icon).error(R.mipmap.ic_no_icon).into(holder.bannerImgView);
//                }
//            }
//        });

        holder.bannerImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                // only add
                if (banner.getType().equalsIgnoreCase("onlyAd")) {
                    Toast.makeText(mContext, "just Ad", Toast.LENGTH_SHORT).show();

                } else if (banner.getType().equalsIgnoreCase("LeadToActivity") &&
                banner.getIsCompany().equalsIgnoreCase("no")&&banner.getFlagName().equalsIgnoreCase("no")) {

                    if (banner.getIVehicleCategoryId().equalsIgnoreCase("More Deliveries")) {
                /*        Bundle bn = new Bundle();

                        bn.putString("selType", Utils.CabGeneralType_Deliver);
                        bn.putBoolean("isRestart", false);
                        bn.putBoolean("emoto", true);
                        Log.d("OpenCatType","MOTODELIVERY"+bn);*/

                        topBannerInterfce.onTopBannerClick("MOREDELIVERY", bannerData.get(i).getIServiceId(),banner.getIVehicleCategoryId());

                        //new StartActProcess(mContext).startActWithData(MainActivity.class, bn);
                       // topBannerInterfce.onTopBannerClick("MOREDELIVERY", bannerData.get(i).getIServiceId(),banner.getIVehicleCategoryId());
                    }
                   else if (banner.getIVehicleCategoryId().equalsIgnoreCase("Restaurant")) {
                        Bundle bn = new Bundle();
                        bn.putBoolean("isback", true);
                        new StartActProcess(mContext).startActWithData(FoodDeliveryHomeActivity.class, bn);
                    }
                   else if (banner.getIVehicleCategoryId().equalsIgnoreCase("Taxi Ride")) {
                        Bundle bn= new Bundle();
                        bn.putString("selType", Utils.CabGeneralType_Ride);
                        bn.putBoolean("isRestart", false);
                        new StartActProcess(mContext).startActWithData(MainActivity.class, bn);
                       // topBannerInterfce.onTopBannerClick("MOTORENTAL", bannerData.get(i).getIServiceId(),banner.getIVehicleCategoryId());
                    }
                  else  if (banner.getIVehicleCategoryId().equalsIgnoreCase("Car Rent")) {
                        Bundle bn= new Bundle();

                        bn.putString("selType", Utils.CabGeneralType_Ride);
                        bn.putBoolean("isRestart", false);
                        bn.putBoolean("emoto", true);
                        Log.d("OpenCatType","MOTORIDE"+bn);

                        new StartActProcess(mContext).startActWithData(MainActivity.class, bn);

                      //  topBannerInterfce.onTopBannerClick("RENTAL", bannerData.get(i).getIServiceId(),banner.getIVehicleCategoryId());

                    }
                   else if (banner.getIVehicleCategoryId().equalsIgnoreCase("Commoun Delivery")) {
                        topBannerInterfce.onTopBannerClick("MOREDELIVERY", bannerData.get(i).getIServiceId(),banner.getIVehicleCategoryId());

                    }
                    // catagory

                }
                // services
                else if(banner.getType().equalsIgnoreCase("LeadToActivity") &&
                        banner.getIsCompany().equalsIgnoreCase("no")&&banner.getFlagName().equalsIgnoreCase("yes")){
                    topBannerInterfce.onTopBannerClick("DELIVERALL", bannerData.get(i).getIServiceId(),banner.getIVehicleCategoryId());
                    //services

                }
                else if(banner.getType().equalsIgnoreCase("LeadToActivity") &&
                        banner.getIsCompany().equalsIgnoreCase("yes")&&banner.getFlagName().equalsIgnoreCase("yes")){

                    Bundle bn = new Bundle();
                    bn.putString("iCompanyId", banner.getICompanyId());
                    bn.putString("Restaurant_Status", banner.getRestaurantStatus());
                    bn.putString("ispriceshow", banner.getIspriceshow());
                    bn.putString("lat", latitude);
                    bn.putString("long", longitude);
                    bn.putString("vCompany",banner.getVCompany());
                    bn.putString("deliveryPrice",banner.getDeliveryPrice().toString());
                    bn.putString("vAvgRating",banner.getVAvgRating().toString());
                    bn.putString("Restaurant_Cuisine",banner.getRestaurantCuisine());
                    bn.putString("Restaurant_OrderPrepareTimeConverted",generalFunctions.convertNumberWithRTL(banner.getRestaurantOrderPrepareTime()));
                    new StartActProcess(mContext).startActForResult(RestaurantAllDetailsNewActivity.class, bn, 111);
                    //store

                }

               /* if (banner.getFlagName().equalsIgnoreCase("yes")) {


                    HashMap<String, String> hashMap = new HashMap<>(); //list_item.get(i);



                    //  topBannerInterfce.onTopBannerClick("DELIVERALL", bannerData.get(i).getIServiceId(),banner.getIVehicleCategoryId());


                    if (!banner.getICompanyId().equalsIgnoreCase("")){
                        //  if (internetConnection.isNetworkConnected()) {

                *//*                Bundle bn = new Bundle();
                                bn.putString("iCompanyId", banner.getICompanyId());
                                bn.putString("Restaurant_Status", banner.getRestaurantStatus());
                                bn.putString("ispriceshow", banner.getIspriceshow());
                                bn.putString("lat", latitude);
                                bn.putString("long", longitude);
                                bn.putString("vCompany",banner.getVCompany());
                                bn.putString("deliveryPrice",banner.getDeliveryPrice().toString());
                                bn.putString("vAvgRating",banner.getVAvgRating().toString());
                                bn.putString("Restaurant_Cuisine",banner.getRestaurantCuisine());
                                bn.putString("Restaurant_OrderPrepareTimeConverted",generalFunctions.convertNumberWithRTL(banner.getRestaurantOrderPrepareTime()));
                                new StartActProcess(mContext).startActForResult(RestaurantAllDetailsNewActivity.class, bn, 111);*//*
                    } else {
                        //  generalFunc.showMessage(menuImgView, generalFunc.retrieveLangLBl("", "LBL_NO_INTERNET_TXT"));

                    }

                } else {

                    if (banner.getIVehicleCategoryId().equalsIgnoreCase("More Deliveries")) {

                        topBannerInterfce.onTopBannerClick("MOREDELIVERY", bannerData.get(i).getIServiceId(),banner.getIVehicleCategoryId());
                    }
                    if (banner.getIVehicleCategoryId().equalsIgnoreCase("Restaurant")) {
                        Bundle bn = new Bundle();
                        bn.putBoolean("isback", true);
                        new StartActProcess(mContext).startActWithData(FoodDeliveryHomeActivity.class, bn);
                    }
                    if (banner.getIVehicleCategoryId().equalsIgnoreCase("Taxi Ride")) {
                        topBannerInterfce.onTopBannerClick("MOTORENTAL", bannerData.get(i).getIServiceId(),banner.getIVehicleCategoryId());
                    }
                    if (banner.getIVehicleCategoryId().equalsIgnoreCase("Car Rent")) {
                        topBannerInterfce.onTopBannerClick("RENTAL", bannerData.get(i).getIServiceId(),banner.getIVehicleCategoryId());

                    }
                    if (banner.getIVehicleCategoryId().equalsIgnoreCase("Commoun Delivery")) {
                        topBannerInterfce.onTopBannerClick("DELIVERALL", bannerData.get(i).getIServiceId(),banner.getIVehicleCategoryId());

                    }


                }*/



                //   new UberXActivity().onItemClickHandle(1,"Home");

               /* switch (banner.getType()) {



                    case "1": {

                        *//*if (onItemClickList != null) {
                            onItemClickList.onItemClick(1);
                        }*//*

                        if (CAT_TYPE_MODE.equalsIgnoreCase("0")) {
                            if (listner != null) {
                             //   listner.onItemClickBanner(1);
                                listner.onItemClickBanner(1);

                            }
                        } else {
                            if (false) {
//                    if (viewHolder.serviceCheckbox != null) {
//                        viewHolder.serviceCheckbox.setChecked(!viewHolder.serviceCheckbox.isChecked());
//                    }
                            } else {
                                if (listner != null) {
                                  //  listner.onItemClickBanner(1);
                                    listner.onItemClickBanner(1);
                                }
                            }
                        }
                        // new OpenCatType(mContext, mapData).execute();
                        // new StartActProcess(mContext).startActWithData(CommonDeliveryTypeSelectionActivity.class, bn);

                    }
                    break;

                    case "2": {
                        Bundle bn = new Bundle();

                        //new StartActProcess(mContext).startActWithData(FoodDeliveryHomeActivity.class, bn);
                        Intent intent = new Intent(mContext,FoodDeliveryHomeActivity.class);
                        mContext.startActivity(intent);
                    }
                    break;
                    case "3": {
                        if (listner != null) {
                            //  listner.onItemClickBanner(1);
                            listner.onItemClickBanner(2);
                        }
                      *//*  Bundle bn = new Bundle();
                        new StartActProcess(mContext).startActWithData(MainActivity.class, bn);*//*
                    }
                    break;

                    default:
                        break;

                }*/

            }
        });


    }

    public interface newIntercaeBannerClick {

        void onItemClickBanner(int position);
    }

    @Override
    public int getItemCount() {
        return bannerData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView bannerImgView;
        TextView serviceNameTxt, bookNowTxt;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            bannerImgView = itemView.findViewById(R.id.bannerImgView);
            serviceNameTxt = itemView.findViewById(R.id.serviceNameTxt);
            bookNowTxt = itemView.findViewById(R.id.bookNowTxt);
        }
    }
}
