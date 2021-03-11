package com.adapter.files;

import android.content.Context;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.servo.user.R;
import com.squareup.picasso.Picasso;
import com.utils.Utils;
import com.view.CreateRoundedView;
import android.widget.TextView;
import com.view.SelectableRoundedImageView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Admin on 02-03-2017.
 */
public class UberXBannerAdapter extends RecyclerView.Adapter<UberXBannerAdapter.ViewHolder> {

    private ArrayList<HashMap<String, String>> list_item;
    private LayoutInflater inflater;
    private Context context;
    OnBannerItemClickList onItemClickList;
    int height;

    public UberXBannerAdapter(Context context, ArrayList<HashMap<String, String>> list_item) {
        this.context = context;
        this.list_item = list_item;
        inflater = LayoutInflater.from(context);
        height= (int) (((Utils.getScreenPixelWidth(context) - Utils.dipToPixels(context, 20)) / 1.77777778));

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rdu_banner_design, parent, false);

        UberXBannerAdapter.ViewHolder viewHolder = new UberXBannerAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        HashMap<String, String> mapData = list_item.get(position);

        String iVehicleCategoryId = mapData.get("iVehicleCategoryId");

        String vBannerImage = mapData.get("vBannerImage");
        //bug_003 start
        if (!vBannerImage.equalsIgnoreCase("")) {

            Picasso.with(context).load(vBannerImage)
                    .placeholder(R.mipmap.ic_no_icon)
                    //.memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .fit().into(holder.bannerImgView);
        } else {
            Picasso.with(context).load("ss")
                    //.memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
            .placeholder(R.mipmap.ic_no_icon).fit().into(holder.bannerImgView);

        }
        //bug_003 stop

        if (iVehicleCategoryId.equalsIgnoreCase("ride") ||
                iVehicleCategoryId.equalsIgnoreCase("delivery") ||
                iVehicleCategoryId.equalsIgnoreCase("motodelivery")
                || iVehicleCategoryId.equalsIgnoreCase("motoride") ||
                iVehicleCategoryId.equalsIgnoreCase("rental") ||
                iVehicleCategoryId.equalsIgnoreCase("motorental")) {
            holder.serviceNameTxt.setText(mapData.get("vCategorybanner"));

        } else {
            holder.serviceNameTxt.setText(mapData.get("vCategory"));
        }

        holder.bookNowTxt.setText(mapData.get("LBL_BOOK_NOW"));

        CardView.LayoutParams layoutParams = (CardView.LayoutParams) holder.bannerAreaContainerView.getLayoutParams();

        layoutParams.height = height;
        holder.bannerAreaContainerView.setLayoutParams(layoutParams);

        int color1=context.getResources().getColor(R.color.appThemeColor_1);
        int color2=context.getResources().getColor(R.color.appThemeColor_2);
        int btnRadius=Utils.dipToPixels(context, 8);
        int strokeWidth=Utils.dipToPixels(context, 0);

        new CreateRoundedView(color1, btnRadius, strokeWidth, color1, holder.serviceNameTxt);
        new CreateRoundedView(color2, btnRadius, strokeWidth, color1, holder.bookNowTxt);

        holder.bannerImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickList != null) {
                    onItemClickList.onBannerItemClick(position);
                }
            }
        });

    }


    public void setOnItemClickList(OnBannerItemClickList onItemClickList) {
        this.onItemClickList = onItemClickList;
    }

    public interface OnBannerItemClickList {
        void onBannerItemClick(int position);
    }

    @Override
    public int getItemCount() {
        return list_item.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {


        public View contentArea;
        public View bannerAreaContainerView;
        public SelectableRoundedImageView bannerImgView;
        public TextView bookNowTxt, serviceNameTxt;

        public ViewHolder(View view) {
            super(view);

            bannerImgView = (SelectableRoundedImageView) view.findViewById(R.id.bannerImgView);
            bookNowTxt = (TextView) view.findViewById(R.id.bookNowTxt);
            serviceNameTxt = (TextView) view.findViewById(R.id.serviceNameTxt);
            bannerAreaContainerView = view.findViewById(R.id.bannerAreaContainerView);
        }
    }


}