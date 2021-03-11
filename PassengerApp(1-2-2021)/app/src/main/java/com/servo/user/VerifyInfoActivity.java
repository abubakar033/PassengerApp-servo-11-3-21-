package com.servo.user;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.general.files.MyApp;
import com.general.files.SetUserData;
import com.general.files.StartActProcess;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.utils.Utils;
import com.GenerateAlertBox;
import com.view.MButton;
import android.widget.TextView;
import com.view.MaterialRippleLayout;
import com.view.editBox.MaterialEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class VerifyInfoActivity extends AppCompatActivity {

    CardView emailView, smsView;
    ProgressBar loading;
    MaterialEditText codeBox;
    MaterialEditText emailBox;

    ImageView backImgView;
    GeneralFunctions generalFunc;
    String required_str = "";
    String error_verification_code = "";

    String userProfileJson = "";
    TextView titleTxt;

    MButton okBtn, emailOkBtn;
    MButton resendBtn, emailResendBtn;
    MButton editBtn, emailEditBtn;
    Bundle bundle;
    String reqType = "";
    String vEmail = "", vPhone = "";

    String phoneVerificationCode = "";
    String emailVerificationCode = "";

    TextView phonetxt;
    TextView emailTxt;


    boolean isEditInfoTapped = false;
    CountDownTimer countDnTimer;
    CountDownTimer countDnEmailTimer;

    int resendTime = 0;
    int resendSecAfter;
    int maxAllowdCount;
    int resendSecInMilliseconds;
    boolean isProcessRunning = false;
    boolean isEmailSendProcessRunning = false;

    // Edit Email Or Number
    boolean isDialogOpen = false;
    private String error_email_str = "";
    BottomSheetDialog editInfoDialog;
    boolean isCountrySelected = false;
    private String vPhoneCode;
    private String vCountryCode;
    String msg = "";
    ImageView logoutImageview;
    boolean isbackshow = true;
    boolean isrestart = false;

    boolean isonlyphoneVerified = false;
    boolean isEmailVeried = false;

    private FirebaseAuth firebaseAuth;
    String verification_Id;
    private PhoneAuthProvider.ForceResendingToken forceResendingToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks stateChangedCallbacks;
    PhoneAuthProvider phoneAuthProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_info);

        Toast.makeText(this,"Verify Act",Toast.LENGTH_SHORT).show();

        generalFunc = MyApp.getInstance().getGeneralFun(getActContext());
        userProfileJson = generalFunc.retrieveValue(Utils.USER_PROFILE_JSON);

        bundle = new Bundle();
        bundle = getIntent().getExtras();
        msg = bundle.getString("msg");

        resendSecAfter = GeneralFunctions.parseIntegerValue(30, generalFunc.getJsonValue(Utils.VERIFICATION_CODE_RESEND_TIME_IN_SECONDS_KEY, generalFunc.retrieveValue(Utils.USER_PROFILE_JSON)));
        maxAllowdCount = GeneralFunctions.parseIntegerValue(5, generalFunc.getJsonValue(Utils.VERIFICATION_CODE_RESEND_COUNT_KEY, generalFunc.retrieveValue(Utils.USER_PROFILE_JSON)));
        resendTime = GeneralFunctions.parseIntegerValue(30, generalFunc.getJsonValue(Utils.VERIFICATION_CODE_RESEND_COUNT_RESTRICTION_KEY, generalFunc.retrieveValue(Utils.USER_PROFILE_JSON)));
        resendSecInMilliseconds = resendSecAfter * 1 * 1000;


        phonetxt = ((TextView) findViewById(R.id.phoneTxt));
        emailTxt = ((TextView) findViewById(R.id.emailTxt));

        if (!getIntent().hasExtra("MOBILE")) {
            userProfileJson = generalFunc.retrieveValue(Utils.USER_PROFILE_JSON);

            vEmail = generalFunc.getJsonValue("vEmail", userProfileJson);
            vPhone = generalFunc.getJsonValue("vPhoneCode", userProfileJson) + generalFunc.getJsonValue("vPhone", userProfileJson);
        } else {
            vPhone = generalFunc.getJsonValue("vPhoneCode", userProfileJson) + "" + getIntent().getStringExtra("MOBILE");
        }

        emailView = (CardView) findViewById(R.id.emailView);
        smsView = (CardView) findViewById(R.id.smsView);

        if (msg.equalsIgnoreCase("DO_EMAIL_PHONE_VERIFY")) {
            emailView.setVisibility(View.VISIBLE);
            smsView.setVisibility(View.VISIBLE);
            reqType = "DO_EMAIL_PHONE_VERIFY";
        } else if (msg.equalsIgnoreCase("DO_EMAIL_VERIFY")) {
            emailView.setVisibility(View.VISIBLE);
            smsView.setVisibility(View.GONE);
            reqType = "DO_EMAIL_VERIFY";
        } else if (msg.equalsIgnoreCase("DO_PHONE_VERIFY")) {
            smsView.setVisibility(View.VISIBLE);
            emailView.setVisibility(View.GONE);
            reqType = "DO_PHONE_VERIFY";
        }

        okBtn = ((MaterialRippleLayout) findViewById(R.id.okBtn)).getChildView();
        resendBtn = ((MaterialRippleLayout) findViewById(R.id.resendBtn)).getChildView();
        editBtn = ((MaterialRippleLayout) findViewById(R.id.editBtn)).getChildView();
        codeBox = (MaterialEditText) findViewById(R.id.codeBox);
        emailBox = (MaterialEditText) findViewById(R.id.emailCodeBox);
        emailOkBtn = ((MaterialRippleLayout) findViewById(R.id.emailOkBtn)).getChildView();
        emailResendBtn = ((MaterialRippleLayout) findViewById(R.id.emailResendBtn)).getChildView();
        emailEditBtn = ((MaterialRippleLayout) findViewById(R.id.emailEditBtn)).getChildView();

        titleTxt = (TextView) findViewById(R.id.titleTxt);
        backImgView = (ImageView) findViewById(R.id.backImgView);
        backImgView.setOnClickListener(new setOnClickList());
        loading = (ProgressBar) findViewById(R.id.loading);

        okBtn.setId(Utils.generateViewId());
        okBtn.setOnClickListener(new setOnClickList());

        resendBtn.setId(Utils.generateViewId());
        resendBtn.setOnClickListener(new setOnClickList());

        editBtn.setId(Utils.generateViewId());
        editBtn.setOnClickListener(new setOnClickList());

        emailOkBtn.setId(Utils.generateViewId());
        emailOkBtn.setOnClickListener(new setOnClickList());

        emailResendBtn.setId(Utils.generateViewId());
        emailResendBtn.setOnClickListener(new setOnClickList());

        emailEditBtn.setId(Utils.generateViewId());
        emailEditBtn.setOnClickListener(new setOnClickList());

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.useAppLanguage();

        stateChangedCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                Log.e("dispatmsg", "ss:::" + phoneAuthCredential);

                signInWithPhoneAuthCredential(phoneAuthCredential);
            }


            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.e("dispatmsg", "777:::" + e.getMessage());
                if (e.getMessage().equalsIgnoreCase("We have blocked all requests from this device due to unusual activity. Try again later.")) {
                    final GenerateAlertBox alertBox = new GenerateAlertBox(getActContext());
                    alertBox.setContentMessage("", generalFunc.retrieveLangLBl("", "LBL_DEVICE_BLOCK_FIREBASE_TXT"));
                    alertBox.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_BTN_OK_GENERAL"));
                    alertBox.setBtnClickList(new GenerateAlertBox.HandleAlertBtnClick() {
                        @Override
                        public void handleBtnClick(int btn_id) {
                            if (btn_id == 1) {
                                alertBox.closeAlertBox();
                            }
                        }
                    });
                    alertBox.showAlertBox();
                }
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
//                    field_phone_number.setError("Invalid Phone Number");

                    Log.e("dispatmsg", "" + e.getMessage());
                    final GenerateAlertBox alertBox = new GenerateAlertBox(getActContext());
                    alertBox.setContentMessage("", generalFunc.retrieveLangLBl("", "LBL_FIREBASE_AUTH_STOP_TXT"));
                    alertBox.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_BTN_OK_GENERAL"));
                    alertBox.setBtnClickList(new GenerateAlertBox.HandleAlertBtnClick() {
                        @Override
                        public void handleBtnClick(int btn_id) {
                            if (btn_id == 1) {
                                alertBox.closeAlertBox();
                            }
                        }
                    });
                    alertBox.showAlertBox();
                } else if (e instanceof FirebaseTooManyRequestsException) {

                    final GenerateAlertBox alertBox = new GenerateAlertBox(getActContext());
                    alertBox.setContentMessage("", generalFunc.retrieveLangLBl("", "LBL_FIREBASE_QUOTA_EXCEED"));
                    alertBox.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_BTN_OK_GENERAL"));
                    alertBox.setBtnClickList(new GenerateAlertBox.HandleAlertBtnClick() {
                        @Override
                        public void handleBtnClick(int btn_id) {
                            if (btn_id == 1) {
                                alertBox.closeAlertBox();
                            }
                        }
                    });
                    alertBox.showAlertBox();
                }
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                verification_Id = verificationId;
                forceResendingToken = token;
                Log.d("dispatmsg", "----cdeSent---" + verificationId + "  --  " + forceResendingToken);

//                loading.setVisibility(View.GONE);
                /*field_verification_code.setVisibility(View.VISIBLE);
                button_verify_phone.setVisibility(View.VISIBLE);
                button_resend.setVisibility(View.VISIBLE);
                button_start_verification.setVisibility(View.GONE);*/
            }
        };
        setLabels();

        handleSendSms();


        if (generalFunc.retrieveValue(Utils.SITE_TYPE_KEY).equalsIgnoreCase("Demo")) {
            findViewById(R.id.helpOTPTxtView).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.helpOTPTxtView).setVisibility(View.GONE);
        }

        if (getIntent().getStringExtra("isbackshow") != null && getIntent().getStringExtra("isbackshow").equalsIgnoreCase("No")) {
            isbackshow = false;
        }


        isrestart = getIntent().getBooleanExtra("isrestart", false);


        if (generalFunc.isDeliverOnlyEnabled() && (isbackshow == false || isrestart)) {
            logoutImageview = (ImageView) findViewById(R.id.logoutImageview);
            logoutImageview.setVisibility(View.VISIBLE);
            logoutImageview.setOnClickListener(new setOnClickList());
            backImgView.setVisibility(View.GONE);
            backImgView.setOnClickListener(null);
        }
    }

    public void handleSendSms() {
        if (msg.equalsIgnoreCase("DO_EMAIL_PHONE_VERIFY")) {
            sendVerificationSMS("Both");
        } else if (msg.equalsIgnoreCase("DO_EMAIL_VERIFY")) {
            sendVerificationSMS("Email");
        } else if (msg.equalsIgnoreCase("DO_PHONE_VERIFY")) {
            sendVerificationSMS("Mobile");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential phoneAuthCredential) {
        firebaseAuth.signInWithCredential(phoneAuthCredential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.e("methodcall", "signInWithCredential:success" + " " + task.getResult().getUser());
                            /*FirebaseUser user = task.getResult().getUser();
                            startActivity(new Intent(VerifyInfoActivity.this, MainActivity.class));
                            finish();*/
//                            smsView.setVisibility(View.GONE);
                            reqType = "PHONE_VERIFIED";
                            handleSendSms();

                        } else {
                            Log.e("methodcall000", "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
//                                field_verification_code.setError("Invalid code.");
                                if (task.getException().getMessage().equalsIgnoreCase("")) {
                                    final GenerateAlertBox alertBox = new GenerateAlertBox(getActContext());
                                    alertBox.setContentMessage("", generalFunc.retrieveLangLBl("", "LBL_FIREBASE_CODE_EXPIRED"));
                                    alertBox.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"));
                                    alertBox.setBtnClickList(new GenerateAlertBox.HandleAlertBtnClick() {
                                        @Override
                                        public void handleBtnClick(int btn_id) {
                                            if (btn_id == 1) {
                                                alertBox.closeAlertBox();
                                            }
                                        }

                                    });
                                    alertBox.showAlertBox();
                                } else {
                                    Utils.setErrorFields(codeBox, error_verification_code);
                                }
                            }
                        }
                    }
                });
    }


    private void setLabels() {
        titleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_ACCOUNT_VERIFY_TXT"));
        ((TextView) findViewById(R.id.smsTitleTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_MOBILE_VERIFy_TXT"));
        ((TextView) findViewById(R.id.smsSubTitleTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_SMS_SENT_TO") + " ");
        ((TextView) findViewById(R.id.emailTitleTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_EMAIL_VERIFy_TXT"));
        ((TextView) findViewById(R.id.emailSubTitleTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_EMAIL_SENT_TO") + " ");
        ((TextView) findViewById(R.id.smsHelpTitleTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_SMS_SENT_NOTE"));
        ((TextView) findViewById(R.id.emailHelpTitleTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_EMAIL_SENT_NOTE"));

        ((TextView) findViewById(R.id.phoneTxt)).setText("+" + vPhone);
        ((TextView) findViewById(R.id.emailTxt)).setText(vEmail);

        okBtn.setText(generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"));
        resendBtn.setText(generalFunc.retrieveLangLBl("", "LBL_RESEND_SMS"));
        editBtn.setText(generalFunc.retrieveLangLBl("", "LBL_EDIT_MOBILE"));

        emailOkBtn.setText(generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"));
        emailResendBtn.setText(generalFunc.retrieveLangLBl("", "LBL_RESEND_EMAIL"));
        emailEditBtn.setText(generalFunc.retrieveLangLBl("", "LBL_EDIT_EMAIL"));

        error_verification_code = generalFunc.retrieveLangLBl("", "LBL_VERIFICATION_CODE_INVALID");
        required_str = generalFunc.retrieveLangLBl("", "LBL_FEILD_REQUIRD_ERROR_TXT");
        error_email_str = generalFunc.retrieveLangLBl("", "LBL_FEILD_EMAIL_ERROR_TXT");
        if (smsView.getVisibility() == View.VISIBLE) {
            startFirebaseAuthProcess();
            resendProcess("Mobile");
        }
    }

    public void sendVerificationSMS(String showTimerFor) {

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "sendVerificationSMS");
        parameters.put("iMemberId", generalFunc.getMemberId());
        parameters.put("MobileNo", vPhone);
        parameters.put("UserType", Utils.app_type);
        parameters.put("REQ_TYPE", reqType);

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(responseString -> {

            loading.setVisibility(View.GONE);

            if (responseString != null && !responseString.equals("")) {

                boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

                if (isDataAvail == true) {

                    switch (reqType) {
                        case "DO_EMAIL_PHONE_VERIFY":
                            if (!generalFunc.getJsonValue(Utils.message_str, responseString).equals("")) {
                                generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("",
                                        generalFunc.getJsonValue(Utils.message_str, responseString)));
                            } else {

                                if (!generalFunc.getJsonValue(Utils.message_str + "_email", responseString).equalsIgnoreCase("LBL_EMAIL_VERIFICATION_FAILED_TXT")) {
                                    emailVerificationCode = generalFunc.getJsonValue(Utils.message_str + "_email", responseString);
                                } else {
                                    generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("",
                                            generalFunc.getJsonValue(Utils.message_str + "_email", responseString)));
                                }
                            }
                            break;
                        case "DO_EMAIL_VERIFY":
                            emailVerificationCode = generalFunc.getJsonValue(Utils.message_str, responseString);
                            break;
                        case "DO_PHONE_VERIFY":
                            phoneVerificationCode = generalFunc.getJsonValue(Utils.message_str, responseString);
                            break;
                        case "PHONE_VERIFIED":
                            enableOrDisable(true, showTimerFor);
                            removecountDownTimer("Mobile");
                            isProcessRunning = false;

                            verifySuccessMessage(generalFunc.retrieveLangLBl("",
                                    generalFunc.getJsonValue(Utils.message_str, responseString)), true, false);

                            break;
                        case "EMAIL_VERIFIED":
                            enableOrDisable(true, showTimerFor);
                            removecountDownTimer("Email");
                            isEmailSendProcessRunning = false;

                            verifySuccessMessage(generalFunc.retrieveLangLBl("",
                                    generalFunc.getJsonValue(Utils.message_str, responseString)), false, true);
                            break;


                    }
                    String userdetails = generalFunc.getJsonValue("userDetails", responseString);
                    if (!userdetails.equals("") && userdetails != null) {
                        String messageData = generalFunc.getJsonValue(Utils.message_str, userdetails);
                        generalFunc.storeData(Utils.USER_PROFILE_JSON, messageData);
                    }
                    checkVerification(responseString, isDataAvail, showTimerFor);
                } else {
                    checkVerification(responseString, isDataAvail, showTimerFor);
//                    generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)));

                }
            } else {
                generalFunc.showError();
            }
        });
        exeWebServer.execute();
    }

    private void checkVerification(String responseString, boolean isDataAvail, String showTimerFor) {
        switch (reqType) {
            case "DO_EMAIL_PHONE_VERIFY":
                if (generalFunc.getJsonValue("eEmailFailed", responseString).equals("Yes")) {
                    enableOrDisable(true, showTimerFor);
                    removecountDownTimer("Both");
                } else if (generalFunc.getJsonValue("eEmailFailed", responseString).equals("Yes")) {
                    enableOrDisable(true, showTimerFor);
                    removecountDownTimer("Email");
                    resendProcess("Mobile");
                } else if (generalFunc.getJsonValue("eSMSFailed", responseString).equals("Yes")) {
                    enableOrDisable(true, showTimerFor);
                    removecountDownTimer("Mobile");
                    resendProcess("Email");
                } else if (isDataAvail) {
                    resendProcess(showTimerFor);
                } else if (!isDataAvail) {
                    enableOrDisable(true, showTimerFor);
                    removecountDownTimer("Both");
                }
                break;
            case "DO_EMAIL_VERIFY":
                if (generalFunc.getJsonValue("eEmailFailed", responseString).equals("Yes")) {
                    enableOrDisable(true, showTimerFor);
                    removecountDownTimer("Email");
                } else if (isDataAvail) {
                    resendProcess(showTimerFor);
                } else if (!isDataAvail) {
                    enableOrDisable(true, showTimerFor);
                    removecountDownTimer("Email");
                }
                break;

        }
    }


    private void startFirebaseAuthProcess() {
//        loading.setVisibility(View.VISIBLE);

        if (!getIntent().hasExtra("MOBILE")) {
            userProfileJson = generalFunc.retrieveValue(Utils.USER_PROFILE_JSON);

            vEmail = generalFunc.getJsonValue("vEmail", userProfileJson);
            vPhone = generalFunc.getJsonValue("vPhoneCode", userProfileJson) + "" + generalFunc.getJsonValue("vPhone", userProfileJson);
        } else {
            vPhone = generalFunc.getJsonValue("vPhoneCode", userProfileJson) + "" + getIntent().getStringExtra("MOBILE");
        }

        if (!validatePhoneNumber(vPhone)) {
            return;
        }
        Log.e("methodcall", "" + vPhone);
        startPhoneNumberVerification(vPhone);
    }

    private void startPhoneNumberVerification(String phoneNumber) {
        phoneNumber = "+" + phoneNumber;
        Log.e("methodcall", "11525::::" + phoneNumber);

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                stateChangedCallbacks);        // OnVerificationStateChangedCallbacks

    }

    private boolean validatePhoneNumber(String vPhone) {
        String phoneNumber = vPhone;
        if (TextUtils.isEmpty(phoneNumber)) {

//            pho.setError("Invalid phone number.");
//            Toast.makeText(getActContext(), "Invalid phone number", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    public void verifySuccessMessage(String message, final boolean sms, final boolean email) {
        final GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
        generateAlert.setCancelable(false);
        generateAlert.setBtnClickList(btn_id -> {
            generateAlert.closeAlertBox();
            if (TextUtils.isEmpty(generalFunc.getMemberId())) {
                if (TextUtils.isEmpty(generalFunc.getMemberId())) {
                    isProcessRunning = false;
                    processBackEvent(true);
                }
            } else {
                if (sms == true) {
                    smsView.setVisibility(View.GONE);
                    isProcessRunning = false;
                    if (emailView.getVisibility() == View.GONE) {
                        processBackEvent(false);
                    }
                } else if (email == true) {
                    emailView.setVisibility(View.GONE);
                    isProcessRunning = false;
                    if (smsView.getVisibility() == View.GONE) {
                        processBackEvent(false);
                    }
                }
            }
        });
        generateAlert.setContentMessage("", message);
        generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"));
        generateAlert.showAlertBox();
    }


    private void processBackEvent(boolean normalFlow) {
        if (isrestart) {
            MyApp.getInstance().restartWithGetDataApp();
        } else if (isbackshow == false) {
            new StartActProcess(getActContext()).setOkResult();
            finish();
        } else {
            if (normalFlow) {
                new StartActProcess(getActContext()).setOkResult();
            }
            VerifyInfoActivity.super.onBackPressed();
        }
    }


    public void resendProcess(final String showTimerFor) {

        if (!Utils.checkText(showTimerFor)) {
            enableOrDisable(true, showTimerFor);
            removecountDownTimer(showTimerFor);
            return;
        }


        enableOrDisable(false, showTimerFor);

        if (Utils.checkText(showTimerFor)) {
            setTime(generalFunc.parseLongValue(0L, String.valueOf(resendSecInMilliseconds)), showTimerFor);
            removecountDownTimer(showTimerFor);

            if (showTimerFor.equalsIgnoreCase("Email")) {
                showEmailTimer(showTimerFor);
            } else if (showTimerFor.equalsIgnoreCase("Mobile")) {
                showTimer(showTimerFor);
            } else if (showTimerFor.equalsIgnoreCase("Both")) {
                showTimer("Mobile");
                showEmailTimer("Email");
            }

        } else {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    enableOrDisable(true, showTimerFor);
                }
            }, resendSecInMilliseconds);
        }
    }

    private void setTime(long milliseconds, String showTimerFor) {
        int minutes = (int) (milliseconds / 1000) / 60;
        int seconds = (int) (milliseconds / 1000) % 60;

        if (showTimerFor.equalsIgnoreCase("Both")) {
            resendBtn.setTextColor(Color.parseColor("#FFFFFF"));
            emailResendBtn.setTextColor(Color.parseColor("#FFFFFF"));
            resendBtn.setText(String.format("%02d:%02d", minutes, seconds));
            emailResendBtn.setText(String.format("%02d:%02d", minutes, seconds));
        } else if (showTimerFor.equalsIgnoreCase("Email")) {
            emailResendBtn.setTextColor(Color.parseColor("#FFFFFF"));
            emailResendBtn.setText(String.format("%02d:%02d", minutes, seconds));
        } else if (showTimerFor.equalsIgnoreCase("Mobile")) {
            resendBtn.setTextColor(Color.parseColor("#FFFFFF"));
            resendBtn.setText(String.format("%02d:%02d", minutes, seconds));
        }

    }

    public void showTimer(String showTimerFor) {
        countDnTimer = new CountDownTimer(resendSecInMilliseconds, 1000) {
            @Override
            public void onTick(long milliseconds) {
                isProcessRunning = true;
                setTime(milliseconds, showTimerFor);
            }

            @Override
            public void onFinish() {
                isProcessRunning = false;
                // this function will be called when the timecount is finished
                enableOrDisable(true, showTimerFor);
                removecountDownTimer("Mobile");
            }
        }.start();

    }


    public void showEmailTimer(String showTimerFor) {


        countDnEmailTimer = new CountDownTimer(resendSecInMilliseconds, 1000) {
            @Override
            public void onTick(long milliseconds) {
                isEmailSendProcessRunning = true;
                setTime(milliseconds, showTimerFor);
            }

            @Override
            public void onFinish() {
                isEmailSendProcessRunning = false;
                // this function will be called when the timecount is finished
                enableOrDisable(true, showTimerFor);
                removecountDownTimer("Email");
            }
        }.start();

    }

    private void removecountDownTimer(String type) {

        if (type.equalsIgnoreCase("Mobile")) {
            if (countDnTimer != null) {
                countDnTimer.cancel();
                countDnTimer = null;
                isProcessRunning = false;
            }
        } else if (type.equalsIgnoreCase("Email")) {
            if (countDnEmailTimer != null) {
                countDnEmailTimer.cancel();
                countDnEmailTimer = null;
                isEmailSendProcessRunning = false;
            }
        } else if (type.equalsIgnoreCase("Both")) {
            if (countDnTimer != null) {
                countDnTimer.cancel();
                countDnTimer = null;
                isProcessRunning = false;
            }

            if (countDnEmailTimer != null) {
                countDnEmailTimer.cancel();
                countDnEmailTimer = null;
                isEmailSendProcessRunning = false;
            }
        }

    }

    public Context getActContext() {
        return VerifyInfoActivity.this;
    }


    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            int i = view.getId();
            Utils.hideKeyboard(VerifyInfoActivity.this);
            if (i == R.id.backImgView) {
                onBackPressed();
            } else if (i == okBtn.getId()) {
                /*boolean isCodeEntered = Utils.checkText(codeBox) ?
                        ((phoneVerificationCode.equalsIgnoreCase(Utils.getText(codeBox)) ||
                                (generalFunc.retrieveValue(Utils.SITE_TYPE_KEY).equalsIgnoreCase("Demo") && Utils.getText(codeBox).equalsIgnoreCase("12345"))) ? true
                                : Utils.setErrorFields(codeBox, error_verification_code)) : Utils.setErrorFields(codeBox, required_str);
                if (isCodeEntered) {
                    reqType = "PHONE_VERIFIED";
                    sendVerificationSMS("");
                }*/
                String code = codeBox.getText().toString();
                if (TextUtils.isEmpty(code)) {
                    codeBox.setError("Cannot Empty");
                    return;
                }
                if ((verification_Id != null && !verification_Id.equalsIgnoreCase("")) && (code != null && !code.equalsIgnoreCase(""))) {
                    verifyPhoneNumberWithCode(verification_Id, code);
                }
            } else if (i == resendBtn.getId()) {
                reqType = "DO_PHONE_VERIFY";
                resendProcess("Mobile");
                resendVerificationCode(vPhone, forceResendingToken);


            } else if (i == editBtn.getId()) {
                Bundle bn = new Bundle();
                bn.putBoolean("isEdit", true);
                bn.putBoolean("isMobile", true);
                isonlyphoneVerified = true;
                isEditInfoTapped = true;
                openEditDilaog("Mobile");
            } else if (i == emailOkBtn.getId()) {
                boolean isEmailCodeEntered = Utils.checkText(emailBox) ?
                        ((emailVerificationCode.equalsIgnoreCase(Utils.getText(emailBox)) ||
                                (generalFunc.retrieveValue(Utils.SITE_TYPE_KEY).equalsIgnoreCase("Demo") && Utils.getText(emailBox).equalsIgnoreCase("12345"))) ? true
                                : Utils.setErrorFields(emailBox, error_verification_code)) : Utils.setErrorFields(emailBox, required_str);
                if (isEmailCodeEntered) {
                    reqType = "EMAIL_VERIFIED";
                    sendVerificationSMS("");
                }
            } else if (i == emailResendBtn.getId()) {
                reqType = "DO_EMAIL_VERIFY";
                sendVerificationSMS("Email");
            } else if (i == emailEditBtn.getId()) {
                isEditInfoTapped = true;
                openEditDilaog("Email");
            } else if (i == R.id.logoutImageview) {
                MyApp.getInstance().logOutFromDevice(false);

            }
        }
    }

    private void verifyPhoneNumberWithCode(String verification_id, String code) {
        Log.e("methodcall", "00::" + verification_id + " " + code);

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verification_id, code);
        signInWithPhoneAuthCredential(credential);


    }

    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+" + phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                stateChangedCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }

    public void openEditDilaog(String type) {
        // Reset Country Selection

        isCountrySelected = false;
        vPhoneCode = "";
        vCountryCode = "";

        editInfoDialog = new BottomSheetDialog(getActContext());
        View contentView = View.inflate(getActContext(), R.layout.design_edit_phn_email_dialog, null);
        editInfoDialog.setContentView(contentView);
        BottomSheetBehavior mBehavior = BottomSheetBehavior.from((View) contentView.getParent());
        mBehavior.setPeekHeight(1500);
        View bottomSheetView = editInfoDialog.getWindow().getDecorView().findViewById(R.id.design_bottom_sheet);
        BottomSheetBehavior.from(bottomSheetView).setHideable(false);
        setCancelable(editInfoDialog, false);

        TextView titleTxt, hintTxt, errorTxt, updateEmailTxt, updateMobileTxt, cancelTxt;
        LinearLayout updateEmailArea, updateMobileArea;
        ImageView iv_img_icon;
        EditText mobileBox, countryBox, emailBox;

        titleTxt = (TextView) editInfoDialog.findViewById(R.id.titleTxt);
        hintTxt = (TextView) editInfoDialog.findViewById(R.id.hintTxt);
        errorTxt = (TextView) editInfoDialog.findViewById(R.id.errorTxt);
        iv_img_icon = (ImageView) editInfoDialog.findViewById(R.id.iv_img_icon);
        updateEmailTxt = (TextView) editInfoDialog.findViewById(R.id.updateEmailTxt);
        cancelTxt = (TextView) editInfoDialog.findViewById(R.id.cancelTxt);
        updateMobileTxt = (TextView) editInfoDialog.findViewById(R.id.updateMobileTxt);
        emailBox = (EditText) editInfoDialog.findViewById(R.id.emailBox);
        mobileBox = (EditText) editInfoDialog.findViewById(R.id.mobileBox);
        countryBox = (EditText) editInfoDialog.findViewById(R.id.countryBox);
        updateEmailArea = (LinearLayout) editInfoDialog.findViewById(R.id.updateEmailArea);
        updateMobileArea = (LinearLayout) editInfoDialog.findViewById(R.id.updateMobileArea);


        String text = type.equalsIgnoreCase("Email") ? generalFunc.retrieveLangLBl("", "LBL_EMAIL_LBL_TXT") : generalFunc.retrieveLangLBl("", "LBL_MOBILE_NUMBER_HEADER_TXT");
        titleTxt.setText(text);

        String hintText = type.equalsIgnoreCase("Email") ? generalFunc.retrieveLangLBl("To update your existing email id, please enter new email id below.", "LBL_EMAIL_EDIT_NOTE") : generalFunc.retrieveLangLBl("To update your existing mobile number, please enter new mobile number below.", "LBL_MOBILE_EDIT_NOTE");
        hintTxt.setText(hintText);

        int icon = type.equalsIgnoreCase("Email") ? R.mipmap.ic_verify_email : R.mipmap.ic_mobile;
        iv_img_icon.setImageResource(icon);


        cancelTxt.setText(generalFunc.retrieveLangLBl("Cancel", "LBL_CANCEL_TXT"));

        updateEmailArea.setVisibility(View.GONE);
        updateMobileArea.setVisibility(View.GONE);
        errorTxt.setVisibility(View.GONE);

        if (type.equalsIgnoreCase("Email")) {
            updateEmailArea.setVisibility(View.VISIBLE);
            updateMobileArea.setVisibility(View.GONE);
        } else if (type.equalsIgnoreCase("Mobile")) {
            updateEmailArea.setVisibility(View.GONE);
            updateMobileArea.setVisibility(View.VISIBLE);
        }

        //set KeyPad
        mobileBox.setInputType(InputType.TYPE_CLASS_NUMBER);
        emailBox.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS | InputType.TYPE_CLASS_TEXT);
        mobileBox.setImeOptions(EditorInfo.IME_ACTION_DONE);

        // Set Existing Details
        String vPhoneCodeStr = generalFunc.getJsonValue("vPhoneCode", userProfileJson);
        if (Utils.checkText(vPhoneCodeStr)) {
            countryBox.setText("+" + vPhoneCodeStr);
            isCountrySelected = true;
            vPhoneCode = vPhoneCodeStr;
            vCountryCode = generalFunc.getJsonValue("vCountry", userProfileJson);
        }
        Utils.removeInput(countryBox);

        countryBox.setOnClickListener(v -> new StartActProcess(getActContext()).startActForResult(SelectCountryActivity.class, Utils.SELECT_COUNTRY_REQ_CODE));

        countryBox.setOnTouchListener((v, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_UP && !countryBox.hasFocus()) {
                countryBox.performClick();
            }
            return true;
        });


        updateEmailTxt.setOnClickListener(view -> {

            // Hide KeyBoard
            Utils.hideKeyPad(VerifyInfoActivity.this);

            if (type.equalsIgnoreCase("Email")) {

                boolean emailEntered = Utils.checkText(emailBox) ?
                        (generalFunc.isEmailValid(Utils.getText(emailBox)) ? true : false)
                        : false;


                if (!emailEntered && !Utils.checkText(emailBox)) {
                    errorTxt.setText(Utils.checkText(required_str) ? StringUtils.capitalize(required_str.toLowerCase().trim()) : required_str);
                } else if (!emailEntered && Utils.checkText(emailBox) && !generalFunc.isEmailValid(Utils.getText(emailBox))) {
                    errorTxt.setText(Utils.checkText(error_email_str) ? StringUtils.capitalize(error_email_str.toLowerCase().trim()) : error_email_str);
                }


                if (emailEntered == false) {

                    errorTxt.setVisibility(View.VISIBLE);
                    return;
                }
                errorTxt.setVisibility(View.GONE);

                if (Utils.getText(emailBox).trim().equalsIgnoreCase(generalFunc.getJsonValue("vEmail", userProfileJson).trim())) {
                    editInfoDialog.dismiss();
                    return;
                }
                updateProfile(type, Utils.getText(emailBox), "", vCountryCode, vPhoneCode);

            }
        });


        updateMobileTxt.setOnClickListener(view -> {

            // Hide KeyBoard
            Utils.hideKeyPad(VerifyInfoActivity.this);

            boolean mobileEntered = Utils.checkText(mobileBox) ? true : false;
            boolean countryEntered = isCountrySelected ? true : false;

            if (!mobileEntered || countryEntered) {

                errorTxt.setText(Utils.checkText(required_str) ? StringUtils.capitalize(required_str.toLowerCase().trim()).toLowerCase() : required_str);
            } else if (mobileEntered && (mobileBox.length() < 3)) {
                errorTxt.setText(Utils.checkText(generalFunc.retrieveLangLBl("", "LBL_INVALID_MOBILE_NO")) ? StringUtils.capitalize(generalFunc.retrieveLangLBl("", "LBL_INVALID_MOBILE_NO").toLowerCase().trim()).toLowerCase() : generalFunc.retrieveLangLBl("", "LBL_INVALID_MOBILE_NO"));
            }

            if (mobileEntered == false || countryEntered == false) {
                errorTxt.setVisibility(View.VISIBLE);
                return;
            }

            errorTxt.setVisibility(View.GONE);

            String currentMobileNum = generalFunc.getJsonValue("vPhone", userProfileJson);
            String currentPhoneCode = generalFunc.getJsonValue("vPhoneCode", userProfileJson);

            if (!currentPhoneCode.equals(vPhoneCode) || !currentMobileNum.equals(Utils.getText(mobileBox))) {
                updateProfile(type, "", Utils.getText(mobileBox), vCountryCode, vPhoneCode);
                return;
            }

            editInfoDialog.dismiss();


        });

        cancelTxt.setOnClickListener(view -> editInfoDialog.dismiss());

        editInfoDialog.setOnDismissListener(dialogInterface -> isDialogOpen = false);
        isDialogOpen = true;
        editInfoDialog.show();

    }


    public void setCancelable(Dialog dialogview, boolean cancelable) {
        final Dialog dialog = dialogview;
        View touchOutsideView = dialog.getWindow().getDecorView().findViewById(R.id.touch_outside);
        View bottomSheetView = dialog.getWindow().getDecorView().findViewById(R.id.design_bottom_sheet);

        if (cancelable) {
            touchOutsideView.setOnClickListener(v -> {
                if (dialog.isShowing()) {
                    dialog.cancel();
                }
            });
            BottomSheetBehavior.from(bottomSheetView).setHideable(true);
        } else {
            touchOutsideView.setOnClickListener(null);
            BottomSheetBehavior.from(bottomSheetView).setHideable(false);
        }
    }


    public void updateProfile(String type, String email, String mobile, String countryCode, String vPhoneCode) {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "updateUserProfileDetail");
        parameters.put("iMemberId", generalFunc.getMemberId());
        parameters.put("vName", generalFunc.getJsonValue("vName", userProfileJson));
        parameters.put("vLastName", generalFunc.getJsonValue("vLastName", userProfileJson));
        parameters.put("vPhone", type.equalsIgnoreCase("Mobile") ? mobile : generalFunc.getJsonValue("vPhone", userProfileJson));
        parameters.put("vPhoneCode", type.equalsIgnoreCase("Mobile") ? vPhoneCode : generalFunc.getJsonValue("vPhoneCode", userProfileJson));
        parameters.put("vCountry", type.equalsIgnoreCase("Mobile") ? countryCode : generalFunc.getJsonValue("vCountry", userProfileJson));
        parameters.put("vEmail", type.equalsIgnoreCase("Email") ? email : generalFunc.getJsonValue("vEmail", userProfileJson));
        parameters.put("CurrencyCode", generalFunc.getJsonValue("vCurrencyPassenger", userProfileJson));
        parameters.put("LanguageCode", generalFunc.getJsonValue("vLang", userProfileJson));
        parameters.put("UserType", Utils.app_type);

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(responseString -> {

            if (responseString != null && !responseString.equals("")) {

                boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

                if (isDataAvail == true) {

                    String messgeJson = generalFunc.getJsonValue(Utils.message_str, responseString);
                    generalFunc.storeData(Utils.USER_PROFILE_JSON, messgeJson);
                    responseString = generalFunc.retrieveValue(Utils.USER_PROFILE_JSON);


                    new SetUserData(responseString, generalFunc, getActContext(), false);
                    userProfileJson = generalFunc.retrieveValue(Utils.USER_PROFILE_JSON);


                    vEmail = generalFunc.getJsonValue("vEmail", userProfileJson);
                    vPhone = generalFunc.getJsonValue("vPhoneCode", userProfileJson) + generalFunc.getJsonValue("vPhone", userProfileJson);


                    ((TextView) findViewById(R.id.phoneTxt)).setText("+" + vPhone);
                    ((TextView) findViewById(R.id.emailTxt)).setText(vEmail);


                    String ePhoneVerified = generalFunc.getJsonValue("ePhoneVerified", userProfileJson);
                    String eEmailVerified = generalFunc.getJsonValue("eEmailVerified", userProfileJson);


                    enableOrDisable(true, type);
                    removecountDownTimer(type);

                    if (type.equalsIgnoreCase("Mobile") && !ePhoneVerified.equalsIgnoreCase("Yes")) {
                        reqType = "DO_PHONE_VERIFY";
                        phoneVerificationCode = "";
                    } else if (type.equalsIgnoreCase("Email") && !eEmailVerified.equalsIgnoreCase("Yes")) {
                        reqType = "DO_EMAIL_VERIFY";
                        emailVerificationCode = "";
                    }

                    editInfoDialog.dismiss();
                    sendVerificationSMS(type);


                } else {
                    generalFunc.showGeneralMessage("",
                            generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)));
                }
            } else {
                generalFunc.showError();
            }
        });
        exeWebServer.execute();
    }

    public void enableOrDisable(boolean activate, String showTimerFor) {

        if (Utils.checkText(showTimerFor) && showTimerFor.equalsIgnoreCase("Both")) {
            setButtonEnabled(resendBtn, activate);
            setButtonEnabled(emailResendBtn, activate);
        } else if (Utils.checkText(showTimerFor) && showTimerFor.equalsIgnoreCase("Email")) {
            setButtonEnabled(emailResendBtn, activate);
        } else if (Utils.checkText(showTimerFor) && showTimerFor.equalsIgnoreCase("Mobile")) {
            setButtonEnabled(resendBtn, activate);
        } else if (!Utils.checkText(showTimerFor)) {
            setButtonEnabled(resendBtn, activate);
            setButtonEnabled(emailResendBtn, activate);
        }

        if (activate && Utils.checkText(showTimerFor)) {
            if (Utils.checkText(showTimerFor) && showTimerFor.equalsIgnoreCase("Both")) {
                resendBtn.setText(generalFunc.retrieveLangLBl("", "LBL_RESEND_SMS"));
                emailResendBtn.setText(generalFunc.retrieveLangLBl("", "LBL_RESEND_EMAIL"));
            } else if (Utils.checkText(showTimerFor) && showTimerFor.equalsIgnoreCase("Email")) {
                emailResendBtn.setText(generalFunc.retrieveLangLBl("", "LBL_RESEND_EMAIL"));
            } else if (Utils.checkText(showTimerFor) && showTimerFor.equalsIgnoreCase("Mobile")) {
                resendBtn.setText(generalFunc.retrieveLangLBl("", "LBL_RESEND_SMS"));

            } else if (!Utils.checkText(showTimerFor)) {
                resendBtn.setText(generalFunc.retrieveLangLBl("", "LBL_RESEND_SMS"));
                emailResendBtn.setText(generalFunc.retrieveLangLBl("", "LBL_RESEND_EMAIL"));
            }

        }
    }

    private void setButtonEnabled(MButton btn, boolean setEnable) {
        btn.setFocusableInTouchMode(setEnable);
        btn.setFocusable(setEnable);
        btn.setEnabled(setEnable);
        btn.setOnClickListener(setEnable ? new setOnClickList() : null);
        btn.setTextColor(setEnable ? Color.parseColor("#FFFFFF") : Color.parseColor("#BABABA"));
        btn.setClickable(setEnable);
    }

    @Override
    public void onBackPressed() {
        removecountDownTimer("Both");
        if (generalFunc.isDeliverOnlyEnabled() && (isbackshow == false || isrestart)) {

        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        removecountDownTimer("Both");
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Utils.SELECT_COUNTRY_REQ_CODE && resultCode == Activity.RESULT_OK && data != null) {
            if (editInfoDialog != null) {

                vCountryCode = data.getStringExtra("vCountryCode");
                vPhoneCode = data.getStringExtra("vPhoneCode");
                isCountrySelected = true;

                ((EditText) editInfoDialog.findViewById(R.id.countryBox)).setText("+" + vPhoneCode);
            }
        }
    }
}
