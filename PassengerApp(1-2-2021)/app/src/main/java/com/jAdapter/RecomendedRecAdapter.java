package com.jAdapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.JResponseClasses.RecomendedAddItemCallBack;
import com.general.files.GeneralFunctions;
import com.servo.user.R;
import com.squareup.picasso.Picasso;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class RecomendedRecAdapter extends RecyclerView.Adapter<RecomendedRecAdapter.ViewHolder> {

    ArrayList<HashMap<String, String>> list;
    Context mContext;
    Drawable noIcon = null;
    RecomendedAddItemCallBack recomendedAddItemCal;
    GeneralFunctions generalFunctions;

    public RecomendedRecAdapter(ArrayList<HashMap<String, String>> list, Context mContext, RecomendedAddItemCallBack recomendedAddItemCallBack,GeneralFunctions generalFunctions) {
        this.list = list;
        this.mContext = mContext;
        this.generalFunctions = generalFunctions;
        noIcon = mContext.getResources().getDrawable(R.mipmap.ic_no_icon);
        this.recomendedAddItemCal= recomendedAddItemCallBack;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recoemended_recy_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, @SuppressLint("RecyclerView") int i) {
        HashMap<String, String> mapData = list.get(i);

        String name = mapData.get("vItemType");
        String desc = mapData.get("vItemDesc");
        String nameCheck = mapData.get("vCategoryName");
        String fPrice = mapData.get("fDiscountPricewithsymbolConverted");
        String vImage = mapData.get("vImage");
        String fDiscountPrice = mapData.get("fDiscountPrice");
        String foodType= mapData.get("eFoodType");



        if (TextUtils.isEmpty(desc)){
            viewHolder.item_dec.setVisibility(View.GONE);
        }
        if (TextUtils.isEmpty(name)){
            viewHolder.item_name.setVisibility(View.GONE);
        }

        if (foodType.equalsIgnoreCase("Veg")){
            viewHolder.foodtype_IV.setBackgroundResource(R.drawable.veg);
        }
        else if (foodType.equalsIgnoreCase("NonVeg")){
            viewHolder.foodtype_IV.setBackgroundResource(R.drawable.nonveg);

        }
        if (foodType.trim().equalsIgnoreCase("")){
            viewHolder.foodtype_IV.setVisibility(View.GONE);
        }

        viewHolder.item_name.setText(name);
        viewHolder.item_dec.setText(desc);
        viewHolder.item_price.setText(fPrice);
        viewHolder.addtext.setText(generalFunctions.retrieveLangLBl("Add","LBL_ADD"));



        if(vImage.isEmpty()) {
            Picasso.with(mContext)
                    .load(R.mipmap.ic_no_icon)
                    .placeholder(R.mipmap.ic_no_icon)
                    .error(R.mipmap.ic_no_icon)
                    .into(viewHolder.item_iv);
//            myViewHolder.iv_img_icon.setImageResource(R.drawable.amu_bubble_mask);
            //  myViewHolder.cv_parent_layout.setVisibility(View.GONE);
        }

        else /*if (vImage != null)*/ {
            Picasso.with(mContext)
                    .load(vImage != null ? vImage : "https://www.test.com/ghg").placeholder(R.mipmap.ic_no_icon).error(noIcon)
                    .fit().into(viewHolder.item_iv);
            Picasso.with(mContext)
                    .load(vImage != null ? vImage : "https://www.test.com/ghg").placeholder(R.mipmap.ic_no_icon).error(noIcon)
                    .fit().into(viewHolder.item_iv);
        }

        viewHolder.add_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recomendedAddItemCal.onAddItemReco(i);
            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView item_iv;
        TextView item_name, item_dec, item_price,addtext;
        LinearLayout add_layout;
        ImageView foodtype_IV;


        public ViewHolder(@NonNull View itemView) {

            super(itemView);
            item_iv = itemView.findViewById(R.id.reco_iv_img_icon1);
            addtext = itemView.findViewById(R.id.addtext);
            item_name = itemView.findViewById(R.id._reco_tv_item_name1);
            item_dec = itemView.findViewById(R.id.reco_tv_description1);
            item_price = itemView.findViewById(R.id.reco_tv_new_price1);
            add_layout = itemView.findViewById(R.id.reco_addLayout1);
            foodtype_IV = itemView.findViewById(R.id.reco_foodType_IV);

        }
    }
}
