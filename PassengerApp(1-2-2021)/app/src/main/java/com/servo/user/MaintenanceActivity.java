package com.servo.user;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.general.files.GeneralFunctions;
import com.general.files.MyApp;
import com.general.files.StartActProcess;
import com.utils.Utils;
import com.view.MButton;
import android.widget.TextView;
import com.view.MaterialRippleLayout;

public class MaintenanceActivity extends AppCompatActivity {

    TextView maitenanceHTxt, maitenanceMsgTxt;

    GeneralFunctions generalFunctions;

    MButton btn_type2;
    int submitBtnId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintenance);

        Toast.makeText(this,"Man Act",Toast.LENGTH_SHORT).show();

        btn_type2 = ((MaterialRippleLayout) findViewById(R.id.btn_type2)).getChildView();
        btn_type2.setId(submitBtnId);

        submitBtnId = Utils.generateViewId();
        btn_type2.setId(submitBtnId);

        btn_type2.setOnClickListener(new setOnClickList());

        generalFunctions = MyApp.getInstance().getGeneralFun(MaintenanceActivity.this);
        maitenanceMsgTxt = (TextView) findViewById(R.id.maitenanceMsgTxt);
        maitenanceHTxt = (TextView) findViewById(R.id.maitenanceHTxt);
        maitenanceHTxt.setText(generalFunctions.retrieveLangLBl("", "LBL_MAINTENANCE_HEADER_MSG"));
        maitenanceMsgTxt.setText(generalFunctions.retrieveLangLBl("", "LBL_MAINTENANCE_CONTENT_MSG"));
        btn_type2.setText(generalFunctions.retrieveLangLBl("", "LBL_CONTACT_US_HEADER_TXT"));


    }


    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            int i = view.getId();

            if (i == submitBtnId) {
                new StartActProcess(MaintenanceActivity.this).startAct(ContactUsActivity.class);
            }


        }
    }
}
