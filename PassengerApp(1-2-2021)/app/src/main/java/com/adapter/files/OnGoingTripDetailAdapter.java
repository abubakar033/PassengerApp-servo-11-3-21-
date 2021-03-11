package com.adapter.files;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.servo.user.R;
import com.general.files.GeneralFunctions;
import com.utils.Utils;
import com.view.CreateRoundedView;
import android.widget.TextView;
import com.view.SelectableRoundedImageView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Admin on 22-02-2017.
 */
public class OnGoingTripDetailAdapter extends RecyclerView.Adapter<OnGoingTripDetailAdapter.ViewHolder> {

    public GeneralFunctions generalFunc;
    ArrayList<HashMap<String, String>> list_item;
    Context mContext;
    OnItemClickList onItemClickList;

    public OnGoingTripDetailAdapter(Context mContext, ArrayList<HashMap<String, String>> list_item, GeneralFunctions generalFunc) {
        this.mContext = mContext;
        this.list_item = list_item;
        this.generalFunc = generalFunc;
    }

    @Override
    public OnGoingTripDetailAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_design_ongoing_trip_cell, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        HashMap<String, String> item = list_item.get(position);
        viewHolder.tripStatusTxt.setText(item.get("msg"));

        String time=item.get("time");
        viewHolder.tripStatusTimeTxt.setText(time);
        viewHolder.tripTimeTxt.setText(time);

        viewHolder.tripTimeTxt.setVisibility(View.GONE);
        int color=mContext.getResources().getColor(R.color.appThemeColor_1);

        new CreateRoundedView(color, Utils.dipToPixels(mContext, 60), 0, color, viewHolder.driverImgView);

        if (item.get("eType").equalsIgnoreCase(Utils.eType_Multi_Delivery)) {
            viewHolder.noTxt.setText("" + (position + 1));
            viewHolder.noTxt.setVisibility(View.VISIBLE);
        } else {
            Drawable mDrawable = null;

            String status = item.get("status");
            if (status.equalsIgnoreCase("Accept")) {
                mDrawable = mContext.getResources().getDrawable(R.mipmap.one);
            } else if (status.equalsIgnoreCase("Arrived")) {
                mDrawable = mContext.getResources().getDrawable(R.mipmap.two);
            } else if (status.equalsIgnoreCase("Onway")) {
                mDrawable = mContext.getResources().getDrawable(R.mipmap.three);
            } else if (status.equalsIgnoreCase("Delivered")) {
                mDrawable = mContext.getResources().getDrawable(R.mipmap.four);
            } else if (status.equalsIgnoreCase("Cancelled")) {
                if (!status.equalsIgnoreCase("Arrived")) {
                    mDrawable = mContext.getResources().getDrawable(R.mipmap.two);

                } else if (!status.equalsIgnoreCase("Onway")) {
                    mDrawable = mContext.getResources().getDrawable(R.mipmap.three);
                } else {
                    mDrawable = mContext.getResources().getDrawable(R.mipmap.four);
                }
            } else if (status.equalsIgnoreCase("On the way")) {
                mDrawable = mContext.getResources().getDrawable(R.mipmap.five);
            }

            if (mDrawable != null) {
                mDrawable.setColorFilter(new PorterDuffColorFilter(mContext.getResources().getColor(R.color.white), PorterDuff.Mode.MULTIPLY));
                viewHolder.driverImgView.setImageDrawable(mDrawable);
            }
        }
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

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tripStatusTxt;
        public TextView tripStatusTimeTxt;
        public TextView tripTimeTxt;
        public TextView noTxt;
        public SelectableRoundedImageView driverImgView;

        public ViewHolder(View view) {
            super(view);

            tripStatusTxt = (TextView) view.findViewById(R.id.tripStatusTxt);
            tripStatusTimeTxt = (TextView) view.findViewById(R.id.tripStatusTimeTxt);
            tripTimeTxt = (TextView) view.findViewById(R.id.tripTimeTxt);
            noTxt = (TextView) view.findViewById(R.id.noTxt);
            driverImgView = (SelectableRoundedImageView) view.findViewById(R.id.driverImgView);
        }
    }

}
