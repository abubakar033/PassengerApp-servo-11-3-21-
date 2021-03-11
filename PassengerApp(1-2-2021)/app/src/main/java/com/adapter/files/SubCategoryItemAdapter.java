package com.adapter.files;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.servo.user.R;
import com.general.files.GeneralFunctions;
import com.model.DeliveryIconDetails;
import com.squareup.picasso.Picasso;
import com.utils.Utils;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class SubCategoryItemAdapter extends RecyclerView.Adapter<SubCategoryItemAdapter.ViewHolder> {

    GeneralFunctions generalFunc;
    ArrayList<DeliveryIconDetails.SubCatData> listData;
    Context mContext;
    setSubCategoryClickList setSubCategoryClickListobj;
    int width;

    public SubCategoryItemAdapter(GeneralFunctions generalFunc, ArrayList<DeliveryIconDetails.SubCatData> listData, Context mContext) {
        this.generalFunc = generalFunc;
        this.listData = listData;
        this.mContext = mContext;
        width = (int) ((Utils.getScreenPixelWidth(mContext) - Utils.dipToPixels(mContext, 16)) / 2);;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_icon_layout, parent, false);

        SubCategoryItemAdapter.ViewHolder viewHolder = new SubCategoryItemAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        DeliveryIconDetails.SubCatData data = listData.get(position);

        holder.boxTitleTxt.setText(data.getvSubCategory());
        holder.boxDescTxt.setText(data.gettSubCategoryDesc());
        Picasso.with(mContext)
                .load(data.getvImage())
                .into(holder.boxImage);

        Log.d("OpenCatType","geteDeliveryType"+data.geteDeliveryType());
        Log.d("OpenCatType","getDataMap"+data.getDataMap());

        holder.containerView.setOnClickListener(view -> setSubCategoryClickListobj.itemSubCategoryClick(position, data.geteDeliveryType(), data.getDataMap()));

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.contentView.getLayoutParams();

        params.width = width;

        holder.contentView.setLayoutParams(params);
    }


    public void setOnClickList(setSubCategoryClickList setSubCategoryClickListobj) {
        this.setSubCategoryClickListobj = setSubCategoryClickListobj;
    }

    public interface setSubCategoryClickList {
        void itemSubCategoryClick(int position, String eDeliveryType, HashMap<String, String> dataMap);
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    // inner class to hold a reference to each item of RecyclerView
    public class ViewHolder extends RecyclerView.ViewHolder {


        private TextView boxTitleTxt;
        private TextView boxDescTxt;
        private ImageView boxImage;
        private View containerView;
        private View contentView;


        public ViewHolder(View view) {
            super(view);

            containerView = view;
            contentView = view.findViewById(R.id.contentView);
            boxTitleTxt = (TextView) view.findViewById(R.id.boxTitleTxt);
            boxDescTxt = (TextView) view.findViewById(R.id.boxDescTxt);
            boxImage = (ImageView) view.findViewById(R.id.boxImage);

        }
    }
}
