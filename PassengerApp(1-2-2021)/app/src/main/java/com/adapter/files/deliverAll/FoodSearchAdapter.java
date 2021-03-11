package com.adapter.files.deliverAll;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.JResponseClasses.AddFoodSearchItem;
import com.junaid_condition_sets.StaticClass;
import com.servo.user.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class FoodSearchAdapter extends RecyclerView.Adapter<FoodSearchAdapter.ViewHolder> {

    ArrayList<HashMap<String, String>> foodSearchArrList;
    Context mContext;
    OnItemClickList onItemClickList;
    StaticClass st;
    AddFoodSearchItem addFoodSearchItem;




    public FoodSearchAdapter(Context context, ArrayList<HashMap<String, String>> mapArrayList,AddFoodSearchItem addFoodSearchItem) {
        this.mContext = context;
        this.foodSearchArrList = mapArrayList;
        st = new StaticClass(mContext);
    }

    public FoodSearchAdapter() {

    }

    public void passIntercase(AddFoodSearchItem addFoodSearchItem){
        this.addFoodSearchItem=addFoodSearchItem;

    }

    @Override
    public FoodSearchAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.j_restaurant_detail, parent, false);
        FoodSearchAdapter.ViewHolder viewHolder = new FoodSearchAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(FoodSearchAdapter.ViewHolder myViewHolder, @SuppressLint("RecyclerView") int position) {

        HashMap<String, String> mapData = foodSearchArrList.get(position);

        String name = mapData.get("vItemType");
        String desc = mapData.get("vItemDesc");
        String nameCheck = mapData.get("vCategoryName");
        String fPrice = mapData.get("fPrice");
        String vImage = mapData.get("vImage");
        String fDiscountPrice = mapData.get("fDiscountPrice");

        if(TextUtils.isEmpty(name)&&TextUtils.isEmpty(desc)&&vImage.isEmpty()){
            myViewHolder.cv_parent_layout.setVisibility(View.GONE);
        }

        if(name == null ||name.trim().length()== 0){
            myViewHolder.tv_item_name.setVisibility(View.GONE);
            myViewHolder.tv_item_name1.setVisibility(View.GONE);

        }
        else {
            myViewHolder.tv_item_name.setText(name);
            myViewHolder.tv_item_name1.setText(name);
        }
        if(desc == null ||desc.trim().length()== 0){
            myViewHolder.tv_description.setVisibility(View.GONE);
            myViewHolder.tv_description1.setVisibility(View.GONE);
        }
        else {
            myViewHolder.tv_description.setText(desc);

            myViewHolder.tv_description1.setText(desc);

        }






        myViewHolder.ex_old_price.setText(st.htmlText("<del>$" + fPrice + "</del>"));
        myViewHolder.tv_old_price.setText(st.htmlText("<del>$" + fPrice + "</del>"));
        myViewHolder.tv_new_price.setText("$"+fDiscountPrice);
        myViewHolder.tv_new_price1.setText("$"+fDiscountPrice);

        if(vImage.isEmpty()) {
            Picasso.with(mContext)
                    .load(R.mipmap.ic_no_icon)
                    .placeholder(R.mipmap.ic_no_icon)
                    .error(R.mipmap.ic_no_icon)
                    .into(myViewHolder.iv_img_icon);
//            myViewHolder.iv_img_icon.setImageResource(R.drawable.amu_bubble_mask);
            //  myViewHolder.cv_parent_layout.setVisibility(View.GONE);
        }

        else /*if (vImage != null)*/ {
            Picasso.with(mContext)
                    .load(vImage != null ? vImage : "https://www.test.com/ghg").placeholder(R.mipmap.ic_no_icon).error(mContext.getResources().getDrawable(R.mipmap.ic_no_icon))
                    .fit().into(myViewHolder.iv_img_icon);
            Picasso.with(mContext)
                    .load(vImage != null ? vImage : "https://www.test.com/ghg").placeholder(R.mipmap.ic_no_icon).error(mContext.getResources().getDrawable(R.mipmap.ic_no_icon))
                    .fit().into(myViewHolder.iv_img_icon1);
        }


        myViewHolder.cv_parent_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

             /*   if(selected==i){
                    selected=-1;
                }
                else {selected = i;}

                notifyDataSetChanged();*/
            }
        });



        String eFoodType = mapData.get("eFoodType");

       /* if(selected!=i) {
            myViewHolder.iv_non_veg_type.setVisibility(eFoodType.equals("NonVeg") ? View.VISIBLE : View.GONE);
            myViewHolder.iv_veg_type.setVisibility(eFoodType.equals("NonVeg") ? View.GONE : View.VISIBLE);
        }

        if(selected==i) {
            myViewHolder.iv_non_veg_type.setVisibility( View.GONE);
            myViewHolder.iv_veg_type.setVisibility(View.GONE);
        }*/

        myViewHolder.addLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  mItemClickListener.onItemClickList(myViewHolder.addLayout, i);
            }
        });

        myViewHolder.addLayout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

              //  HashMap<String,String> hashMap= foodSearchArrList.get(position);
                addFoodSearchItem.addSearchItem(foodSearchArrList.get(position));

                // saveMap(paymentTypeHashmap);

             //   new StartActProcess(mContext).startActForResult(AddBasketActivity.class, bn, Utils.ADD_TO_BASKET);
              //  mItemClickListener.onItemClickList(myViewHolder.addLayout1, i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return foodSearchArrList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CardView cv_parent_layout;
        TextView tv_item_name, tv_description, tv_old_price, tv_new_price;
        TextView tv_new_price1,tv_item_name1,ex_old_price;

        TextView tv_description1;

        ImageView iv_veg_type, iv_non_veg_type, iv_img_icon;
        ImageView iv_img_icon1;

        RelativeLayout rv_collaps_lay;
        LinearLayout ll_expand_lay;

        LinearLayout addLayout,addLayout1;

        public ViewHolder(View view) {
            super(view);
            cv_parent_layout = view.findViewById(R.id.cv_parent_layout);
            tv_item_name = view.findViewById(R.id.tv_item_name);
            tv_description = view.findViewById(R.id.tv_description);
            tv_item_name1 = view.findViewById(R.id.tv_item_name1);
            tv_old_price = view.findViewById(R.id.tv_old_price);
            tv_new_price = view.findViewById(R.id.tv_new_price);
            iv_veg_type = view.findViewById(R.id.iv_veg_type);
            iv_non_veg_type = view.findViewById(R.id.iv_non_veg_type);
            addLayout = view.findViewById(R.id.addLayout);
            addLayout1 = view.findViewById(R.id.addLayout1);
            iv_img_icon = view.findViewById(R.id.iv_img_icon);
            ll_expand_lay = view.findViewById(R.id.ll_expand_lay);
            rv_collaps_lay = view.findViewById(R.id.rv_collaps_lay);
            tv_description1 = view.findViewById(R.id.tv_description1);
            iv_img_icon1 = view.findViewById(R.id.iv_img_icon1);
            tv_new_price1 = view.findViewById(R.id.tv_new_price1);
            ex_old_price = view.findViewById(R.id.tv_price_old);
        }
    }


    public void setOnItemClickList(OnItemClickList onItemClickList) {
        this.onItemClickList = onItemClickList;
    }

    public interface OnItemClickList {
        void onItemClick(int position);
    }


}
