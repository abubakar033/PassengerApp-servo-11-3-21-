package com.adapter.files;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.servo.user.R;
import com.general.files.GeneralFunctions;
import com.utils.Utils;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Admin on 09-07-2016.
 */
public class HistoryRecycleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;
    public GeneralFunctions generalFunc;

    ArrayList<HashMap<String, String>> list;
    Context mContext;
    boolean isFooterEnabled = false;
    View footerView;

    FooterViewHolder footerHolder;
    private OnItemClickListener mItemClickListener;
    String userProfileJson;
    String SERVICE_PROVIDER_FLOW = "";


    public HistoryRecycleAdapter(Context mContext, ArrayList<HashMap<String, String>> list, GeneralFunctions generalFunc, boolean isFooterEnabled) {
        this.mContext = mContext;
        this.list = list;
        this.generalFunc = generalFunc;
        this.isFooterEnabled = isFooterEnabled;
        userProfileJson = generalFunc.retrieveValue(Utils.USER_PROFILE_JSON);
        SERVICE_PROVIDER_FLOW = generalFunc.getJsonValue("SERVICE_PROVIDER_FLOW", userProfileJson);
    }

    public void setOnItemClickListener(OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_FOOTER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_list, parent, false);
            this.footerView = v;
            return new FooterViewHolder(v);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history_design, parent, false);
            return new ViewHolder(view);
        }

    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {


        if (holder instanceof ViewHolder) {
            final HashMap<String, String> item = list.get(position);
            final ViewHolder viewHolder = (ViewHolder) holder;

            String vPackageName=item.get("vPackageName");
            if (vPackageName != null && !vPackageName.equalsIgnoreCase("")) {
                viewHolder.packageTxt.setVisibility(View.VISIBLE);
                viewHolder.packageTxt.setText(vPackageName);
            } else {
                viewHolder.packageTxt.setVisibility(View.GONE);
            }

            viewHolder.historyNoHTxt.setText(item.get("LBL_BOOKING_NO") + "#");

            viewHolder.historyNoVTxt.setText(item.get("vRideNo"));

            String ConvertedTripRequestDateDate=item.get("ConvertedTripRequestDateDate");
            String formattedListingDate=item.get("formattedListingDate");

            if (ConvertedTripRequestDateDate != null) {
                viewHolder.dateTxt.setText(ConvertedTripRequestDateDate);
            } else {
                viewHolder.dateTxt.setText(formattedListingDate);
            }

            viewHolder.statusHTxt.setText(item.get("LBL_Status") + ":");

            viewHolder.sourceAddressTxt.setText(item.get("tSaddress"));
            viewHolder.sourceAddressHTxt.setText(item.get("LBL_PICK_UP_LOCATION"));
            viewHolder.destAddressHTxt.setText(item.get("LBL_DEST_LOCATION"));

            String APP_TYPE=item.get("APP_TYPE");
            if (APP_TYPE.equalsIgnoreCase(Utils.CabGeneralTypeRide_Delivery_UberX) || APP_TYPE.equalsIgnoreCase("Ride-Delivery")) {
                viewHolder.etypeTxt.setText(item.get("eType"));
                viewHolder.dateTxt.setVisibility(View.VISIBLE);
            } else {
                viewHolder.dateTxt.setVisibility(View.GONE);
                if (ConvertedTripRequestDateDate != null) {
                    viewHolder.etypeTxt.setText(ConvertedTripRequestDateDate);
                } else {
                    viewHolder.etypeTxt.setText(formattedListingDate);
                }
            }

            viewHolder.slecttypearea.setVisibility(View.VISIBLE);
            viewHolder.SelectedTypeNameTxt.setText(item.get("vServiceTitle"));

            if (item.get("tDaddress").equals("")) {
                viewHolder.destAddressTxt.setVisibility(View.GONE);
                viewHolder.imagedest.setVisibility(View.GONE);
                viewHolder.destAddressHTxt.setVisibility(View.GONE);
            } else {
                viewHolder.destAddressTxt.setVisibility(View.VISIBLE);
                viewHolder.destAddressHTxt.setVisibility(View.VISIBLE);
                viewHolder.imagedest.setVisibility(View.VISIBLE);
                viewHolder.destAddressTxt.setText(item.get("tDaddress"));
            }

            viewHolder.statusVTxt.setText(item.get("iActive"));

            viewHolder.contentArea.setOnClickListener(view -> {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClickList(view, position);
                }
            });


        } else {
            FooterViewHolder footerHolder = (FooterViewHolder) holder;
            this.footerHolder = footerHolder;
        }


    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionFooter(position) && isFooterEnabled == true) {
            return TYPE_FOOTER;
        }
        return TYPE_ITEM;
    }

    private boolean isPositionFooter(int position) {
        return position == list.size();
    }

    // Return the size of your itemsData (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if (isFooterEnabled == true) {
            return list.size() + 1;
        } else {
            return list.size();
        }

    }

    public void addFooterView() {
//        Logger.d("Footer", "added");
        this.isFooterEnabled = true;
        notifyDataSetChanged();
        if (footerHolder != null)
            footerHolder.progressArea.setVisibility(View.VISIBLE);
    }

    public void removeFooterView() {
//        Logger.d("Footer", "removed");
        if (footerHolder != null)
            footerHolder.progressArea.setVisibility(View.GONE);
//        footerHolder.progressArea.setPadding(0, -1 * footerView.getHeight(), 0, 0);
    }


    public interface OnItemClickListener {
        void onItemClickList(View v, int position);
    }

    // inner class to hold a reference to each item of RecyclerView
    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView historyNoHTxt;
        public TextView historyNoVTxt;
        public TextView dateTxt;
        public TextView sourceAddressTxt;
        public TextView destAddressTxt;
        public TextView sourceAddressHTxt;
        public TextView destAddressHTxt;
        public TextView statusHTxt;
        public TextView statusVTxt;
        public LinearLayout contentArea;
        public TextView etypeTxt;
        public ImageView imagedest;
        public LinearLayout slecttypearea;
        public TextView SelectedTypeNameTxt;
        public TextView packageTxt;


        public ViewHolder(View view) {
            super(view);

            historyNoHTxt = (TextView) view.findViewById(R.id.historyNoHTxt);
            historyNoVTxt = (TextView) view.findViewById(R.id.historyNoVTxt);
            dateTxt = (TextView) view.findViewById(R.id.dateTxt);
            sourceAddressTxt = (TextView) view.findViewById(R.id.sourceAddressTxt);
            destAddressTxt = (TextView) view.findViewById(R.id.destAddressTxt);
            sourceAddressHTxt = (TextView) view.findViewById(R.id.sourceAddressHTxt);
            destAddressHTxt = (TextView) view.findViewById(R.id.destAddressHTxt);
            imagedest = (ImageView) view.findViewById(R.id.imagedest);
            statusHTxt = (TextView) view.findViewById(R.id.statusHTxt);
            statusVTxt = (TextView) view.findViewById(R.id.statusVTxt);
            contentArea = (LinearLayout) view.findViewById(R.id.contentArea);
            etypeTxt = (TextView) view.findViewById(R.id.etypeTxt);
            slecttypearea = (LinearLayout) view.findViewById(R.id.slecttypearea);
            SelectedTypeNameTxt = (TextView) view.findViewById(R.id.SelectedTypeNameTxt);
            packageTxt = (TextView) view.findViewById(R.id.packageTxt);


        }
    }

    class FooterViewHolder extends RecyclerView.ViewHolder {
        LinearLayout progressArea;

        public FooterViewHolder(View itemView) {
            super(itemView);

            progressArea = (LinearLayout) itemView;

        }
    }
}
