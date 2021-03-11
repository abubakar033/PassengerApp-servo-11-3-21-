package com.adapter.files.deliverAll;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
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
import com.squareup.picasso.Picasso;
import com.utils.Utils;

import android.widget.TextView;
import com.view.SelectableRoundedImageView;

import java.util.ArrayList;
import java.util.HashMap;


public class RestaurantAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ArrayList<HashMap<String, String>> resArrList;
    Context mContext;
    public RestaurantOnClickListener restaurantOnClickListener;
    boolean isFooterEnabled = false;
    FooterViewHolder footerHolder;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;
    View footerView;
    GeneralFunctions generalFunctions;
    String ENABLE_FAVORITE_STORE_MODULE = "";
    String userProfileJson = "";

    int enabledColor, disabledColor, appCompactColor;
    PorterDuff.Mode porterDuffMode;
    int backColor;
    int strokeColor;

    public RestaurantAdapter(Context context, ArrayList<HashMap<String, String>> mapArrayList, boolean isFooterEnabled) {
        this.mContext = context;
        this.resArrList = mapArrayList;
        this.isFooterEnabled = isFooterEnabled;
        generalFunctions = MyApp.getInstance().getGeneralFun(context);
        userProfileJson = generalFunctions.retrieveValue(Utils.USER_PROFILE_JSON);
        ENABLE_FAVORITE_STORE_MODULE = generalFunctions.getJsonValue("ENABLE_FAVORITE_STORE_MODULE", userProfileJson);

        enabledColor = mContext.getResources().getColor(R.color.black);
        disabledColor = mContext.getResources().getColor(R.color.gray);
        appCompactColor = ContextCompat.getColor(mContext, R.color.gray);
        porterDuffMode = PorterDuff.Mode.SRC_IN;
//        backColor=mContext.getResources().getColor(R.color.appThemeColor_1);
        strokeColor= Color.parseColor("#B1AFAF");
        backColor= Color.parseColor("#B1AFAF");
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        if (viewType == TYPE_FOOTER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_list, parent, false);
            this.footerView = v;
            return new FooterViewHolder(v);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_restaurant_list_design_new, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {




        if (holder instanceof RestaurantAdapter.ViewHolder) {

            RestaurantAdapter.ViewHolder resholder = (RestaurantAdapter.ViewHolder) holder;
            HashMap<String, String> data = resArrList.get(position);
            resholder.restaurantNameTxt.setText(data.get("vCompany"));

            resholder.minOrderTxt.setText(generalFunctions.retrieveLangLBl("Delivery Price","LBL_DELIVERY_PRICE"));
            resholder.dev_timeLbl.setText(generalFunctions.retrieveLangLBl("Delivery time","LBL_DELIVERY_TIME"));

            String vAvgRating = data.get("vAvgRating");
            if (vAvgRating != null && !vAvgRating.equalsIgnoreCase("") && !vAvgRating.equalsIgnoreCase("0")) {
                resholder.restaurantRateTxt.setText(data.get("vAvgRatingConverted"));
                resholder.rating_layout.setVisibility(View.VISIBLE);
            } else {
                resholder.rating_layout.setVisibility(View.GONE);
            }
            if (data.get("Restaurant_PricePerPersonConverted").equalsIgnoreCase("")||data.get("Restaurant_PricePerPersonConverted").length()==0){

            }
            resholder.RestCuisineTXT.setText(data.get("Restaurant_Cuisine"));
            // fixme abubakar set delivery price



          //  resholder.pricePerPersonTxt.setText(data.get("deliveryPrice").trim().equalsIgnoreCase("")&&data.get("deliveryPrice")==null?"":"$ "+data.get("deliveryPrice"));
            resholder.pricePerPersonTxt.setText(data.get("deliveryPrice").trim().equalsIgnoreCase("")?"Not Set":"$ "+data.get("deliveryPrice"));


            resholder.deliveryTimeTxt.setText(data.get("Restaurant_OrderPrepareTimeConverted"));

            if (!data.get("Restaurant_OfferMessage_short").equalsIgnoreCase("")) {
                resholder.offerArea.setVisibility(View.VISIBLE);
                resholder.offerTxt.setText(data.get("Restaurant_OfferMessage_shortConverted"));
              //  resholder.offerTxt.setTextColor(mContext.getResources().getColor(R.color.white));
              //  resholder.offerImage.setColorFilter(ContextCompat.getColor(mContext, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
            } else {
                resholder.offerArea.setVisibility(View.GONE);
            }

            String vImage = data.get("vImage");

            if (vImage != null && !vImage.trim().equals("")) {
                Picasso.with(mContext)
                        .load(vImage).placeholder(R.mipmap.ic_no_icon).error(mContext.getResources().getDrawable(R.mipmap.ic_no_icon))
                        .into(resholder.vImageImgView);
            } else {
                resholder.vImageImgView.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.ic_no_icon));
            }


            String fav = data.get("eFavStore");
            //Fav Store Features
            if (ENABLE_FAVORITE_STORE_MODULE.equalsIgnoreCase("Yes") && Utils.checkText(fav) && fav.equalsIgnoreCase("Yes")) {
                resholder.favImage.setVisibility(View.VISIBLE);

            } else {
                resholder.favImage.setVisibility(View.GONE);
            }


            if (data.get("Restaurant_Status").equalsIgnoreCase("Closed")) {

                resholder.restaurantNameTxt.setTextColor(disabledColor);
               // resholder.offerTxt.setTextColor(disabledColor);
                resholder.RestCuisineTXT.setTextColor(disabledColor);
                //resholder.pricePerPersonTxt.setTextColor(disabledColor);
               // resholder.minOrderTxt.setTextColor(disabledColor);
              //  resholder.offerImage.setColorFilter(appCompactColor, porterDuffMode);
              //  resholder.timerImage.setColorFilter(appCompactColor, porterDuffMode);
                resholder.resStatusTxt.setVisibility(View.VISIBLE);
                if (data.get("timeslotavailable").equalsIgnoreCase("Yes")) {
                    if (data.get("eAvailable").equalsIgnoreCase("No")) {
                        resholder.resStatusTxt.setText(data.get("LBL_NOT_ACCEPT_ORDERS_TXT"));
                    }
                } else {
                    if (!data.get("Restaurant_Opentime").equalsIgnoreCase("")) {
                        resholder.resStatusTxt.setText(data.get("LBL_CLOSED_TXT") + ": " + data.get("Restaurant_OpentimeConverted"));
                    } else {
                        resholder.resStatusTxt.setText(data.get("LBL_CLOSED_TXT"));
                    }
                }
                resholder.resStatusTxt.setTextColor(mContext.getResources().getColor(R.color.redlight));
            } else {
                resholder.resStatusTxt.setVisibility(View.GONE);

                PorterDuff.Mode porterDuffMode = PorterDuff.Mode.SRC_IN;

                resholder.restaurantNameTxt.setTextColor(enabledColor);
                int color=mContext.getResources().getColor(R.color.itemgray);
                resholder.RestCuisineTXT.setTextColor(color);
              //  resholder.pricePerPersonTxt.setTextColor(color);
             //   resholder.deliveryTimeTxt.setTextColor(color);
              //  resholder.minOrderTxt.setTextColor(enabledColor);
              //  resholder.offerTxt.setTextColor(mContext.getResources().getColor(R.color.white));
               // resholder.offerImage.setColorFilter(ContextCompat.getColor(mContext, R.color.white), porterDuffMode);
              //  resholder.timerImage.setColorFilter(ContextCompat.getColor(mContext, R.color.gray), porterDuffMode);
            }
            // fixme current


          //  new CreateRoundedView(Color.parseColor("#ffffff"), 5, 1,Color.parseColor("#ffffff"), resholder.restaurantRateTxt);




            resholder.restaurantAdptrLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("resSize", "click");
                    if (restaurantOnClickListener != null) {
                        Log.e("resSize", "clickNotnull");
                        restaurantOnClickListener.setOnRestaurantclick(position);
                    }
                }
            });
        } else {
            RestaurantAdapter.FooterViewHolder footerHolder = (FooterViewHolder) holder;
            this.footerHolder = footerHolder;
        }

    }
    public void update(ArrayList<HashMap<String, String>> filterist) {

        Log.d("valuechkkkkkkkkkkk","adapter::"+filterist);

        resArrList.clear();
        resArrList = new ArrayList<>();
        resArrList.addAll(filterist);

        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionFooter(position) && isFooterEnabled == true) {
            return TYPE_FOOTER;
        }
        return TYPE_ITEM;
    }


    private boolean isPositionFooter(int position) {
        return position == resArrList.size();
    }


    @Override
    public int getItemCount() {
        if (isFooterEnabled == true) {
            return resArrList.size() + 1;
        } else {
            return resArrList.size();
        }
    }

    public void addFooterView() {
        this.isFooterEnabled = true;
        notifyDataSetChanged();
        if (footerHolder != null)
            footerHolder.progressArea.setVisibility(View.VISIBLE);
    }

    public void removeFooterView() {
        if (footerHolder != null)
            footerHolder.progressArea.setVisibility(View.GONE);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView restaurantRateTxt;

        TextView restaurantNameTxt, deliveryTimeTxt, minOrderTxt, RestCuisineTXT, pricePerPersonTxt, offerTxt,dev_timeLbl;
        TextView resStatusTxt;
        SelectableRoundedImageView imgView;
        RelativeLayout restaurantAdptrLayout,rating_layout;
                LinearLayout offerArea;
        ImageView offerImage, timerImage, vImageImgView;
        //Fav Store Features
        LikeButton likeButton;
        ImageView favImage;

        public ViewHolder(View view) {
            super(view);

            imgView = (SelectableRoundedImageView) view.findViewById(R.id.imgView);
            restaurantNameTxt = view.findViewById(R.id.restaurantNameTxt);
            resStatusTxt = view.findViewById(R.id.resStatusTxt);
            restaurantRateTxt = view.findViewById(R.id.restaurantRateTxt);
            RestCuisineTXT = view.findViewById(R.id.RestCuisineTXT);
            offerTxt = view.findViewById(R.id.offerTxt);
            pricePerPersonTxt = view.findViewById(R.id.pricePerPersonTxt);
            deliveryTimeTxt = view.findViewById(R.id.deliveryTimeTxt);
            minOrderTxt = view.findViewById(R.id.minOrderTxt);
            dev_timeLbl = view.findViewById(R.id.dev_timeLbl);
            restaurantAdptrLayout = view.findViewById(R.id.restaurantAdptrLayout);
            offerArea = view.findViewById(R.id.offerArea);
            offerImage = view.findViewById(R.id.offerImage);
            timerImage = view.findViewById(R.id.timerImage);
            vImageImgView = view.findViewById(R.id.imgView);
            favImage = view.findViewById(R.id.favImage);
            likeButton = (LikeButton) view.findViewById(R.id.likeButton);
            rating_layout= view.findViewById(R.id.rating_layout);
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

    class FooterViewHolder extends RecyclerView.ViewHolder {
        LinearLayout progressArea;

        public FooterViewHolder(View itemView) {
            super(itemView);

            progressArea = (LinearLayout) itemView;

        }
    }
}
