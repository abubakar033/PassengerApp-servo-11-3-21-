package com.adapter.files;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.servo.user.MultiDeliverySecondPhaseActivity;
import com.servo.user.R;
import com.general.files.GeneralFunctions;
import com.model.Multi_Delivery_Data;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Admin on 09-07-2016.
 */
public class MultiDestinationItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;
    public GeneralFunctions generalFunc;
    ArrayList<Multi_Delivery_Data> list;
    Context mContext;
    boolean isFooterEnabled = false;
    View footerView;
    FooterViewHolder footerHolder;
    MultiDeliverySecondPhaseActivity multiDeliverySecondPhaseActivity;
    private OnItemClickListener mItemClickListener;


    public MultiDestinationItemAdapter(Context mContext, ArrayList<Multi_Delivery_Data> list, GeneralFunctions generalFunc, boolean isFooterEnabled) {
        this.mContext = mContext;
        this.list = list;
        this.generalFunc = generalFunc;
        this.isFooterEnabled = isFooterEnabled;
        multiDeliverySecondPhaseActivity = (MultiDeliverySecondPhaseActivity) mContext;
    }

    public void setOnItemClickListener(OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        multiDeliverySecondPhaseActivity.height = parent.getMeasuredHeight();

        if (viewType == TYPE_FOOTER) {
            View v = LayoutInflater.from(mContext).inflate(R.layout.footer_list, parent, false);
            this.footerView = v;
            return new FooterViewHolder(v);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.multi_dest_item_layout, parent, false);
            return new ViewHolder(view);
        }

    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {


        if (holder instanceof ViewHolder) {
            final Multi_Delivery_Data item = list.get(position);
            final ViewHolder viewHolder = (ViewHolder) holder;
            item.setListPos(position);

            if (item.getIsFromLoc().equalsIgnoreCase("Yes")) {
                viewHolder.iv_round.setColorFilter(mContext.getResources().getColor(R.color.fr_round_icon_color));
                viewHolder.toTitleTxt.setText(item.getFRLable());
                viewHolder.toTitleTxt.setTextColor(mContext.getResources().getColor(R.color.appThemeColor_TXT_1));
                viewHolder.toValueTxt.setText(item.getDestAddress());
                viewHolder.removeAdd.setVisibility(View.GONE);
            } else {
                viewHolder.iv_round.setColorFilter(mContext.getResources().getColor(R.color.to_round_icon_color));
                viewHolder.toTitleTxt.setTextColor(mContext.getResources().getColor(R.color.appThemeColor_2));
                viewHolder.toTitleTxt.setText(item.getToLable());
                viewHolder.toValueTxt.setHint(item.getHintLable());
                if (position == 1 && list.size() == 2) {
                    viewHolder.toTitleTxt.setText(item.getToLable());
                    viewHolder.removeAdd.setVisibility(View.GONE);
                } else {
                    viewHolder.toTitleTxt.setText("" + (position));
                    viewHolder.removeAdd.setVisibility(View.VISIBLE);
                }
            }

            viewHolder.toValueTxt.setText(item.getDestAddress());

            viewHolder.removeAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onItemClickList("Remove", position);
                    }
                }
            });

            viewHolder.mainSelectionArea.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onItemClickList("Select", position);
                    }
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
        this.isFooterEnabled = true;
        notifyDataSetChanged();
        if (footerHolder != null)
            footerHolder.progressArea.setVisibility(View.VISIBLE);
    }

    public void removeFooterView() {
        if (footerHolder != null)
            footerHolder.progressArea.setVisibility(View.GONE);
    }


    public interface OnItemClickListener {
        void onItemClickList(String type, int position);
    }

    // inner class to hold a reference to each item of RecyclerView
    public class ViewHolder extends RecyclerView.ViewHolder {


        private TextView toTitleTxt, toValueTxt;
        private LinearLayout removeAdd;
        private ImageView iv_round;
        private LinearLayout mainSelectionArea;


        public ViewHolder(View view) {
            super(view);

            toTitleTxt = (TextView) view.findViewById(R.id.toTitleTxt);
            toValueTxt = (TextView) view.findViewById(R.id.toValueTxt);
            removeAdd = (LinearLayout) view.findViewById(R.id.removeAdd);
            iv_round = (ImageView) view.findViewById(R.id.iv_round);
            mainSelectionArea = (LinearLayout) view.findViewById(R.id.mainSelectionArea);
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
