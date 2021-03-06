package com.adapter.files;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.servo.user.R;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Admin on 26-09-2017.
 */

public class PackageAdapter extends RecyclerView.Adapter<PackageAdapter.ViewHolder> {

    Context mContext;
    int pos = 0;

    ArrayList<HashMap<String, String>> recentList;
    PackageAdapter.setPackageClickList packageClickList;
    View view;

    public PackageAdapter(Context context, ArrayList<HashMap<String, String>> list) {
        this.mContext = context;
        this.recentList = list;

    }

    public void selPos(int pos) {
        this.pos = pos;

    }

    @Override
    public PackageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(mContext).inflate(R.layout.item_package_row, parent, false);


        return new PackageAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PackageAdapter.ViewHolder holder, final int position) {
        HashMap<String, String> map=recentList.get(position);
        holder.pkgNameTxt.setText(map.get("vPackageName"));
        holder.pkgPriceTxt.setText(map.get("fPrice"));

        if (pos == position) {
            holder.radioBtn.setChecked(true);
        } else {
            holder.radioBtn.setChecked(false);
        }
        holder.radioBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                packageClickList.itemPackageClick(position);
            }
        });
        holder.mainArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.radioBtn.performClick();
            }
        });


    }

    @Override
    public int getItemCount() {
        return recentList.size();
    }

    public void itemPackageClick(PackageAdapter.setPackageClickList packageClickList) {
        this.packageClickList = packageClickList;
    }

    public interface setPackageClickList {
        void itemPackageClick(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView pkgNameTxt;
        TextView pkgPriceTxt;
        RadioButton radioBtn;
        LinearLayout mainArea;

        public ViewHolder(View itemView) {
            super(itemView);

            pkgNameTxt = (TextView) itemView.findViewById(R.id.pkgNameTxt);
            pkgPriceTxt = (TextView) itemView.findViewById(R.id.pkgPriceTxt);
            radioBtn = (RadioButton) itemView.findViewById(R.id.radioBtn);
            mainArea = (LinearLayout) itemView.findViewById(R.id.mainArea);

        }
    }

}
