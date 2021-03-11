package com.general.files;

import android.app.Activity;
import android.util.Log;

import com.servo.user.BuildConfig;
import com.servo.user.MyProfileActivity;
import com.servo.user.PrescriptionActivity;
import com.rest.RestClient;
import com.utils.CommonUtilities;
import com.utils.Logger;
import com.utils.Utils;
import com.MyProgressDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Admin on 08-07-2016.
 */
public class UploadProfileImage {

    String selectedImagePath;
    String responseString = "";

    String temp_File_Name = "";
    ArrayList<String[]> paramsList;

    Activity act;
    MyProgressDialog myPDialog;
    GeneralFunctions generalFunc;

    public UploadProfileImage(Activity myProfileAct, String selectedImagePath, String temp_File_Name, ArrayList<String[]> paramsList) {
        this.selectedImagePath = selectedImagePath;
        this.temp_File_Name = temp_File_Name;
        this.paramsList = paramsList;
        this.act = myProfileAct;
        this.generalFunc = MyApp.getInstance().getGeneralFun(act);
    }

    public void execute() {
        myPDialog = new MyProgressDialog(act, false, generalFunc.retrieveLangLBl("Loading", "LBL_LOADING_TXT"));
        try {
            myPDialog.show();
        } catch (Exception e) {

        }
        String filePath = "";
        if (!selectedImagePath.equalsIgnoreCase("")) {


            Log.d("SERVER_WEBSERVICE_PATH",""+CommonUtilities.SERVER_WEBSERVICE_PATH);
            Log.d("SERVER_WEBSERVICE_PATH","selectedImagePath:"+selectedImagePath);

            filePath=generalFunc.decodeFile(selectedImagePath, Utils.ImageUpload_DESIREDWIDTH,
                    Utils.ImageUpload_DESIREDHEIGHT, temp_File_Name);

            Log.d("SERVER_WEBSERVICE_PATH","filePath:"+filePath);

        }


        if (filePath.equals("")) {
            Log.d("SERVER_WEBSERVICE_PATH","filePath.equals(\"\"):"+filePath);

            HashMap<String, String> dataParams = new HashMap<>();
            for (int i = 0; i < paramsList.size(); i++) {
                String[] arrData = paramsList.get(i);

                dataParams.put(arrData[0], arrData[1]);
            }

            Logger.d("SERVER_WEBSERVICE_PATH","if:"+CommonUtilities.SERVER_WEBSERVICE_PATH);

            Call<Object> call = RestClient.getClient("POST", CommonUtilities.SERVER).getResponse(CommonUtilities.SERVER_WEBSERVICE_PATH, dataParams);
            call.enqueue(new Callback<Object>() {
                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {
                    if (response.isSuccessful()) {
                        // request successful (status code 200, 201)
                        responseString = RestClient.getGSONBuilder().toJson(response.body());

                        fireResponse();
                    } else {
                        responseString = "";
                        fireResponse();
                    }
                }

                @Override
                public void onFailure(Call<Object> call, Throwable t) {
                    Logger.d("DataError", "::" + t.getMessage());
                    responseString = "";
                    fireResponse();
                }

            });

            return;
        }



        Log.d("SERVER_WEBSERVICE_PATH","after if:"+filePath);



        File file = new File(filePath);

        MultipartBody.Part filePart = null;
        if (!file.equals("")) {
            Log.d("SERVER_WEBSERVICE_PATH","!file.equals(\"\"):"+filePath);

            filePart = MultipartBody.Part.createFormData("vImage", temp_File_Name, RequestBody.create(MediaType.parse("multipart/form-data"), file));
        }

        HashMap<String, RequestBody> dataParams = new HashMap<>();

        for (int i = 0; i < paramsList.size(); i++) {
            String[] arrData = paramsList.get(i);
            dataParams.put(arrData[0], RequestBody.create(MediaType.parse("text/plain"), arrData[1]));
        }

        if (dataParams != null) {
            dataParams.put("tSessionId", RequestBody.create(MediaType.parse("text/plain"), generalFunc.getMemberId().equals("") ? "" : generalFunc.retrieveValue(Utils.SESSION_ID_KEY)));
            dataParams.put("deviceHeight", RequestBody.create(MediaType.parse("text/plain"), Utils.getScreenPixelHeight(act) + ""));
            dataParams.put("deviceWidth", RequestBody.create(MediaType.parse("text/plain"), Utils.getScreenPixelWidth(act) + ""));
            dataParams.put("GeneralUserType", RequestBody.create(MediaType.parse("text/plain"), Utils.app_type));
            dataParams.put("GeneralMemberId", RequestBody.create(MediaType.parse("text/plain"), generalFunc.getMemberId()));
            dataParams.put("GeneralDeviceType", RequestBody.create(MediaType.parse("text/plain"), "" + Utils.deviceType));
            dataParams.put("GeneralAppVersion", RequestBody.create(MediaType.parse("text/plain"), BuildConfig.VERSION_NAME));
            dataParams.put("vTimeZone", RequestBody.create(MediaType.parse("text/plain"), generalFunc.getTimezone()));
            dataParams.put("vUserDeviceCountry", RequestBody.create(MediaType.parse("text/plain"), Utils.getUserDeviceCountryCode(act)));
            dataParams.put("APP_TYPE", RequestBody.create(MediaType.parse("text/plain"), ExecuteWebServerUrl.CUSTOM_APP_TYPE));
            dataParams.put("UBERX_PARENT_CAT_ID", RequestBody.create(MediaType.parse("text/plain"), ExecuteWebServerUrl.CUSTOM_UBERX_PARENT_CAT_ID));
            dataParams.put("vCurrentTime", RequestBody.create(MediaType.parse("text/plain"), generalFunc.getCurrentGregorianDateHourMin()));

            Log.d("SERVER_WEBSERVICE_PATH","dataParams != null"+filePath);


        }


        Call<Object> call = RestClient.getClient("POST", CommonUtilities.SERVER).uploadData(CommonUtilities.SERVER_WEBSERVICE_PATH, filePart, dataParams);
        Log.d("SERVER_WEBSERVICE_PATH","call:"+filePath);
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful()) {
                    Log.d("SERVER_WEBSERVICE_PATH","isSuccessful:");

                    responseString = RestClient.getGSONBuilder().toJson(response.body());
                    fireResponse();
                } else {
                    Log.d("SERVER_WEBSERVICE_PATH","call_else:");

                    responseString = "";
                    fireResponse();
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Log.d("SERVER_WEBSERVICE_PATH","onFailure:");

                Logger.d("DataError", "::" + t.getMessage());
                responseString = "";
                fireResponse();
            }
        });

    }

    public void fireResponse() {
        try {
            if (myPDialog != null) {
                myPDialog.close();
            }
        } catch (Exception e) {

        }
        if (act instanceof MyProfileActivity) {
            ((MyProfileActivity) act).handleImgUploadResponse(responseString);
        } else if (act instanceof PrescriptionActivity) {
            ((PrescriptionActivity) act).handleImgUploadResponse(responseString);
        }
    }

}


