package com.servo.user.deliverAll;

import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.servo.user.R;
import com.general.files.GeneralFunctions;
import com.general.files.MyApp;
import com.general.files.StartActProcess;
import com.realmModel.Cart;
import com.utils.Utils;
import com.view.MButton;
import android.widget.TextView;
import com.view.MaterialRippleLayout;

import io.realm.Realm;

public class OrderPlaceConfirmActivity extends AppCompatActivity {


    ImageView backImgView;
    TextView titleTxt;
    GeneralFunctions generalFunc;
    MButton btn_type2;
    TextView placeOrderNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_place_confirm);
        generalFunc = MyApp.getInstance().getGeneralFun(getActContext());
        titleTxt = (TextView) findViewById(R.id.titleTxt);
        backImgView = (ImageView) findViewById(R.id.backImgView);
        placeOrderNote = (TextView) findViewById(R.id.placeOrderNote);
        backImgView.setOnClickListener(new setOnClickList());
        btn_type2 = ((MaterialRippleLayout) findViewById(R.id.btn_type2)).getChildView();
        btn_type2.setId(Utils.generateViewId());
        btn_type2.setOnClickListener(new setOnClickList());

        if (getIntent().getStringExtra("deliveryType").equalsIgnoreCase("Take away")){
            titleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_ORDER_PLACED"));
            btn_type2.setText(generalFunc.retrieveLangLBl("done", "LBL_DONE_ORDER"));
            placeOrderNote.setText(generalFunc.retrieveLangLBl("yur order has been placed", "LBL_YOUR_ORDER_IS_PLACED"));
        }
        else {
            titleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_ORDER_PLACED"));
            btn_type2.setText(generalFunc.retrieveLangLBl("", "LBL_TRACK_YOUR_ORDER"));
            placeOrderNote.setText(generalFunc.retrieveLangLBl("", "LBL_ORDER_PLACE_MSG"));
        }


      //  Toast.makeText(this, ""+ getIntent().getStringExtra("deliveryType"), Toast.LENGTH_SHORT).show();



        Realm realm = MyApp.getRealmInstance();
        realm.beginTransaction();
        realm.delete(Cart.class);
        realm.commitTransaction();


    }

    @Override
    protected void onResume() {
        super.onResume();

        if (generalFunc.prefHasKey(Utils.iServiceId_KEY) && generalFunc != null && !generalFunc.isDeliverOnlyEnabled()) {
            generalFunc.removeValue(Utils.iServiceId_KEY);
        }

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        MyApp.getInstance().restartWithGetDataApp();
    }

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Utils.hideKeyboard(getActContext());
            int i = view.getId();
            if (i == R.id.backImgView) {

                MyApp.getInstance().restartWithGetDataApp();
            } else if (i == btn_type2.getId()) {

                if (getIntent().getStringExtra("deliveryType").equalsIgnoreCase("Take away")){
                    Toast.makeText(OrderPlaceConfirmActivity.this, "Order PickedUp From store", Toast.LENGTH_SHORT).show();
                }
                else {
                Bundle bn = new Bundle();
                bn.putBoolean("isRestart", true);
                bn.putString("iOrderId", getIntent().getStringExtra("iOrderId"));

                // new StartActProcess(getActContext()).startActWithData(TrackOrderActivity.class, bn);
                new StartActProcess(getActContext()).startActWithData(ActiveOrderActivity.class, bn);}

            }
        }
    }


    public Context getActContext() {
        return OrderPlaceConfirmActivity.this;
    }
}
