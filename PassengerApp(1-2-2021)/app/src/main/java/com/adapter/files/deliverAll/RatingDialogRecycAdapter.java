package com.adapter.files.deliverAll;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.servo.user.R;
import com.utils.Logger;

import android.widget.TextView;
import com.view.simpleratingbar.SimpleRatingBar;

import java.util.ArrayList;
import java.util.HashMap;

public class RatingDialogRecycAdapter extends RecyclerView.Adapter<RatingDialogRecycAdapter.ViewHolder> {

    Context mContext;
    ArrayList<HashMap<String, String>> ratingInfo_arr;
    View view;

    public RatingDialogRecycAdapter(Context context, ArrayList<HashMap<String, String>> mapArrayList) {
        this.mContext = context;
        this.ratingInfo_arr = mapArrayList;
    }

    @Override
    public RatingDialogRecycAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        Logger.d("ratingadapt::::", "call");
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_rating_dialog_list_design, parent, false);


        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RatingDialogRecycAdapter.ViewHolder holder, int position) {

        holder.datetxt.setText("15/03/2018");
        holder.nametxt.setText("vidhan S.");
        holder.typetxt.setText("Delivery");


        Logger.d("ratingadapt::::", "" + position);
    }

    @Override
    public int getItemCount() {
        return ratingInfo_arr.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView datetxt, typetxt, nametxt;
        SimpleRatingBar dialogRatingBar;

        public ViewHolder(View itemView) {
            super(itemView);

            dialogRatingBar = itemView.findViewById(R.id.dialogRatingBar);
            datetxt = (TextView) itemView.findViewById(R.id.dateRateTxt);
            typetxt = (TextView) itemView.findViewById(R.id.typeeTxt);
            nametxt = (TextView) itemView.findViewById(R.id.nameeTxt);
        }
    }
}
