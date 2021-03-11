package com.adapter.files;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.servo.user.R;
import com.general.files.GeneralFunctions;
import com.model.DeliveryIconDetails;

import android.widget.TextView;

import java.util.ArrayList;

public class DeliveryIconAdapter extends RecyclerView.Adapter<DeliveryIconAdapter.ViewHolder> {


    GeneralFunctions generalFunc;
    ArrayList<DeliveryIconDetails> listData;
    Context mContext;


    SubCategoryItemAdapter.setSubCategoryClickList setSubCategoryClickList;

    public DeliveryIconAdapter(GeneralFunctions generalFunc, ArrayList<DeliveryIconDetails> listData, Context mContext, SubCategoryItemAdapter.setSubCategoryClickList setSubCategoryClickList) {
        this.generalFunc = generalFunc;
        this.listData = listData;
        this.mContext = mContext;
        this.setSubCategoryClickList = setSubCategoryClickList;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.delivery_icon_layout, parent, false);

        DeliveryIconAdapter.ViewHolder viewHolder = new DeliveryIconAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ArrayList<DeliveryIconDetails.SubCatData> sublistdata = new ArrayList<>();

        DeliveryIconDetails data=listData.get(position);
        holder.headerTitleTxt.setText(data.getvCategory());
        holder.subHeaderTitleTxt.setText(data.gettCategoryDesc());

        ArrayList<DeliveryIconDetails.SubCatData> itemData = data.getSubDataList();
        for (int i = 0; i < itemData.size(); i++) {
            sublistdata.add(itemData.get(i));
        }

        Log.d("OpenCatType",""+sublistdata);
        SubCategoryItemAdapter subCategoryItemAdapter = new SubCategoryItemAdapter(generalFunc, sublistdata, mContext);
        subCategoryItemAdapter.setOnClickList(setSubCategoryClickList);

        GridLayoutManager mLayoutManager = new GridLayoutManager(mContext, 2);
        holder.deliveryRecycleview.setLayoutManager(mLayoutManager);
        holder.deliveryRecycleview.setAdapter(subCategoryItemAdapter);
        subCategoryItemAdapter.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    // inner class to hold a reference to each item of RecyclerView
    public class ViewHolder extends RecyclerView.ViewHolder {


        private TextView headerTitleTxt;
        private TextView subHeaderTitleTxt;
        private View containerView;
        RecyclerView deliveryRecycleview;

        public ViewHolder(View view) {
            super(view);

            containerView = view;
            headerTitleTxt = (TextView) view.findViewById(R.id.headerTitleTxt);
            subHeaderTitleTxt = (TextView) view.findViewById(R.id.subHeaderTitleTxt);
            deliveryRecycleview = (RecyclerView) view.findViewById(R.id.deliveryRecycleview);

        }
    }
}
