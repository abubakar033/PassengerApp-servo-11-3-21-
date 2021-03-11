package com.servo.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.WebDialog;
import com.facebook.login.widget.LoginButton;
import com.fragments.SignInFragment;
import com.fragments.SignUpFragment;
import com.general.files.GeneralFunctions;
import com.general.files.GetDeviceToken;
import com.general.files.InternetConnection;
import com.general.files.MyApp;
import com.general.files.RegisterFbLoginResCallBack;
import com.general.files.RegisterGoogleLoginResCallBack;
import com.general.files.RegisterLinkedinLoginResCallBack;
import com.general.files.RegisterTwitterLoginResCallBack;
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
import com.utils.Logger;
import com.utils.Utils;
import android.widget.TextView;

import java.util.Arrays;

public class AppLoignRegisterActivity extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_SIGN_IN = 001;
    public TextView titleTxt;
    public GeneralFunctions generalFunc;
    public TextView signheaderHint, orTxt;
    ImageView backImgView;
    FrameLayout container;
    String type = "";
    RelativeLayout imagefacebook,   imageGoogle;
        ImageView    imagetwitter, imagelinkedin;
    CallbackManager callbackManager;
    LoginButton loginButton;
    GoogleApiClient mGoogleApiClient;
    SignUpFragment signUpFragment;
    boolean isGoogleLogin = true;
    boolean isFacebookLogin = true;
    boolean isTwitterLogin = true;
    boolean isLinkdinLogin = true;
    LinearLayout socialarea;
    private TwitterLoginButton twitterloginButton;
    private InternetConnection intCheck;
    TextView continue_google,continue_fb,RegisterText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.d("activityCreated", ":1:" + getClass().getSimpleName());

        try {
//            FacebookSdk.sdkInitialize(getActContext());
            WebDialog.setWebDialogTheme(R.style.FBDialogtheme);
            setContentView(R.layout.activity_app_loign_register);

            type = getIntent().getStringExtra("type");
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
            setLabel();
        } catch (Exception e) {

            Logger.d("exception oncreate()", e.toString());
        }

    }


    private void initview() {

        generalFunc = MyApp.getInstance().getGeneralFun(getActContext());
        FacebookSdk.setApplicationId(generalFunc.retrieveValue(Utils.FACEBOOK_APPID_KEY));
        titleTxt = (TextView) findViewById(R.id.titleTxt);
        backImgView = (ImageView) findViewById(R.id.backImgView);
        backImgView.setOnClickListener(new setOnClickList());
        container = (FrameLayout) findViewById(R.id.container);
        socialarea = (LinearLayout) findViewById(R.id.socialarea);

        signheaderHint = (TextView) findViewById(R.id.signheaderHint);
        orTxt = (TextView) findViewById(R.id.orTxt);


        imagefacebook =  findViewById(R.id.imagefacebook);
        continue_google =  findViewById(R.id.continue_google);
        RegisterText =  findViewById(R.id.RegisterText);
        continue_fb =  findViewById(R.id.continue_fb);


        imagetwitter = (ImageView) findViewById(R.id.imagetwitter);
        imageGoogle =  findViewById(R.id.imageGoogle);
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
        twitterloginButton.setCallback(new RegisterTwitterLoginResCallBack(getActContext()));


        callbackManager = CallbackManager.Factory.create();

//        loginButton.setReadPermissions(Arrays.asList("public_profile", "email", "user_friends", "user_about_me"));
        // , "user_about_me" deprecated and not working
        //loginButton.setReadPermissions(Arrays.asList("public_profile", "email", "user_friends"));
        loginButton.setReadPermissions(Arrays.asList("public_profile", "email"));

        loginButton.registerCallback(callbackManager, new RegisterFbLoginResCallBack(getActContext(), callbackManager));


    }

    public Context getActContext() {
        return AppLoignRegisterActivity.this;
    }


    private void setLabel() {

        if (type != null) {
            if (type.equals("login")) {
                RegisterText.setText(generalFunc.retrieveLangLBl("", "LBL_SIGN_IN_TXT"));
                titleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_SIGN_IN_TXT"));
                signheaderHint.setText(generalFunc.retrieveLangLBl("", "LBL_SIGN_IN_WITH_SOC_ACC"));
                hadnleFragment(new SignInFragment());
            } else {
                RegisterText.setText(generalFunc.retrieveLangLBl("Register", "LBL_REGISTER"));
                titleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_SIGN_UP"));
                signheaderHint.setText(generalFunc.retrieveLangLBl("", "LBL_SIGN_UP_WITH_SOC_ACC"));
                hadnleFragment(new SignUpFragment());
            }
        }
        orTxt.setText(generalFunc.retrieveLangLBl("", "LBL_OR_TXT"));
        continue_google.setText(generalFunc.retrieveLangLBl("Continue with google", "LBL_CONTINUE_WITH_GOOGLE"));
        continue_fb.setText(generalFunc.retrieveLangLBl("Continue with facebook", "LBL_CONTINUE_WITH_FACEBOOK"));
        //check
    }

    public void hadnleFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
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
            if(mGoogleApiClient!=null && !mGoogleApiClient.equals("")) {
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
            }
            new RegisterGoogleLoginResCallBack(getActContext()).handleSignInResult(result);
        } else if (requestCode == Utils.SELECT_COUNTRY_REQ_CODE) {
            SignUpFragment.setdata(requestCode, resultCode, data);
        } else if (requestCode == 140) {
            // twitterloginButton.setCallback(new RegisterTwitterLoginResCallBack(getActContext()));
            twitterloginButton.onActivityResult(requestCode, resultCode, data);


        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {

            int i = view.getId();
            Utils.hideKeyboard(getActContext());
            if (i == backImgView.getId()) {
                Utils.hideKeyboard(AppLoignRegisterActivity.this);
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
                    RegisterLinkedinLoginResCallBack registerLinkedinLoginResCallBack = new RegisterLinkedinLoginResCallBack(getActContext());
                    registerLinkedinLoginResCallBack.continueLogin();


                }

            }
        }
    }


    public void manageSinchClient() {
        if (getSinchServiceInterface()!=null && !getSinchServiceInterface().isStarted()) {
            getSinchServiceInterface().startClient("Passenger" + "_" + generalFunc.getMemberId());

            GetDeviceToken getDeviceToken = new GetDeviceToken(generalFunc);

            getDeviceToken.setDataResponseListener(vDeviceToken -> {

                if (!vDeviceToken.equals("")) {
                    try {
                        getSinchServiceInterface().getSinchClient().registerPushNotificationData(vDeviceToken.getBytes());

                    } catch (Exception e) {

                    }
                }


            });
            getDeviceToken.execute();
        }
    }
}
