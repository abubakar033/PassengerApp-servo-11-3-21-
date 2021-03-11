package com.servo.user;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;

import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.general.files.MyApp;
import com.general.files.StartActProcess;
import com.model.DeliveryDetails;
import com.utils.Utils;
import com.GenerateAlertBox;
import com.view.MButton;
import android.widget.TextView;
import com.view.MaterialRippleLayout;
import com.view.editBox.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class EnterDeliveryDetailsActivity extends AppCompatActivity implements TextWatcher {

    public static String RECEIVER_NAME_KEY = "RECEIVER_NAME";
    public static String RECEIVER_MOBILE_KEY = "RECEIVER_MOBILE";
    public static String PICKUP_INS_KEY = "PICKUP_INS";
    public static String DELIVERY_INS_KEY = "DELIVERY_INS";
    public static String PACKAGE_DETAILS_KEY = "PACKAGE_DETAILS";
    public static String PACKAGE_TYPE_NAME_KEY = "PACKAGE_TYPE_NAME";
    public static String PACKAGE_TYPE_ID_KEY = "PACKAGE_TYPE_ID";

    TextView titleTxt;
    ImageView backImgView;

    GeneralFunctions generalFunc;

    MaterialEditText receiverNameEditBox;
    MaterialEditText receiverMobileEditBox;
    MaterialEditText pickUpInstructionEditBox;
    MaterialEditText deliveryInstructionEditBox;
    MaterialEditText packageTypeBox;
    MaterialEditText packageDetailsEditBox;

    MButton btn_type2;
    MButton btn_reset;

    String required_str = "";

    androidx.appcompat.app.AlertDialog alert_packageTypes;

    ArrayList<String[]> list_packageType_items = new ArrayList<>();

    String currentPackageTypeId = "";
    private String userProfileJson;

    // Store Delivery Details

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_delivery_details);

        generalFunc = MyApp.getInstance().getGeneralFun(getActContext());

        userProfileJson = generalFunc.retrieveValue(Utils.USER_PROFILE_JSON);

        titleTxt = (TextView) findViewById(R.id.titleTxt);
        backImgView = (ImageView) findViewById(R.id.backImgView);

        receiverNameEditBox = (MaterialEditText) findViewById(R.id.receiverNameEditBox);
        receiverMobileEditBox = (MaterialEditText) findViewById(R.id.receiverMobileEditBox);
        pickUpInstructionEditBox = (MaterialEditText) findViewById(R.id.pickUpInstructionEditBox);
        deliveryInstructionEditBox = (MaterialEditText) findViewById(R.id.deliveryInstructionEditBox);
        packageTypeBox = (MaterialEditText) findViewById(R.id.packageTypeBox);
        packageDetailsEditBox = (MaterialEditText) findViewById(R.id.packageDetailsEditBox);

        receiverNameEditBox.addTextChangedListener(this);
        receiverMobileEditBox.addTextChangedListener(this);
        pickUpInstructionEditBox.addTextChangedListener(this);
        deliveryInstructionEditBox.addTextChangedListener(this);
        packageTypeBox.addTextChangedListener(this);
        packageDetailsEditBox.addTextChangedListener(this);

        btn_type2 = ((MaterialRippleLayout) findViewById(R.id.btn_type2)).getChildView();
        btn_reset = ((MaterialRippleLayout) findViewById(R.id.btn_reset)).getChildView();

        btn_reset.setOnClickListener(new setOnClickList());

        btn_type2.setId(Utils.generateViewId());

        receiverMobileEditBox.setInputType(InputType.TYPE_CLASS_NUMBER);

        receiverNameEditBox.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        receiverMobileEditBox.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        pickUpInstructionEditBox.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        deliveryInstructionEditBox.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        packageDetailsEditBox.setImeOptions(EditorInfo.IME_ACTION_DONE);

        Utils.removeInput(packageTypeBox);

        backImgView.setOnClickListener(new setOnClickList());
        btn_type2.setOnClickListener(new setOnClickList());
        packageTypeBox.setOnTouchListener(new setOnTouchList());
        packageTypeBox.setOnClickListener(new setOnClickList());

        setLabels();

        setStoredDeliveryDetails();

        handleResetButton();
    }

    private void setStoredDeliveryDetails() {

        if (Utils.checkText(generalFunc.retrieveValue(Utils.DELIVERY_DETAILS_KEY))) {
            String data = generalFunc.retrieveValue(Utils.DELIVERY_DETAILS_KEY);

            JSONArray deliveriesArr = generalFunc.getJsonArray("deliveries", data);
            if (deliveriesArr != null) {
                for (int j = 0; j < deliveriesArr.length(); j++) {
                    JSONObject ja = generalFunc.getJsonObject(deliveriesArr, j);
                    DeliveryDetails deliveryDetails = new DeliveryDetails();
                    deliveryDetails.setRecipientName("" + generalFunc.getJsonValueStr(RECEIVER_NAME_KEY, ja));
                    deliveryDetails.setRecipientPhoneNumber("" + generalFunc.getJsonValueStr(RECEIVER_MOBILE_KEY, ja));
                    deliveryDetails.setPickupInstruction("" + generalFunc.getJsonValueStr(PICKUP_INS_KEY, ja));
                    deliveryDetails.setDeliveryInstruction("" + generalFunc.getJsonValueStr(DELIVERY_INS_KEY, ja));
                    deliveryDetails.setPackageDetails("" + generalFunc.getJsonValueStr(PACKAGE_DETAILS_KEY, ja));
                    deliveryDetails.setvPackageTypeName("" + generalFunc.getJsonValueStr(PACKAGE_TYPE_NAME_KEY, ja));
                    deliveryDetails.setPackageTypeId("" + generalFunc.getJsonValueStr(PACKAGE_TYPE_ID_KEY, ja));
                    setData(deliveryDetails);
                }
            }

        }

    }

    private void setData(DeliveryDetails deliveryDetails) {
        receiverNameEditBox.setText(deliveryDetails.getRecipientName());
        receiverMobileEditBox.setText(deliveryDetails.getRecipientPhoneNumber());
        currentPackageTypeId = deliveryDetails.getPackageTypeId();
        packageTypeBox.setText("" + deliveryDetails.getvPackageTypeName());
        deliveryInstructionEditBox.setText("" + deliveryDetails.getDeliveryInstruction());
        pickUpInstructionEditBox.setText("" + deliveryDetails.getPickupInstruction());
        packageDetailsEditBox.setText("" + deliveryDetails.getPackageDetails());
    }

    public void setLabels() {
        titleTxt.setText(generalFunc.retrieveLangLBl("Delivery Details", "LBL_DELIVERY_DETAILS"));

        receiverNameEditBox.setBothText(generalFunc.retrieveLangLBl("Receiver Name", "LBL_RECEIVER_NAME"));
        receiverMobileEditBox.setBothText(generalFunc.retrieveLangLBl("Receiver Mobile", "LBL_RECEIVER_MOBILE"));
        pickUpInstructionEditBox.setBothText(generalFunc.retrieveLangLBl("Pickup instruction", "LBL_PICK_UP_INS"));
        deliveryInstructionEditBox.setBothText(generalFunc.retrieveLangLBl("Delivery instruction", "LBL_DELIVERY_INS"));
        packageTypeBox.setBothText(generalFunc.retrieveLangLBl("Package Type", "LBL_PACKAGE_TYPE"),
                generalFunc.retrieveLangLBl("Select package type", "LBL_SELECT_PACKAGE_TYPE"));
        packageDetailsEditBox.setBothText(generalFunc.retrieveLangLBl("Package Details", "LBL_PACKAGE_DETAILS"));

        required_str = generalFunc.retrieveLangLBl("", "LBL_FEILD_REQUIRD_ERROR_TXT");


        String isDeliverNow=getIntent().getStringExtra("isDeliverNow");

        if (isDeliverNow != null && isDeliverNow.equals("true")) {
            btn_type2.setText(generalFunc.retrieveLangLBl("Deliver Now", "LBL_DELIVER_NOW"));
        } else {
            btn_type2.setText(generalFunc.retrieveLangLBl("Send Request", "LBL_CONFIRM_BOOKING"));
        }

        btn_reset.setText(generalFunc.retrieveLangLBl("", "LBL_RESET"));

    }

    public Context getActContext() {
        return EnterDeliveryDetailsActivity.this;
    }

    public void loadPackageTypes() {

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "loadPackageTypes");

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);

        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(String responseString) {

                if (responseString != null && !responseString.equals("")) {

                    boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

                    if (isDataAvail == true) {

                        buildPackageTypes(generalFunc.getJsonValue(Utils.message_str, responseString));

                    } else {
                        generalFunc.showGeneralMessage("",
                                generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)));
                    }
                } else {
                    generalFunc.showError();
                }
            }
        });
        exeWebServer.execute();
    }

    public void buildPackageTypes(String message) {
        list_packageType_items.clear();

        ArrayList<String> items = new ArrayList<>();

        JSONArray arr_data = generalFunc.getJsonArray(message);
        if (arr_data != null) {
            for (int i = 0; i < arr_data.length(); i++) {
                JSONObject obj_temp = generalFunc.getJsonObject(arr_data, i);

                String[] arr_str_data = new String[2];
                arr_str_data[0] = generalFunc.getJsonValueStr("iPackageTypeId", obj_temp);
                arr_str_data[1] = generalFunc.getJsonValueStr("vName", obj_temp);

                list_packageType_items.add(arr_str_data);
                items.add(generalFunc.getJsonValueStr("vName", obj_temp));
            }


            CharSequence[] cs_currency_txt = items.toArray(new CharSequence[items.size()]);

            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getActContext());
            builder.setTitle(generalFunc.retrieveLangLBl("Select package type", "LBL_SELECT_PACKAGE_TYPE"));

            builder.setItems(cs_currency_txt, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    // Do something with the selection

                    if (alert_packageTypes != null) {
                        alert_packageTypes.dismiss();
                    }

                    String selectedPackageTypeId = list_packageType_items.get(item)[0];

                    currentPackageTypeId = selectedPackageTypeId;

                    packageTypeBox.setText(list_packageType_items.get(item)[1]);

                }
            });


            alert_packageTypes = builder.create();
            if (generalFunc.isRTLmode() == true) {
                generalFunc.forceRTLIfSupported(alert_packageTypes);
            }

            showPackageTypes();
        }
    }

    public void showPackageTypes() {
        if (alert_packageTypes != null) {
            alert_packageTypes.show();
        }
    }

    public void checkDetails() {

        boolean receiverNameEntered = Utils.checkText(receiverNameEditBox) ? true : Utils.setErrorFields(receiverNameEditBox, required_str);
        boolean receiverMobileEntered = Utils.checkText(receiverMobileEditBox) ? true : Utils.setErrorFields(receiverMobileEditBox, required_str);
        boolean pickUpInsEntered = Utils.checkText(pickUpInstructionEditBox) ? true : Utils.setErrorFields(pickUpInstructionEditBox, required_str);
        boolean deliveryInsEntered = Utils.checkText(deliveryInstructionEditBox) ? true : Utils.setErrorFields(deliveryInstructionEditBox, required_str);
        boolean packageDetailsEntered = Utils.checkText(packageDetailsEditBox) ? true : Utils.setErrorFields(packageDetailsEditBox, required_str);
        boolean packageTypeSelected = !currentPackageTypeId.trim().equals("") ? true : Utils.setErrorFields(packageTypeBox, required_str);

        if (receiverMobileEntered) {
            receiverMobileEntered = receiverMobileEditBox.length() >= 3 ? true : Utils.setErrorFields(receiverMobileEditBox, generalFunc.retrieveLangLBl("", "LBL_INVALID_MOBILE_NO"));
        }

        if (receiverNameEntered == false || receiverMobileEntered == false || pickUpInsEntered == false || deliveryInsEntered == false
                || packageDetailsEntered == false || packageTypeSelected == false) {
            return;
        }

        Bundle data = new Bundle();
        data.putString(RECEIVER_NAME_KEY, Utils.getText(receiverNameEditBox));
        data.putString(RECEIVER_MOBILE_KEY, Utils.getText(receiverMobileEditBox));
        data.putString(PICKUP_INS_KEY, Utils.getText(pickUpInstructionEditBox));
        data.putString(DELIVERY_INS_KEY, Utils.getText(deliveryInstructionEditBox));
        data.putString(PACKAGE_DETAILS_KEY, Utils.getText(packageDetailsEditBox));
        data.putString(PACKAGE_TYPE_ID_KEY, currentPackageTypeId);
        data.putString(PACKAGE_TYPE_NAME_KEY, Utils.getText(packageTypeBox));

        storeDetails(data);
    }

    private void storeDetails(Bundle data) {
        DeliveryDetails details = addOrEditDetails();

        JSONArray jaStore = new JSONArray();
        JSONObject deliveriesObj = new JSONObject();

        JSONObject deliveryDetailsParametersStore = new JSONObject();
        try {

            deliveryDetailsParametersStore.put(RECEIVER_NAME_KEY, details.getRecipientName());
            deliveryDetailsParametersStore.put(RECEIVER_MOBILE_KEY, details.getRecipientPhoneNumber());
            deliveryDetailsParametersStore.put(PICKUP_INS_KEY, details.getPickupInstruction());
            deliveryDetailsParametersStore.put(DELIVERY_INS_KEY, details.getDeliveryInstruction());
            deliveryDetailsParametersStore.put(PACKAGE_DETAILS_KEY, details.getPackageDetails());
            deliveryDetailsParametersStore.put(PACKAGE_TYPE_ID_KEY, details.getPackageTypeId());
            deliveryDetailsParametersStore.put(PACKAGE_TYPE_NAME_KEY, details.getvPackageTypeName());
            jaStore.put(deliveryDetailsParametersStore);

            generalFunc.removeValue(Utils.DELIVERY_DETAILS_KEY);

            try {
                generalFunc.storeData(Utils.DELIVERY_DETAILS_KEY, deliveriesObj.put("deliveries", jaStore).toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        (new StartActProcess(getActContext())).setOkResult(data);

        backImgView.performClick();
    }

    private DeliveryDetails addOrEditDetails() {
        DeliveryDetails deliveryDetails = new DeliveryDetails();
        deliveryDetails.setRecipientName("" + Utils.getText(receiverNameEditBox));
        deliveryDetails.setRecipientPhoneNumber(Utils.getText(receiverMobileEditBox));
        deliveryDetails.setPickupInstruction("" + Utils.getText(pickUpInstructionEditBox));
        deliveryDetails.setDeliveryInstruction("" + Utils.getText(deliveryInstructionEditBox));
        deliveryDetails.setPackageDetails("" + Utils.getText(packageDetailsEditBox));
        deliveryDetails.setPackageTypeId("" + currentPackageTypeId);
        deliveryDetails.setvPackageTypeName("" + Utils.getText(packageTypeBox));
        return deliveryDetails;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {


    }

    @Override
    public void afterTextChanged(Editable s) {
        handleResetButton();

    }

    public class setOnTouchList implements View.OnTouchListener {

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_UP && !view.hasFocus()) {
                view.performClick();
            }
            return true;
        }
    }

    public void handleResetButton() {
        try {

            boolean receiverNameEntered = Utils.checkText(receiverNameEditBox) ? true : false;
            boolean receiverMobileEntered = Utils.checkText(receiverMobileEditBox) ? true : false;
            boolean pickUpInsEntered = Utils.checkText(pickUpInstructionEditBox) ? true : false;
            boolean deliveryInsEntered = Utils.checkText(deliveryInstructionEditBox) ? true : false;
            boolean packageDetailsEntered = Utils.checkText(packageDetailsEditBox) ? true : false;
            boolean packageTypeSelected = !currentPackageTypeId.trim().equals("") ? true : false;

            if (receiverNameEntered == true || receiverMobileEntered == true || pickUpInsEntered == true || deliveryInsEntered == true
                    || packageDetailsEntered == true || packageTypeSelected == true) {
                //enable
                btn_reset.setEnabled(true);
                btn_reset.setOnClickListener(new setOnClickList());
                btn_reset.setTextColor(Color.parseColor("#FFFFFF"));
            } else {
                btn_reset.setEnabled(false);
                btn_reset.setOnClickListener(null);
                btn_reset.setTextColor(Color.parseColor("#BABABA"));
            }
        } catch (Exception e) {

        }


    }

    public void resetAllView() {
        receiverNameEditBox.setText("");
        receiverMobileEditBox.setText("");
        currentPackageTypeId = "";
        packageTypeBox.setText("");
        deliveryInstructionEditBox.setText("");
        pickUpInstructionEditBox.setText("");
        packageDetailsEditBox.setText("");

    }

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            int i = view.getId();
            Utils.hideKeyboard(getActContext());
            if (i == R.id.backImgView) {
                EnterDeliveryDetailsActivity.super.onBackPressed();
            } else if (i == btn_type2.getId()) {
                checkDetails();
            } else if (i == R.id.packageTypeBox) {

                if (alert_packageTypes != null) {
                    showPackageTypes();
                } else {
                    loadPackageTypes();
                }
            } else if (view == btn_reset) {

                final GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
                generateAlert.setCancelable(false);
                generateAlert.setBtnClickList(new GenerateAlertBox.HandleAlertBtnClick() {
                    @Override
                    public void handleBtnClick(int btn_id) {
                        if (btn_id == 0) {
                            generateAlert.closeAlertBox();
                        } else {
                            generateAlert.closeAlertBox();
                            resetAllView();
                        }
                    }
                });
                generateAlert.setContentMessage("", generalFunc.retrieveLangLBl("", "LBL_ALL_DATA_CLEAR"));
                generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_CLEAR"));
                generateAlert.setNegativeBtn(generalFunc.retrieveLangLBl("", "LBL_CANCEL_TXT"));
                generateAlert.showAlertBox();

            }
        }
    }

}
