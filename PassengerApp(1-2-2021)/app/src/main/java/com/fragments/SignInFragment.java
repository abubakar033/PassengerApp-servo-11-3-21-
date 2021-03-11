package com.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputLayout;
import androidx.fragment.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.servo.user.AppLoignRegisterActivity;
import com.servo.user.ContactUsActivity;
import com.servo.user.ForgotPasswordActivity;
import com.servo.user.R;
import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.general.files.OpenMainProfile;
import com.general.files.SetUserData;
import com.general.files.StartActProcess;
import com.utils.Utils;
import com.GenerateAlertBox;

import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class SignInFragment extends Fragment {


    EditText emailBox;
    EditText passwordBox;
    RelativeLayout btn_type2;

    TextInputLayout emailBoxTIP;
    TextInputLayout passwordBoxTIP;


    TextView reg_btn_text;

    AppLoignRegisterActivity appLoginAct;
    GeneralFunctions generalFunc;

    int submitBtnId;
    TextView forgetPassTxt;

    View view;

    String required_str = "";
    String error_email_str = "";

    TextView registerTxt;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sign_in, container, false);

        appLoginAct = (AppLoignRegisterActivity) getActivity();
        generalFunc = appLoginAct.generalFunc;


        emailBox =  view.findViewById(R.id.emailBox);
      //  emailBox.setHelperTextAlwaysShown(true);

        passwordBox =  view.findViewById(R.id.passwordBox);
        emailBoxTIP =  view.findViewById(R.id.emailBoxTIP);
        passwordBoxTIP =  view.findViewById(R.id.passwordBoxTIP);
        btn_type2 = view.findViewById(R.id.btn_type2);
        reg_btn_text = view.findViewById(R.id.reg_btn_text);
        forgetPassTxt = (TextView) view.findViewById(R.id.forgetPassTxt);

        registerTxt = (TextView) view.findViewById(R.id.registerTxt);
        registerTxt.setOnClickListener(new setOnClickList());


        passwordBox.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        passwordBox.setTypeface(generalFunc.getDefaultFont(getActContext()));


        emailBox.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS | InputType.TYPE_CLASS_TEXT);

        emailBox.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        passwordBox.setImeOptions(EditorInfo.IME_ACTION_DONE);

        submitBtnId = Utils.generateViewId();
        btn_type2.setId(submitBtnId);

        btn_type2.setOnClickListener(new setOnClickList());
        forgetPassTxt.setOnClickListener(new setOnClickList());

        setLabels();


        return view;
    }


    public void setLabels() {

        emailBoxTIP.setHint(generalFunc.retrieveLangLBl("", "LBL_PHONE_EMAIL"));
        emailBoxTIP.setHelperText(generalFunc.retrieveLangLBl("", "LBL_SIGN_IN_MOBILE_EMAIL_HELPER"));
        passwordBoxTIP.setHint(generalFunc.retrieveLangLBl("", "LBL_PASSWORD_LBL_TXT"));
        registerTxt.setText(generalFunc.retrieveLangLBl("", "LBL_DONT_HAVE_AN_ACCOUNT"));

        forgetPassTxt.setText(generalFunc.retrieveLangLBl("", "LBL_FORGET_PASS_TXT"));
        reg_btn_text.setText(generalFunc.retrieveLangLBl("", "LBL_LOGIN"));

        required_str = generalFunc.retrieveLangLBl("", "LBL_FEILD_REQUIRD_ERROR_TXT");
        error_email_str = generalFunc.retrieveLangLBl("", "LBL_FEILD_EMAIL_ERROR_TXT");
    }

    public void checkValues() {
        Utils.hideKeyboard(getActContext());
        String noWhiteSpace = generalFunc.retrieveLangLBl("Password should not contain whitespace.", "LBL_ERROR_NO_SPACE_IN_PASS");
        String pass_length = generalFunc.retrieveLangLBl("Password must be", "LBL_ERROR_PASS_LENGTH_PREFIX")
                + " " + Utils.minPasswordLength + " " + generalFunc.retrieveLangLBl("or more character long.", "LBL_ERROR_PASS_LENGTH_SUFFIX");


        boolean emailEntered = Utils.checkText(emailBox.getText().toString().replace("+", "")) ? true
                : Utils.setErrorFields(emailBox, required_str);

        boolean passwordEntered = Utils.checkText(passwordBox) ?
                (Utils.getText(passwordBox).contains(" ") ? Utils.setErrorFields(passwordBox, noWhiteSpace)
                        : (Utils.getText(passwordBox).length() >= Utils.minPasswordLength ? true : Utils.setErrorFields(passwordBox, pass_length)))
                : Utils.setErrorFields(passwordBox, required_str);


        String regexStr = "^[0-9]*$";

        if (emailBox.getText().toString().trim().replace("+", "").matches(regexStr)) {
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

    public void signInUser() {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "signIn");
        parameters.put("vEmail", Utils.getText(emailBox));
        parameters.put("vPassword", Utils.getText(passwordBox));
        parameters.put("vDeviceType", Utils.deviceType);
        parameters.put("UserType", Utils.app_type);
        parameters.put("vCurrency", generalFunc.retrieveValue(Utils.DEFAULT_CURRENCY_VALUE));
        parameters.put("vLang", generalFunc.retrieveValue(Utils.LANGUAGE_CODE_KEY));

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setIsDeviceTokenGenerate(true, "vDeviceToken", generalFunc);
        exeWebServer.setDataResponseListener(responseString -> {

            btn_type2.setEnabled(true);
            if (responseString != null && !responseString.equals("")) {

                boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

                if (isDataAvail == true) {
                    new SetUserData(responseString, generalFunc, getActContext(), true);
                    appLoginAct.manageSinchClient();
                    generalFunc.storeData(Utils.USER_PROFILE_JSON, generalFunc.getJsonValue(Utils.message_str, responseString));
                    new OpenMainProfile(getActContext(),
                            generalFunc.getJsonValue(Utils.message_str, responseString), false, generalFunc).startProcess();

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
        });
        exeWebServer.execute();
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
        return appLoginAct.getActContext();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Utils.hideKeyboard(getActivity());
    }

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            int i = view.getId();
            Utils.hideKeyboard(getActivity());
            if (i == submitBtnId) {
                checkValues();
            } else if (i == forgetPassTxt.getId()) {
//                String link = generalFunc.retrieveValue(Utils.LINK_FORGET_PASS_KEY);
//                new StartActProcess(appLoginAct.getActContext()).openURL(link);

                new StartActProcess(getActContext()).startAct(ForgotPasswordActivity.class);
            } else if (i == registerTxt.getId()) {
                appLoginAct.titleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_SIGN_UP"));
                appLoginAct.hadnleFragment(new SignUpFragment());
                appLoginAct.signheaderHint.setText(generalFunc.retrieveLangLBl("", "LBL_SIGN_UP_WITH_SOC_ACC"));
            }

        }
    }
}
