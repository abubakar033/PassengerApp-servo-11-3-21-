package com.jAdapter;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import com.general.files.GeneralFunctions;
import com.junaid_condition_sets.SharedPreference;
import com.servo.user.R;
import com.sinch.gson.Gson;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.utils.CommonUtilities;
import com.utils.Logger;
import com.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;

public class j_banner_adapter extends RecyclerView.Adapter<j_banner_adapter.MyViewHolder> {

    private final int TYPE_GRID = 0;
    private final int TYPE_BANNER = 1;
    private final int NO_TYPE = -1;

    public GeneralFunctions generalFunc;
    ArrayList<HashMap<String, String>> list_item;
    Context mContext;
    OnItemClickList onItemClickList;

    String CAT_TYPE_MODE = "0";

    boolean isForceToGrid = false;
    int positionOfSeperatorView = -1;
    boolean ismultiSelect = false;
    String userProfileJson;

    int dimension;
    int bannerHeight;
    int bannerWidth;
    int width;
    int margin;
    int grid;

    SharedPreference instant_banner_data;

    public j_banner_adapter(Context mContext, ArrayList<HashMap<String, String>> list_item, GeneralFunctions generalFunc) {
        this.mContext = mContext;
        this.list_item = list_item;
        this.generalFunc = generalFunc;
        userProfileJson = generalFunc.retrieveValue(Utils.USER_PROFILE_JSON);
        if (generalFunc.getJsonValue("SERVICE_PROVIDER_FLOW", userProfileJson).equalsIgnoreCase("PROVIDER")) {
            ismultiSelect = true;
        }
        dimension = mContext.getResources().getDimensionPixelSize(R.dimen.category_grid_size);
        margin = mContext.getResources().getDimensionPixelSize(R.dimen.category_banner_left_right_margin);
        grid = mContext.getResources().getDimensionPixelSize(R.dimen.category_grid_size);
        width = margin * 2;
        bannerWidth = Utils.getWidthOfBanner(mContext, width);
        bannerHeight = Utils.getHeightOfBanner(mContext, width, "16:9");

        instant_banner_data = new SharedPreference(mContext,"instant_banner_data");
        instant_banner_data.saveStringValue("list_item",new Gson().toJson(list_item));

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.j_banner_layout, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {

        HashMap<String, String> item = list_item.get(position);

        String vCategory = item.get("vCategory");
        String vLogo_image = item.get("vLogo_image");
        String vBannerImage = item.get("vBannerImage");

//        holder.seperatorView.setVisibility(View.GONE);
//        if (!generalFunc.getJsonValue("RDU_HOME_PAGE_LAYOUT_DESIGN", userProfileJson).equalsIgnoreCase("Banner/Icon")) {
//            if (positionOfSeperatorView == -1 || positionOfSeperatorView == position) {
//                holder.seperatorView.setVisibility(View.VISIBLE);
//                positionOfSeperatorView = position;
//            }
//        }

        String vCategoryBanner = item.get("vCategoryBanner");
//        holder.serviceNameTxt.setText(vCategoryBanner.equalsIgnoreCase("") ? vCategory : vCategoryBanner);
//        holder.bookNowTxt.setText(item.get("tBannerButtonText"));

//        CardView.LayoutParams layoutParams = (CardView.LayoutParams) holder.bannerAreaContainerView.getLayoutParams();
//        layoutParams.height = bannerHeight;

        int btnRadius = Utils.dipToPixels(mContext, 8);
        int strokeWidth = Utils.dipToPixels(mContext, 0);
        int color1 = mContext.getResources().getColor(R.color.appThemeColor_1);
        int color2 = mContext.getResources().getColor(R.color.appThemeColor_2);

//        new CreateRoundedView(color1, btnRadius, strokeWidth, color1, holder.bookNowTxt);
//        new CreateRoundedView(color2, btnRadius, strokeWidth, color1, holder.serviceNameTxt);

        String imageURL = Utils.getResizeImgURL(mContext, vBannerImage, bannerWidth, bannerHeight);

        Picasso.with(mContext).load(vBannerImage.equals("") ? CommonUtilities.SERVER : imageURL).placeholder(R.mipmap.ic_no_icon).into(holder.bannerImgView, new Callback() {
            @Override
            public void onSuccess() { }

            @Override
            public void onError() {

                if (!vBannerImage.contains("http") && !vBannerImage.equals("")) {
                    Picasso.with(mContext).load(GeneralFunctions.parseIntegerValue(0, vBannerImage)).placeholder(R.mipmap.ic_no_icon).error(R.mipmap.ic_no_icon).into(holder.bannerImgView);
                }
            }
        });

        holder.bannerAreaContainerView.setOnClickListener(view -> {
            if (onItemClickList != null) {
                onItemClickList.onItemClick(position);
            }
        });


        if (CAT_TYPE_MODE.equals("0")) {

            holder.bannerAreaContainerView.setOnTouchListener(new SetOnTouchList(TYPE_BANNER, position, false));

                /*holder.containerView.setOnTouchListener((view, motionEvent) -> {
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            scaleView(view, 0.97f, 0.97f, motionEvent.getAction());
                            break;
                        case MotionEvent.ACTION_UP:
                        case MotionEvent.ACTION_CANCEL:
                            scaleView(view, (float) 1.0, (float) 1.0, motionEvent.getAction());
                            break;
                     }
                    return true;
                });*/
        } else {
            Logger.d("TouchOnBanner", "Removed");
            holder.bannerAreaContainerView.setOnTouchListener(null);
        }

    }

    @Override
    public int getItemCount() {
        return list_item.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

//        public View containerView;
        CardView bannerAreaContainerView;
//        public View seperatorView;
        public ImageView bannerImgView;
//        public TextView bookNowTxt, serviceNameTxt;

        public MyViewHolder(@NonNull View view) {
            super(view);
//            containerView = view.findViewById(R.id.containerView);
            bannerImgView = view.findViewById(R.id.bannerImgView);
//            bookNowTxt = (TextView) view.findViewById(R.id.bookNowTxt);
//            serviceNameTxt = (TextView) view.findViewById(R.id.serviceNameTxt);
            bannerAreaContainerView = (CardView) view.findViewById(R.id.bannerAreaContainerView);
//            seperatorView = view.findViewById(R.id.seperatorView);
        }

    }


    private class SetOnTouchList implements View.OnTouchListener {
        int viewType;
        int item_position;
        boolean isBlockDownEvent = false;

        public SetOnTouchList(int viewType, int item_position, boolean isBlockDownEvent) {
            this.item_position = item_position;
            this.viewType = viewType;
            this.isBlockDownEvent = isBlockDownEvent;
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {

            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (!isBlockDownEvent) {
                        scaleView(view, viewType == TYPE_BANNER ? 0.97f : 0.85f, viewType == TYPE_BANNER ? 0.97f : 0.85f, motionEvent.getAction(), viewType, item_position);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (list_item.get(item_position).get("LAST_CLICK_TIME") == null || (System.currentTimeMillis() - GeneralFunctions.parseLongValue(0, list_item.get(item_position).get("LAST_CLICK_TIME"))) > 1000) {
                        scaleView(view, (float) 1.0, (float) 1.0, motionEvent.getAction(), viewType, item_position);
                    }else{
                        scaleView(view, (float) 1.0, (float) 1.0, MotionEvent.ACTION_CANCEL, viewType, item_position);
                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                    scaleView(view, (float) 1.0, (float) 1.0, motionEvent.getAction(), viewType, item_position);
                    break;
            }
            return true;
        }
    }


    public interface OnItemClickList {
        void onItemClick(int position);
        void onMultiItem(String id, boolean b);
    }


    public void setOnItemClickList(OnItemClickList onItemClickList) {
        this.onItemClickList = onItemClickList;
    }

    public void scaleView(View v, float startScale, float endScale, int motionEvent, int viewType, int item_position) {

        v.setOnTouchListener(new SetOnTouchList(viewType, item_position, true));

        Animation anim = new ScaleAnimation(
                startScale, endScale, // Start and end values for the X axis scaling
                startScale, endScale, // Start and end values for the Y axis scaling
                Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
                Animation.RELATIVE_TO_SELF, 0.5f); // Pivot point of Y scaling
        anim.setFillAfter(true); // Needed to keep the result of the animation
        anim.setDuration(100);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (motionEvent == MotionEvent.ACTION_UP) {
                    list_item.get(item_position).put("LAST_CLICK_TIME", "" + System.currentTimeMillis());
                    v.performClick();
                }
                v.setOnTouchListener(new SetOnTouchList(viewType, item_position, false));
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        v.startAnimation(anim);
    }

}