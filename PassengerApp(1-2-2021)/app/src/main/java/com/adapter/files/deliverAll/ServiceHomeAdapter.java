package com.adapter.files.deliverAll;

import android.content.Context;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

import com.servo.user.R;
import com.general.files.GeneralFunctions;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.utils.CommonUtilities;
import com.utils.Logger;
import com.utils.Utils;
import com.view.CreateRoundedView;
import android.widget.TextView;
import com.view.SelectableRoundedImageView;

import java.util.ArrayList;
import java.util.HashMap;

public class ServiceHomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    public GeneralFunctions generalFunc;
    ArrayList<HashMap<String, String>> list_item;
    Context mContext;
    OnItemClickList onItemClickList;

    String CAT_TYPE_MODE = "0";

    int bannerWidth;
    int bannerHeight;
    int bannerFactor;

    public ServiceHomeAdapter(Context mContext, ArrayList<HashMap<String, String>> list_item, GeneralFunctions generalFunc) {
        this.mContext = mContext;
        this.list_item = list_item;
        this.generalFunc = generalFunc;
        bannerFactor=mContext.getResources().getDimensionPixelSize(R.dimen.category_banner_left_right_margin) * 2;
        bannerHeight=Utils.getHeightOfBanner(mContext, bannerFactor, "18:9");
        bannerWidth=Utils.getWidthOfBanner(mContext, bannerFactor);

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_service_banner_design, parent, false);
        BannerViewHolder bannerViewHolder = new BannerViewHolder(view);
        return bannerViewHolder;


    }

    public void setCategoryMode(String CAT_TYPE_MODE) {
        this.CAT_TYPE_MODE = CAT_TYPE_MODE;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder parentViewHolder, final int position) {

        HashMap<String, String> item = list_item.get(position);


        BannerViewHolder holder = (BannerViewHolder) parentViewHolder;


        holder.seperatorView.setVisibility(View.GONE);

        int listSize=list_item.size();

        if (listSize > 1) {
            if (listSize - 1 != position) {
                holder.seperatorView.setVisibility(View.VISIBLE);
            }
        }


        holder.serviceNameTxt.setText(item.get("vServiceName"));

        holder.bookNowTxt.setText(item.get("LBL_BOOK_NOW"));

        CardView.LayoutParams layoutParams = (CardView.LayoutParams) holder.bannerAreaContainerView.getLayoutParams();

        layoutParams.height = Utils.getHeightOfBanner(mContext, mContext.getResources().getDimensionPixelSize(R.dimen.category_banner_left_right_margin) * 2, "18:9");

        int color1=mContext.getResources().getColor(R.color.appThemeColor_1);
        int color2=mContext.getResources().getColor(R.color.appThemeColor_1);
        int btnRadius=Utils.dipToPixels(mContext, 8);
        int strokeWidth=Utils.dipToPixels(mContext, 0);

        new CreateRoundedView(color1, btnRadius, strokeWidth, color1, holder.bookNowTxt);
        new CreateRoundedView(color2, btnRadius, strokeWidth, color1, holder.serviceNameTxt);


        String vImage=item.get("vImage");
        String imageURL = Utils.getResizeImgURL(mContext, vImage, bannerWidth, bannerHeight);

        Picasso.with(mContext).load(vImage.equals("") ? CommonUtilities.SERVER : imageURL).
                placeholder(R.mipmap.ic_no_icon).into(holder.bannerImgView, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {
                try {

                    if (!vImage.contains("http") && !vImage.equals("")) {
                        Picasso.with(mContext).load(GeneralFunctions.parseIntegerValue(0, vImage)).placeholder(R.mipmap.ic_no_icon).error(R.mipmap.ic_no_icon).into(holder.bannerImgView);
                    }
                } catch (Exception e) {
                    Logger.d("Exception", "::" + e.toString());

                }
            }
        });




        holder.containerView.setOnClickListener(view -> {
            if (onItemClickList != null) {
                onItemClickList.onItemClick(position);
            }
        });

        if (CAT_TYPE_MODE.equals("0")) {

            holder.containerView.setOnTouchListener((view, motionEvent) -> {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        scaleView(view, 0.97f, 0.97f);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        scaleView(view, (float) 1.0, (float) 1.0);
                        break;
                }
                return false;
            });
        } else {
            Logger.d("TouchOnBanner", "Removed");
            holder.containerView.setOnTouchListener(null);
        }


    }

    public void scaleView(View v, float startScale, float endScale) {
        Animation anim = new ScaleAnimation(
                startScale, endScale, // Start and end values for the X axis scaling
                startScale, endScale, // Start and end values for the Y axis scaling
                Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
                Animation.RELATIVE_TO_SELF, 0.5f); // Pivot point of Y scaling
        anim.setFillAfter(true); // Needed to keep the result of the animation
        anim.setDuration(100);
        v.startAnimation(anim);
    }

    @Override
    public int getItemCount() {
        return list_item.size();
    }


    public void setOnItemClickList(OnItemClickList onItemClickList) {
        this.onItemClickList = onItemClickList;
    }

    public interface OnItemClickList {
        void onItemClick(int position);
    }


    public class BannerViewHolder extends RecyclerView.ViewHolder {


        public View containerView;
        public View bannerAreaContainerView;
        public View seperatorView;
        public SelectableRoundedImageView bannerImgView;
        public TextView bookNowTxt, serviceNameTxt;

        public BannerViewHolder(View view) {
            super(view);
            containerView = view.findViewById(R.id.containerView);
            bannerImgView = (SelectableRoundedImageView) view.findViewById(R.id.bannerImgView);
            bookNowTxt = (TextView) view.findViewById(R.id.bookNowTxt);
            serviceNameTxt = (TextView) view.findViewById(R.id.serviceNameTxt);
            bannerAreaContainerView = view.findViewById(R.id.bannerAreaContainerView);
            seperatorView = view.findViewById(R.id.seperatorView);
        }
    }


}
