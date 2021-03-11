package com.adapter.files;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;


import com.servo.user.R;
import com.general.files.GeneralFunctions;
import com.utils.Utils;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class NotificationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public GeneralFunctions generalFunc;
    ArrayList<HashMap<String, String>> list;
    Context mContext;
    private NotificationAdapter.OnItemClickListener mItemClickListener;
    boolean isFooterEnabled = false;


    String type;
    View footerView;
    NotificationAdapter.FooterViewHolder footerHolder;

    public NotificationAdapter(Context mContext, ArrayList<HashMap<String, String>> list, String type, GeneralFunctions generalFunc, boolean isFooterEnabled) {
        this.mContext = mContext;
        this.list = list;
        this.generalFunc = generalFunc;
        this.type = type;
        this.isFooterEnabled = isFooterEnabled;
    }

    public void setOnItemClickListener(NotificationAdapter.OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
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


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification_view, parent, false);
        return new NotificationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final NotificationAdapter.ViewHolder viewHolder = (NotificationAdapter.ViewHolder) holder;
        HashMap<String, String> map=list.get(position);
        String vTitle=map.get("vTitle");
        if (vTitle != null && !vTitle.equalsIgnoreCase("")) {
            viewHolder.titleTxt.setText(vTitle);
        }

        String tDescription=map.get("tDescription");
        if (tDescription != null && !tDescription.equalsIgnoreCase("")) {
            viewHolder.detailsTxt.setText(tDescription);
        }

        String dDateTime=map.get("dDateTime");
        if (dDateTime != null && !dDateTime.equalsIgnoreCase("")) {
            viewHolder.dateTxt.setText(generalFunc.getDateFormatedType(dDateTime, Utils.OriginalDateFormate, Utils.dateFormateInList));
        }

        viewHolder.readMoreTxt.setText(map.get("readMoreLbl"));

        viewHolder.readMoreTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mItemClickListener != null) {
                    mItemClickListener.onReadMoreItemClick(view, position);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView titleTxt;
        public TextView detailsTxt;
        public TextView dateTxt;
        public TextView readMoreTxt;


        public ViewHolder(View view) {
            super(view);

            titleTxt = (TextView) view.findViewById(R.id.titleTxt);
            detailsTxt = (TextView) view.findViewById(R.id.detailsTxt);
            dateTxt = (TextView) view.findViewById(R.id.dateTxt);
            readMoreTxt = (TextView) view.findViewById(R.id.readMoreTxt);


        }
    }

    public interface OnItemClickListener {

        void onReadMoreItemClick(View v, int position);

    }


    class FooterViewHolder extends RecyclerView.ViewHolder {
        LinearLayout progressArea;

        public FooterViewHolder(View itemView) {
            super(itemView);

            progressArea = (LinearLayout) itemView;

        }
    }

}
