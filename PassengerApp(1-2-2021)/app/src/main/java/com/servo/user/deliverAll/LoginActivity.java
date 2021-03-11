package com.servo.user.deliverAll;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.servo.user.ContactUsActivity;
import com.servo.user.ForgotPasswordActivity;
import com.servo.user.R;
import com.servo.user.VerifyInfoActivity;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.WebDialog;
import com.facebook.login.widget.LoginButton;
import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.general.files.InternetConnection;
import com.general.files.MyApp;
import com.general.files.RegisterFbLoginResCallBack;
import com.general.files.RegisterGoogleLoginResCallBack;
import com.general.files.RegisterLinkedinLoginResCallBack;
import com.general.files.RegisterTwitterLoginResCallBack;
import com.general.files.SetUserData;
import com.general.files.StartActProcess;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.utils.Utils;
import com.GenerateAlertBox;
import com.view.MButton;
import android.widget.TextView;
import com.view.MaterialRippleLayout;
import com.view.editBox.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    MaterialEditText emailBox;
    MaterialEditText passwordBox;
    MButton btn_type2;

    GeneralFunctions generalFunc;

    int submitBtnId;
    TextView forgetPassTxt;

    View view;

    String required_str = "";
    String error_email_str = "";

    TextView registerTxt;

    public TextView titleTxt;

    ImageView backImgView;

    public TextView signheaderHint, orTxt;
    GoogleApiClient mGoogleApiClient;
    CallbackManager callbackManager;
    LinearLayout socialarea;
    ImageView imagefacebook, imagetwitter, imageGoogle,imagelinkedin;
    boolean isGoogleLogin = true;
    boolean isFacebookLogin = true;
    boolean isTwitterLogin = true;
    boolean isLinkdinLogin = true;
    LoginButton loginButton;
    private TwitterLoginButton twitterloginButton;
    private static final int RC_SIGN_IN = 001;
    private static final int RC_SIGN_UP = 002;
    private InternetConnection intCheck;

    JSONObject userProfileJsonObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      /*  FacebookSdk.sdkInitialize(getActContext());
        FacebookSdk.setWebDialogTheme(R.style.FBDialogtheme);*/
        WebDialog.setWebDialogTheme(R.style.FBDialogtheme);
        setContentView(R.layout.activity_login);

        intCheck = new InternetConnection(this);


        TwitterAuthConfig authConfig = new TwitterAuthConfig(getActContext().getResources().getString(R.string.com_twitter_sdk_android_CONSUMER_KEY), getActContext().getResources().getString(R.string.com_twitter_sdk_android_CONSUMER_SECRET));

        TwitterConfig config = new TwitterConfig.Builder(this)
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(authConfig)
                .debug(true)
                .build();
        Twitter.initialize(config);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(getActContext())
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        callbackManager = CallbackManager.Factory.create();

        initview();


        setLabels();
    }

    private void initview() {

        generalFunc = MyApp.getInstance().getGeneralFun(getActContext());
        FacebookSdk.setApplicationId(generalFunc.retrieveValue(Utils.FACEBOOK_APPID_KEY));
        titleTxt = (TextView) findViewById(R.id.titleTxt);
        backImgView = (ImageView) findViewById(R.id.backImgView);
        backImgView.setOnClickListener(new setOnClickList());


        emailBox = (MaterialEditText) findViewById(R.id.emailBox);
        passwordBox = (MaterialEditText) findViewById(R.id.passwordBox);
        btn_type2 = ((MaterialRippleLayout) findViewById(R.id.btn_type2)).getChildView();
        forgetPassTxt = (TextView) findViewById(R.id.forgetPassTxt);


        registerTxt = (TextView) findViewById(R.id.registerTxt);
        registerTxt.setOnClickListener(new setOnClickList());

        signheaderHint = (TextView) findViewById(R.id.signheaderHint);
        orTxt = (TextView) findViewById(R.id.orTxt);


        passwordBox.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        passwordBox.setTypeface(generalFunc.getDefaultFont(getActContext()));


        emailBox.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS | InputType.TYPE_CLASS_TEXT);

        emailBox.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        passwordBox.setImeOptions(EditorInfo.IME_ACTION_DONE);

        submitBtnId = Utils.generateViewId();
        btn_type2.setId(submitBtnId);

        btn_type2.setOnClickListener(new setOnClickList());
        forgetPassTxt.setOnClickListener(new setOnClickList());

        socialarea = (LinearLayout) findViewById(R.id.socialarea);

        signheaderHint = (TextView) findViewById(R.id.signheaderHint);
        orTxt = (TextView) findViewById(R.id.orTxt);


        imagefacebook = (ImageView) findViewById(R.id.imagefacebook);
        imagetwitter = (ImageView) findViewById(R.id.imagetwitter);
        imageGoogle = (ImageView) findViewById(R.id.imageGoogle);
        imagelinkedin = (ImageView) findViewById(R.id.imagelinkedin);

        imagefacebook.setOnClickListener(new setOnClickList());
        imagetwitter.setOnClickListener(new setOnClickList());
        imageGoogle.setOnClickListener(new setOnClickList());
        imagelinkedin.setOnClickListener(new setOnClickList());

        if (generalFunc.retrieveValue(Utils.FACEBOOK_LOGIN).equalsIgnoreCase("NO")) {
            isFacebookLogin = false;
            imagefacebook.setVisibility(View.GONE);
        }

        if (generalFunc.retrieveValue(Utils.GOOGLE_LOGIN).equalsIgnoreCase("NO")) {
            isGoogleLogin = false;
            imageGoogle.setVisibility(View.GONE);
        }
        if (generalFunc.retrieveValue(Utils.TWITTER_LOGIN).equalsIgnoreCase("NO")) {
            isTwitterLogin = false;
            imagetwitter.setVisibility(View.GONE);
        }
        if (!isTwitterLogin && !isGoogleLogin & !isFacebookLogin) {
            socialarea.setVisibility(View.GONE);

        }

        if (generalFunc.retrieveValue(Utils.LINKDIN_LOGIN).equalsIgnoreCase("NO")) {
            isLinkdinLogin = false;
            imagelinkedin.setVisibility(View.GONE);
        }
        loginButton = new LoginButton(getActContext());

        twitterloginButton = new TwitterLoginButton(getActContext());
        twitterloginButton.setCallback(new RegisterTwitterLoginResCallBack(getActContext(), getIntent().getBooleanExtra("isRestart", true)));


        callbackManager = CallbackManager.Factory.create();

        loginButton.setReadPermissions(Arrays.asList("public_profile", "email"));

        loginButton.registerCallback(callbackManager, new RegisterFbLoginResCallBack(getActContext(), callbackManager, getIntent().getBooleanExtra("isRestart", true)));
    }


    public void setLabels() {
        emailBox.setBothText(generalFunc.retrieveLangLBl("", "LBL_PHONE_EMAIL"));
        passwordBox.setBothText(generalFunc.retrieveLangLBl("", "LBL_PASSWORD_LBL_TXT"));
        registerTxt.setText(generalFunc.retrieveLangLBl("", "LBL_DONT_HAVE_AN_ACCOUNT"));

        forgetPassTxt.setText(generalFunc.retrieveLangLBl("", "LBL_FORGET_PASS_TXT"));
        btn_type2.setText(generalFunc.retrieveLangLBl("", "LBL_LOGIN"));

        required_str = generalFunc.retrieveLangLBl("", "LBL_FEILD_REQUIRD_ERROR_TXT");
        error_email_str = generalFunc.retrieveLangLBl("", "LBL_FEILD_EMAIL_ERROR_TXT");

        titleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_SIGN_IN_TXT"));
        signheaderHint.setText(generalFunc.retrieveLangLBl("", "LBL_SIGN_IN_WITH_SOC_ACC"));
        signheaderHint.setText(generalFunc.retrieveLangLBl("", "LBL_SIGN_IN_WITH_SOC_ACC"));
        orTxt.setText(generalFunc.retrieveLangLBl("", "LBL_OR_TXT"));
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            //handleSignInResult(result);

            new RegisterGoogleLoginResCallBack(getActContext(), getIntent().getBooleanExtra("isRestart", true)).handleSignInResult(result);
        } else if (requestCode == Utils.SELECT_COUNTRY_REQ_CODE) {

        } else if (requestCode == RC_SIGN_UP && resultCode == RESULT_OK) {
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_OK, returnIntent);
            finish();

        } else if (requestCode == 140) {
            // twitterloginButton.setCallback(new RegisterTwitterLoginResCallBack(getActContext()));
            twitterloginButton.onActivityResult(requestCode, resultCode, data);


        } else if (requestCode == Utils.VERIFY_MOBILE_REQ_CODE && resultCode == RESULT_OK) {
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        } else if (requestCode == Utils.SOCIAL_LOGIN_REQ_CODE && resultCode == RESULT_OK) {
            String userProfileJson = generalFunc.retrieveValue(Utils.USER_PROFILE_JSON);
            userProfileJsonObj = generalFunc.getJsonObject(userProfileJson);

            String ePhoneVerified = generalFunc.getJsonValue("ePhoneVerified", userProfileJson);

            if (!ePhoneVerified.equals("Yes")) {
                notifyVerifyMobile();
                return;
            }

            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_OK, returnIntent);
            finish();

        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }

    }


    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            int i = view.getId();
            Utils.hideKeyboard(getActContext());
            if (i == submitBtnId) {
                checkValues();
            } else if (i == forgetPassTxt.getId()) {
                new StartActProcess(getActContext()).startAct(ForgotPasswordActivity.class);
            } else if (i == registerTxt.getId()) {
                Bundle bn = new Bundle();
                bn.putBoolean("isRestart", getIntent().getBooleanExtra("isRestart", true));
                new StartActProcess(getActContext()).startActForResult(SignUpActivity.class, bn, RC_SIGN_UP);

            }

            if (i == backImgView.getId()) {
                Utils.hideKeyboard(LoginActivity.this);
                finish();

            } else if (i == imagefacebook.getId()) {
                if (!intCheck.isNetworkConnected() && !intCheck.check_int()) {

                    generalFunc.showError();
                } else {
                    loginButton.performClick();
                }

            } else if (i == imagetwitter.getId()) {

                if (!intCheck.isNetworkConnected() && !intCheck.check_int()) {

                    generalFunc.showError();
                } else {
                    twitterloginButton.performClick();
                }


            } else if (i == imageGoogle.getId()) {
                if (!intCheck.isNetworkConnected() && !intCheck.check_int()) {

                    generalFunc.showError();
                } else {

                    Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                    startActivityForResult(signInIntent, RC_SIGN_IN);
                }

            } else if (i == imagelinkedin.getId()) {
                if (!intCheck.isNetworkConnected() && !intCheck.check_int()) {

                    generalFunc.showError();
                } else {
                    RegisterLinkedinLoginResCallBack registerLinkedinLoginResCallBack = new RegisterLinkedinLoginResCallBack(getActContext(),getIntent().getBooleanExtra("isRestart", true));
                    registerLinkedinLoginResCallBack.continueLogin();
                }
            }


        }
    }

    public void checkValues() {
        Utils.hideKeyboard(getActContext());
        String noWhiteSpace = generalFunc.retrieveLangLBl("Password should not contain whitespace.", "LBL_ERROR_NO_SPACE_IN_PASS");
        String pass_length = generalFunc.retrieveLangLBl("Password must be", "LBL_ERROR_PASS_LENGTH_PREFIX")
                + " " + Utils.minPasswordLength + " " + generalFunc.retrieveLangLBl("or more character long.", "LBL_ERROR_PASS_LENGTH_SUFFIX");


        boolean emailEntered = Utils.checkText(emailBox) ? true
                : Utils.setErrorFields(emailBox, required_str);

        boolean passwordEntered = Utils.checkText(passwordBox) ?
                (Utils.getText(passwordBox).contains(" ") ? Utils.setErrorFields(passwordBox, noWhiteSpace)
                        : (Utils.getText(passwordBox).length() >= Utils.minPasswordLength ? true : Utils.setErrorFields(passwordBox, pass_length)))
                : Utils.setErrorFields(passwordBox, required_str);


        String regexStr = "^[0-9]*$";

        if (emailBox.getText().toString().trim().matches(regexStr)) {
            if (emailEntered) {
                emailEntered = emailBox.length() >= 3 ? true : Utils.setErrorFields(emailBox, generalFunc.retrieveLangLBl("", "LBL_INVALID_MOBILE_NO"));
            }

        } else {
            emailEntered = Utils.checkText(emailBox) ?
                    (generalFunc.isEmailValid(Utils.getText(emailBox)) ? true : Utils.setErrorFields(emailBox, error_email_str))
                    : Utils.setErrorFields(emailBox, required_str);

            if (emailEntered == false) {
                return;
            }
        }

        if (emailEntered == false || passwordEntered == false) {
            return;
        }

        btn_type2.setEnabled(false);
        signInUser();
    }

    public void notifyVerifyMobile() {
        String vPhoneCode = generalFunc.getJsonValueStr("vPhoneCode", userProfileJsonObj);
//        String vPhoneCode = generalFunc.retrieveValue(Utils.DefaultPhoneCode);
        Bundle bn = new Bundle();
        bn.putString("MOBILE", vPhoneCode + generalFunc.getJsonValue("vPhone", userProfileJsonObj));
        bn.putString("msg", "DO_PHONE_VERIFY");
        bn.putBoolean("isrestart", false);
        bn.putString("isbackshow", "No");
        new StartActProcess(getActContext()).startActForResult(VerifyInfoActivity.class, bn, Utils.VERIFY_MOBILE_REQ_CODE);
    }


    public void signInUser() {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "signIn");
        parameters.put("vEmail", Utils.getText(emailBox));
        parameters.put("vPassword", Utils.getText(passwordBox));
        parameters.put("vDeviceType", Utils.deviceType);
        parameters.put("UserType", Utils.app_type);
        parameters.put("vCurrency", generalFunc.retrieveValue(Utils.DEFAULT_CURRENCY_VALUE));
        parameters.put("vLang", generalFunc.retrieveValue(Utils.LANGUAGE_CODE_KEY));
        parameters.put("eSystem", Utils.eSystem_Type);

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setIsDeviceTokenGenerate(true, "vDeviceToken", generalFunc);
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(String responseString) {

                btn_type2.setEnabled(true);
                if (responseString != null && !responseString.equals("")) {

                    boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

                    if (isDataAvail) {
                        new SetUserData(responseString, generalFunc, getActContext(), true);
                        generalFunc.storeData(Utils.USER_PROFILE_JSON, generalFunc.getJsonValue(Utils.message_str, responseString));
                        setGeneralData();

                        String userProfileJson = generalFunc.retrieveValue(Utils.USER_PROFILE_JSON);

                        String ePhoneVerified = generalFunc.getJsonValue("ePhoneVerified", userProfileJson);

                        if (!ePhoneVerified.equals("Yes")) {
                            notifyVerifyMobile();
                            return;
                        }

                        Intent returnIntent = new Intent();
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();


                    } else {
                        passwordBox.setText("");
                        if (generalFunc.getJsonValue("eStatus", responseString).equalsIgnoreCase("Deleted")) {
                            openContactUsDialog(responseString);
                        } else if (generalFunc.getJsonValue("eStatus", responseString).equalsIgnoreCase("Inactive")) {
                            openContactUsDialog(responseString);
                        } else {
                            generalFunc.showGeneralMessage("",
                                    generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)));
                        }
                    }
                } else {
                    generalFunc.showError();
                }
            }
        });
        exeWebServer.execute();
    }

    public void setGeneralData() {
        String responseString = generalFunc.retrieveValue(Utils.USER_PROFILE_JSON);
        ArrayList<String> removeData=new ArrayList<>();
        HashMap<String,String> storeData=new HashMap<>();
        userProfileJsonObj = generalFunc.getJsonObject(responseString);
        storeData.put(Utils.PUBNUB_PUB_KEY, generalFunc.getJsonValueStr("PUBNUB_PUBLISH_KEY", userProfileJsonObj));
        storeData.put(Utils.PUBNUB_SUB_KEY, generalFunc.getJsonValueStr("PUBNUB_SUBSCRIBE_KEY", userProfileJsonObj));
        storeData.put(Utils.PUBNUB_SEC_KEY, generalFunc.getJsonValueStr("PUBNUB_SECRET_KEY", userProfileJsonObj));
        storeData.put(Utils.SESSION_ID_KEY, generalFunc.getJsonValueStr("tSessionId", userProfileJsonObj));
        storeData.put(Utils.RIDER_REQUEST_ACCEPT_TIME_KEY, generalFunc.getJsonValueStr("RIDER_REQUEST_ACCEPT_TIME", userProfileJsonObj));
        storeData.put(Utils.DEVICE_SESSION_ID_KEY, generalFunc.getJsonValueStr("tDeviceSessionId", userProfileJsonObj));

        storeData.put(Utils.FETCH_TRIP_STATUS_TIME_INTERVAL_KEY, generalFunc.getJsonValueStr("FETCH_TRIP_STATUS_TIME_INTERVAL", userProfileJsonObj));

        storeData.put(Utils.APP_DESTINATION_MODE, generalFunc.getJsonValueStr("APP_DESTINATION_MODE", userProfileJsonObj));
        storeData.put(Utils.APP_TYPE, generalFunc.getJsonValueStr("APP_TYPE", userProfileJsonObj));
        storeData.put(Utils.SITE_TYPE_KEY, generalFunc.getJsonValueStr("SITE_TYPE", userProfileJsonObj));
        storeData.put(Utils.ENABLE_TOLL_COST, generalFunc.getJsonValueStr("ENABLE_TOLL_COST", userProfileJsonObj));
        storeData.put(Utils.TOLL_COST_APP_ID, generalFunc.getJsonValueStr("TOLL_COST_APP_ID", userProfileJsonObj));
        storeData.put(Utils.TOLL_COST_APP_CODE, generalFunc.getJsonValueStr("TOLL_COST_APP_CODE", userProfileJsonObj));
        storeData.put(Utils.HANDICAP_ACCESSIBILITY_OPTION, generalFunc.getJsonValueStr("HANDICAP_ACCESSIBILITY_OPTION", userProfileJsonObj));
        storeData.put(Utils.CHILD_SEAT_ACCESSIBILITY_OPTION, generalFunc.getJsonValueStr("CHILD_SEAT_ACCESSIBILITY_OPTION", userProfileJsonObj));
        storeData.put(Utils.WHEEL_CHAIR_ACCESSIBILITY_OPTION, generalFunc.getJsonValueStr("WHEEL_CHAIR_ACCESSIBILITY_OPTION", userProfileJsonObj));
        storeData.put(Utils.CHILD_SEAT_ACCESSIBILITY_OPTION, generalFunc.getJsonValueStr("CHILD_SEAT_ACCESSIBILITY_OPTION", userProfileJsonObj));
        storeData.put(Utils.WHEEL_CHAIR_ACCESSIBILITY_OPTION, generalFunc.getJsonValueStr("WHEEL_CHAIR_ACCESSIBILITY_OPTION", userProfileJsonObj));
        storeData.put(Utils.FEMALE_RIDE_REQ_ENABLE, generalFunc.getJsonValueStr("FEMALE_RIDE_REQ_ENABLE", userProfileJsonObj));
        storeData.put(Utils.PUBNUB_DISABLED_KEY, generalFunc.getJsonValueStr("PUBNUB_DISABLED", userProfileJsonObj));
        storeData.put("showCountryList", generalFunc.getJsonValueStr("showCountryList", userProfileJsonObj));

        storeData.put(Utils.ISWALLETBALNCECHANGE, "No");

        /*Multi Delivery Enabled*/
        storeData.put(Utils.ENABLE_MULTI_DELIVERY_KEY, generalFunc.getJsonValueStr(Utils.ENABLE_MULTI_DELIVERY_KEY, userProfileJsonObj));
        storeData.put(Utils.ALLOW_MULTIPLE_DEST_ADD_KEY, generalFunc.getJsonValueStr(Utils.ALLOW_MULTIPLE_DEST_ADD_KEY, userProfileJsonObj));

        removeData.add("userHomeLocationLatitude");
        removeData.add("userHomeLocationLongitude");
        removeData.add("userHomeLocationAddress");
        removeData.add("userWorkLocationLatitude");
        removeData.add("userWorkLocationLongitude");
        removeData.add("userWorkLocationAddress");
        generalFunc.removeValue(removeData);

        if (generalFunc.getJsonArray("UserFavouriteAddress", responseString) == null) {
            return;
        }

        JSONArray userFavouriteAddressArr = generalFunc.getJsonArray("UserFavouriteAddress", responseString);
        if (userFavouriteAddressArr.length() > 0) {

            for (int i = 0; i < userFavouriteAddressArr.length(); i++) {
                JSONObject dataItem = generalFunc.getJsonObject(userFavouriteAddressArr, i);

                if (generalFunc.getJsonValueStr("eType", dataItem).equalsIgnoreCase("HOME")) {

                    storeData.put("userHomeLocationLatitude", generalFunc.getJsonValueStr("vLatitude", dataItem));
                    storeData.put("userHomeLocationLongitude", generalFunc.getJsonValueStr("vLongitude", dataItem));
                    storeData.put("userHomeLocationAddress", generalFunc.getJsonValueStr("vAddress", dataItem));

                } else if (generalFunc.getJsonValueStr("eType", dataItem).equalsIgnoreCase("WORK")) {
                    storeData.put("userWorkLocationLatitude", generalFunc.getJsonValueStr("vLatitude", dataItem));
                    storeData.put("userWorkLocationLongitude", generalFunc.getJsonValueStr("vLongitude", dataItem));
                    storeData.put("userWorkLocationAddress", generalFunc.getJsonValueStr("vAddress", dataItem));

                }

            }
        }

        generalFunc.storeData(storeData);

    }


    public void openContactUsDialog(String responseString) {
        GenerateAlertBox alertBox = new GenerateAlertBox(getActContext());
        alertBox.setContentMessage("", generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)));
        alertBox.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"));
        alertBox.setNegativeBtn(generalFunc.retrieveLangLBl("", "LBL_CONTACT_US_TXT"));
        alertBox.setBtnClickList(new GenerateAlertBox.HandleAlertBtnClick() {
            @Override
            public void handleBtnClick(int btn_id) {

                alertBox.closeAlertBox();
                if (btn_id == 0) {
                    new StartActProcess(getActContext()).startAct(ContactUsActivity.class);
                }
            }
        });
        alertBox.showAlertBox();
    }

    public Context getActContext() {
        return LoginActivity.this;
    }
}
