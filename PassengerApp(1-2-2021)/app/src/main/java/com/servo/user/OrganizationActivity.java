package com.servo.user;

import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.general.files.GeneralFunctions;
import com.general.files.MyApp;
import com.general.files.StartActProcess;
import com.utils.Utils;
import com.view.MButton;
import android.widget.TextView;
import com.view.MaterialRippleLayout;

import java.util.HashMap;

public class OrganizationActivity extends AppCompatActivity {

    public GeneralFunctions generalFunc;
    TextView titleTxt;
    ImageView backImgView;
    LinearLayout orgnizationArea;
    TextView orgnizationNameTxt, headingTxt;
    String selectOrgnizationId = "";
    MButton btn_type2;
    ImageView imagearrow;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organization);
        generalFunc = MyApp.getInstance().getGeneralFun(getActContext());
        titleTxt = (TextView) findViewById(R.id.titleTxt);
        backImgView = (ImageView) findViewById(R.id.backImgView);
        orgnizationArea = (LinearLayout) findViewById(R.id.orgnizationArea);
        orgnizationNameTxt = (TextView) findViewById(R.id.orgnizationNameTxt);
        btn_type2 = ((MaterialRippleLayout) findViewById(R.id.btn_type2)).getChildView();
        headingTxt = (TextView) findViewById(R.id.headingTxt);
        imagearrow = (ImageView) findViewById(R.id.imagearrow);

        if (generalFunc.isRTLmode()) {
            imagearrow.setRotation(180);
        }

        btn_type2.setId(Utils.generateViewId());
        btn_type2.setOnClickListener(new setOnClickList());
        orgnizationArea.setOnClickListener(new setOnClickList());
        backImgView.setOnClickListener(new setOnClickList());
        headingTxt.setText(generalFunc.retrieveLangLBl("", "LBL_SELECT_ORGANIZATION_LINK_TO"));
        titleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_PROFILE_SETUP"));
        btn_type2.setText(generalFunc.retrieveLangLBl("", "LBL_BTN_NEXT_TXT"));
        orgnizationNameTxt.setText("- " + generalFunc.retrieveLangLBl("", "LBL_SELECT_TXT") + " -");


    }

    public Context getActContext() {
        return OrganizationActivity.this;
    }

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Utils.hideKeyboard(getActContext());
            if (view.getId() == R.id.backImgView) {

                onBackPressed();
            } else if (view.getId() == R.id.orgnizationArea) {
                Bundle bn = new Bundle();
                bn.putString("iUserProfileMasterId", getIntent().getStringExtra("iUserProfileMasterId"));
                new StartActProcess(getActContext()).startActForResult(SelectOrganizationActivity.class, bn, Utils.SELECT_ORGANIZATION_CODE);
            }
            if (view.getId() == btn_type2.getId()) {

                if (selectOrgnizationId.equalsIgnoreCase("")|| selectOrgnizationId == null) {
                    generalFunc.showMessage(backImgView, generalFunc.retrieveLangLBl("", "LBL_SELECT_ORGANIZATION"));
                    return;
                }

                Bundle bn = new Bundle();


                HashMap<String, String> data = new HashMap<>();
                data.put("email", getIntent().getStringExtra("email"));
                data.put("iUserProfileMasterId", getIntent().getStringExtra("iUserProfileMasterId"));
                data.put("iOrganizationId", selectOrgnizationId);
                data.put("vCompany", orgnizationNameTxt.getText().toString().trim());
                bn.putSerializable("data", data);
                new StartActProcess(getActContext()).startActWithData(MyBusinessProfileActivity.class, bn);

            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            // selectOrgnization
            orgnizationNameTxt.setText(data.getStringExtra("vCompany"));
            selectOrgnizationId = data.getStringExtra("iOrganizationId");

        }
    }
}
