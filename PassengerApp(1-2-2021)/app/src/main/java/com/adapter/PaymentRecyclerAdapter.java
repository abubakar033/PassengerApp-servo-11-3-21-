package com.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.JResponseClasses.PaymentMethodInterface;
import com.PaymentDataClass;
import com.servo.user.R;

import java.util.ArrayList;
import java.util.HashMap;

public class PaymentRecyclerAdapter extends RecyclerView.Adapter<PaymentRecyclerAdapter.ViewHolder> {
    HashMap<String, String> paymentMethod;
    Context context;
    ArrayList<PaymentDataClass> list;
    PaymentMethodInterface paymentMethodInterface;
    public int mSelectedItem = -1;
    private RadioButton lastCheckedRB = null;


    public PaymentRecyclerAdapter(Context context, HashMap<String, String> paymentMethod, PaymentMethodInterface paymentMethodInterface) {
        this.paymentMethod = paymentMethod;
        this.context = context;
        this.paymentMethodInterface = paymentMethodInterface;
        hasgToArrey();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.payment_recycler_adapter_item, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    public void hasgToArrey() {
        list = new ArrayList<>();
        PaymentDataClass PaymentDataClass = new PaymentDataClass();

        int count = paymentMethod.size();

        int i = 0;
        for (HashMap.Entry<String, String> entry : paymentMethod.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            PaymentDataClass obj = new PaymentDataClass();
            obj.setId(entry.getKey());
            obj.setPaymentName(entry.getValue());
            list.add(obj);
            i++;
            if (count == i) {
                break;
            }
        }


    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, @SuppressLint("RecyclerView") int position) {
        viewHolder.radioButton.setChecked(position == mSelectedItem);


        viewHolder.radioButton.setText(list.get(position).getPaymentName());
        viewHolder.radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




                    if (list.size()>1){
                        if (lastCheckedRB != null) {

                    lastCheckedRB.setChecked(false);
                }
                        lastCheckedRB = viewHolder.radioButton;
                    }
                //store the clicked radiobutton

               // if (list.size()>0){

              //  }

                paymentMethodInterface.onpaymentselect(list.get(position).getId(), list.get(position).getPaymentName());

            }
        });

    }


    @Override
    public int getItemCount() {
        return paymentMethod.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RadioButton radioButton;
        ConstraintLayout radio_main;

        // RadioGroup radioGroup;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            radioButton = itemView.findViewById(R.id.payment_item);
            radio_main = itemView.findViewById(R.id.radio_main);
            // radioGroup= itemView.findViewById(R.id.paymentRadioGroud);
        }
    }
}
