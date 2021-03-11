package com.servo.user.deliverAll;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Toast;

import com.JResponseClasses.PaymentMethodInterface;
import com.adapter.PaymentRecyclerAdapter;
import com.servo.user.AccountverificationActivity;
import com.servo.user.AddAddressActivity;
import com.servo.user.CardPaymentActivity;
import com.servo.user.ContactUsActivity;
import com.servo.user.CouponActivity;
import com.servo.user.ListAddressActivity;
import com.servo.user.MyWalletActivity;
import com.servo.user.PrescriptionActivity;
import com.servo.user.QuickPaymentActivity;
import com.servo.user.R;
import com.servo.user.VerifyInfoActivity;
import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.general.files.InternetConnection;
import com.general.files.MyApp;
import com.general.files.StartActProcess;
import com.realmModel.Cart;
import com.realmModel.Options;
import com.realmModel.Topping;
import com.utils.CommonUtilities;
import com.utils.Logger;
import com.utils.Utils;
import com.view.ErrorView;
import com.GenerateAlertBox;
import com.view.MButton;
import android.widget.TextView;
import com.view.MaterialRippleLayout;
import com.MyProgressDialog;
import com.view.editBox.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import io.realm.Realm;
import io.realm.RealmResults;

public class CheckOutActivity extends AppCompatActivity implements PaymentMethodInterface {

    private static final int RC_SIGN_IN_UP = 007;
    private static final int ADD_ADDRESS = 006;
    private static final int SEL_ADDRESS = 005;
    private static final int SEL_CARD = 004;
    ImageView backImgView;
    TextView titleTxt;
    GeneralFunctions generalFunc;
    MButton btn_type2, btn_clearcart;
    int submitBtnId;
    TextView cartSummaryTxtView;
    TextView editOrderTxtView;
    double finalTotal = 0;
    RealmResults<Cart> realmCartList;
    String retursantAddresss = "";
    ArrayList<Cart> cartList;
    LinearLayout itemContainer;
    TextView restaurantNameTxtView;
    String currencySymbol = "";
    TextView subtotalHTxt, subtotalVTxt;
    TextView applyCouponHTxt;
    TextView appliedPromoHTxtView;
    ImageView couponCodeImgView;
    View couponCodeArea;
    boolean isLogin = false;
    TextView noLoginNoteTxt;
    TextView deliveryAddressHtxt;
    TextView changeAddresssTxt;
    LinearLayout instantPaymentLL, useExistingCardLL;
    TextView addressTxt;
    View delveryAddressArea;
    String userProfileJson;
    TextView paymentModeTitle;

    String selAddress = "";
    String selLatitude = "";
    String selLongitude = "";
    String selAddressId = "";

    MaterialEditText deliveryInstructionBox;

    RadioGroup paymentRadioGroup, deliveryRadioGroup;
    RadioButton cardRadio, cashRadio;
    RadioButton delivryradio, take_awayRadio;
    TextView cardNumTxt;
    boolean isDialogOpen = false;
    LinearLayout promocodeArea;
    TextView promocodeappliedHTxt;
    TextView promocodeappliedVTxt;
    String ePaymentOption = "Cash";
    ErrorView errorView;
    InternetConnection internetConnection;
    ScrollView contentArea;
    LinearLayout topArea, bottomArea;
    String iOrderId = "";
    LinearLayout farecontainer;
    String restaurantstatus = "";
    String ToTalAddress = "0";
    boolean isselectaddress = false;
    int totalqtycnt = 0;
    LinearLayout maxItemarea, btn_type2area;
    TextView maxitemTitleTxtView, maxitemmsgTxtView;
    TextView casenote;
    ArrayList<HashMap<String, String>> itemArraylist = new ArrayList<HashMap<String, String>>();
    AppCompatCheckBox checkboxWallet;
    String isWalletSelect = "No";
    TextView walletAmountTxt;
    boolean isFromEditCard = false;
    boolean iswalletZero = false;
    String selectedMethod = ""; // "Instant","Manual"
    ImageView iv_edit_card, instant_arrow_right, manual_arrow_right;
    LinearLayout manual_arrow_area, editcard_area;
    TextView cardTitleTxt, cardDescTxt, existingCardTxt, instantPaymentTxt, delivery_type_text;
    private String appliedPromoCode = "";
    private boolean isCardValidated;
    public String eWalletIgnore = "No";
    String SITE_TYPE = "";

    private String SYSTEM_PAYMENT_FLOW = "";
    private String APP_PAYMENT_MODE = "";
    private String APP_PAYMENT_METHOD = "";
    boolean isCODAllow = true;
    ImageView rightImgView;
    RecyclerView paymentTypeRecycler;
    String paymentId = "", paymentType = "";
    String deliveryTypeText = "";
    String chrgisWithDevivery = "", takeAwayChargis = "";
    boolean isTakeAway = false;
    boolean isChecked = false;

    String DisplayUserWalletDebitAmount = "";
    LinearLayout WalletLay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);
        initViews();
        getLocalData();
        internetConnection = new InternetConnection(getActContext());
        if (internetConnection.isNetworkConnected()) {
            CheckOutOrderEstimateDetails("", false);
        } else {
            generateErrorView();
        }

        deliveryRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                int radioButtonID = deliveryRadioGroup.getCheckedRadioButtonId();

                RadioButton radioButton = (RadioButton) deliveryRadioGroup.findViewById(radioButtonID);


                // deliveryTypeText = (String) radioButton.getText();

                if (((String) radioButton.getText()).equalsIgnoreCase((generalFunc.retrieveLangLBl("take away", "LBL_ORDER_ORDER_TAKEAWAY")))) {


                    deliveryTypeText = "take away";

                    farecontainer.setVisibility(View.GONE);
                    deliveryAddressHtxt.setText(generalFunc.retrieveLangLBl("Store Address", "LBL_STORE_ADDRESS"));

                    addressTxt.setText(retursantAddresss);
                    subtotalHTxt.setText(generalFunc.retrieveLangLBl("Total amount", "LBL_TOTAL_AMOUNT"));
                    isTakeAway = true;
                    btn_type2.setText(generalFunc.retrieveLangLBl("Confirm Order", "LBL_CONFIRM_ORDER_TEXT"));
                    if (isChecked) {
                        walletAmountTxt.setText(takeAwayChargis);
                    }
                    WalletLay.setVisibility(View.GONE);
                    checkboxWallet.setSelected(false);
                } else {
                    deliveryAddressHtxt.setText(generalFunc.retrieveLangLBl("", "LBL_DELIVERY_ADDRESS"));

                    addressTxt.setText(generalFunc.retrieveValue(Utils.SELECT_ADDRESSS));
                    subtotalHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_SUB_TOTAL"));

                    farecontainer.setVisibility(View.VISIBLE);

                    deliveryTypeText = "Delivery";
                    isTakeAway = false;
                    btn_type2.setText(generalFunc.retrieveLangLBl("Confirm Order", "LBL_CONFIRM_ORDER_TEXT"));
                    walletAmountTxt.setText(chrgisWithDevivery);
                    WalletLay.setVisibility(View.VISIBLE);
                }

                //  Toast.makeText(CheckOutActivity.this, ""+ deliveryTypeText, Toast.LENGTH_SHORT).show();


            }
        });
        setpaymentRecyclerAdapter();
    }


    //
    // fixme abubakar checkout activity
    public MyProgressDialog showLoader() {
        MyProgressDialog myPDialog = new MyProgressDialog(getActContext(), false, generalFunc.retrieveLangLBl("Loading", "LBL_LOADING_TXT"));
        myPDialog.show();

        return myPDialog;
    }


    public void getLocalData() {
        try {
            finalTotal = 0;
            realmCartList = getCartData();
            if (realmCartList.size() > 0) {
                cartList = new ArrayList<>(realmCartList);
                setData();
            }
        } catch (Exception e) {
        }


    }


    public RealmResults<Cart> getCartData() {
        Realm realm = MyApp.getRealmInstance();
        return realm.where(Cart.class).findAll();
    }


    public void setData() {

        restaurantNameTxtView.setText(cartList.get(0).getvCompany());

        Log.d("restaurantNameTxtView", "" + cartList.get(0).getvCompany());

        //addItemView();
    }

    public void addItemView() {
        totalqtycnt = 0;
        for (int i = 0; i < itemArraylist.size(); i++) {

            int pos = i;

            LayoutInflater topinginflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View editCartView = topinginflater.inflate(R.layout.item_checkout_row, null);
            TextView itemNameTxtView = (TextView) editCartView.findViewById(R.id.itemNameTxtView);
            TextView itemMenuNameTxtView = (TextView) editCartView.findViewById(R.id.itemMenuNameTxtView);
            TextView itemPriceTxtView = (TextView) editCartView.findViewById(R.id.itemPriceTxtView);
            TextView itemstrikePriceTxtView = (TextView) editCartView.findViewById(R.id.itemstrikePriceTxtView);
            TextView QTYNumberTxtView = (TextView) editCartView.findViewById(R.id.QTYNumberTxtView);
            View itemview = (View) editCartView.findViewById(R.id.itemview);

            HashMap<String, String> itemData = itemArraylist.get(i);
            String fOfferAmt = itemData.get("fOfferAmt");
            double fOfferAmtVal = generalFunc.parseDoubleValue(0, fOfferAmt != null ? fOfferAmt : "");

            if (fOfferAmtVal > 0) {
                itemPriceTxtView.setText(currencySymbol + generalFunc.convertNumberWithRTL(itemData.get("fPrice")));

                itemstrikePriceTxtView.setVisibility(View.VISIBLE);
                itemstrikePriceTxtView.setPaintFlags(itemstrikePriceTxtView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                itemstrikePriceTxtView.setText(currencySymbol + generalFunc.convertNumberWithRTL(itemData.get("fOriginalPrice")));
            } else {
                itemstrikePriceTxtView.setVisibility(View.GONE);
                itemPriceTxtView.setText(currencySymbol + generalFunc.convertNumberWithRTL(itemData.get("fPrice")));
            }

            itemNameTxtView.setText(itemData.get("vItemType"));

            String optionaddonname = itemData.get("optionaddonname");

            if (optionaddonname != null && !optionaddonname.equalsIgnoreCase("")) {
                itemMenuNameTxtView.setText("(" + optionaddonname + ")");

                itemMenuNameTxtView.setVisibility(View.VISIBLE);
            } else {
                itemMenuNameTxtView.setVisibility(View.GONE);
            }
            QTYNumberTxtView.setText("x" + generalFunc.convertNumberWithRTL(itemData.get("iQty")));
            totalqtycnt = totalqtycnt + GeneralFunctions.parseIntegerValue(0, cartList.get(i).getQty());

            if (cartList.size() == 1) {
                itemview.setVisibility(View.GONE);
            } else if (i == cartList.size() - 1) {
                itemview.setVisibility(View.GONE);
            } else {
                itemview.setVisibility(View.VISIBLE);
            }


            itemContainer.addView(editCartView);
        }

        //subtotalVTxt.setText(currencySymbol + finalTotal + "");

        manageMaxQtyArea();
        setButtonTxt();
    }

    public void manageMaxQtyArea() {

        int maxqty = GeneralFunctions.parseIntegerValue(0, generalFunc.retrieveValue(Utils.COMPANY_MAX_QTY));
        if (maxqty != 0) {
            if (totalqtycnt > maxqty) {
                maxItemarea.setVisibility(View.VISIBLE);
                btn_type2area.setVisibility(View.GONE);
            } else {
                maxItemarea.setVisibility(View.GONE);
                btn_type2area.setVisibility(View.VISIBLE);
            }
        }
    }

    public void setButtonTxt() {
        if (generalFunc.getMemberId() != null && !generalFunc.getMemberId().equalsIgnoreCase("")) {
            btn_type2.setText(generalFunc.retrieveLangLBl("Confirm Order", "LBL_CONFIRM_ORDER_TEXT"));

            //  btn_type2.setText(generalFunc.retrieveLangLBl("", "LBL_PAY") + " " + generalFunc.convertNumberWithRTL(currencySymbol + finalTotal + ""));
        }
    }

    public String getOptionPrice(String id) {
        String optionPrice = "";
        Realm realm = MyApp.getRealmInstance();
        Options options = realm.where(Options.class).equalTo("iOptionId", id).findFirst();
        if (options != null) {
            return options.getfUserPrice();
        }
        return optionPrice;
    }

    public String getToppingPrice(String id) {
        String optionPrice = "";

        Realm realm = MyApp.getRealmInstance();
        Topping options = realm.where(Topping.class).equalTo("iOptionId", id).findFirst();

        if (options != null) {
            return options.getfUserPrice();
        }


        return optionPrice;

    }


    public Context getActContext() {
        return CheckOutActivity.this;
    }

    public void initViews() {
        generalFunc = MyApp.getInstance().getGeneralFun(getActContext());
        getUserProfileJson();
        SITE_TYPE = generalFunc.getJsonValue("SITE_TYPE", userProfileJson);

        if (getIntent().getBooleanExtra("isPrescription", false)) {
            rightImgView = (ImageView) findViewById(R.id.rightImgView);
            rightImgView.setImageDrawable(getActContext().getResources().getDrawable(R.drawable.ic_medical_prescription));
            rightImgView.setVisibility(View.VISIBLE);
            rightImgView.setOnClickListener(new setOnClickList());

        }


//        SYSTEM_PAYMENT_FLOW="Method-1";
//        APP_PAYMENT_MODE="Card";

        paymentTypeRecycler = findViewById(R.id.paymentTypeRecycler);

        errorView = (ErrorView) findViewById(R.id.errorView);
        deliveryInstructionBox = (MaterialEditText) findViewById(R.id.deliveryInstructionBox);
        checkboxWallet = (AppCompatCheckBox) findViewById(R.id.checkboxWallet);
        walletAmountTxt = (TextView) findViewById(R.id.walletAmountTxt);
        contentArea = (ScrollView) findViewById(R.id.contentArea);
        topArea = (LinearLayout) findViewById(R.id.topArea);
        bottomArea = (LinearLayout) findViewById(R.id.bottomArea);
        titleTxt = (TextView) findViewById(R.id.titleTxt);
        promocodeArea = (LinearLayout) findViewById(R.id.promocodeArea);
        promocodeappliedHTxt = (TextView) findViewById(R.id.promocodeappliedHTxt);
        promocodeappliedVTxt = (TextView) findViewById(R.id.promocodeappliedVTxt);
        appliedPromoHTxtView = (TextView) findViewById(R.id.appliedPromoHTxtView);
        delivery_type_text = (TextView) findViewById(R.id.delivery_type_text);
        appliedPromoHTxtView.setText(generalFunc.retrieveLangLBl("", "LBL_APPLIED_COUPON_CODE"));


        backImgView = (ImageView) findViewById(R.id.backImgView);
        cartSummaryTxtView = (TextView) findViewById(R.id.cartSummaryTxtView);
        editOrderTxtView = (TextView) findViewById(R.id.editOrderTxtView);
        editOrderTxtView.setOnClickListener(new setOnClickList());
        btn_type2 = ((MaterialRippleLayout) findViewById(R.id.btn_type2)).getChildView();
        btn_clearcart = ((MaterialRippleLayout) findViewById(R.id.btn_clearcart)).getChildView();
        itemContainer = (LinearLayout) findViewById(R.id.itemContainer);
        restaurantNameTxtView = (TextView) findViewById(R.id.restaurantNameTxtView);
        subtotalVTxt = (TextView) findViewById(R.id.subtotalVTxt);
        subtotalHTxt = (TextView) findViewById(R.id.subtotalHTxt);
        applyCouponHTxt = (TextView) findViewById(R.id.applyCouponHTxt);
        couponCodeArea = findViewById(R.id.couponCodeArea);
        noLoginNoteTxt = (TextView) findViewById(R.id.noLoginNoteTxt);
        deliveryAddressHtxt = (TextView) findViewById(R.id.deliveryAddressHtxt);
        changeAddresssTxt = (TextView) findViewById(R.id.changeAddresssTxt);
        paymentModeTitle = (TextView) findViewById(R.id.paymentModeTitle);
        paymentRadioGroup = (RadioGroup) findViewById(R.id.paymentRadioGroup);
        cardRadio = (RadioButton) findViewById(R.id.cardRadio);
        cashRadio = (RadioButton) findViewById(R.id.cashRadio);
        cardNumTxt = (TextView) findViewById(R.id.cardNumTxt);
        farecontainer = (LinearLayout) findViewById(R.id.farecontainer);
        maxItemarea = (LinearLayout) findViewById(R.id.maxItemarea);
        btn_type2area = (LinearLayout) findViewById(R.id.btn_type2area);
        maxitemTitleTxtView = (TextView) findViewById(R.id.maxitemTitleTxtView);
        maxitemmsgTxtView = (TextView) findViewById(R.id.maxitemmsgTxtView);
        casenote = (TextView) findViewById(R.id.casenote);
        cashRadio.setChecked(true);

        instantPaymentLL = (LinearLayout) findViewById(R.id.instantPaymentLL);
        WalletLay = (LinearLayout) findViewById(R.id.WalletLay);
        useExistingCardLL = (LinearLayout) findViewById(R.id.useExistingCardLL);
        iv_edit_card = (ImageView) findViewById(R.id.iv_edit_card);
        manual_arrow_right = (ImageView) findViewById(R.id.manual_arrow_right);
        instant_arrow_right = (ImageView) findViewById(R.id.instant_arrow_right);
        couponCodeImgView = (ImageView) findViewById(R.id.couponCodeImgView);

        manual_arrow_area = (LinearLayout) findViewById(R.id.manual_arrow_area);
        editcard_area = (LinearLayout) findViewById(R.id.editcard_area);

        instantPaymentTxt = (TextView) findViewById(R.id.instantPaymentTxt);
        existingCardTxt = (TextView) findViewById(R.id.existingCardTxt);
        cardDescTxt = (TextView) findViewById(R.id.cardDescTxt);
        cardTitleTxt = (TextView) findViewById(R.id.cardTitleTxt);

        cardTitleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_DELIVER_CARD_TITLE_TXT"));
        cardDescTxt.setText(generalFunc.retrieveLangLBl("", "LBL_DELIVER_CARD_DESC_TXT"));
        existingCardTxt.setText(generalFunc.retrieveLangLBl("", "LBL_DELIVER_MANAGE_CARD"));
        instantPaymentTxt.setText(generalFunc.retrieveLangLBl("", "LBL_DELIVER_INSTANT_PAYMENT"));
        delivery_type_text.setText(generalFunc.retrieveLangLBl("Delivery Type", "LBL_DELIVERY_TYPE"));


        delivryradio = findViewById(R.id.deliveryRadio);


        take_awayRadio = findViewById(R.id.takeaweyRadio);

        delivryradio.setText(generalFunc.retrieveLangLBl("delivery", "LBL_DELIVERY"));
        take_awayRadio.setText(generalFunc.retrieveLangLBl("take away", "LBL_ORDER_ORDER_TAKEAWAY"));
        deliveryRadioGroup = findViewById(R.id.deliveryRadioGroup);

        if (generalFunc.isRTLmode()) {
            couponCodeImgView.setRotation(180);
        }


        paymentRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {

            if (checkedId == cardRadio.getId()) {

                ePaymentOption = "Card";

                if (SYSTEM_PAYMENT_FLOW.equalsIgnoreCase("Method-1")) {
                    if (!generalFunc.isDeliverOnlyEnabled()) {
                        findViewById(R.id.cardDetailsArea).setVisibility(View.VISIBLE);

                        if (!Utils.checkText(selectedMethod)) {
//                        useExistingCardLL.performClick();

                            selectedMethod = "Manual";

                            if (checkCardConfig(isWalletSelect, false, false)) {
                                existingPaymentView(true);
                            } else {
                                defaultPaymentView(true);
                            }

                        }

                        contentArea.post(() -> contentArea.fullScroll(ScrollView.FOCUS_DOWN));
                    } else if (generalFunc.isDeliverOnlyEnabled()) {
                        selectedMethod = "Instant";
                    }
                }

            } else if (checkedId == cashRadio.getId()) {
                ePaymentOption = "Cash";
                selectedMethod = "";
                findViewById(R.id.cardDetailsArea).setVisibility(View.GONE);
            }
        });
        checkboxWallet.setOnClickListener(view -> {

            if (iswalletZero) {
                final GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
                generateAlert.setCancelable(false);
                generateAlert.setBtnClickList(btn_id -> {
                    if (btn_id == 0) {
                        checkboxWallet.setChecked(false);
                        generateAlert.closeAlertBox();
                    } else {


                        checkboxWallet.setChecked(false);
                        if (APP_PAYMENT_MODE.equalsIgnoreCase("Cash")) {
                            new StartActProcess(getActContext()).startAct(ContactUsActivity.class);
                        } else {
                            Bundle bn = new Bundle();
                            bn.putString("iServiceId", generalFunc.getServiceId());
                            new StartActProcess(getActContext()).startActWithData(MyWalletActivity.class, bn);
                        }

                    }

                });
                generateAlert.setContentMessage("", generalFunc.retrieveLangLBl("", "LBL_LOW_WALLET_BAL"));

                if (APP_PAYMENT_MODE.equalsIgnoreCase("Cash")) {
                    generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_CONTACT_US_TXT"));
                } else {
                    generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_ADD_NOW"));
                }
                generateAlert.setNegativeBtn(generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"));
                generateAlert.showAlertBox();


            } else {
                if (iswalletZero) {
                    return;
                }
                if (checkboxWallet.isChecked()) {

                    //  setpaymentRecyclerAdapter();
                    paymentTypeRecycler.setVisibility(View.GONE);
                    paymentType = "";
                    paymentId = "";
                    isWalletSelect = "Yes";
                    isChecked = true;
                    //   x.clearCheck();
                } else {

                    paymentTypeRecycler.setVisibility(View.VISIBLE);
                    paymentType = "";
                    paymentId = "";
                    isChecked = false;

                    setpaymentRecyclerAdapter();

                    isWalletSelect = "No";

                }
                CheckOutOrderEstimateDetails(appliedPromoCode, false);
            }

        });

        changeAddresssTxt.setOnClickListener(new setOnClickList());

        instantPaymentLL.setOnClickListener(new setOnClickList());
        useExistingCardLL.setOnClickListener(new setOnClickList());

        addressTxt = (TextView) findViewById(R.id.addressTxt);
        delveryAddressArea = findViewById(R.id.delveryAddressArea);
        couponCodeArea.setOnClickListener(new setOnClickList());
        submitBtnId = Utils.generateViewId();
        btn_type2.setId(submitBtnId);
        btn_type2.setOnClickListener(new setOnClickList());
        btn_clearcart.setOnClickListener(new setOnClickList());
        backImgView.setOnClickListener(new setOnClickList());
        editcard_area.setOnClickListener(new setOnClickList());

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            isFromEditCard = bundle.getBoolean("isFromEditCard");
        }

        setLabel();

        if (!generalFunc.retrieveValue(Utils.SELECT_ADDRESSS).equalsIgnoreCase("")) {
            selAddress = generalFunc.retrieveValue(Utils.SELECT_ADDRESSS);
            selLatitude = generalFunc.retrieveValue(Utils.SELECT_LATITUDE);
            selLongitude = generalFunc.retrieveValue(Utils.SELECT_LONGITUDE);
            selAddressId = generalFunc.retrieveValue(Utils.SELECT_ADDRESS_ID);

            Log.d("selAddressssssss", "generalFunc::" + selAddress);

            addressTxt.setText(selAddress);


        }


        if (APP_PAYMENT_MODE.equalsIgnoreCase("Cash")) {
            cashRadio.setVisibility(View.VISIBLE);
            cardRadio.setVisibility(View.GONE);
            cashRadio.setChecked(true);
        } else if (APP_PAYMENT_MODE.equalsIgnoreCase("Card")) {
            cashRadio.setVisibility(View.GONE);
            cardRadio.setVisibility(View.VISIBLE);
            cardRadio.setChecked(true);
        } else {
            cardRadio.setVisibility(View.VISIBLE);
            cashRadio.setVisibility(View.VISIBLE);
        }
        handleWalletView();

        if (!SYSTEM_PAYMENT_FLOW.equalsIgnoreCase("Method-1")) {
            cardRadio.setText(generalFunc.retrieveLangLBl("", "LBL_PAY_BY_WALLET_TXT"));
            checkboxWallet.setVisibility(View.GONE);
            walletAmountTxt.setVisibility(View.GONE);
        }
    }


    public void handleWalletView() {
        if (SYSTEM_PAYMENT_FLOW.equalsIgnoreCase("Method-1")) {
            String WALLET_ENABLE = userProfileJson != null ? generalFunc.getJsonValue(Utils.WALLET_ENABLE, userProfileJson) : "";

            if (!WALLET_ENABLE.equals("") && WALLET_ENABLE.equalsIgnoreCase("Yes") && !generalFunc.getJsonValue("user_available_balance_amount", userProfileJson).equalsIgnoreCase("") &&
                    GeneralFunctions.parseDoubleValue(0, generalFunc.getJsonValue("user_available_balance_amount", userProfileJson)) > 0) {
                checkboxWallet.setVisibility(View.VISIBLE);
                walletAmountTxt.setVisibility(View.VISIBLE);
                iswalletZero = false;


            } else {
                iswalletZero = true;
                checkboxWallet.setVisibility(View.VISIBLE);
                walletAmountTxt.setVisibility(View.VISIBLE);
            }
        }
    }

    public void generateErrorView() {
//        contentArea.setVisibility(View.GONE);
//        topArea.setVisibility(View.GONE);
//        bottomArea.setVisibility(View.GONE);

        generalFunc.generateErrorView(errorView, "LBL_ERROR_TXT", "LBL_NO_INTERNET_TXT");

        manageView();
        errorView.setOnRetryListener(() -> CheckOutOrderEstimateDetails(appliedPromoCode, false));
    }

    public void setpaymentRecyclerAdapter() {

        HashMap<String, String> PaymentMethod = (HashMap<String, String>) getIntent().getSerializableExtra("paymentMethod");
        Log.d("getSerializableExtra", "::" + PaymentMethod);

        // if (PaymentMethod.size()==0){


        PaymentRecyclerAdapter paymentRecyclerAdapter = new PaymentRecyclerAdapter(CheckOutActivity.this, loadMap(), this);
        paymentTypeRecycler.setLayoutManager(new LinearLayoutManager(this));
        paymentTypeRecycler.setAdapter(paymentRecyclerAdapter);
        // }


    }

    public void setLabel() {

        cardRadio.setText(generalFunc.retrieveLangLBl("Pay Online", "LBL_PAY_ONLINE_TXT"));

        if (SYSTEM_PAYMENT_FLOW.equalsIgnoreCase("Method-1")) {


            cardRadio.setText(generalFunc.retrieveLangLBl("", "LBL_CARD"));


        } else if (!SYSTEM_PAYMENT_FLOW.equalsIgnoreCase("Method-1")) {
            cardRadio.setText(generalFunc.retrieveLangLBl("", "LBL_PAY_BY_WALLET_TXT"));


        }
        cashRadio.setText(generalFunc.retrieveLangLBl("Cash Payment", "LBL_CASH_PAYMENT_TXT"));

        paymentModeTitle.setText(generalFunc.retrieveLangLBl("", "LBL_SELECET_PAYMENT_MODE"));
        titleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_CHECKOUT"));
        cartSummaryTxtView.setText(generalFunc.retrieveLangLBl("", "LBL_CART_SUMMARY"));

        checkboxWallet.setText(generalFunc.retrieveLangLBl("Use Wallet Balance", "LBL_USE_WALLET_BAL"));

        subtotalHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_SUB_TOTAL"));
        editOrderTxtView.setText(generalFunc.retrieveLangLBl("", "LBL_CUSTOMIZE"));
        applyCouponHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_APPLY_COUPON"));
        changeAddresssTxt.setText(generalFunc.retrieveLangLBl("", "LBL_CHANGE"));
        deliveryAddressHtxt.setText(generalFunc.retrieveLangLBl("", "LBL_DELIVERY_ADDRESS"));
        deliveryInstructionBox.setHint(generalFunc.retrieveLangLBl("", "LBL_DELIVERY_INSTRUCTION"));
        if (generalFunc.getMemberId().equalsIgnoreCase("")) {
            btn_type2.setText(generalFunc.retrieveLangLBl("", "LBL_LOGIN_TO_CONTINUE"));
        } else {
            btn_type2.setText(generalFunc.retrieveLangLBl("Confirm Order", "LBL_CONFIRM_ORDER_TEXT"));

            // btn_type2.setText(generalFunc.retrieveLangLBl("", "LBL_PAY"));

        }
        noLoginNoteTxt.setText(generalFunc.retrieveLangLBl("You will be able to review delivery address and payment options after login", "LBL_NO_LOGIN_NOTE"));

        if (!generalFunc.getMemberId().equalsIgnoreCase("")) {
            noLoginNoteTxt.setVisibility(View.GONE);
            if (GeneralFunctions.parseIntegerValue(0, generalFunc.getJsonValue("ToTalAddress", userProfileJson)) > 0) {
                delveryAddressArea.setVisibility(View.VISIBLE);
            }
        } else {
            noLoginNoteTxt.setVisibility(View.VISIBLE);
            delveryAddressArea.setVisibility(View.GONE);
        }

        maxitemTitleTxtView.setText(generalFunc.retrieveLangLBl("", "LBL_TO_MANY_ITEMS"));
        maxitemmsgTxtView.setText(generalFunc.retrieveLangLBl("", "LBL_MAX_QTY_NOTE") + " " + generalFunc.retrieveValue(Utils.COMPANY_MAX_QTY) + " " + generalFunc.retrieveLangLBl("", "LBL_TO_PROCEED"));
        btn_clearcart.setText(generalFunc.retrieveLangLBl("", "LBL_CLEAR_CART"));

    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            getUserProfileJson();

            handleWalletView();
            if (!generalFunc.getMemberId().equalsIgnoreCase("")) {
                noLoginNoteTxt.setVisibility(View.GONE);
                if (GeneralFunctions.parseIntegerValue(0, ToTalAddress) > 0) {
                    delveryAddressArea.setVisibility(View.VISIBLE);
                }

            } else {
                noLoginNoteTxt.setVisibility(View.VISIBLE);
                delveryAddressArea.setVisibility(View.GONE);
            }
        } catch (Exception e) {

        }
    }

    private void getUserProfileJson() {
        userProfileJson = generalFunc.retrieveValue(Utils.USER_PROFILE_JSON);
        SYSTEM_PAYMENT_FLOW = generalFunc.getJsonValue("SYSTEM_PAYMENT_FLOW", userProfileJson);
        APP_PAYMENT_MODE = generalFunc.getJsonValue("APP_PAYMENT_MODE", userProfileJson);
        APP_PAYMENT_METHOD = generalFunc.getJsonValue("APP_PAYMENT_METHOD", userProfileJson);

    }

    public void openAddressDailog(String manageNote) {

        final BottomSheetDialog addAddressDailog = new BottomSheetDialog(getActContext());
        View contentView = View.inflate(getActContext(), R.layout.dialog_add_address, null);

        addAddressDailog.setContentView(contentView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                Utils.dpToPx(280, getActContext())));
        addAddressDailog.setCancelable(false);
        BottomSheetBehavior mBehavior = BottomSheetBehavior.from((View) contentView.getParent());
        mBehavior.setPeekHeight(Utils.dpToPx(280, getActContext()));
        View bottomSheetView = addAddressDailog.getWindow().getDecorView().findViewById(R.id.design_bottom_sheet);

        TextView selAddressTxt = (TextView) bottomSheetView.findViewById(R.id.selAddressTxt);
        TextView cancelTxt = (TextView) bottomSheetView.findViewById(R.id.cancelTxt);
        TextView noaddressNote = (TextView) bottomSheetView.findViewById(R.id.noaddressNote);
        TextView addAddressBtn = (TextView) bottomSheetView.findViewById(R.id.addAddressBtn);
        addAddressBtn.setText(generalFunc.retrieveLangLBl("", "LBL_ADD_ADDRESS"));

        cancelTxt.setText(generalFunc.retrieveLangLBl("", "LBL_CANCEL_TXT"));
        if (manageNote.equalsIgnoreCase("")) {
            noaddressNote.setText(generalFunc.retrieveLangLBl("", "LBL_NO_ADDRESS_AVAILABLE_NOTE"));
        } else {
            noaddressNote.setText(generalFunc.retrieveLangLBl("", "LBL_CHANGE_ADDRESS_AVAILABLE_NOTE"));
        }

        cancelTxt.setOnClickListener(v -> {
            addAddressDailog.dismiss();
            finish();
        });

        addAddressBtn.setOnClickListener(v -> {

            addAddressDailog.dismiss();

            Bundle bn = new Bundle();
            bn.putString("iCompanyId", cartList.get(0).getiCompanyId());
            new StartActProcess(getActContext()).startActForResult(AddAddressActivity.class, bn, ADD_ADDRESS);

        });


        addAddressDailog.show();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN_UP && resultCode == RESULT_OK) {
            noLoginNoteTxt.setVisibility(View.GONE);
            getUserProfileJson();
            setGeneralData();
            setButtonTxt();
//            if (GeneralFunctions.parseIntegerValue(0, ToTalAddress) > 0) {
//                Bundle bn = new Bundle();
//                bn.putString("iCompanyId", cartList.get(0).getiCompanyId());
//                new StartActProcess(getActContext()).startActForResult(ListAddressActivity.class, bn, SEL_ADDRESS);
//            } else {
//                openAddressDailog();
//            }
            if (internetConnection.isNetworkConnected()) {
                CheckOutOrderEstimateDetails("", false);
            } else {
                generateErrorView();
            }
        } else if (requestCode == SEL_ADDRESS && resultCode == RESULT_OK) {

            getUserProfileJson();
            if (data.getStringExtra("ToTalAddress") != null) {
                ToTalAddress = data.getStringExtra("ToTalAddress");
            }
            if (GeneralFunctions.parseIntegerValue(0, ToTalAddress) > 0) {
                if (data != null && data.getStringExtra("address") != null) {
                    addressTxt.setText(data.getStringExtra("address"));
                    selAddress = data.getStringExtra("address");

                    Log.d("selAddressssssss", "getStringExtra::" + selAddress);

                    selAddressId = data.getStringExtra("addressId");

                    isselectaddress = true;

                    HashMap<String, String> storeData = new HashMap<>();
                    storeData.put(Utils.SELECT_ADDRESS_ID, selAddressId);
                    storeData.put(Utils.SELECT_ADDRESSS, data.getStringExtra("address"));
                    storeData.put(Utils.SELECT_LATITUDE, data.getStringExtra("vLatitude"));
                    storeData.put(Utils.SELECT_LONGITUDE, data.getStringExtra("vLongitude"));
                    generalFunc.storeData(storeData);

                    CheckOutOrderEstimateDetails(appliedPromoCode, false);
                }
                delveryAddressArea.setVisibility(View.VISIBLE);
            } else {
                delveryAddressArea.setVisibility(View.GONE);
                //openAddressDailog("");
                CheckOutOrderEstimateDetails(appliedPromoCode, false);

            }

        } else if (requestCode == SEL_ADDRESS) {

            if (data != null && data.getBooleanExtra("isDeleted", false)) {

                ArrayList<String> removeData = new ArrayList<>();
                removeData.add(Utils.SELECT_ADDRESS_ID);
                removeData.add(Utils.SELECT_ADDRESSS);
                removeData.add(Utils.SELECT_LATITUDE);
                removeData.add(Utils.SELECT_LONGITUDE);
                generalFunc.removeValue(removeData);

                CheckOutOrderEstimateDetails(appliedPromoCode, false);
            }

        } else if (requestCode == ADD_ADDRESS) {

            if (resultCode == RESULT_OK) {
                getUserProfileJson();
                if (internetConnection.isNetworkConnected()) {
                    generalFunc.removeValue(Utils.SELECT_ADDRESS_ID);
                    CheckOutOrderEstimateDetails(appliedPromoCode, false);
                } else {
                    generateErrorView();
                }
            } else {
                onBackPressed();
            }
        } else if (requestCode == Utils.VERIFY_MOBILE_REQ_CODE) {
            getUserProfileJson();
        } else if (requestCode == Utils.SOCIAL_LOGIN_REQ_CODE && resultCode == RESULT_OK) {
            getUserProfileJson();

            String ePhoneVerified = generalFunc.getJsonValue("ePhoneVerified", userProfileJson);

            if (!ePhoneVerified.equals("Yes")) {
                notifyVerifyMobile();
                return;
            }


        } else if (requestCode == Utils.CARD_PAYMENT_REQ_CODE && resultCode == RESULT_OK && data != null) {

            getUserProfileJson();
            isCardValidated = true;

            existingPaymentView(false);

            // addDrawer.changeUserProfileJson(this.userProfileJson);
        } else if (requestCode == Utils.SELECT_COUPON_REQ_CODE && resultCode == RESULT_OK) {
            String couponCode = data.getStringExtra("CouponCode");
            if (couponCode == null) {
                couponCode = "";
            }
            appliedPromoCode = couponCode;
            CheckOutOrderEstimateDetails(couponCode, true);
            setPromoCode();
        } else if (requestCode == Utils.REQ_VERIFY_INSTANT_PAYMENT_CODE && resultCode == RESULT_OK && data != null) {

            if (data != null) {
                orderCompleted();
            } else {
                generalFunc.showError();
            }


        }

    }

    public boolean checkCardConfig(String isWalletSelect, boolean redirect, boolean openAddCardView) {

        boolean isCardAdded = false;
        if (selectedMethod.equalsIgnoreCase("Manual")) {
            if (APP_PAYMENT_METHOD.equalsIgnoreCase("Stripe")) {
                String vStripeCusId = generalFunc.getJsonValue("vStripeCusId", userProfileJson);
                if (vStripeCusId.equals("")) {
                    if (openAddCardView) {
                        OpenCardPaymentAct(isWalletSelect);
                    }
                    isCardAdded = false;
                } else {
                    if (redirect) {
                        showPaymentBox(isWalletSelect);
                    }
                    isCardAdded = true;
                }
            }
        } /*else if (selectedMethod.equalsIgnoreCase("Instant")) {
            orderPlaceCallForCard("", isWalletSelect);
        } else {
            orderPlaceCallForCard("", isWalletSelect);
        }*/
        return isCardAdded;
    }

    public void OpenCardPaymentAct(String isWalletSelect) {
        Bundle bn = new Bundle();
        bn.putBoolean("fromCheckout", true);
        bn.putString("isWalletSelect", isWalletSelect);
        new StartActProcess(getActContext()).startActForResult(CardPaymentActivity.class, bn, Utils.CARD_PAYMENT_REQ_CODE);
    }

    public void showPaymentBox(String isWalletSelect) {
        androidx.appcompat.app.AlertDialog alertDialog;
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getActContext());
        builder.setTitle("");
        builder.setCancelable(false);
        LayoutInflater inflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.input_box_view, null);
        builder.setView(dialogView);

        final MaterialEditText input = (MaterialEditText) dialogView.findViewById(R.id.editBox);
        final TextView subTitleTxt = (TextView) dialogView.findViewById(R.id.subTitleTxt);

        Utils.removeInput(input);

        subTitleTxt.setVisibility(View.VISIBLE);
        subTitleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_TITLE_PAYMENT_ALERT"));
        input.setText(generalFunc.getJsonValue("vCreditCard", userProfileJson));

        builder.setPositiveButton(generalFunc.retrieveLangLBl("Confirm", "LBL_BTN_TRIP_CANCEL_CONFIRM_TXT"), (dialog, which) -> {
            dialog.cancel();
            placeOrder();

        });
        builder.setNeutralButton(generalFunc.retrieveLangLBl("Change", "LBL_CHANGE"), (dialog, which) -> {
            dialog.cancel();
            OpenCardPaymentAct(isWalletSelect);
            //ridelaterclick = false;

        });
        builder.setNegativeButton(generalFunc.retrieveLangLBl("Cancel", "LBL_CANCEL_TXT"), (dialog, which) -> {
            dialog.cancel();
            //ridelaterclick = false;

        });


        alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    public void checkPaymentCard(String isWalletSelect, boolean validate) {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "CheckCard");
        parameters.put("iUserId", generalFunc.getMemberId());

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(responseString -> {

            if (responseString != null && !responseString.equals("")) {

                String action = generalFunc.getJsonValue(Utils.action_str, responseString);
                if (action.equals("1")) {

                    isCardValidated = true;

                    /*if (isWalletSelect.equalsIgnoreCase("Yes") && iswalletZero) {
                        addMoneyToWallet("", isWalletSelect);
                    }else {
                        orderPlaceCallForCard("", isWalletSelect);
                    }*/

                    if (validate) {
                        placeOrder();
                    }
                } else {
                    generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)));
                }
            } else {
                generalFunc.showError();
            }
        });
        exeWebServer.execute();
    }


    private void addMoneyToWallet(String token, String CheckUserWallet) {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "addMoneyUserWalletByChargeCard");
        parameters.put("CheckUserWallet", CheckUserWallet);
        parameters.put("iMemberId", generalFunc.getMemberId());
        parameters.put("fAmount", getIntent().getStringExtra("fAmount"));
        parameters.put("UserType", Utils.app_type);
        parameters.put("vStripeToken", token);
        parameters.put("eSystem", Utils.eSystem_Type);

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(responseString -> {

            if (responseString != null && !responseString.equals("")) {

                boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

                if (isDataAvail == true) {

                    generalFunc.storeData(Utils.USER_PROFILE_JSON, generalFunc.getJsonValue(Utils.message_str, responseString));
                    getUserProfileJson();

                    GenerateAlertBox generateAlertBox = new GenerateAlertBox(getActContext());
                    generateAlertBox.setCancelable(false);
                    generateAlertBox.setContentMessage("", generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str_one, responseString)));

                    generateAlertBox.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_OK"));
                    generateAlertBox.setBtnClickList(btn_id -> {
                        generateAlertBox.closeAlertBox();
                        orderPlaceCallForCard("", isWalletSelect);
                    });
                    generateAlertBox.showAlertBox();
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


    public void orderPlaceCallForCard(String token, String CheckUserWallet) {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "CaptureCardPaymentOrder");
        parameters.put("iUserId", generalFunc.getMemberId());
        parameters.put("ePaymentOption", "Card");
        parameters.put("iOrderId", iOrderId);
        parameters.put("vStripeToken", token);
        parameters.put("CheckUserWallet", CheckUserWallet);
        parameters.put("returnUrl", CommonUtilities.WEBSERVICE);
        parameters.put("eSystem", Utils.eSystem_Type);
        parameters.put("vPayMethod", selectedMethod);

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(responseString -> {

            if (responseString != null && !responseString.equals("")) {

                String action = generalFunc.getJsonValue(Utils.action_str, responseString);
                if (action.equals("1")) {

                    if (selectedMethod.equalsIgnoreCase("Manual")) {
                        orderCompleted();
                    } else if (selectedMethod.equalsIgnoreCase("Instant")) {
                        Bundle bn = new Bundle();
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("URL", generalFunc.getJsonValue(Utils.message_str, responseString));
                        map.put("vPageTitle", generalFunc.retrieveLangLBl("", "LBL_INSTANT_PAYMENT_PAGE_TITLE_TXT"));
                        bn.putSerializable("data", map);
                        new StartActProcess(getActContext()).startActForResult(QuickPaymentActivity.class, bn, Utils.REQ_VERIFY_INSTANT_PAYMENT_CODE);

                    }

                } else {
                    generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)));
                }
            } else {
                generalFunc.showError();
            }
        });
        exeWebServer.execute();

    }

    private void orderCompleted() {
        Bundle bn = new Bundle();
        bn.putBoolean("isRestart", true);
//                        bn.putString("iOrderId", getIntent().getStringExtra("iOrderId"));
        bn.putString("iOrderId", iOrderId);

        new StartActProcess(getActContext()).startActWithData(OrderPlaceConfirmActivity.class, bn);
    }

    public void notifyVerifyMobile() {
        String vPhoneCode = generalFunc.retrieveValue(Utils.DefaultPhoneCode);
        Bundle bn = new Bundle();
        bn.putString("MOBILE", vPhoneCode + generalFunc.getJsonValue("vPhone", userProfileJson));
        bn.putString("msg", "DO_PHONE_VERIFY");
        bn.putBoolean("isrestart", false);

        new StartActProcess(getActContext()).startActForResult(VerifyInfoActivity.class, bn, Utils.VERIFY_MOBILE_REQ_CODE);

    }

    private void existingPaymentView(boolean highlightView) {
        existingCardTxt.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        setViewBasedOnSelection(highlightView);
        cardNumTxt.setVisibility(View.VISIBLE);
        editcard_area.setVisibility(View.VISIBLE);
        manual_arrow_right.setVisibility(View.GONE);
        cardNumTxt.setText(generalFunc.getJsonValue("vCreditCard", userProfileJson));
    }

    private void defaultPaymentView(boolean highlightView) {
        existingCardTxt.getLayoutParams().height = Utils.dpToPx(35, getActContext());
        setViewBasedOnSelection(highlightView);
        cardNumTxt.setVisibility(View.GONE);
        editcard_area.setVisibility(View.GONE);
        manual_arrow_right.setVisibility(View.VISIBLE);
    }

    private void setViewBasedOnSelection(boolean highlightView) {
        if (highlightView) {
            if (selectedMethod.equalsIgnoreCase("Manual")) {
                existingCardTxt.setTextColor(getResources().getColor(R.color.appThemeColor_1));
                instantPaymentTxt.setTextColor(getResources().getColor(R.color.black));
                instant_arrow_right.setColorFilter(ContextCompat.getColor(getActContext(), R.color.black), PorterDuff.Mode.MULTIPLY);
                manual_arrow_right.setColorFilter(ContextCompat.getColor(getActContext(), R.color.appThemeColor_1), PorterDuff.Mode.MULTIPLY);

            } else if (selectedMethod.equalsIgnoreCase("Instant")) {
                existingCardTxt.setTextColor(getResources().getColor(R.color.black));
                instantPaymentTxt.setTextColor(getResources().getColor(R.color.appThemeColor_1));
                instant_arrow_right.setColorFilter(ContextCompat.getColor(getActContext(), R.color.appThemeColor_1), PorterDuff.Mode.MULTIPLY);
                manual_arrow_right.setColorFilter(ContextCompat.getColor(getActContext(), R.color.black), PorterDuff.Mode.MULTIPLY);
            }
        }
    }

    public void defaultPromoView() {
        promocodeArea.setVisibility(View.GONE);
        appliedPromoHTxtView.setVisibility(View.GONE);

        couponCodeImgView.setImageResource(R.mipmap.ic_arrow_right);
        couponCodeImgView.setOnClickListener(null);
        applyCouponHTxt.setTextColor(Color.parseColor("#333333"));
        applyCouponHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_APPLY_COUPON"));

        promocodeappliedHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_APPLIED_COUPON_CODE"));

    }

    public void appliedPromoView() {
        appliedPromoHTxtView.setVisibility(View.VISIBLE);
        applyCouponHTxt.setText(appliedPromoCode);
        applyCouponHTxt.setTextColor(getResources().getColor(R.color.appThemeColor_1));
        couponCodeImgView.setImageResource(R.mipmap.ic_close_icon_);
        couponCodeImgView.setOnClickListener(new setOnClickList());
        appliedPromoHTxtView.setText(generalFunc.retrieveLangLBl("", "LBL_APPLIED_COUPON_CODE"));
    }

    public void setCancelable(Dialog dialogview, boolean cancelable) {
        final Dialog dialog = dialogview;
        View touchOutsideView = dialog.getWindow().getDecorView().findViewById(R.id.touch_outside);
        View bottomSheetView = dialog.getWindow().getDecorView().findViewById(R.id.design_bottom_sheet);

        if (cancelable) {
            touchOutsideView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dialog.isShowing()) {
                        dialog.cancel();
                    }
                }
            });
            BottomSheetBehavior.from(bottomSheetView).setHideable(true);
        } else {
            touchOutsideView.setOnClickListener(null);
            BottomSheetBehavior.from(bottomSheetView).setHideable(false);
        }
    }

    public void manageWalletCheckBox() {
        if (SYSTEM_PAYMENT_FLOW.equalsIgnoreCase("Method-1")) {
            checkboxWallet.setVisibility(View.VISIBLE);
            walletAmountTxt.setVisibility(View.VISIBLE);
            iswalletZero = true;
        }
    }

    public void CheckOutOrderEstimateDetails(String promoCode, boolean isPromoApplied) {
        Logger.d("CheckPromoValue", "::" + promoCode);

        try {
            JSONArray orderedItemArr = new JSONArray();
            for (int i = 0; i < cartList.size(); i++) {
                JSONObject object = new JSONObject();
                object.put("iMenuItemId", cartList.get(i).getiMenuItemId());
                object.put("iFoodMenuId", cartList.get(i).getiFoodMenuId());
                object.put("vOptionId", cartList.get(i).getiOptionId());
                object.put("vAddonId", cartList.get(i).getiToppingId());
                object.put("iQty", cartList.get(i).getQty());
                orderedItemArr.put(object);
            }


            HashMap<String, String> data = new HashMap<>();
            data.put(Utils.SELECT_ADDRESS_ID, "");
            data.put(Utils.SELECT_LATITUDE, "");
            data.put(Utils.SELECT_LONGITUDE, "");
            data = generalFunc.retrieveValue(data);

            HashMap<String, String> parameters = new HashMap<String, String>();
            parameters.put("type", "CheckOutOrderEstimateDetails");
            parameters.put("iUserId", generalFunc.getMemberId());
            parameters.put("iCompanyId", cartList.get(0).getiCompanyId());
            parameters.put("iUserAddressId", data.get(Utils.SELECT_ADDRESS_ID));
            parameters.put("vCouponCode", promoCode.trim());
            parameters.put("ePaymentOption", ePaymentOption);
            parameters.put("vInstruction", deliveryInstructionBox.getText().toString().trim());
            parameters.put("OrderDetails", orderedItemArr.toString());
            parameters.put("PassengerLat", data.get(Utils.SELECT_LATITUDE));
            parameters.put("PassengerLon", data.get(Utils.SELECT_LONGITUDE));
            parameters.put("CheckUserWallet", isWalletSelect);
            parameters.put("eSystem", Utils.eSystem_Type);

            ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
            exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
            exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
                @Override
                public void setResponse(String responseString) {
                    JSONObject responseObj = generalFunc.getJsonObject(responseString);

                    if (responseObj != null && !responseObj.equals("")) {
                        manageView();

                        String action = generalFunc.getJsonValueStr(Utils.action_str, responseObj);
                        if (action.equals("1")) {
                            errorView.setVisibility(View.GONE);
                            appliedPromoCode = promoCode;


                            // if (SYSTEM_PAYMENT_FLOW.equalsIgnoreCase("Method-1")) {

                            if (generalFunc.getJsonValueStr("DISABLE_CASH_PAYMENT_OPTION", responseObj).equalsIgnoreCase("yes")) {

                                if (Utils.checkText(APP_PAYMENT_MODE) && !APP_PAYMENT_MODE.equalsIgnoreCase("Cash")) {
                                    cashRadio.setEnabled(false);
                                    cashRadio.setChecked(false);
                                    isCODAllow = false;

                                    if (!APP_PAYMENT_MODE.equalsIgnoreCase("Card")) {
                                        casenote.setVisibility(View.VISIBLE);
                                        casenote.setText(generalFunc.retrieveLangLBl("", "LBL_COD_NOT_AVAILABLE_TXT") + " " + generalFunc.getJsonValue("fOutStandingAmount", responseObj));
                                    }
                                    if (!APP_PAYMENT_MODE.equalsIgnoreCase("Cash")) {
                                        cardRadio.setChecked(true);
                                    }
                                }

                                manageWalletCheckBox();

                            } else {
                                String WALLET_ENABLE = userProfileJson != null ? generalFunc.getJsonValue(Utils.WALLET_ENABLE, userProfileJson) : "";
                                if (!WALLET_ENABLE.equals("") && WALLET_ENABLE.equalsIgnoreCase("Yes") && !generalFunc.getJsonValue("user_available_balance_amount", userProfileJson).equalsIgnoreCase("") &&
                                        GeneralFunctions.parseDoubleValue(0, generalFunc.getJsonValue("user_available_balance_amount", userProfileJson)) > 0) {

                                    casenote.setVisibility(View.GONE);
                                    cashRadio.setEnabled(true);
                                    manageWalletCheckBox();

                                } else {
                                    manageWalletCheckBox();
                                }


                            }
                            // }

                            currencySymbol = generalFunc.getJsonValueStr("currencySymbol", responseObj);
                            itemArraylist.clear();
                            JSONArray OrderDetailsItemsArr = generalFunc.getJsonArray("OrderDetailsItemsArr", responseObj);

                            if (itemContainer.getChildCount() > 0) {
                                itemContainer.removeAllViewsInLayout();
                            }

                            if (OrderDetailsItemsArr != null && OrderDetailsItemsArr.length() > 0) {
                                for (int j = 0; j < OrderDetailsItemsArr.length(); j++) {
                                    JSONObject itemObj = generalFunc.getJsonObject(OrderDetailsItemsArr, j);
                                    HashMap<String, String> itemmap = new HashMap<>();
                                    itemmap.put("iMenuItemId", generalFunc.getJsonValueStr("iMenuItemId", itemObj));
                                    itemmap.put("iFoodMenuId", generalFunc.getJsonValueStr("iFoodMenuId", itemObj));
                                    itemmap.put("vItemType", generalFunc.getJsonValueStr("vItemType", itemObj));
                                    itemmap.put("iQty", generalFunc.getJsonValueStr("iQty", itemObj));
                                    itemmap.put("fOfferAmt", generalFunc.getJsonValueStr("fOfferAmt", itemObj));
                                    itemmap.put("fOriginalPrice", generalFunc.getJsonValueStr("fOriginalPrice", itemObj));
                                    itemmap.put("fPrice", generalFunc.getJsonValueStr("fPrice", itemObj));
                                    itemmap.put("currencySymbol", generalFunc.getJsonValueStr("currencySymbol", itemObj));
                                    itemmap.put("optionaddonname", generalFunc.getJsonValueStr("optionaddonname", itemObj));

                                    itemArraylist.add(itemmap);

                                }
                                addItemView();
                            }

                            if (!isselectaddress) {
                                String UserSelectedAddressId = generalFunc.getJsonValueStr("UserSelectedAddressId", responseObj);

                                if (UserSelectedAddressId != null && !UserSelectedAddressId.equalsIgnoreCase("")) {
                                    HashMap<String, String> storeData = new HashMap<>();
                                    storeData.put(Utils.SELECT_ADDRESSS, generalFunc.getJsonValueStr("UserSelectedAddress", responseObj));
                                    storeData.put(Utils.SELECT_LATITUDE, generalFunc.getJsonValueStr("UserSelectedLatitude", responseObj));
                                    storeData.put(Utils.SELECT_LONGITUDE, generalFunc.getJsonValueStr("UserSelectedLongitude", responseObj));
                                    storeData.put(Utils.SELECT_ADDRESS_ID, UserSelectedAddressId);
                                    generalFunc.storeData(storeData);

                                    addressTxt.setText(generalFunc.retrieveValue(Utils.SELECT_ADDRESSS));

                                } else {
                                    isselectaddress = false;
                                    addressTxt.setText(generalFunc.retrieveValue(Utils.SELECT_ADDRESSS));
                                }
                            } else {
                                isselectaddress = false;
                                addressTxt.setText(generalFunc.retrieveValue(Utils.SELECT_ADDRESSS));

                            }


                            subtotalVTxt.setText(generalFunc.convertNumberWithRTL(generalFunc.getJsonValueStr("fSubTotal", responseObj)));

                            takeAwayChargis = generalFunc.convertNumberWithRTL(generalFunc.getJsonValueStr("fSubTotal", responseObj));

                            restaurantstatus = generalFunc.getJsonValueStr("restaurantstatus", responseObj);

                            retursantAddresss = generalFunc.getJsonValueStr("vRestuarantLocationTakeAway", responseObj);
                            Log.d("vrestuarantLocationtake", "" + generalFunc.getJsonValueStr("vRestuarantLocationTakeAway", responseObj));


                            //LBL_DELIVERY_CHARGE
                            if (!generalFunc.getMemberId().equals("") && !generalFunc.getJsonValueStr("fNetTotalAmount", responseObj).equalsIgnoreCase("0")) {

                                //   btn_type2.setText(generalFunc.retrieveLangLBl("", "LBL_PAY") + " " + generalFunc.convertNumberWithRTL(generalFunc.getJsonValueStr("fNetTotal", responseObj)));

                                btn_type2.setText(generalFunc.retrieveLangLBl("Confirm Order", "LBL_CONFIRM_ORDER_TEXT"));

                                chrgisWithDevivery = generalFunc.convertNumberWithRTL(generalFunc.getJsonValueStr("fNetTotal", responseObj));

                            } else {
                                if (!generalFunc.getMemberId().equals("")) {
                                    //   btn_type2.setText(generalFunc.retrieveLangLBl("", "LBL_PAY"));
                                    btn_type2.setText(generalFunc.retrieveLangLBl("Confirm Order", "LBL_CONFIRM_ORDER_TEXT"));

                                }
                            }

                            if (SYSTEM_PAYMENT_FLOW.equalsIgnoreCase("Method-1")) {

                                String DisplayCardPayment = generalFunc.getJsonValueStr("DisplayCardPayment", responseObj);
                                if (!DisplayCardPayment.equalsIgnoreCase("") && DisplayCardPayment.equalsIgnoreCase("Yes")) {
                                    cardRadio.setEnabled(true);
                                } else {
                                    cardRadio.setEnabled(false);
                                    cardRadio.setChecked(false);
                                    cashRadio.setChecked(true);
                                }

                                DisplayUserWalletDebitAmount = generalFunc.getJsonValueStr("DisplayUserWalletDebitAmount", responseObj);
                                if (DisplayUserWalletDebitAmount != null &&
                                        !DisplayUserWalletDebitAmount.equalsIgnoreCase("")) {
                                    // change amount

                                    if (isTakeAway) {
                                        DisplayUserWalletDebitAmount = takeAwayChargis;
                                    }


                                    walletAmountTxt.setText(generalFunc.convertNumberWithRTL(DisplayUserWalletDebitAmount));

                                } else {
                                    walletAmountTxt.setText("");
                                }
                            }

                            if (!generalFunc.getJsonValueStr("fDiscount", responseObj).equals("") && !generalFunc.getJsonValueStr("fDiscount", responseObj).equals("0")) {
                                appliedPromoView();
                            } else {
                                defaultPromoView();
                            }


//                            if (isPromoApplied) {
//                                // generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_PROMO_APPLIED"));
//                            } else {
//                                defaultPromoView();
//                            }

                            JSONArray FareDetailsArr = null;
                            FareDetailsArr = generalFunc.getJsonArray("FareDetailsArr", responseObj);

                            if (farecontainer.getChildCount() > 0) {
                                farecontainer.removeAllViews();
                            }
                            addFareDetailLayout(FareDetailsArr);


                            if (generalFunc.getMemberId().equalsIgnoreCase("")) {
                                isLogin = false;
                            } else {
                                ToTalAddress = generalFunc.getJsonValueStr("ToTalAddress", responseObj);
                                isLogin = true;
                                if (GeneralFunctions.parseIntegerValue(0, ToTalAddress) > 0) {
                                    ToTalAddress = generalFunc.getJsonValueStr("ToTalAddress", responseObj);
                                    onResume();
                                } else {
                                    openAddressDailog(generalFunc.getJsonValueStr("RestaurantAddressNotMatch", responseObj));
                                }
                            }
                        } else if (action.equals("01")) {
                            generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_PROMOCODE_ALREADY_USED"));
                        } else {


                            if (generalFunc.getJsonValueStr(Utils.message_str, responseObj) != null && generalFunc.getJsonValueStr(Utils.message_str, responseObj).equalsIgnoreCase("LBL_INVALID_PROMOCODE")) {
                                generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_INVALID_PROMOCODE"));
                                return;
                            }

                            if (generalFunc.getJsonValueStr(Utils.message_str, responseObj) != null && generalFunc.getJsonValueStr(Utils.message_str, responseObj).equalsIgnoreCase("LBL_PROMOCODE_COMPLETE_USAGE_LIMIT")) {
                                generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_PROMOCODE_COMPLETE_USAGE_LIMIT"));
                                return;
                            }

                            if (generalFunc.getMemberId().equalsIgnoreCase("")) {
                                isLogin = false;
                            } else {

                                if (generalFunc.getJsonValueStr("ToTalAddress", responseObj) != null) {
                                    ToTalAddress = generalFunc.getJsonValueStr("ToTalAddress", responseObj);
                                    isLogin = true;
                                    if (GeneralFunctions.parseIntegerValue(0, ToTalAddress) > 0) {
                                        ToTalAddress = generalFunc.getJsonValueStr("ToTalAddress", responseObj);
                                        onResume();
                                    } else {
                                        openAddressDailog(generalFunc.getJsonValueStr("RestaurantAddressNotMatch", responseObj));
                                        return;
                                    }
                                }
                            }

                            generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", generalFunc.getJsonValueStr(Utils.message_str, responseObj)));


                        }
                    } else {
                        generalFunc.showError();
                    }
                }
            });
            exeWebServer.execute();
        } catch (Exception e) {

        }

    }

    private void addFareDetailLayout(JSONArray jobjArray) {


        boolean islast = false;
        for (int i = 0; i < jobjArray.length(); i++) {
            JSONObject jobject = generalFunc.getJsonObject(jobjArray, i);
            try {
                if ((i == jobjArray.length() - 1)) {
                    islast = true;
                } else {
                    islast = false;
                }
                addFareDetailRow(jobject.names().getString(0), jobject.get(jobject.names().getString(0)).toString(), islast);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private void addFareDetailRow(String row_name, String row_value, boolean islast) {
        LayoutInflater infalInflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = infalInflater.inflate(R.layout.design_fare_breakdown_row, null);

        TextView titleHTxt = (TextView) convertView.findViewById(R.id.titleHTxt);
        TextView titleVTxt = (TextView) convertView.findViewById(R.id.titleVTxt);
        View seperatorView = convertView.findViewById(R.id.seperatorView);

        titleHTxt.setText(generalFunc.convertNumberWithRTL(row_name));
        titleVTxt.setText(generalFunc.convertNumberWithRTL(row_value));

        if (islast) {
            Typeface fontBold = Typeface.createFromAsset(getAssets(), getActContext().getResources().getString(R.string.robotobold));
          /*  titleHTxt.setTypeface(fontBold);
            titleHTxt.mTypeface = fontBold;*/

      /*      titleVTxt.setTypeface(fontBold);
            titleVTxt.mTypeface = fontBold;*/





            seperatorView.setVisibility(View.VISIBLE);
        } else {
            Typeface fontReg = Typeface.createFromAsset(getAssets(), getActContext().getResources().getString(R.string.robotoRegular));
          /*  titleHTxt.setTypeface(fontReg);
            titleHTxt.mTypeface = fontReg;*/

           /* titleVTxt.setTypeface(fontReg);
            titleVTxt.mTypeface = fontReg;*/
        }

        farecontainer.addView(convertView);
    }

    public void manageView() {
        if (internetConnection.isNetworkConnected()) {
            errorView.setVisibility(View.GONE);
        } else {
            errorView.setVisibility(View.VISIBLE);
        }
    }

    public void orderPlaceCall() {
        try {
            JSONArray orderedItemArr = new JSONArray();
            for (int i = 0; i < cartList.size(); i++) {
                JSONObject object = new JSONObject();
                object.put("iMenuItemId", cartList.get(i).getiMenuItemId());
                object.put("iFoodMenuId", cartList.get(i).getiFoodMenuId());
                object.put("vOptionId", cartList.get(i).getiOptionId());
                object.put("vAddonId", cartList.get(i).getiToppingId());
                object.put("iQty", cartList.get(i).getQty());
                orderedItemArr.put(object);
            }
            HashMap<String, String> parameters = new HashMap<String, String>();
            parameters.put("type", "CheckOutOrderDetails");
            parameters.put("iUserId", generalFunc.getMemberId());
            parameters.put("iCompanyId", cartList.get(0).getiCompanyId());
            parameters.put("iUserAddressId", generalFunc.retrieveValue(Utils.SELECT_ADDRESS_ID));
            parameters.put("vCouponCode", appliedPromoCode);
            parameters.put("ePaymentOption", ePaymentOption);
            parameters.put("vInstruction", deliveryInstructionBox.getText().toString().trim());
            parameters.put("OrderDetails", orderedItemArr.toString());
            parameters.put("CheckUserWallet", isWalletSelect);
            parameters.put("iOrderId", iOrderId);
            parameters.put("paymentId", paymentId);
            parameters.put("paymentType", paymentType);
            parameters.put("deliveryType", deliveryTypeText);

            Log.d("responseString real", "responseString real :" + parameters);


            // fixme abubakar send type


            if (!ePaymentOption.equalsIgnoreCase("Cash") && (!SYSTEM_PAYMENT_FLOW.equalsIgnoreCase("Method-1"))) {
                parameters.put("CheckUserWallet", "Yes");
                parameters.put("ePayWallet", "Yes");

            }
            parameters.put("eSystem", Utils.eSystem_Type);
            parameters.put("eWalletIgnore", eWalletIgnore);

            ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
            exeWebServer.setIsDeviceTokenGenerate(true, "vDeviceToken", generalFunc);


            exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
            exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
                @Override
                public void setResponse(String responseString) {
                    // fixme oderPlace check
                    if (generalFunc.getJsonValue(Utils.message_str, responseString) != null && generalFunc.getJsonValue(Utils.message_str, responseString).equalsIgnoreCase("LBL_PROMOCODE_COMPLETE_USAGE_LIMIT")) {
                        generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_PROMOCODE_COMPLETE_USAGE_LIMIT"));
                        return;
                        //fixme abubakar remove chk
                    }


                    if (responseString != null && !responseString.equals("")) {


                        Log.d("responseString", "String::" + responseString);
                        Log.d("responseString", "action::" + generalFunc.getJsonValue(Utils.action_str, responseString));
                        Log.d("responseString", "oderid::" + generalFunc.getJsonValue("iOrderId", responseString));

                        //test
                        String action = generalFunc.getJsonValue(Utils.action_str, responseString);
                        if (action.equals("1")) {

                            iOrderId = generalFunc.getJsonValue("iOrderId", responseString);
                            Bundle bn = new Bundle();
                            bn.putString("iOrderId", iOrderId);
                            bn.putString("deliveryType", deliveryTypeText);
                            if (ePaymentOption.equalsIgnoreCase("card") && SYSTEM_PAYMENT_FLOW.equalsIgnoreCase("Method-1")) {
                                orderPlaceCallForCard("", isWalletSelect);
                            } else {
                                new StartActProcess(getActContext()).startActWithData(OrderPlaceConfirmActivity.class, bn);
                            }

                        } else {

                            iOrderId = generalFunc.getJsonValue("iOrderId", responseString);
                            if (generalFunc.getJsonValue(Utils.message_str, responseString).equalsIgnoreCase("LOW_WALLET_AMOUNT")) {


                                String walletMsg = "";
                                String low_balance_content_msg = generalFunc.getJsonValue("low_balance_content_msg", responseString);

                                if (low_balance_content_msg != null && !low_balance_content_msg.equalsIgnoreCase("")) {
                                    walletMsg = low_balance_content_msg;
                                } else {
                                    walletMsg = generalFunc.retrieveLangLBl("", "LBL_WALLET_LOW_AMOUNT_MSG_TXT");
                                }

                                if (!isCODAllow && SYSTEM_PAYMENT_FLOW.equalsIgnoreCase("Method-2")) {
                                    generalFunc.showGeneralMessage(generalFunc.retrieveLangLBl("", "LBL_LOW_WALLET_BALANCE"), walletMsg,
                                            generalFunc.retrieveLangLBl("", "LBL_CANCEL_TXT"), generalFunc.retrieveLangLBl("", "LBL_ADD_MONEY"), button_Id -> {
                                                if (button_Id == 1) {

                                                    new StartActProcess(getActContext()).startAct(MyWalletActivity.class);
                                                } else {

                                                }
                                            });

                                } else {

                                    generalFunc.showGeneralMessage(generalFunc.retrieveLangLBl("", "LBL_LOW_WALLET_BALANCE"), walletMsg, generalFunc.getJsonValue("IS_RESTRICT_TO_WALLET_AMOUNT", responseString).equalsIgnoreCase("No") ? generalFunc.retrieveLangLBl("", "LBL_OK") :
                                            generalFunc.retrieveLangLBl("", "LBL_CANCEL_TXT"), generalFunc.retrieveLangLBl("", "LBL_ADD_MONEY"), generalFunc.getJsonValue("IS_RESTRICT_TO_WALLET_AMOUNT", responseString).equalsIgnoreCase("NO") ? generalFunc.retrieveLangLBl("", "LBL_CANCEL_TXT") :
                                            "", button_Id -> {
                                        if (button_Id == 1) {

                                            new StartActProcess(getActContext()).startAct(MyWalletActivity.class);
                                        } else if (button_Id == 0) {
                                            if (generalFunc.getJsonValue("IS_RESTRICT_TO_WALLET_AMOUNT", responseString).equalsIgnoreCase("No")) {
                                                eWalletIgnore = "Yes";
                                                placeOrder();
                                            }
                                        }
                                    });
                                }

                                return;

                            }

                            if (generalFunc.getJsonValue(Utils.message_str, responseString).equalsIgnoreCase("LBL_RESTAURANTS_CLOSE_NOTE")) {
                                generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_RESTAURANTS_CLOSE_NOTE"));

                            } else if (generalFunc.getJsonValue(Utils.message_str, responseString).equalsIgnoreCase("LBL_MENU_ITEM_NOT_AVAILABLE_TXT")) {
                                generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_MENU_ITEM_NOT_AVAILABLE_TXT"));

                            }

                        }
                    } else {
                        generalFunc.showError();
                    }
                }
            });
            exeWebServer.execute();
        } catch (Exception e) {

        }

    }

    public void setGeneralData() {
        HashMap<String, String> storeData = new HashMap<>();
        storeData.put(Utils.PUBNUB_PUB_KEY, generalFunc.getJsonValue("PUBNUB_PUBLISH_KEY", userProfileJson));
        storeData.put(Utils.PUBNUB_SUB_KEY, generalFunc.getJsonValue("PUBNUB_SUBSCRIBE_KEY", userProfileJson));
        storeData.put(Utils.PUBNUB_SEC_KEY, generalFunc.getJsonValue("PUBNUB_SECRET_KEY", userProfileJson));
        storeData.put(Utils.SESSION_ID_KEY, generalFunc.getJsonValue("tSessionId", userProfileJson));
        storeData.put(Utils.RIDER_REQUEST_ACCEPT_TIME_KEY, generalFunc.getJsonValue("RIDER_REQUEST_ACCEPT_TIME", userProfileJson));
        storeData.put(Utils.DEVICE_SESSION_ID_KEY, generalFunc.getJsonValue("tDeviceSessionId", userProfileJson));
        storeData.put(Utils.FETCH_TRIP_STATUS_TIME_INTERVAL_KEY, generalFunc.getJsonValue("FETCH_TRIP_STATUS_TIME_INTERVAL", userProfileJson));
        storeData.put(Utils.APP_DESTINATION_MODE, generalFunc.getJsonValue("APP_DESTINATION_MODE", userProfileJson));
        storeData.put(Utils.APP_TYPE, generalFunc.getJsonValue("APP_TYPE", userProfileJson));
        storeData.put(Utils.SITE_TYPE_KEY, generalFunc.getJsonValue("SITE_TYPE", userProfileJson));
        storeData.put(Utils.ENABLE_TOLL_COST, generalFunc.getJsonValue("ENABLE_TOLL_COST", userProfileJson));
        storeData.put(Utils.TOLL_COST_APP_ID, generalFunc.getJsonValue("TOLL_COST_APP_ID", userProfileJson));
        storeData.put(Utils.TOLL_COST_APP_CODE, generalFunc.getJsonValue("TOLL_COST_APP_CODE", userProfileJson));
        storeData.put(Utils.HANDICAP_ACCESSIBILITY_OPTION, generalFunc.getJsonValue("HANDICAP_ACCESSIBILITY_OPTION", userProfileJson));
        storeData.put(Utils.FEMALE_RIDE_REQ_ENABLE, generalFunc.getJsonValue("FEMALE_RIDE_REQ_ENABLE", userProfileJson));
        storeData.put(Utils.PUBNUB_DISABLED_KEY, generalFunc.getJsonValue("PUBNUB_DISABLED", userProfileJson));

        storeData.put(Utils.ENABLE_SOCKET_CLUSTER_KEY, generalFunc.getJsonValue("ENABLE_SOCKET_CLUSTER", userProfileJson));
        storeData.put(Utils.SC_CONNECT_URL_KEY, generalFunc.getJsonValue("SC_CONNECT_URL", userProfileJson));
        storeData.put(Utils.GOOGLE_SERVER_ANDROID_PASSENGER_APP_KEY, generalFunc.getJsonValue("GOOGLE_SERVER_ANDROID_PASSENGER_APP_KEY", userProfileJson));
        storeData.put("DESTINATION_UPDATE_TIME_INTERVAL", generalFunc.getJsonValue("DESTINATION_UPDATE_TIME_INTERVAL", userProfileJson));
        storeData.put(Utils.DELIVERALL_KEY, generalFunc.getJsonValue(Utils.DELIVERALL_KEY, userProfileJson));
        storeData.put(Utils.ONLYDELIVERALL_KEY, generalFunc.getJsonValue(Utils.ONLYDELIVERALL_KEY, userProfileJson));
        storeData.put("showCountryList", generalFunc.getJsonValue("showCountryList", userProfileJson));

        storeData.put(Utils.ISWALLETBALNCECHANGE, "No");

        /*Multi Delivery Enabled*/
        storeData.put(Utils.ENABLE_MULTI_DELIVERY_KEY, generalFunc.getJsonValue(Utils.ENABLE_MULTI_DELIVERY_KEY, userProfileJson));
        storeData.put(Utils.ALLOW_MULTIPLE_DEST_ADD_KEY, generalFunc.getJsonValue(Utils.ALLOW_MULTIPLE_DEST_ADD_KEY, userProfileJson));
        generalFunc.storeData(storeData);

    }

    public void setPromoCode() {
        if (appliedPromoCode.equalsIgnoreCase("")) {
            defaultPromoView();
        } else {
            appliedPromoView();
        }
    }

    private void placeOrder() {
        if (internetConnection.isNetworkConnected()) {


            if (SITE_TYPE.equalsIgnoreCase("Demo")) {

                generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_NOTE_PLACE_ORDER_DEMO"), generalFunc.retrieveLangLBl("", "LBL_CANCEL_TXT"), generalFunc.retrieveLangLBl("", "LBL_OK"), buttonId -> {
                    if (buttonId == 1) {
                        orderPlaceCall();
                    }
                });
            } else {
                orderPlaceCall();
            }
        } else {
            generalFunc.showError();

        }
    }

    private HashMap<String, String> loadMap() {
        HashMap<String, String> outputMap = new HashMap<>();
        SharedPreferences pSharedPref = getApplicationContext().getSharedPreferences("MyVariables",
                Context.MODE_PRIVATE);
        try {
            if (pSharedPref != null) {
                String jsonString = pSharedPref.getString("paymentMethod", (new JSONObject()).toString());
                JSONObject jsonObject = new JSONObject(jsonString);
                Iterator<String> keysItr = jsonObject.keys();
                while (keysItr.hasNext()) {
                    String key = keysItr.next();
                    outputMap.put(key, (String) jsonObject.get(key));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return outputMap;
    }

    @Override
    public void onpaymentselect(String id, String type) {
        // fixme abubakar delivry type
        paymentId = id;
        paymentType = type;

    }

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Utils.hideKeyboard(getActContext());
            int i = view.getId();
            if (i == R.id.backImgView) {
                onBackPressed();

                // fixme oderbutton
            } else if (i == submitBtnId) {


                if (isWalletSelect.equalsIgnoreCase("no")) {
                    if (paymentType.isEmpty() && paymentType.length() == 0) {
                        Toast.makeText(CheckOutActivity.this, generalFunc.retrieveLangLBl("Please select payment method",
                                "LBL_PLEASE_SELECT_PAYMENT_METHOD"), Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                if (deliveryRadioGroup.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(CheckOutActivity.this, generalFunc.retrieveLangLBl("please select delivery type",
                            "LBL_PLEASE_SELECT_DELIVERY_TYPE "), Toast.LENGTH_SHORT).show();
                    return;
                }


                if (generalFunc.getMemberId().equalsIgnoreCase("")) {
                    Bundle bn = new Bundle();
                    bn.putBoolean("isRestart", false);
                    new StartActProcess(getActContext()).startActForResult(LoginActivity.class, bn, RC_SIGN_IN_UP);
                } else {
                    getUserProfileJson();
                    if (ePaymentOption.equalsIgnoreCase("")) {

                        generalFunc.showMessage(backImgView, generalFunc.retrieveLangLBl("", "LBL_CHOOSE_PAYMENT_METHOD"));
                        return;
                    }

                  /*  if (APP_PAYMENT_MODE.equalsIgnoreCase("Cash") && !checkboxWallet.isChecked() && casenote.getVisibility()==View.VISIBLE) {
                        generalFunc.showMessage(backImgView, generalFunc.retrieveLangLBl("", "LBL_COD_NOT_AVAILABLE_TXT"));
                        return;
                    }
*/
                    if (!restaurantstatus.equalsIgnoreCase("") && restaurantstatus.equalsIgnoreCase("closed")) {
                        generalFunc.showMessage(backImgView, generalFunc.retrieveLangLBl("", "LBL_RESTAURANTS_CLOSE_NOTE"));
                        return;
                    }


                    if (generalFunc.getJsonValue("vPhone", userProfileJson).equals("") || generalFunc.getJsonValue("vEmail", userProfileJson).equals("")) {
                        //open account verification screen
                        if (generalFunc.getMemberId() != null && !generalFunc.getMemberId().equals("")) {
                            if (!generalFunc.getMemberId().equals("")) {
                                Bundle bn = new Bundle();
                                bn.putBoolean("isbackshow", true);
                                new StartActProcess(getActContext()).startActForResult(AccountverificationActivity.class, bn, Utils.SOCIAL_LOGIN_REQ_CODE);
                                return;
                            }
                        }
                    }


                    String ePhoneVerified = generalFunc.getJsonValue("ePhoneVerified", userProfileJson);
                    if (!ePhoneVerified.equalsIgnoreCase("Yes")) {
                        notifyVerifyMobile();
                        return;
                    }


                    // fixme abubakar send oderdata
                    if (ePaymentOption.equalsIgnoreCase("card") && !generalFunc.isDeliverOnlyEnabled() && SYSTEM_PAYMENT_FLOW.equalsIgnoreCase("Method-1")) {
                        if (!Utils.checkText(selectedMethod)) {

                            generalFunc.showMessage(backImgView, generalFunc.retrieveLangLBl("Choose payment method for card payment", "LBL_CHOOSE_PAY_ONLINE_METHOD"));
                            return;

                        }

                        if (Utils.checkText(selectedMethod) && selectedMethod.equalsIgnoreCase("Manual") && cardNumTxt.getVisibility() == View.GONE) {
                            useExistingCardLL.performClick();
                            return;
                        }

                        if (selectedMethod.equalsIgnoreCase("Manual") && cardNumTxt.getVisibility() == View.VISIBLE && isCardValidated == false) {
                            checkPaymentCard(isWalletSelect, true);
                            return;
                        }

                        placeOrder();
                    } else {
                        placeOrder();
                    }


                }
            } else if (i == editOrderTxtView.getId()) {
                if (isFromEditCard) {
                    onBackPressed();
                } else {
                    new StartActProcess(getActContext()).startAct(EditCartActivity.class);
                    finish();
                }
            } else if (i == couponCodeArea.getId()) {
                if (generalFunc.getMemberId().equalsIgnoreCase("")) {
                    generalFunc.showMessage(applyCouponHTxt, generalFunc.retrieveLangLBl("", "LBL_PROMO_CODE_LOGIN_HINT"));
                } else {
                    Bundle bn = new Bundle();
                    bn.putString("CouponCode", appliedPromoCode);
                    bn.putString("eSystem", Utils.eSystem_Type);
                    new StartActProcess(getActContext()).startActForResult(CouponActivity.class, bn, Utils.SELECT_COUPON_REQ_CODE);
                }
            } else if (i == changeAddresssTxt.getId()) {

                Bundle bn = new Bundle();
                bn.putString("iCompanyId", cartList.get(0).getiCompanyId());
                bn.putString("addressid", generalFunc.retrieveValue(Utils.SELECT_ADDRESS_ID));
                bn.putString("eSystem", Utils.eSystem_Type);
                new StartActProcess(getActContext()).startActForResult(ListAddressActivity.class, bn, SEL_ADDRESS);
            } else if (i == useExistingCardLL.getId()) {

                selectedMethod = "Manual";

                if (checkCardConfig(isWalletSelect, false, true)) {
                    existingPaymentView(true);
                } else {
                    defaultPaymentView(true);
                }

            } else if (i == instantPaymentLL.getId()) {
                selectedMethod = "Instant";
                defaultPaymentView(true);
            } else if (i == R.id.editcard_area) {
                OpenCardPaymentAct(isWalletSelect);
            } else if (i == R.id.couponCodeImgView) {
                generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_DELETE_CONFIRM_COUPON_MSG"), generalFunc.retrieveLangLBl("", "LBL_NO"), generalFunc.retrieveLangLBl("", "LBL_YES"), buttonId -> {
                    if (buttonId == 1) {
                        appliedPromoCode = "";
                        CheckOutOrderEstimateDetails("", false);
                        generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_COUPON_REMOVE_SUCCESS"));
                    }
                });

            } else if (i == btn_clearcart.getId()) {

                Realm realm = MyApp.getRealmInstance();
                generalFunc.removeAllRealmData(realm);
                Bundle bn = new Bundle();
                bn.putBoolean("isRestart", true);
                new StartActProcess(getActContext()).startActWithData(EditCartActivity.class, bn);

            } else if (i == rightImgView.getId()) {
                Bundle bn = new Bundle();
                bn.putBoolean("isBack", true);
                // new StartActProcess(mContext).startActForResult(MyProfileActivity.class, bn, Utils.MY_PROFILE_REQ_CODE);
                new StartActProcess(getActContext()).startActWithData(PrescriptionActivity.class, bn);
            }
        }

    }

}
