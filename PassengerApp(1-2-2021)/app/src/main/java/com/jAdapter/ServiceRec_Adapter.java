package com.jAdapter;

import android.content.Context;
import android.content.res.TypedArray;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.general.files.OpenCatType;
import com.servo.user.R;
import com.squareup.picasso.Picasso;
import com.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;

public class ServiceRec_Adapter  extends RecyclerView.Adapter<ServiceRec_Adapter.ViewHolder> {

    ArrayList<HashMap<String, String>> service_list_item_new;
    Context mContext;
    String lat,lang,addd;
    int bannerHeight;
    int bannerWidth;
    int width;
    int margin;
    int dimension;


    public ServiceRec_Adapter(ArrayList<HashMap<String, String>> service_list_item_new,String lat,String lang,String add, Context context) {
        this.service_list_item_new = service_list_item_new;
        this.mContext = context;
        this.lat= lat;
        this.lang= lang;
        this.addd= add;
        width=margin * 2;
        dimension=mContext.getResources().getDimensionPixelSize(R.dimen.category_grid_size);
        margin=mContext.getResources().getDimensionPixelSize(R.dimen.category_banner_left_right_margin);
        //  this.Service_list_item1= Service_list_item;
        bannerWidth=Utils.getWidthOfBanner(mContext, width);
        bannerWidth=Utils.getWidthOfBanner(mContext, width);
        bannerHeight=Utils.getHeightOfBanner(mContext, width, "16:9");



    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.j_delivery_services_vertical_layout, viewGroup, false);
        ViewHolder bannerViewHolder = new ViewHolder(view);
        return bannerViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        HashMap<String, String> newhashMap = service_list_item_new.get(i);

       // String vBannerImage = newhashMap.get("vImage");
        String vBannerImage = "s";



        if (newhashMap.get("iServiceId").equalsIgnoreCase("9")){
            if (vBannerImage != null && !vBannerImage.isEmpty()) {
                Picasso.with(mContext).load(vBannerImage)
                        .placeholder(R.drawable.shop_icon)
                        // .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                        .fit().into(viewHolder.catImgView);
            } else {
                Picasso.with(mContext).load("ss")
                        //.memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                        .placeholder(R.drawable.shop_icon).fit().into(viewHolder.catImgView);}
          //  viewHolder.catImgView.setBackgroundResource(R.drawable.shop_icon);
        }
        if (newhashMap.get("iServiceId").equalsIgnoreCase("2")){

            if (vBannerImage != null && !vBannerImage.isEmpty()) {
                Picasso.with(mContext).load(vBannerImage)
                        .placeholder(R.drawable.food_icon)
                        // .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                        .fit().into(viewHolder.catImgView);
            } else {
                Picasso.with(mContext).load("ss")
                        //.memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                        .placeholder(R.drawable.food_icon).fit().into(viewHolder.catImgView);}
/*
            viewHolder.catImgView.setBackgroundResource(R.drawable.food_icon);
*/
        }
        if (newhashMap.get("iServiceId").equalsIgnoreCase("5")){

            if (vBannerImage != null && !vBannerImage.isEmpty()) {
                Picasso.with(mContext).load(vBannerImage)
                        .placeholder(R.drawable.pharmacy_icon)
                        // .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                        .fit().into(viewHolder.catImgView);
            } else {
                Picasso.with(mContext).load("ss")
                        //.memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                        .placeholder(R.drawable.pharmacy_icon).fit().into(viewHolder.catImgView);}
/*
            viewHolder.catImgView.setBackgroundResource(R.drawable.pharmacy_icon);
*/
        }
        if (newhashMap.get("iServiceId").equalsIgnoreCase("7")){

            if (vBannerImage != null && !vBannerImage.isEmpty()) {
                Picasso.with(mContext).load(vBannerImage)
                        .placeholder(R.drawable.cloth_icon)
                        // .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                        .fit().into(viewHolder.catImgView);
            } else {
                Picasso.with(mContext).load("ss")
                        //.memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                        .placeholder(R.drawable.cloth_icon).fit().into(viewHolder.catImgView);}
        /*    viewHolder.catImgView.setBackgroundResource(R.drawable.cloth_icon);*/
        }
        if (newhashMap.get("iServiceId").equalsIgnoreCase("1")){
            if (vBannerImage != null && !vBannerImage.isEmpty()) {
                Picasso.with(mContext).load(vBannerImage)
                        .placeholder(R.drawable.food_icon)
                        // .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                        .fit().into(viewHolder.catImgView);
            } else {
                Picasso.with(mContext).load("ss")
                        //.memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                        .placeholder(R.drawable.food_icon).fit().into(viewHolder.catImgView);}

     /*       viewHolder.catImgView.setBackgroundResource(R.drawable.food_icon);*/
        }
      /*  String vBannerImage = newhashMap.get("vImage");

        if (vBannerImage != null && !vBannerImage.isEmpty()) {
            Picasso.with(mContext).load(vBannerImage)
                    .placeholder(R.mipmap.ic_no_icon)
                    // .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .fit().into(viewHolder.catImgView);
        } else {
            Picasso.with(mContext).load("ss")
                    //.memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .placeholder(R.mipmap.ic_no_icon).fit().into(viewHolder.catImgView);

        }*/

       // String vBannerImage = "https://www.servo.com.ly/admin/images/" + newhashMap.get("vImage");

//
//        String vCategoryBanner = item.get("vCategoryBanner");
//
      //  String imageURL = Utils.getResizeImgURL(mContext, vBannerImage, bannerWidth, bannerHeight);
//
       // Picasso.with(mContext).load(vBannerImage).fit().placeholder(R.mipmap.ic_no_icon).into(viewHolder.catImgView);



        viewHolder.uberXCatNameTxtView.setText(newhashMap.get("vCategory"));
        viewHolder.contentArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                int[] attrs = new int[]{R.attr.selectableItemBackground};
                TypedArray typedArray = mContext.obtainStyledAttributes(attrs);
                int backgroundResource = typedArray.getResourceId(0, 0);
                viewHolder.contentArea.setBackgroundResource(backgroundResource);
                HashMap<String,String> hashMap=new HashMap<>();

                hashMap.put("latitude",lat);
                hashMap.put("longitude",lang);
                hashMap.put("address",addd);
                hashMap.put("eCatType",""+newhashMap.get("eCatType"));
                hashMap.put("iServiceId",""+newhashMap.get("iServiceId"));



                new OpenCatType(mContext,hashMap).execute();

            }
        });






    }

    @Override
    public int getItemCount() {
        return service_list_item_new.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView uberXCatNameTxtView;

        //public View contentArea;
        public ImageView catImgView;
        public ImageView arrowImageView;
        public AppCompatCheckBox serviceCheckbox;
        RelativeLayout contentArea;

        TextView uberXCatDescription;
        public ViewHolder(@NonNull View view) {
            super(view);
            uberXCatNameTxtView = (TextView) view.findViewById(R.id.uberXCatNameTxtView);
           // contentArea = view.findViewById(R.id.contentArea);
            catImgView = (ImageView) view.findViewById(R.id.catImgView);
            arrowImageView = (ImageView) view.findViewById(R.id.arrowImageView);
            serviceCheckbox = (AppCompatCheckBox) view.findViewById(R.id.serviceCheckbox);
            uberXCatDescription = view.findViewById(R.id.uberXCatDescription);
            contentArea = view.findViewById(R.id.contentArea);
        }
    }
}
