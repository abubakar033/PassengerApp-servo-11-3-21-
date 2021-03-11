package com.servo.user;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.general.files.MyApp;
import com.general.files.StartActProcess;
import com.utils.Utils;
import com.GenerateAlertBox;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.HashMap;

public class ConfirmEmergencyTapActivity extends AppCompatActivity {

    TextView titleTxt;
    ImageView backImgView;

    GeneralFunctions generalFunc;

    String userProfileJson;
    public String iTripId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_emergency_tap);

        generalFunc = MyApp.getInstance().getGeneralFun(getActContext());


        titleTxt = (TextView) findViewById(R.id.titleTxt);
        backImgView = (ImageView) findViewById(R.id.backImgView);

        userProfileJson = generalFunc.retrieveValue(Utils.USER_PROFILE_JSON);
        iTripId = getIntent().getStringExtra("TripId");

        setLabels();

        backImgView.setOnClickListener(new setOnClickList());
        (findViewById(R.id.policeContactArea)).setOnClickListener(new setOnClickList());
        (findViewById(R.id.emeContactArea)).setOnClickListener(new setOnClickList());

    }

    public void setLabels() {
        titleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_EMERGENCY_CONTACT"));
        ((TextView) findViewById(R.id.pageTitle)).setText(generalFunc.retrieveLangLBl("USE IN CASE OF EMERGENCY", "LBL_CONFIRM_EME_PAGE_TITLE"));
        ((TextView) findViewById(R.id.callPoliceTxt)).setText(generalFunc.retrieveLangLBl("Call Police Control Room", "LBL_CALL_POLICE"));
        ((TextView) findViewById(R.id.sendAlertTxt)).setText(generalFunc.retrieveLangLBl("Send message to your emergency contacts.",
                "LBL_SEND_ALERT_EME_CONTACT"));
    }

    public void sendAlertToEmeContacts() {
        final HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "sendAlertToEmergencyContacts");
        parameters.put("iUserId", generalFunc.getMemberId());
        parameters.put("iTripId", iTripId);
        parameters.put("UserType", Utils.userType);

        final ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(String responseString) {
                JSONObject responseObj=generalFunc.getJsonObject(responseString);

                if (responseObj != null && !responseObj.equals("")) {

                    if (generalFunc.checkDataAvail(Utils.action_str, responseObj) == true) {
                        generalFunc.showGeneralMessage("",
                                generalFunc.retrieveLangLBl("", generalFunc.getJsonValueStr(Utils.message_str, responseObj)));

                    } else {

                        if (generalFunc.getJsonValueStr(Utils.message_str_one, responseObj).equalsIgnoreCase("SmsError")) {

                            generalFunc.showGeneralMessage("", generalFunc.getJsonValueStr(Utils.message_str, responseObj));

                        } else {

                            final GenerateAlertBox generateAlertBox = new GenerateAlertBox(getActContext());
                            generateAlertBox.setContentMessage("", generalFunc.retrieveLangLBl("", generalFunc.getJsonValueStr(Utils.message_str, responseObj)));

                            generateAlertBox.setBtnClickList(new GenerateAlertBox.HandleAlertBtnClick() {
                                @Override
                                public void handleBtnClick(int btn_id) {
                                    new StartActProcess(getActContext()).startAct(EmergencyContactActivity.class);
                                    generateAlertBox.closeAlertBox();

                                }
                            });
                            generateAlertBox.setPositiveBtn(generalFunc.retrieveLangLBl("Ok", "LBL_BTN_OK_TXT"));

                            generateAlertBox.showAlertBox();

                        }
                    }
                } else {
                    generalFunc.showError();
                }
            }
        });
        exeWebServer.execute();
    }

    public Context getActContext() {
        return ConfirmEmergencyTapActivity.this;
    }

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            int i = view.getId();
            Utils.hideKeyboard(getActContext());
            if (i == R.id.backImgView) {
                ConfirmEmergencyTapActivity.super.onBackPressed();

            } else if (i == R.id.policeContactArea) {

                try {
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:" + generalFunc.getJsonValue("SITE_POLICE_CONTROL_NUMBER", userProfileJson)));
                    startActivity(callIntent);
                } catch (Exception e) {
                    // TODO: handle exception
                }
            } else if (i == R.id.emeContactArea) {
                sendAlertToEmeContacts();
            }
        }
    }

}
