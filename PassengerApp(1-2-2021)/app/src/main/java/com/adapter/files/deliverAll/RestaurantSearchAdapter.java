package com.adapter.files.deliverAll;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PorterDuff;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.servo.user.R;
import com.general.files.GeneralFunctions;
import com.general.files.MyApp;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.squareup.picasso.Picasso;
import com.utils.Utils;

import android.widget.TextView;
import com.view.SelectableRoundedImageView;

import java.util.ArrayList;
import java.util.HashMap;


public class RestaurantSearchAdapter extends RecyclerView.Adapter<RestaurantSearchAdapter.ViewHolder> {

    ArrayList<HashMap<String, String>> resArrList;
    Context mContext;
    GeneralFunctions generalFunctions;
    public RestaurantOnClickListener restaurantOnClickListener;
    String ENABLE_FAVORITE_STORE_MODULE = "";
    String userProfileJson = "";

    int enabledColor, disabledColor, appCompactColor;
    PorterDuff.Mode porterDuffMode;
    int dimension;

    public RestaurantSearchAdapter(Context context, ArrayList<HashMap<String, String>> mapArrayList) {
        this.mContext = context;
        this.resArrList = mapArrayList;
        generalFunctions = MyApp.getInstance().getGeneralFun(context);
        userProfileJson = generalFunctions.retrieveValue(Utils.USER_PROFILE_JSON);
        ENABLE_FAVORITE_STORE_MODULE = generalFunctions.getJsonValue("ENABLE_FAVORITE_STORE_MODULE", userProfileJson);

        enabledColor = mContext.getResources().getColor(R.color.itemgray);
        disabledColor = mContext.getResources().getColor(R.color.gray);
        appCompactColor = ContextCompat.getColor(mContext, R.color.gray);
        porterDuffMode = PorterDuff.Mode.SRC_IN;
        dimension = mContext.getResources().getDimensionPixelSize(R.dimen.restaurant_img_size_home_screen);

    }

    @Override
    public RestaurantSearchAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_restaurant_list_design_new, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RestaurantSearchAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        HashMap<String, String> mapData=resArrList.get(position);

        holder.restaurantNameTxt.setText(mapData.get("vCompany"));
        holder.restaurantRateTxt.setText(mapData.get("vAvgRatingConverted"));
        holder.RestCuisineTXT.setText(mapData.get("Restaurant_Cuisine"));
        holder.deliveryTimeTxt.setText(mapData.get("Restaurant_OrderPrepareTimeConverted"));
        holder.dev_timeLbl.setText((generalFunctions.retrieveLangLBl("Delivery time","LBL_DELIVERY_TIME")));

        holder.minOrderTxt.setText(generalFunctions.retrieveLangLBl("Delivery Price","LBL_DELIVERY_PRICE"));


        holder.pricePerPersonTxt.setText(mapData.get("deliveryPrice").trim().equalsIgnoreCase("")?"Not Set":"$ "+mapData.get("deliveryPrice"));



        String vAvgRating = mapData.get("vAvgRating");
        if (vAvgRating != null && !vAvgRating.equalsIgnoreCase("") && !vAvgRating.equalsIgnoreCase("0")) {
            holder.restaurantRateTxt.setText(mapData.get("vAvgRatingConverted"));
            holder.rating_layout.setVisibility(View.VISIBLE);
        } else {
            holder.rating_layout.setVisibility(View.GONE);
        }

       // holder.deliveryTimeTxt.setText(mapData.get("Restaurant_OrderPrepareTimeConverted"));

        if (!mapData.get("Restaurant_OfferMessage_short").equalsIgnoreCase("")) {
            holder.offerArea.setVisibility(View.VISIBLE);
            holder.offerTxt.setText(mapData.get("Restaurant_OfferMessage_shortConverted"));
        } else {
            holder.offerArea.setVisibility(View.GONE);
        }

        String vImage=mapData.get("vImage") ;

        if (vImage != null && !vImage.trim().isEmpty()) {
            String imageURL = Utils.getResizeImgURL(mContext, vImage, dimension, dimension);
            Picasso.with(mContext)
                    .load(imageURL).placeholder(R.mipmap.ic_no_icon).error(mContext.getResources().getDrawable(R.mipmap.ic_no_icon))
                    .into(holder.vImageImgView);
        } else {
            holder.vImageImgView.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.ic_no_icon));
        }

//
//        //Fav Store Features
//        if (ENABLE_FAVORITE_STORE_MODULE.equalsIgnoreCase("Yes")) {
//            holder.likeButton.setVisibility(View.VISIBLE);
//        } else {
//            holder.likeButton.setVisibility(View.GONE);
//        }
//        holder.likeButton.setOnAnimationEndListener(new OnAnimationEndListener() {
//            @Override
//            public void onAnimationEnd(LikeButton likeButton) {
//                Log.d("Liked", "Animation End for %s" + likeButton);
//            }
//        });

        String fav = mapData.get("eFavStore");
        //Fav Store Features
        if (ENABLE_FAVORITE_STORE_MODULE.equalsIgnoreCase("Yes") && Utils.checkText(fav) && fav.equalsIgnoreCase("Yes")) {
            holder.favImage.setVisibility(View.VISIBLE);

        } else {
            holder.favImage.setVisibility(View.GONE);
        }
        holder.likeButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                if (restaurantOnClickListener != null) {
                    Log.e("resSize", "clickNotnull");
                    restaurantOnClickListener.setOnRestaurantclick(position, true);
                }
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                if (restaurantOnClickListener != null) {
                    Log.e("resSize", "clickNotnull");
                    restaurantOnClickListener.setOnRestaurantclick(position, false);
                }
            }
        });

//            String fav = data.get("isFavourite");
//            if (Utils.checkText(fav) && fav.equalsIgnoreCase("Yes")) {
//                resholder.likeButton.setLiked(true);
//            } else {
//                resholder.likeButton.setLiked(false);
//            }
        if (mapData.get("Restaurant_Status").equalsIgnoreCase("Closed")) {


            holder.restaurantNameTxt.setTextColor(disabledColor);

            holder.offerTxt.setTextColor(disabledColor);
            holder.RestCuisineTXT.setTextColor(disabledColor);
         //   holder.pricePerPersonTxt.setTextColor(disabledColor);
            holder.deliveryTimeTxt.setTextColor(disabledColor);
          //  holder.minOrderTxt.setTextColor(disabledColor);
         //   holder.offerImage.setColorFilter(appCompactColor, porterDuffMode);
//            holder.timerImage.setColorFilter(appCompactColor, porterDuffMode);


            holder.resStatusTxt.setVisibility(View.VISIBLE);
            if (!mapData.get("Restaurant_Opentime").equalsIgnoreCase("")) {
                holder.resStatusTxt.setText(mapData.get("LBL_CLOSED_TXT") + ": " + mapData.get("Restaurant_OpentimeConverted"));
            } else {
                holder.resStatusTxt.setText(mapData.get("LBL_CLOSED_TXT"));
            }


            if (mapData.get("eAvailable").equalsIgnoreCase("No")) {
                holder.resStatusTxt.setText(mapData.get("LBL_NOT_ACCEPT_ORDERS_TXT"));


            }
            holder.resStatusTxt.setTextColor(mContext.getResources().getColor(R.color.red));

        } else {
            holder.resStatusTxt.setVisibility(View.GONE);

            holder.restaurantNameTxt.setTextColor(mContext.getResources().getColor(R.color.black));
            holder.RestCuisineTXT.setTextColor(enabledColor);
            holder.pricePerPersonTxt.setTextColor(enabledColor);
            holder.deliveryTimeTxt.setTextColor(enabledColor);
          //  holder.minOrderTxt.setTextColor(enabledColor);
        //    holder.offerImage.setColorFilter(ContextCompat.getColor(mContext, R.color.offerColor), porterDuffMode);
          //  holder.timerImage.setColorFilter(ContextCompat.getColor(mContext, R.color.black), porterDuffMode);
        }


       // holder.minOrderTxt.setText(mapData.get("Restaurant_MinOrderValue"));

        //new CreateRoundedView(mContext.getResources().getColor(R.color.appThemeColor_1), 5, 1, Color.parseColor("#ffffff"), holder.restaurantRateTxt);

//        if (position == 1) {
//            new CreateRoundedView(Color.parseColor("#a7d24f"), 5, 1, Color.parseColor("#ffffff"), holder.restaurantRateTxt);
//        } else if (position == 2) {
//            new CreateRoundedView(Color.parseColor("#ffb900"), 5, 1, Color.parseColor("#ffffff"), holder.restaurantRateTxt);
//        }


        holder.restaurantAdptrLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("resSize", "click");
                if (restaurantOnClickListener != null) {
                    Log.e("resSize", "clickNotnull");
                    restaurantOnClickListener.setOnRestaurantclick(position);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return resArrList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView restaurantRateTxt;

        TextView restaurantNameTxt,dev_timeLbl, deliveryTimeTxt, minOrderTxt, RestCuisineTXT, pricePerPersonTxt, offerTxt;
        TextView resStatusTxt;
        SelectableRoundedImageView imgView;
        LinearLayout  offerArea,textlaytime;
        RelativeLayout restaurantAdptrLayout,rating_layout;
        ImageView offerImage, timerImage, vImageImgView;
        //Fav Store Features
        LikeButton likeButton;
        ImageView favImage;

        public ViewHolder(View itemView) {
            super(itemView);

            imgView = (SelectableRoundedImageView) itemView.findViewById(R.id.imgView);
            restaurantNameTxt = itemView.findViewById(R.id.restaurantNameTxt);
            resStatusTxt = itemView.findViewById(R.id.resStatusTxt);
            restaurantRateTxt = itemView.findViewById(R.id.restaurantRateTxt);
            dev_timeLbl = itemView.findViewById(R.id.dev_timeLbl);
            RestCuisineTXT = itemView.findViewById(R.id.RestCuisineTXT);
            offerTxt = itemView.findViewById(R.id.offerTxt);
            pricePerPersonTxt = itemView.findViewById(R.id.pricePerPersonTxt);
            deliveryTimeTxt = itemView.findViewById(R.id.deliveryTimeTxt);
            minOrderTxt = itemView.findViewById(R.id.minOrderTxt);
            restaurantAdptrLayout = itemView.findViewById(R.id.restaurantAdptrLayout);
            offerArea = itemView.findViewById(R.id.offerArea);
            offerImage = itemView.findViewById(R.id.offerImage);
            timerImage = itemView.findViewById(R.id.timerImage);
            vImageImgView = itemView.findViewById(R.id.imgView);
            favImage = itemView.findViewById(R.id.favImage);
            likeButton = (LikeButton) itemView.findViewById(R.id.likeButton);
            rating_layout= itemView.findViewById(R.id.rating_layout);
            textlaytime= itemView.findViewById(R.id.textlaytime);
        }
    }

    public interface RestaurantOnClickListener {
        void setOnRestaurantclick(int position);

        //Fav Store Features
        void setOnRestaurantclick(int position, boolean liked);
    }

    public void setOnRestaurantItemClick(RestaurantOnClickListener onRestaurantItemClick) {
        this.restaurantOnClickListener = onRestaurantItemClick;
    }
}
