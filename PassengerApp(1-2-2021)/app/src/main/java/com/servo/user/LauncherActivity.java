package com.servo.user;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.servo.user.deliverAll.FoodDeliveryHomeActivity;
import com.servo.user.deliverAll.ServiceHomeActivity;
import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.general.files.GetDeviceToken;
import com.general.files.GetLocationUpdates;
import com.general.files.InternetConnection;
import com.general.files.MyApp;
import com.general.files.OpenMainProfile;
import com.general.files.SetUserData;
import com.general.files.StartActProcess;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.security.ProviderInstaller;
import com.utils.Utils;
import com.view.GenerateAlertBox;
import com.view.anim.loader.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import io.github.sac.Socket;

public class LauncherActivity extends BaseActivity implements ProviderInstaller.ProviderInstallListener {

    AVLoadingIndicatorView loaderView;
    InternetConnection intCheck;
    GeneralFunctions generalFunc;

    GetLocationUpdates getLastLocation;

    long autoLoginStartTime = 0;

    /*4.4 lower Device SSl CERTIFICATE ISSUE*/

    private static final int ERROR_DIALOG_REQUEST_CODE = 1;
    private boolean mRetryProviderInstall;
    RelativeLayout rlContentArea;

    GenerateAlertBox currentAlertBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        loaderView = (AVLoadingIndicatorView) findViewById(R.id.loaderView);
        rlContentArea = (RelativeLayout) findViewById(R.id.rlContentArea);

        generalFunc = MyApp.getInstance().getGeneralFun(getActContext());
        getLastLocation = new GetLocationUpdates(getActContext(), 2, false, null);

        intCheck = new InternetConnection(this);

        generalFunc.storeData("isInLauncher", "true");






//        checkConfigurations(true);

        ProviderInstaller.installIfNeededAsync(this, this);



        Socket client = new Socket(generalFunc.retrieveValue(Utils.SC_CONNECT_URL_KEY));

        Log.d("newwwwwwwwwww",""+client);
        Log.d("newwwwwwwwwww","key"+generalFunc.retrieveValue(Utils.SC_CONNECT_URL_KEY));


//        JToast(100);

        //   new StartActProcess(getActContext()).startService(MyBackGroundService.class);

    }

    public void checkConfigurations(boolean isPermissionShown) {
        closeAlert();

        int status = (GoogleApiAvailability.getInstance()).isGooglePlayServicesAvailable(getActContext());

        if (status == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED) {
            showErrorOnPlayServiceDialog(generalFunc.retrieveLangLBl("This application requires updated google play service. " +
                    "Please install Or update it from play store", "LBL_UPDATE_PLAY_SERVICE_NOTE"));
            return;
        } else if (status != ConnectionResult.SUCCESS) {
            showErrorOnPlayServiceDialog(generalFunc.retrieveLangLBl("This application requires updated google play service. " +
                    "Please install Or update it from play store", "LBL_UPDATE_PLAY_SERVICE_NOTE"));
            return;
        }

        if (!generalFunc.isAllPermissionGranted(isPermissionShown)) {
            showNoPermission();
            return;
        }

        if (!intCheck.isNetworkConnected() && !intCheck.check_int()) {
            showNoInternetDialog();
        } else {
            Location mLastLocation = getLastLocation.getLastLocation();
            if (mLastLocation == null) {
                getLastLocation.startLocationUpdates(false);
            }
            JToast(00);
            continueProcess();
        }

    }

    public void continueProcess() {

        JToast(101);

        closeAlert();
        showLoader();

        Utils.setAppLocal(getActContext());
        if (generalFunc.isUserLoggedIn() == true && Utils.checkText(generalFunc.getMemberId())) {
            if (getSinchServiceInterface() == null && !generalFunc.retrieveValue(Utils.SINCH_APP_KEY).equalsIgnoreCase("")) {
                JToast(99);
                new Handler().postDelayed(() -> continueProcess(), 1500);
            } else if (getSinchServiceInterface() != null) {
                JToast(102);
                autoLogin();
                if (!getSinchServiceInterface().isStarted()) {
                    getSinchServiceInterface().startClient(Utils.userType + "_" + generalFunc.getMemberId());
                    GetDeviceToken getDeviceToken = new GetDeviceToken(generalFunc);
                    getDeviceToken.setDataResponseListener(vDeviceToken -> {
                        if (!vDeviceToken.equals("")) {
                            try {
                                getSinchServiceInterface().getSinchClient().registerPushNotificationData(vDeviceToken.getBytes());
                            } catch (Exception ignored) {

                            }
                        }
                    });
                    getDeviceToken.execute();
                }
            } else {
                autoLogin();
            }

        } else {
            downloadGeneralData();
        }

    }

    public void restartAppDailog() {
        closeAlert();
        generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("Please try again.",
                "LBL_TRY_AGAIN_TXT"), generalFunc.retrieveLangLBl("Ok", "LBL_BTN_OK_TXT"),
                "", buttonId -> generalFunc.restartApp());
    }

    public void JToast(int i){

        Log.d("jtoasttttttt","log"+i);


    }

    public void downloadGeneralData() {

        JToast(0);

        closeAlert();
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "generalConfigData");
        parameters.put("UserType", Utils.app_type);
        parameters.put("AppVersion", BuildConfig.VERSION_NAME);
        parameters.put("vLang", generalFunc.retrieveValue(Utils.LANGUAGE_CODE_KEY));
        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setDataResponseListener(responseString -> {

            if (isFinishing()) {
                restartAppDailog();
                return;
            }


            if (responseString != null && !responseString.equals("")) {


                JToast(1);

                boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

                if (isDataAvail) {

                    JToast(2);

                    HashMap<String, String> storeData = new HashMap<>();
                    storeData.put(Utils.FACEBOOK_APPID_KEY, generalFunc.getJsonValue("FACEBOOK_APP_ID", responseString));
                    storeData.put(Utils.LINK_FORGET_PASS_KEY, generalFunc.getJsonValue("LINK_FORGET_PASS_PAGE_PASSENGER", responseString));
                    storeData.put(Utils.APP_GCM_SENDER_ID_KEY, generalFunc.getJsonValue("GOOGLE_SENDER_ID", responseString));
                    storeData.put(Utils.MOBILE_VERIFICATION_ENABLE_KEY, generalFunc.getJsonValue("MOBILE_VERIFICATION_ENABLE", responseString));

                    storeData.put(Utils.CURRENCY_LIST_KEY, generalFunc.getJsonValue("LIST_CURRENCY", responseString));

                    storeData.put("showCountryList", generalFunc.getJsonValue("showCountryList", responseString));

                    storeData.put(Utils.GOOGLE_MAP_LANGUAGE_CODE_KEY, generalFunc.getJsonValue("vGMapLangCode", generalFunc.getJsonValue("DefaultLanguageValues", responseString)));
                    storeData.put(Utils.REFERRAL_SCHEME_ENABLE, generalFunc.getJsonValue("REFERRAL_SCHEME_ENABLE", responseString));
                    storeData.put(Utils.SITE_TYPE_KEY, generalFunc.getJsonValue("SITE_TYPE", responseString));


                    storeData.put(Utils.languageLabelsKey, generalFunc.getJsonValue("LanguageLabels", responseString));
                    storeData.put(Utils.LANGUAGE_LIST_KEY, generalFunc.getJsonValue("LIST_LANGUAGES", responseString));
                    storeData.put(Utils.LANGUAGE_IS_RTL_KEY, generalFunc.getJsonValue("eType", generalFunc.getJsonValue("DefaultLanguageValues", responseString)));
                    storeData.put(Utils.LANGUAGE_CODE_KEY, generalFunc.getJsonValue("vCode", generalFunc.getJsonValue("DefaultLanguageValues", responseString)));
                    storeData.put(Utils.DEFAULT_LANGUAGE_VALUE, generalFunc.getJsonValue("vTitle", generalFunc.getJsonValue("DefaultLanguageValues", responseString)));

                    if (generalFunc.retrieveValue(Utils.DEFAULT_CURRENCY_VALUE).equalsIgnoreCase("")) {
                        storeData.put(Utils.DEFAULT_CURRENCY_VALUE, generalFunc.getJsonValue("vName", generalFunc.getJsonValue("DefaultCurrencyValues", responseString)));
                    }

                    storeData.put(Utils.FACEBOOK_LOGIN, generalFunc.getJsonValue("FACEBOOK_LOGIN", responseString));
                    storeData.put(Utils.GOOGLE_LOGIN, generalFunc.getJsonValue("GOOGLE_LOGIN", responseString));
                    storeData.put(Utils.TWITTER_LOGIN, generalFunc.getJsonValue("TWITTER_LOGIN", responseString));
                    storeData.put(Utils.LINKDIN_LOGIN, generalFunc.getJsonValue("LINKEDIN_LOGIN", responseString));

                    storeData.put(Utils.DefaultCountry, generalFunc.getJsonValue("vDefaultCountry", responseString));
                    storeData.put(Utils.DefaultCountryCode, generalFunc.getJsonValue("vDefaultCountryCode", responseString));
                    storeData.put(Utils.DefaultPhoneCode, generalFunc.getJsonValue("vDefaultPhoneCode", responseString));

                    storeData.put(Utils.DELIVERALL_KEY, generalFunc.getJsonValue(Utils.DELIVERALL_KEY, responseString));
                    storeData.put(Utils.ONLYDELIVERALL_KEY, generalFunc.getJsonValue(Utils.ONLYDELIVERALL_KEY, responseString));

                    storeData.put(Utils.GOOGLE_SERVER_ANDROID_PASSENGER_APP_KEY, generalFunc.getJsonValue("GOOGLE_SERVER_ANDROID_PASSENGER_APP_KEY", responseString));


                    Utils.setAppLocal(getActContext());
                    closeLoader();

                    storeData.put(Utils.SINCH_APP_KEY, generalFunc.getJsonValue(Utils.SINCH_APP_KEY, responseString));
                    storeData.put(Utils.SINCH_APP_SECRET_KEY, generalFunc.getJsonValue(Utils.SINCH_APP_SECRET_KEY, responseString));
                    storeData.put(Utils.SINCH_APP_ENVIRONMENT_HOST, generalFunc.getJsonValue(Utils.SINCH_APP_ENVIRONMENT_HOST, responseString));

                    generalFunc.storeData(storeData);

                    if (generalFunc.getJsonValue("SERVER_MAINTENANCE_ENABLE", responseString).equalsIgnoreCase("Yes")) {
                        JToast(3);
                        new StartActProcess(getActContext()).startAct(MaintenanceActivity.class);
                        finish();
                        return;
                    }


                    if (generalFunc.isDeliverOnlyEnabled()) {

                        JToast(4);

                        JSONArray serviceArray = generalFunc.getJsonArray("ServiceCategories", responseString);

                        if (serviceArray.length() > 1) {
                            JToast(5);
                            ArrayList<HashMap<String, String>> list_item = new ArrayList<>();
                            for (int i = 0; i < serviceArray.length(); i++) {
                                JSONObject serviceObj = generalFunc.getJsonObject(serviceArray, i);
                                HashMap<String, String> servicemap = new HashMap<>();
                                servicemap.put("iServiceId", generalFunc.getJsonValue("iServiceId", serviceObj.toString()));
                                servicemap.put("vServiceName", generalFunc.getJsonValue("vServiceName", serviceObj.toString()));
                                servicemap.put("vImage", generalFunc.getJsonValue("vImage", serviceObj.toString()));
                                list_item.add(servicemap);
                            }
                            Bundle bn = new Bundle();
                            bn.putSerializable("servicedata", list_item);
                            new StartActProcess(getActContext()).startActWithData(ServiceHomeActivity.class, bn);

                        } else {
                            JToast(6);
                            new StartActProcess(getActContext()).startAct(FoodDeliveryHomeActivity.class);
                        }
                    } else {
                        JToast(7);
                        new StartActProcess(getActContext()).startAct(AppLoginActivity.class);
                    }


                    try {
                        JToast(8);
                        ActivityCompat.finishAffinity(LauncherActivity.this);
                    } catch (Exception e) {

                    }

                } else {
                    if (!generalFunc.getJsonValue("isAppUpdate", responseString).trim().equals("")
                            && generalFunc.getJsonValue("isAppUpdate", responseString).equals("true")) {
                        showAppUpdateDialog(generalFunc.retrieveLangLBl("New update is available to download. " +
                                        "Downloading the latest update, you will get latest features, improvements and bug fixes.",
                                generalFunc.getJsonValue(Utils.message_str, responseString)));
                    } else {
                        showError();
                    }
                }
            } else {
                showError();

            }
        });
        exeWebServer.execute();
    }

    public void autoLogin() {

        JToast(111);

        closeAlert();
        autoLoginStartTime = Calendar.getInstance().getTimeInMillis();

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "getDetail");
        parameters.put("iUserId", generalFunc.getMemberId());
        parameters.put("vDeviceType", Utils.deviceType);
        parameters.put("AppVersion", BuildConfig.VERSION_NAME);
        parameters.put("UserType", Utils.app_type);
        if (!generalFunc.retrieveValue(Utils.LANGUAGE_CODE_KEY).equalsIgnoreCase("")) {
            parameters.put("vLang", generalFunc.retrieveValue(Utils.LANGUAGE_CODE_KEY));
        }


        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setIsDeviceTokenGenerate(true, "vDeviceToken", generalFunc);
        exeWebServer.setDataResponseListener(responseString -> {

            Log.d("responseString","test::"+responseString);

            closeLoader();


            if (isFinishing()) {
                return;
            }

            if (responseString != null && !responseString.equals("")) {

                boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

                if (generalFunc.getJsonValue("changeLangCode", responseString).equalsIgnoreCase("Yes")) {
                    //here to manage code
                    new SetUserData(responseString, generalFunc, getActContext(), false);
                }

                String message = generalFunc.getJsonValue(Utils.message_str, responseString);

                if (message.equals("SESSION_OUT")) {
                    autoLoginStartTime = 0;
                    MyApp.getInstance().notifySessionTimeOut();
                    Utils.runGC();
                    return;
                }

                if (isDataAvail) {

                    if (generalFunc.getJsonValue("SERVER_MAINTENANCE_ENABLE", message).equalsIgnoreCase("Yes")) {
                        JToast(12);
                        new StartActProcess(getActContext()).startAct(MaintenanceActivity.class);
                        finish();
                        return;
                    }

                    generalFunc.storeData(Utils.USER_PROFILE_JSON, message);

                    generalFunc.storeData(Utils.DELIVERALL_KEY, generalFunc.getJsonValue(Utils.DELIVERALL_KEY, message));
                    generalFunc.storeData(Utils.ONLYDELIVERALL_KEY, generalFunc.getJsonValue(Utils.ONLYDELIVERALL_KEY, message));

                    if (generalFunc.isDeliverOnlyEnabled()) {

                        if (generalFunc.getJsonValue("vPhone", message).equals("") || generalFunc.getJsonValue("vEmail", message).equals("")) {
                     //   if (generalFunc.getJsonValue("vPhone", message).equals("") || generalFunc.getJsonValue("vEmail", message).equals("")) {
                            //open account verification screen
                            Bundle bn = new Bundle();
                            bn.putBoolean("isRestart", true);
                            JToast(13);
                            new StartActProcess(getActContext()).startActForResult(AccountverificationActivity.class, bn, Utils.SOCIAL_LOGIN_REQ_CODE);
                            return;

                        }


                        String ePhoneVerified = generalFunc.getJsonValue("ePhoneVerified", message);
                        String vPhoneCode = generalFunc.getJsonValue("vPhoneCode", message);
                        String vPhone = generalFunc.getJsonValue("vPhone", message);

                        if (!ePhoneVerified.equals("Yes")) {
                            Bundle bn = new Bundle();
                            bn.putString("MOBILE", vPhoneCode + vPhone);
                            bn.putString("msg", "DO_PHONE_VERIFY");
                            bn.putBoolean("isrestart", true);
                            bn.putString("isbackshow", "No");
                            JToast(14);
                            new StartActProcess(getActContext()).startActForResult(VerifyInfoActivity.class, bn, Utils.VERIFY_MOBILE_REQ_CODE);
                            return;
                        }


                        JSONArray serviceArray = generalFunc.getJsonArray("ServiceCategories", message);

                        if (serviceArray != null && serviceArray.length() > 1 && generalFunc.isAnyDeliverOptionEnabled()) {

                            ArrayList<HashMap<String, String>> list_item = new ArrayList<>();
                            for (int i = 0; i < serviceArray.length(); i++) {
                                JSONObject serviceObj = generalFunc.getJsonObject(serviceArray, i);
                                HashMap<String, String> servicemap = new HashMap<>();
                                servicemap.put("iServiceId", generalFunc.getJsonValue("iServiceId", serviceObj.toString()));
                                servicemap.put("vServiceName", generalFunc.getJsonValue("vServiceName", serviceObj.toString()));
                                servicemap.put("vImage", generalFunc.getJsonValue("vImage", serviceObj.toString()));
                                list_item.add(servicemap);
                            }
                            Bundle bn = new Bundle();
                            bn.putSerializable("servicedata", list_item);
                            JToast(15);
                            new StartActProcess(getActContext()).startActWithData(ServiceHomeActivity.class, bn);
                            try {
                                JToast(16);
                                ActivityCompat.finishAffinity(LauncherActivity.this);
                            } catch (Exception e) {

                            }

                        } else {
                            JToast(17);
                            new StartActProcess(getActContext()).startAct(FoodDeliveryHomeActivity.class);
                            try {
                                JToast(18);
                                ActivityCompat.finishAffinity(LauncherActivity.this);
                            } catch (Exception e) {

                            }
                        }
                    } else {
                        if (Calendar.getInstance().getTimeInMillis() - autoLoginStartTime < 2000) {
                            new Handler().postDelayed(() -> new OpenMainProfile(getActContext(),
                                    generalFunc.getJsonValue(Utils.message_str, responseString), true, generalFunc).startProcess(), 2000);
                        } else {
                            new OpenMainProfile(getActContext(),
                                    generalFunc.getJsonValue(Utils.message_str, responseString), true, generalFunc).startProcess();
                        }
                    }


                } else {
                    autoLoginStartTime = 0;
                    if (!generalFunc.getJsonValue("isAppUpdate", responseString).trim().equals("")
                            && generalFunc.getJsonValue("isAppUpdate", responseString).equals("true")) {

                        showAppUpdateDialog(generalFunc.retrieveLangLBl("New update is available to download. " +
                                        "Downloading the latest update, you will get latest features, improvements and bug fixes.",
                                generalFunc.getJsonValue(Utils.message_str, responseString)));
                    } else {

                        if (generalFunc.getJsonValue(Utils.message_str, responseString).equalsIgnoreCase("LBL_CONTACT_US_STATUS_NOTACTIVE_PASSENGER") ||
                                generalFunc.getJsonValue(Utils.message_str, responseString).equalsIgnoreCase("LBL_ACC_DELETE_TXT")) {

                            showContactUs(generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)));

                            return;
                        }
                        showError("",
                                generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)));
                    }
                }
            } else {
                autoLoginStartTime = 0;
                showError();
                Log.d("responseString","test::"+responseString);
            }
        });
        JToast(200);
        exeWebServer.execute();
    }

    public void showLoader() {
        if (loaderView != null) {
            loaderView.setVisibility(View.VISIBLE);
        }
    }

    public void closeLoader() {
        if (loaderView != null) {
            loaderView.setVisibility(View.GONE);
        }
    }

    private void closeAlert() {
        try {
            if (currentAlertBox != null) {
                currentAlertBox.closeAlertBox();
                currentAlertBox = null;
            }
        } catch (Exception e) {

        }
    }

    public void showContactUs(String content) {
        closeAlert();
        currentAlertBox = generalFunc.showGeneralMessage("", content, generalFunc.retrieveLangLBl("Contact Us", "LBL_CONTACT_US_TXT"), generalFunc.retrieveLangLBl("Ok", "LBL_BTN_OK_TXT"), buttonId -> {
            if (buttonId == 0) {
                new StartActProcess(getActContext()).startAct(ContactUsActivity.class);
                showContactUs(content);
            } else if (buttonId == 1) {
                MyApp.getInstance().logOutFromDevice(true);
            }
        });
    }

    public void showError() {
        closeAlert();
        currentAlertBox = generalFunc.showGeneralMessage("",
                generalFunc.retrieveLangLBl("Please try again.",
                        "LBL_TRY_AGAIN_TXT"), generalFunc.retrieveLangLBl("Cancel", "LBL_CANCEL_TXT"),
                generalFunc.retrieveLangLBl("Retry", "LBL_RETRY_TXT"), buttonId -> handleBtnClick(buttonId, "ERROR"));
    }

    public void showError(String title, String contentMsg) {
        closeAlert();
        currentAlertBox = generalFunc.showGeneralMessage(title, contentMsg, generalFunc.retrieveLangLBl("Cancel", "LBL_CANCEL_TXT"),
                generalFunc.retrieveLangLBl("Retry", "LBL_RETRY_TXT"), buttonId -> handleBtnClick(buttonId, "ERROR"));
    }

    public void showNoInternetDialog() {
        closeAlert();
        currentAlertBox = generalFunc.showGeneralMessage("",
                generalFunc.retrieveLangLBl("No Internet Connection",
                        "LBL_NO_INTERNET_TXT"), generalFunc.retrieveLangLBl("Cancel", "LBL_CANCEL_TXT"),
                generalFunc.retrieveLangLBl("Retry", "LBL_RETRY_TXT"), buttonId -> handleBtnClick(buttonId, "NO_INTERNET"));
    }

    public void showNoPermission() {
        closeAlert();
        currentAlertBox = generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("Application requires some permission to be granted to work. Please allow it.",
                "LBL_ALLOW_PERMISSIONS_APP"), generalFunc.retrieveLangLBl("Cancel", "LBL_CANCEL_TXT"),
                generalFunc.retrieveLangLBl("Allow All", "LBL_ALLOW_ALL_TXT"),
                buttonId -> handleBtnClick(buttonId, "NO_PERMISSION"));
    }

    public void showErrorOnPlayServiceDialog(String content) {
        closeAlert();
        currentAlertBox = generalFunc.showGeneralMessage("", content, generalFunc.retrieveLangLBl("Retry", "LBL_RETRY_TXT"),
                generalFunc.retrieveLangLBl("Update", "LBL_UPDATE"), buttonId -> handleBtnClick(buttonId, "NO_PLAY_SERVICE"));
    }

    public void showAppUpdateDialog(String content) {
        closeAlert();
        currentAlertBox = generalFunc.showGeneralMessage(generalFunc.retrieveLangLBl("New update available",
                "LBL_NEW_UPDATE_AVAIL"), content, generalFunc.retrieveLangLBl("Retry", "LBL_RETRY_TXT"),
                generalFunc.retrieveLangLBl("Update", "LBL_UPDATE"), buttonId -> handleBtnClick(buttonId, "APP_UPDATE"));
    }

    public Context getActContext() {
        return LauncherActivity.this;
    }

    public void handleBtnClick(int buttonId, String alertType) {
        Utils.hideKeyboard(getActContext());
        if (buttonId == 0) {
            if (!alertType.equals("NO_PLAY_SERVICE") && !alertType.equals("APP_UPDATE")) {
                finish();
            } else {
                checkConfigurations(false);
            }
        } else {
            if (alertType.equals("NO_PLAY_SERVICE")) {

                boolean isSuccessfulOpen = new StartActProcess(getActContext()).openURL("market://details?id=com.google.android.gms");
                if (!isSuccessfulOpen) {
                    new StartActProcess(getActContext()).openURL("http://play.google.com/store/apps/details?id=com.google.android.gms");
                }
                checkConfigurations(false);
            } else if (alertType.equals("NO_PERMISSION")) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == false ||
                        ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == false ||
                        ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == false ||
                        ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.CAMERA) == false) {
                    generalFunc.openSettings();
                } else if (!generalFunc.isAllPermissionGranted(false)) {
                    generalFunc.isAllPermissionGranted(true);
                    checkConfigurations(false);
                } else {
                    checkConfigurations(true);
                }

            } else if (alertType.equals("APP_UPDATE")) {
                boolean isSuccessfulOpen = new StartActProcess(getActContext()).openURL("market://details?id=" + BuildConfig.APPLICATION_ID);
                if (!isSuccessfulOpen) {
                    new StartActProcess(getActContext()).openURL("http://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
                }
                checkConfigurations(false);
            } else if (!alertType.equals("NO_GPS")) {
                checkConfigurations(false);
            } else {
                new StartActProcess(getActContext()).
                        startActForResult(Settings.ACTION_LOCATION_SOURCE_SETTINGS, Utils.REQUEST_CODE_GPS_ON);
            }
        }
    }

    @Override
    public void onResume() {

        if (generalFunc.prefHasKey(Utils.iServiceId_KEY) && generalFunc != null /*&& !generalFunc.isDeliverOnlyEnabled()*/) {
            generalFunc.removeValue(Utils.iServiceId_KEY);
        }

        super.onResume();
    }

    @Override
    protected void onPause() {

        super.onPause();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Utils.REQUEST_CODE_GPS_ON:
                checkConfigurations(false);
                break;
            case GeneralFunctions.MY_SETTINGS_REQUEST:
                checkConfigurations(false);
                break;
            case ERROR_DIALOG_REQUEST_CODE:
                mRetryProviderInstall = true;
                break;

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case GeneralFunctions.MY_PERMISSIONS_REQUEST: {
                checkConfigurations(false);
                return;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        generalFunc.storeData("isInLauncher", "false");
        if (getLastLocation != null) {
            getLastLocation.stopLocationUpdates();
        }
    }

    @Override
    public void onProviderInstalled() {
        checkConfigurations(true);
    }

    @Override
    public void onProviderInstallFailed(int errorCode, Intent intent) {
        if (GooglePlayServicesUtil.isUserRecoverableError(errorCode)) {
            // Recoverable error. Show a dialog prompting the user to
            // install/update/enable Google Play services.
            GooglePlayServicesUtil.showErrorDialogFragment(
                    errorCode,
                    this,
                    ERROR_DIALOG_REQUEST_CODE,
                    dialog -> {
                        // The user chose not to take the recovery action
                        onProviderInstallerNotAvailable();
                    });
        } else {
            // Google Play services is not available.
            onProviderInstallerNotAvailable();
        }
    }

    private void onProviderInstallerNotAvailable() {
        // This is reached if the provider cannot be updated for some reason.
        // App should consider all HTTP communication to be vulnerable, and take
        // appropriate action.
        checkConfigurations(true);
        showMessageWithAction(rlContentArea, generalFunc.retrieveLangLBl("provider cannot be updated for some reason.", "LBL_PROVIDER_NOT_AVALIABLE_TXT"));
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (mRetryProviderInstall) {
            ProviderInstaller.installIfNeededAsync(this, this);
        }
        mRetryProviderInstall = false;
    }

    public void showMessageWithAction(View view, String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE);
        snackbar.setDuration(10000);
        snackbar.show();
    }

}
