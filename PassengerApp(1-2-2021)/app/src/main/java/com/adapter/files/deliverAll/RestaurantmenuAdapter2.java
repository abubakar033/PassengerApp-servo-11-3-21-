package com.adapter.files.deliverAll;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.general.files.GeneralFunctions;
import com.junaid_condition_sets.StaticClass;
import com.servo.user.R;
import com.servo.user.deliverAll.RestaurantAllDetailsNewActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class RestaurantmenuAdapter2 extends RecyclerView.Adapter<RestaurantmenuAdapter2.MyViewHolder> {

    private final int TYPE_HEADER = 0;
    private final int TYPE_GRID = 1;
    private final int TYPE_LIST = 2;

    int selected = -1;

    StaticClass st;
    ArrayList<HashMap<String, String>> list;
    Context mContext;
    GeneralFunctions generalFunc;
    private RestaurantmenuAdapter2.OnItemClickListener mItemClickListener;
    int cnt = 0;
    int grayColor = -1;
    Drawable noIcon = null;
    FooterViewHolder footerHolder;

    RestaurantAllDetailsNewActivity restAllDetailsNewAct;

    int imageBackColor;
    boolean isRTLmode = false;
    boolean isFooterEnabled = false;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;

    public RestaurantmenuAdapter2(Context mContext, ArrayList<HashMap<String, String>> list, GeneralFunctions generalFunc,boolean isFooterEnabled) {
        this.mContext = mContext;
        st = new StaticClass(mContext);
        this.list = list;
        this.isFooterEnabled= isFooterEnabled;
        this.generalFunc = generalFunc;

        if (mContext instanceof RestaurantAllDetailsNewActivity) {
            restAllDetailsNewAct = (RestaurantAllDetailsNewActivity) mContext;
        }

        grayColor = mContext.getResources().getColor(R.color.gray);
        imageBackColor = mContext.getResources().getColor(R.color.appThemeColor_1);
        noIcon = mContext.getResources().getDrawable(R.mipmap.ic_no_icon);
        isRTLmode = generalFunc.isRTLmode();

        Log.d("listsizeeeeeeeee", "oncreatSize::" + list.size());

    }

    public void setOnItemClickListener(RestaurantmenuAdapter2.OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClickList(View v, int position, HashMap<String, String> mapdata);
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        if (i == TYPE_FOOTER) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.footer_list, viewGroup, false);
        return new MyViewHolder(view); }
        else{

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.j_restaurant_detail, viewGroup, false);
        return new MyViewHolder(view);}
    }




    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, @SuppressLint("RecyclerView") final int i) {



        HashMap<String, String> mapData = list.get(i);
        if (mapData.get("Type").equalsIgnoreCase("GRID")) {

            String name = mapData.get("vItemType");
            String desc = mapData.get("vItemDesc");
            String nameCheck = mapData.get("vCategoryName");
            String fPrice = mapData.get("fPrice");
            String vImage= mapData.get("vImage");

            String fDiscountPrice = mapData.get("fDiscountPricewithsymbolConverted");
            Log.d("fDiscountPricewit", "" + mapData.get("vHighlightNameLBL"));
            String vHighlightNameTxt = mapData.get("vHighlightNameLBL");

            Log.d("listsizeeeeeeeee", "onBindViewHolder::");


            if (TextUtils.isEmpty(name) && TextUtils.isEmpty(desc) && vImage.isEmpty()) {
                myViewHolder.cv_parent_layout.setVisibility(View.GONE);
            }

            if (name == null || name.trim().length() == 0) {
                myViewHolder.tv_item_name.setVisibility(View.GONE);
                myViewHolder.tv_item_name1.setVisibility(View.GONE);

            } else {
                myViewHolder.tv_item_name.setText(name);
                myViewHolder.tv_item_name1.setText(name);
                // change
            }
            if (vHighlightNameTxt == null || vHighlightNameTxt.trim().length() == 0) {
                myViewHolder.vHighlightNamelay.setVisibility(View.GONE);
            } else {
                myViewHolder.vHighlightNamelay.setVisibility(View.VISIBLE);
                myViewHolder.vHighlightNameTxt.setText(vHighlightNameTxt);

            }
            if (desc == null || desc.trim().length() == 0) {
                myViewHolder.tv_description.setVisibility(View.GONE);
                myViewHolder.tv_description1.setVisibility(View.GONE);
            } else {
                myViewHolder.tv_description.setText(desc);

                myViewHolder.tv_description1.setText(desc);

            }


            // myViewHolder.ex_old_price.setText(st.htmlText("<del>$" + fPrice + "</del>"));
            // myViewHolder.tv_old_price.setText(st.htmlText("<del>$" + fPrice + "</del>"));
            myViewHolder.tv_new_price.setText(fDiscountPrice);
            myViewHolder.tv_new_price1.setText(fDiscountPrice);
            myViewHolder.add_text.setText(generalFunc.retrieveLangLBl("Add", "LBL_ADD"));



            if (vImage.isEmpty()) {
                Glide.with(mContext)
                        .load(R.mipmap.ic_no_icon)
                        .placeholder(R.mipmap.ic_no_icon)
                        .error(R.mipmap.ic_no_icon)
                        .into(myViewHolder.iv_img_icon);
//            myViewHolder.iv_img_icon.setImageResource(R.drawable.amu_bubble_mask);
                //  myViewHolder.cv_parent_layout.setVisibility(View.GONE);
            } else /*if (vImage != null)*/ {
                Glide.with(mContext)
                        .load(vImage != null ? vImage : "https://www.test.com/ghg").placeholder(R.mipmap.ic_no_icon).error(noIcon)
                        .into(myViewHolder.iv_img_icon);
                Glide.with(mContext)
                        .load(vImage != null ? vImage : "https://www.test.com/ghg").placeholder(R.mipmap.ic_no_icon).error(noIcon)
                        .into(myViewHolder.iv_img_icon1);
            }


            myViewHolder.ll_expand_lay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    mItemClickListener.onItemClickList(myViewHolder.addLayout1, i, list.get(i));
                }
            });

            if (selected != i) {
                myViewHolder.rv_collaps_lay.setVisibility(View.GONE);
                myViewHolder.ll_expand_lay.setVisibility(View.VISIBLE);
            } else {
                myViewHolder.rv_collaps_lay.setVisibility(View.GONE);
                myViewHolder.ll_expand_lay.setVisibility(View.VISIBLE);
            }

            String eFoodType = mapData.get("eFoodType");

            if (selected != i) {
                myViewHolder.iv_non_veg_type.setVisibility(eFoodType.equals("NonVeg") ? View.VISIBLE : View.GONE);
                myViewHolder.iv_veg_type.setVisibility(eFoodType.equals("NonVeg") ? View.GONE : View.VISIBLE);
            }

            if (selected == i) {
                myViewHolder.iv_non_veg_type.setVisibility(View.GONE);
                myViewHolder.iv_veg_type.setVisibility(View.GONE);
            }

            myViewHolder.addLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mItemClickListener.onItemClickList(myViewHolder.addLayout, i, list.get(i));
                }
            });

            myViewHolder.addLayout1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mItemClickListener.onItemClickList(myViewHolder.addLayout1, i, list.get(i));
                }
            });

        } else {
          /*  RestaurantmenuAdapter2.FooterViewHolder footerHolder =(FooterViewHolder) this.footerHolder;
            this.footerHolder = footerHolder;*/
        }
    }


    @Override
    public int getItemCount() {
        return list.size();

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

    private boolean isPositionFooter(int position) {
        return position == list.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionFooter(position) && isFooterEnabled == true) {
            return TYPE_FOOTER;
        }
        return TYPE_ITEM;
    }
    public void addItems() {


        notifyDataSetChanged();
    }


    public void update(ArrayList<HashMap<String, String>> filterist) {

        Log.d("valuechkkkkkkkkkkk", "adapter::" + filterist);

        list.clear();
        list = new ArrayList<>();
        list.addAll(filterist);

        notifyDataSetChanged();
    }




    public class FooterViewHolder extends RecyclerView.ViewHolder {
        LinearLayout progressArea;

        public FooterViewHolder(View itemView) {
            super(itemView);

            progressArea = (LinearLayout) itemView;

        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {


        CardView cv_parent_layout;
        TextView tv_item_name, tv_description, tv_old_price, tv_new_price;
        TextView tv_new_price1, tv_item_name1, ex_old_price, add_text;

        TextView tv_description1, vHighlightNameTxt;

        ImageView iv_veg_type, iv_non_veg_type, iv_img_icon;
        ImageView iv_img_icon1;

        RelativeLayout rv_collaps_lay, vHighlightNamelay;
        LinearLayout ll_expand_lay;

        LinearLayout addLayout, addLayout1;

        public MyViewHolder(@NonNull View view) {
            super(view);
            cv_parent_layout = view.findViewById(R.id.cv_parent_layout);
            vHighlightNamelay = view.findViewById(R.id.vHighlightNamelay);
            vHighlightNameTxt = view.findViewById(R.id.vHighlightNameTxt);
            add_text = view.findViewById(R.id.add_text);
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
}
