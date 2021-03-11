package com.servo.user;

import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.general.files.MyApp;
import com.utils.Utils;

import android.widget.TextView;

import java.util.HashMap;

public class ProviderInfoActivity extends AppCompatActivity {

    public GeneralFunctions generalFunc;
    TextView titleTxt;
    ImageView backImgView;
    TextView descTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_info);
        generalFunc = MyApp.getInstance().getGeneralFun(getActContext());
        titleTxt = (TextView) findViewById(R.id.titleTxt);
        backImgView = (ImageView) findViewById(R.id.backImgView);
        backImgView.setOnClickListener(new setOnClickList());
        descTxt = (TextView) findViewById(R.id.descTxt);
        titleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_SERVICE_DESCRIPTION"));
        getProviderInfo();
    }

    public Context getActContext() {
        return ProviderInfoActivity.this;
    }

    public void getProviderInfo() {
        final HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "getProviderServiceDescription");
        parameters.put("iDriverId", getIntent().getStringExtra("iDriverId"));


        final ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(responseString -> {


            if (responseString != null && !responseString.equals("")) {

                if (generalFunc.checkDataAvail(Utils.action_str, responseString) == true) {
                    descTxt.setText(generalFunc.getJsonValue(Utils.message_str, responseString));

                } else {

                    generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)), "", generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"), buttonId -> onBackPressed());

                }


            }

        });
        exeWebServer.execute();
    }


    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Utils.hideKeyboard(getActContext());
            switch (view.getId()) {
                case R.id.backImgView:

                    onBackPressed();

                    break;


            }
        }
    }
}
