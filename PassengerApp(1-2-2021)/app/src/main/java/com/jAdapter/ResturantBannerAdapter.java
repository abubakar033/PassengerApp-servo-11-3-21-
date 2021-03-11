package com.jAdapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.JResponseClasses.ResturentBannerInterface;
import com.adapter.RsBannnerModelClass;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.junaid_condition_sets.SharedPreference;
import com.servo.user.R;
import com.squareup.picasso.Picasso;
import com.utils.Utils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ResturantBannerAdapter extends RecyclerView.Adapter<ResturantBannerAdapter.ViewHolder> {

    SharedPreference instant_banner_data;
    Context mContext;
    ArrayList<HashMap<String, String>> restaurantArr_List1 = new ArrayList<>();

    ArrayList<HashMap<String, String>> list_item;
    int dimension;
    int bannerHeight;
    int bannerWidth;
    int width;
    int margin;
    int grid;
    List<RsBannnerModelClass> bannerData;
    String CAT_TYPE_MODE = "1";
    Activity activity;
    ResturentBannerInterface resturentBannerInterface;

    public ResturantBannerAdapter(Context context, Activity activity, List<RsBannnerModelClass> bannerData, ArrayList<HashMap<String, String>> restaurantArr_List, ResturentBannerInterface bannerInterface) {

        mContext = context;
        this.activity = activity;
        this.bannerData = bannerData;
        this.restaurantArr_List1 = restaurantArr_List;

        this.resturentBannerInterface = bannerInterface;
        instant_banner_data = new SharedPreference(context, "instant_banner_data");
        String data = instant_banner_data.getStringValue("list_item");
        Type listType = new TypeToken<ArrayList<HashMap<String, String>>>() {
        }.getType();
        list_item = new Gson().fromJson(data, listType);
//
        dimension = mContext.getResources().getDimensionPixelSize(R.dimen.category_grid_size);
        margin = mContext.getResources().getDimensionPixelSize(R.dimen.category_banner_left_right_margin);
        grid = mContext.getResources().getDimensionPixelSize(R.dimen.category_grid_size);
        width = margin * 2;
        bannerWidth = Utils.getWidthOfBanner(mContext, width);
        bannerHeight = Utils.getHeightOfBanner(mContext, width, "16:9");

//        Toast.makeText(context,"Nobita: "+bannerData.size(),Toast.LENGTH_LONG).show();

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.j_banner_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, @SuppressLint("RecyclerView") int i) {
        RsBannnerModelClass banner = bannerData.get(i);




        String vBannerImage = "https://www.servo.com.ly/admin/images/" + banner.getThumbnail();

//
//        String vCategoryBanner = item.get("vCategoryBanner");
//
        String imageURL = Utils.getResizeImgURL(mContext, vBannerImage, bannerWidth, bannerHeight);
//
        Picasso.with(mContext).load(imageURL).fit().placeholder(R.mipmap.ic_no_icon).into(viewHolder.bannerImgView);

        viewHolder.bannerImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (resturentBannerInterface != null) {
                    resturentBannerInterface.onResturentBannerSelect(banner.getICompanyId(),banner.getLatitude(),
                            banner.getLongitude(),
                            banner.getVCompany(),
                            banner.getDeliveryPrice(),
                            banner.getIspriceshow(),
                            banner.getVAvgRating().toString(),
                            banner.getRestaurantStatus(),
                            banner.getRestaurantCuisine(),
                            banner.getRestaurantOrderPrepareTime()
                            );
                }


            }
        });


    }

   /* public interface NewResturentIntercaeBannerClick {

        void onResturentClickBanner(int position);
    }*/

    @Override
    public int getItemCount() {
        return bannerData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView bannerImgView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            bannerImgView = itemView.findViewById(R.id.bannerImgView);

        }
    }
}
