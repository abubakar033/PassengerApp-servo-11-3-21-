package com.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.servo.user.R;

import java.util.ArrayList;


public class StorePayAdapter extends RecyclerView.Adapter<StorePayAdapter.Viewholder> {
    Context mContext;
    ArrayList<String> list;

    public StorePayAdapter(Context mContext, ArrayList<String> list) {
        this.mContext = mContext;
        this.list = list;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View rowItem = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.store_pay_adapter_item, viewGroup, false);
        return new Viewholder(rowItem);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder viewholder, int i) {
        viewholder.text_of_payment.setText(list.get(i));

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        TextView text_of_payment;
        public Viewholder(@NonNull View itemView) {
            super(itemView);
            text_of_payment= itemView.findViewById(R.id.text_of_payment);
        }
    }
}
