package com.general.files;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.servo.user.CommonDeliveryTypeSelectionActivity;
import com.servo.user.MainActivity;
import com.servo.user.deliverAll.FoodDeliveryHomeActivity;
import com.utils.Utils;
import com.GenerateAlertBox;

import java.util.HashMap;
import java.util.Locale;

public class OpenCatType {
    Context mContext;
    HashMap<String, String> mapData;
    GeneralFunctions generalFunc;

    public OpenCatType(Context mContext, HashMap<String, String> mapData) {
        this.mContext = mContext;
        this.mapData = mapData;

        generalFunc = new GeneralFunctions(mContext);
    }

    public void execute() {
        //do this umar
        if (mapData.get("eCatType") != null) {
            Bundle bn = new Bundle();
            String s = mapData.get("eCatType").toUpperCase(Locale.US);

            if (mapData.get("latitude") == null || mapData.get("latitude").equalsIgnoreCase("0.0")) {
                bn.putString("latitude", "");
            } else {
                bn.putString("latitude", mapData.get("latitude").toString());
            }
            if (mapData.get("longitude") == null || mapData.get("longitude").equalsIgnoreCase("0.0")) {
                bn.putString("longitude", "");
            } else {
                bn.putString("longitude", mapData.get("longitude").toString());
            }

            if (mapData.get("address") == null || mapData.get("address").equalsIgnoreCase("")) {
                bn.putString("address", "");
            } else {
                bn.putString("address", mapData.get("address").toString());
            }


            if ("RIDE".equals(s)) {
                bn.putString("selType", Utils.CabGeneralType_Ride);
                bn.putBoolean("isRestart", false);
                Log.d("OpenCatType","RIDE"+bn);

                new StartActProcess(mContext).startActWithData(MainActivity.class, bn);
                return;
            } else if ("MOTORIDE".equals(s)) {
                bn.putString("selType", Utils.CabGeneralType_Ride);
                bn.putBoolean("isRestart", false);
                bn.putBoolean("emoto", true);
                Log.d("OpenCatType","MOTORIDE"+bn);

                new StartActProcess(mContext).startActWithData(MainActivity.class, bn);
                return;
            } else if ("DELIVERY".equals(s)) {
                if (mapData.get("eDeliveryType") != null && mapData.get("eDeliveryType").equalsIgnoreCase("Multi")) {
                    bn.putBoolean("fromMulti", true);
                }
                bn.putString("selType", Utils.CabGeneralType_Deliver);
                bn.putBoolean("isRestart", false);
                Log.d("OpenCatType","DELIVERY"+bn);

                new StartActProcess(mContext).startActWithData(MainActivity.class, bn);
                return;
            } else if ("MOTODELIVERY".equals(s)) {
                if (mapData.get("eDeliveryType") != null && mapData.get("eDeliveryType").equalsIgnoreCase("Multi")) {
                    bn.putBoolean("fromMulti", true);
                }
                bn.putString("selType", Utils.CabGeneralType_Deliver);
                bn.putBoolean("isRestart", false);
                bn.putBoolean("emoto", true);
                Log.d("OpenCatType","MOTODELIVERY"+bn);

                new StartActProcess(mContext).startActWithData(MainActivity.class, bn);
                return;
            } else if ("RENTAL".equals(s)) {
                bn.putString("selType", "rental");
                bn.putBoolean("isRestart", false);
                Log.d("OpenCatType","RENTAL"+bn);

                new StartActProcess(mContext).startActWithData(MainActivity.class, bn);
                return;
            } else if ("MOTORENTAL".equals(s)) {
                bn.putString("selType", "rental");
                bn.putBoolean("isRestart", false);
                bn.putBoolean("emoto", true);
                Log.d("OpenCatType","MOTORENTAL"+bn);

                new StartActProcess(mContext).startActWithData(MainActivity.class, bn);
                return;
            } else if ("DELIVERALL".equals(s)) {
                goToDeliverAllScreen(mapData.get("iServiceId"));
                return;
            } else if ("MOREDELIVERY".equals(s)) {
                // fixm abubakar for commin vedelivry
                //do this umar
                bn.putString("iVehicleCategoryId", mapData.get("iVehicleCategoryId"));
                bn.putString("vCategory", mapData.get("vCategory"));
                Log.d("OpenCatType","MOREDELIVERY "+bn);

                new StartActProcess(mContext).startActWithData(CommonDeliveryTypeSelectionActivity.class, bn);
                return;
            }
        }
    }

    // fixme Abubakar goto service id ...
    private void goToDeliverAllScreen(String iServiceId) {
        if (generalFunc.prefHasKey(Utils.iServiceId_KEY) && generalFunc != null) {
            generalFunc.removeValue(Utils.iServiceId_KEY);
        }
        HashMap<String, String> storeData = new HashMap<>();
        storeData.put(Utils.iServiceId_KEY, iServiceId);
        storeData.put("DEFAULT_SERVICE_CATEGORY_ID", iServiceId);
        generalFunc.storeData(storeData);
        getLanguageLabelServiceWise();
    }

    private void getLanguageLabelServiceWise() {

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "getUserLanguagesAsPerServiceType");
        parameters.put("LanguageCode", generalFunc.retrieveValue(Utils.LANGUAGE_CODE_KEY));
        parameters.put("eSystem", Utils.eSystem_Type);

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(mContext, parameters);
        exeWebServer.setLoaderConfig(mContext, true, generalFunc);
        exeWebServer.setDataResponseListener(responseString -> {
            if (responseString != null && !responseString.equals("")) {

                boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

                if (isDataAvail) {
                    generalFunc.storeData(Utils.languageLabelsKey, generalFunc.getJsonValue(Utils.message_str, responseString));
                    generalFunc.storeData(Utils.LANGUAGE_CODE_KEY, generalFunc.getJsonValue("vLanguageCode", responseString));
                    generalFunc.storeData(Utils.LANGUAGE_IS_RTL_KEY, generalFunc.getJsonValue("langType", responseString));

                    generalFunc.storeData(Utils.GOOGLE_MAP_LANGUAGE_CODE_KEY, generalFunc.getJsonValue("vGMapLangCode", responseString));

                    Utils.setAppLocal(mContext);

                    Bundle bn = new Bundle();
                    bn.putBoolean("isback", true);
                    Log.d("OpenCatType","OpenCatTypecall"+bn);

                    new StartActProcess(mContext).startActWithData(FoodDeliveryHomeActivity.class, bn);
                } else {
                    errorCallApi();
                }
            } else {
                errorCallApi();
            }

        });
        exeWebServer.execute();
    }

    private void errorCallApi() {
        final GenerateAlertBox generateAlert = new GenerateAlertBox(mContext);
        generateAlert.setCancelable(false);
        generateAlert.setBtnClickList(btn_id -> {
            if (btn_id == 0) {
                getLanguageLabelServiceWise();
                generateAlert.closeAlertBox();
            } else {
                generateAlert.closeAlertBox();
            }
        });
        generateAlert.setContentMessage("", generalFunc.retrieveLangLBl("", "LBL_ERROR_TXT"));
        generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_RETRY_TXT"));
        generateAlert.setNegativeBtn(generalFunc.retrieveLangLBl("", "LBL_CANCEL_TXT"));
        generateAlert.showAlertBox();
    }
}
