package com.servo.user.deliverAll;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Html;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.servo.user.R;
import com.servo.user.SelectCountryActivity;
import com.servo.user.SupportActivity;
import com.servo.user.VerifyInfoActivity;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.login.widget.LoginButton;
import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.general.files.InternetConnection;
import com.general.files.MyApp;
import com.general.files.RegisterFbLoginResCallBack;
import com.general.files.RegisterGoogleLoginResCallBack;
import com.general.files.RegisterLinkedinLoginResCallBack;
import com.general.files.RegisterTwitterLoginResCallBack;
import com.general.files.SetOnTouchList;
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

import com.facebook.WebDialog;

public class SignUpActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_SIGN_IN = 001;
    public TextView titleTxt;
    ImageView backImgView;
    ImageView imagefacebook, imagetwitter, imageGoogle,imagelinkedin;
    CallbackManager callbackManager;
    LoginButton loginButton;
    GoogleApiClient mGoogleApiClient;
    boolean isGoogleLogin = true;
    boolean isFacebookLogin = true;
    boolean isTwitterLogin = true;
    boolean isLinkdinLogin = true;
    LinearLayout socialarea;
    private TwitterLoginButton twitterloginButton;
    private InternetConnection intCheck;


    MaterialEditText countryBox;
    String vCountryCode = "";
    String vPhoneCode = "";
    boolean isCountrySelected = false;

    GenerateAlertBox generateAlert;
    GeneralFunctions generalFunc;
    MaterialEditText fNameBox;
    MaterialEditText lNameBox;
    MaterialEditText emailBox;
    MaterialEditText passwordBox;
    MaterialEditText invitecodeBox;
    MaterialEditText mobileBox;
    MButton btn_type2;
    // SignUpFragment signUpFrag;
    ImageView inviteQueryImg;
    LinearLayout inviteCodeArea;
    String required_str = "";
    String error_email_str = "";

    TextView signbootomHint, signbtn;

    ImageView countrydropimage, countrydropimagerror;

    CheckBox checkboxTermsCond;
    TextView txtTermsCond;
    public TextView signheaderHint, orTxt;

    JSONObject userProfileJsonObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       /* FacebookSdk.sdkInitialize(getActContext());
        FacebookSdk.setWebDialogTheme(R.style.FBDialogtheme);*/
        WebDialog.setWebDialogTheme(R.style.FBDialogtheme);
        setContentView(R.layout.activity_sign_up);
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

        initview();


        callbackManager = CallbackManager.Factory.create();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    private void initview() {

        generalFunc = MyApp.getInstance().getGeneralFun(getActContext());
        FacebookSdk.setApplicationId(generalFunc.retrieveValue(Utils.FACEBOOK_APPID_KEY));
        titleTxt = (TextView) findViewById(R.id.titleTxt);
        backImgView = (ImageView) findViewById(R.id.backImgView);
        backImgView.setOnClickListener(new setOnClickList());

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
        if (isTwitterLogin == false && isGoogleLogin == false & isFacebookLogin == false) {
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

        loginButton.setReadPermissions(Arrays.asList("public_profile", "email", "user_friends", "user_about_me"));

        loginButton.registerCallback(callbackManager, new RegisterFbLoginResCallBack(getActContext(), callbackManager, getIntent().getBooleanExtra("isRestart", true)));


        generateAlert = new GenerateAlertBox(getActContext());


        fNameBox = (MaterialEditText) findViewById(R.id.fNameBox);
        lNameBox = (MaterialEditText) findViewById(R.id.lNameBox);
        emailBox = (MaterialEditText) findViewById(R.id.emailBox);
        countryBox = (MaterialEditText) findViewById(R.id.countryBox);
        mobileBox = (MaterialEditText) findViewById(R.id.mobileBox);
        passwordBox = (MaterialEditText) findViewById(R.id.passwordBox);
        invitecodeBox = (MaterialEditText) findViewById(R.id.invitecodeBox);
        signbootomHint = (TextView) findViewById(R.id.signbootomHint);
        signbtn = (TextView) findViewById(R.id.signbtn);
        countrydropimage = (ImageView) findViewById(R.id.countrydropimage);
        countrydropimagerror = (ImageView) findViewById(R.id.countrydropimagerror);
        checkboxTermsCond = (CheckBox) findViewById(R.id.checkboxTermsCond);
        txtTermsCond = (TextView) findViewById(R.id.txtTermsCond);
        signbtn.setOnClickListener(new setOnClickList());
        txtTermsCond.setOnClickListener(new setOnClickList());

        vCountryCode = generalFunc.retrieveValue(Utils.DefaultCountryCode);
        vPhoneCode = generalFunc.retrieveValue(Utils.DefaultPhoneCode);

        if (!vPhoneCode.equalsIgnoreCase("")) {
            countryBox.setText("+" + vPhoneCode);
            isCountrySelected = true;
        }


        btn_type2 = ((MaterialRippleLayout) findViewById(R.id.btn_type2)).getChildView();

        inviteQueryImg = (ImageView) findViewById(R.id.inviteQueryImg);

        inviteCodeArea = (LinearLayout) findViewById(R.id.inviteCodeArea);

        inviteQueryImg.setColorFilter(Color.parseColor("#CECECE"));

        inviteQueryImg.setOnClickListener(new setOnClickList());

        inviteCodeArea.setVisibility(View.GONE);

        if (generalFunc.isReferralSchemeEnable()) {
            inviteCodeArea.setVisibility(View.VISIBLE);
        }

        removeInput();
        setLabels();

        btn_type2.setId(Utils.generateViewId());
        btn_type2.setOnClickListener(new setOnClickList());

        passwordBox.setTypeface(Typeface.DEFAULT);
        passwordBox.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        passwordBox.setTypeface(generalFunc.getDefaultFont(getActContext()));
        mobileBox.setInputType(InputType.TYPE_CLASS_NUMBER);
        emailBox.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS | InputType.TYPE_CLASS_TEXT);
        fNameBox.setInputType(InputType.TYPE_CLASS_TEXT);
        lNameBox.setInputType(InputType.TYPE_CLASS_TEXT);

        fNameBox.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        lNameBox.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        emailBox.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        passwordBox.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        mobileBox.setImeOptions(EditorInfo.IME_ACTION_DONE);

        countryBox.setShowClearButton(false);


    }


    public void removeInput() {
        Utils.removeInput(countryBox);

        countryBox.setOnTouchListener(new SetOnTouchList());

        countryBox.setOnClickListener(new setOnClickList());
    }

    public void setLabels() {

        fNameBox.setBothText(generalFunc.retrieveLangLBl("", "LBL_FIRST_NAME_HEADER_TXT"));
        lNameBox.setBothText(generalFunc.retrieveLangLBl("", "LBL_LAST_NAME_HEADER_TXT"));
        emailBox.setBothText(generalFunc.retrieveLangLBl("", "LBL_EMAIL_LBL_TXT"));
        countryBox.setBothText(generalFunc.retrieveLangLBl("", "LBL_COUNTRY_TXT"));
        mobileBox.setBothText(generalFunc.retrieveLangLBl("", "LBL_MOBILE_NUMBER_HEADER_TXT"));
        passwordBox.setBothText(generalFunc.retrieveLangLBl("", "LBL_PASSWORD_LBL_TXT"));

        signbootomHint.setText(generalFunc.retrieveLangLBl("", "LBL_ALREADY_HAVE_ACC"));
        signbtn.setText(generalFunc.retrieveLangLBl("", "LBL_HEADER_TOPBAR_SIGN_IN_TXT"));

        if (generalFunc.retrieveValue(Utils.MOBILE_VERIFICATION_ENABLE_KEY).equals("Yes")) {
            btn_type2.setText(generalFunc.retrieveLangLBl("", "LBL_BTN_NEXT_TXT"));
        } else {
            btn_type2.setText(generalFunc.retrieveLangLBl("", "LBL_BTN_REGISTER_TXT"));
        }

        required_str = generalFunc.retrieveLangLBl("", "LBL_FEILD_REQUIRD_ERROR_TXT");
        error_email_str = generalFunc.retrieveLangLBl("", "LBL_FEILD_EMAIL_ERROR_TXT");

        invitecodeBox.setBothText(generalFunc.retrieveLangLBl("", "LBL_INVITE_CODE_HINT"), generalFunc.retrieveLangLBl("", "LBL_INVITE_CODE_HINT"));

        String attrString1 = generalFunc.retrieveLangLBl("I agree to the", "LBL_TERMS_CONDITION_PREFIX") + " ";
        String attrString2 = generalFunc.retrieveLangLBl("Terms & Conditions and Privacy Policy", "LBL_TERMS_PRIVACY");

        String htmlString = "<font color=\"" + getResources().getColor(R.color.appThemeColor_2) + "\">" + attrString1 + "<u></font>" +
                "<font color=\"" + getResources().getColor(R.color.appThemeColor_1) + "\">" + attrString2 + "</font></u>";


        txtTermsCond.setText(Html.fromHtml(htmlString));


        titleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_SIGN_UP"));

        signheaderHint.setText(generalFunc.retrieveLangLBl("", "LBL_SIGN_UP_WITH_SOC_ACC"));

    }

    public void checkData() {
        Utils.hideKeyboard(getActContext());

        String noWhiteSpace = generalFunc.retrieveLangLBl("Password should not contain whitespace.", "LBL_ERROR_NO_SPACE_IN_PASS");
        String pass_length = generalFunc.retrieveLangLBl("Password must be", "LBL_ERROR_PASS_LENGTH_PREFIX")
                + " " + Utils.minPasswordLength + " " + generalFunc.retrieveLangLBl("or more character long.", "LBL_ERROR_PASS_LENGTH_SUFFIX");

        boolean fNameEntered = Utils.checkText(fNameBox) ? true : Utils.setErrorFields(fNameBox, required_str);
        boolean lNameEntered = Utils.checkText(lNameBox) ? true : Utils.setErrorFields(lNameBox, required_str);
        boolean emailEntered = Utils.checkText(emailBox) ?
                (generalFunc.isEmailValid(Utils.getText(emailBox)) ? true : Utils.setErrorFields(emailBox, error_email_str))
                : Utils.setErrorFields(emailBox, required_str);
        boolean mobileEntered = Utils.checkText(mobileBox) ? true : Utils.setErrorFields(mobileBox, required_str);
        boolean countryEntered = isCountrySelected ? true : false;
        boolean passwordEntered = Utils.checkText(passwordBox) ?
                (Utils.getText(passwordBox).contains(" ") ? Utils.setErrorFields(passwordBox, noWhiteSpace)
                        : (Utils.getText(passwordBox).length() >= Utils.minPasswordLength ? true : Utils.setErrorFields(passwordBox, pass_length)))
                : Utils.setErrorFields(passwordBox, required_str);

        if (countryBox.getText().length() == 0) {
            countryEntered = false;
        }

        if (!countryEntered) {

            Utils.setErrorFields(countryBox, required_str);
            countrydropimagerror.setVisibility(View.VISIBLE);
            countrydropimage.setVisibility(View.GONE);
        } else {
            countrydropimage.setVisibility(View.VISIBLE);
            countrydropimagerror.setVisibility(View.GONE);

        }
        if (mobileEntered) {
            mobileEntered = mobileBox.length() >= 3 ? true : Utils.setErrorFields(mobileBox, generalFunc.retrieveLangLBl("", "LBL_INVALID_MOBILE_NO"));
        }

        if (fNameEntered == false || lNameEntered == false || emailEntered == false || mobileEntered == false
                || countryEntered == false || passwordEntered == false) {
            return;
        }

        if (!checkboxTermsCond.isChecked()) {
            generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_ACCEPT_TERMS_PRIVACY_ALERT"));
            return;
        }

        btn_type2.setEnabled(false);
        registerUser();

    }

    public void registerUser() {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "signup");
        parameters.put("vFirstName", Utils.getText(fNameBox));
        parameters.put("vLastName", Utils.getText(lNameBox));
        parameters.put("vEmail", Utils.getText(emailBox));
        parameters.put("vPhone", Utils.getText(mobileBox));
        parameters.put("vPassword", Utils.getText(passwordBox));
        parameters.put("PhoneCode", vPhoneCode);
        parameters.put("CountryCode", vCountryCode);
        parameters.put("vDeviceType", Utils.deviceType);
        parameters.put("vInviteCode", Utils.getText(invitecodeBox));
        parameters.put("UserType", Utils.userType);
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

                    if (isDataAvail == true) {
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

    public void checkUserExist() {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "isUserExist");
        parameters.put("Email", Utils.getText(emailBox));
        parameters.put("Phone", Utils.getText(mobileBox));
        parameters.put("eSystem", Utils.eSystem_Type);

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(String responseString) {
                btn_type2.setEnabled(true);

                if (responseString != null && !responseString.equals("")) {

                    boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

                    if (isDataAvail == true) {
                        notifyVerifyMobile();
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

    public void notifyVerifyMobile() {
        Bundle bn = new Bundle();
        bn.putString("MOBILE", vPhoneCode + Utils.getText(mobileBox));
        bn.putString("msg", "DO_PHONE_VERIFY");
        bn.putBoolean("isrestart", false);
        bn.putString("isbackshow", "No");
        new StartActProcess(getActContext()).startActForResult(VerifyInfoActivity.class, bn, Utils.VERIFY_MOBILE_REQ_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            //handleSignInResult(result);

            new RegisterGoogleLoginResCallBack(getActContext(), getIntent().getBooleanExtra("isRestart", true)).handleSignInResult(result);
        } else if (requestCode == Utils.SELECT_COUNTRY_REQ_CODE) {
        } else if (requestCode == 140) {
            // twitterloginButton.setCallback(new RegisterTwitterLoginResCallBack(getActContext()));
            twitterloginButton.onActivityResult(requestCode, resultCode, data);


        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }

        if (requestCode == Utils.SELECT_COUNTRY_REQ_CODE && resultCode == RESULT_OK && data != null) {

            vCountryCode = data.getStringExtra("vCountryCode");
            vPhoneCode = data.getStringExtra("vPhoneCode");
            isCountrySelected = true;
            countryBox.setTextColor(getResources().getColor(R.color.black));
            countryBox.setText(vPhoneCode);
        } else if (requestCode == Utils.VERIFY_MOBILE_REQ_CODE && resultCode == RESULT_OK) {
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        }

    }

    public void setGeneralData() {
        String responseString = generalFunc.retrieveValue(Utils.USER_PROFILE_JSON);
        HashMap<String,String> storeData=new HashMap<>();
        ArrayList<String> removeData=new ArrayList<>();
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

    public Context getActContext() {
        return SignUpActivity.this;
    }

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {

            int i = view.getId();
            Utils.hideKeyboard(getActContext());
            if (i == backImgView.getId()) {
                Utils.hideKeyboard(SignUpActivity.this);
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
            }else if (i == imagelinkedin.getId()) {
                if (!intCheck.isNetworkConnected() && !intCheck.check_int()) {

                    generalFunc.showError();
                } else {
                    RegisterLinkedinLoginResCallBack registerLinkedinLoginResCallBack = new RegisterLinkedinLoginResCallBack(getActContext(),getIntent().getBooleanExtra("isRestart", true));
                    registerLinkedinLoginResCallBack.continueLogin();
                }
            }

            if (i == btn_type2.getId()) {
                checkData();
            } else if (i == R.id.countryBox) {
                new StartActProcess(getActContext()).startActForResult(SelectCountryActivity.class, Utils.SELECT_COUNTRY_REQ_CODE);
            } else if (i == inviteQueryImg.getId()) {
                generalFunc.showGeneralMessage(generalFunc.retrieveLangLBl(" What is Referral / Invite Code ?", "LBL_REFERAL_SCHEME_TXT"),
                        generalFunc.retrieveLangLBl("", "LBL_REFERAL_SCHEME"));

            } else if (i == signbtn.getId()) {
                onBackPressed();
            } else if (i == txtTermsCond.getId()) {
                Bundle bn = new Bundle();
                bn.putBoolean("islogin", true);
                new StartActProcess(getActContext()).startActWithData(SupportActivity.class, bn);

            }

        }
    }

}
