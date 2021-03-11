package com.servo.user.deliverAll;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Html;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.servo.user.Help_MainCategory;
import com.servo.user.PrescriptionActivity;
import com.servo.user.R;
import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.general.files.MyApp;
import com.general.files.StartActProcess;
import com.realmModel.Cart;
import com.realmModel.Options;
import com.realmModel.Topping;
import com.utils.Utils;
import com.GenerateAlertBox;
import com.view.MButton;
import android.widget.TextView;
import com.view.MaterialRippleLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class OrderDetailsActivity extends AppCompatActivity {

    LinearLayout billDetails;
    GeneralFunctions generalFunc;

    ImageView backImgView;
    TextView titleTxt, ordertitleTxt;
    String iOrderId = "";
    String iServiceId = "";
    TextView billTitleTxt, totalHText, totalVText;
    View convertView = null;
    LinearLayout farecontainer;
    TextView resturantAddressTxt, deliveryaddressTxt, resturantAddressHTxt, destAddressHTxt;
    TextView paidviaTextH;
    TextView deliverystatusTxt;
    ImageView imgdeliveryStatus;
    View deliveryStatusView;
    LinearLayout deliveryDetails;
    LinearLayout chargeDetailArea, chargeDetailTitleArea;
    private boolean isExpanded = false;
    private ArrayList<HashMap<String, String>> fareList = new ArrayList<>();
    View bgView;
    TextView helpTextView;

    boolean ePaid = false;
    String ePaymentOption = "";
    MButton btn_type2;
    int submitBtnId;
    private RealmResults<Cart> cartRealmList;
    String iCompanyId = "";
    String vCompany = "";
    String toppingId = "";
    String iMenuItemId = "";
    String optionId = "";
    ArrayList<HashMap<String, String>> cartList;
    String currencySymbol = "";
    String fMinOrderValue = "";
    RealmList<Topping> realmToppingList = new RealmList<>();
    RealmList<Options> realmOptionsList = new RealmList<>();
    String isOption = "No";
    String isTooping = "No";
    LinearLayout deliveryCancelDetails;
    TextView deliverycanclestatusTxt;
    ImageView imagecancel;
    LinearLayout btnarea;
    TextView oredrstatusTxt;
    LinearLayout cancelArea;
    private String SYSTEM_PAYMENT_FLOW = "";

    ProgressBar mProgressBar;
    View contentView;

    String userProfileJson = "";
    TextView viewPrescTxtView;
    ArrayList<HashMap<String, String>> imageList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        generalFunc = MyApp.getInstance().getGeneralFun(getActContext());
        SYSTEM_PAYMENT_FLOW = generalFunc.getJsonValue("SYSTEM_PAYMENT_FLOW", userProfileJson);
        cartList = new ArrayList<>();
        billDetails = (LinearLayout) findViewById(R.id.billDetails);
        viewPrescTxtView = (TextView) findViewById(R.id.viewPrescTxtView);
        viewPrescTxtView.setOnClickListener(new setOnClickList());
        titleTxt = (TextView) findViewById(R.id.titleTxt);
        ordertitleTxt = (TextView) findViewById(R.id.ordertitleTxt);
        backImgView = (ImageView) findViewById(R.id.backImgView);
        billTitleTxt = (TextView) findViewById(R.id.billTitleTxt);
        totalHText = (TextView) findViewById(R.id.totalHText);
        totalVText = (TextView) findViewById(R.id.totalVText);
        farecontainer = (LinearLayout) findViewById(R.id.farecontainer);
        resturantAddressTxt = (TextView) findViewById(R.id.resturantAddressTxt);
        resturantAddressHTxt = (TextView) findViewById(R.id.resturantAddressHTxt);
        deliveryaddressTxt = (TextView) findViewById(R.id.deliveryaddressTxt);
        destAddressHTxt = (TextView) findViewById(R.id.destAddressHTxt);
        paidviaTextH = (TextView) findViewById(R.id.paidviaTextH);
        deliverystatusTxt = (TextView) findViewById(R.id.deliverystatusTxt);
        imgdeliveryStatus = (ImageView) findViewById(R.id.imgdeliveryStatus);
        deliveryStatusView = (View) findViewById(R.id.deliveryStatusView);
        deliveryDetails = (LinearLayout) findViewById(R.id.deliveryDetails);
        chargeDetailArea = (LinearLayout) findViewById(R.id.chargeDetailArea);
        chargeDetailTitleArea = (LinearLayout) findViewById(R.id.chargeDetailTitleArea);
        helpTextView = (TextView) findViewById(R.id.helpTextView);
        btn_type2 = ((MaterialRippleLayout) findViewById(R.id.btn_type2)).getChildView();
        deliveryCancelDetails = (LinearLayout) findViewById(R.id.deliveryCancelDetails);
        deliverycanclestatusTxt = (TextView) findViewById(R.id.deliverycanclestatusTxt);
        imagecancel = (ImageView) findViewById(R.id.imagecancel);
        btnarea = (LinearLayout) findViewById(R.id.btnarea);
        oredrstatusTxt = (TextView) findViewById(R.id.oredrstatusTxt);
        cancelArea = (LinearLayout) findViewById(R.id.cancelArea);
        mProgressBar = (ProgressBar) findViewById(R.id.mProgressBar);
        contentView = findViewById(R.id.contentView);


        submitBtnId = Utils.generateViewId();
        btn_type2.setId(submitBtnId);
        btn_type2.setOnClickListener(new setOnClickList());

        helpTextView.setVisibility(View.VISIBLE);
        bgView = (View) findViewById(R.id.bgView);
        bgView.setOnClickListener(new setOnClickList());
        backImgView.setOnClickListener(new setOnClickList());
        helpTextView.setOnClickListener(new setOnClickList());
        iOrderId = getIntent().getStringExtra("iOrderId");
        titleTxt.setVisibility(View.GONE);
        ordertitleTxt.setVisibility(View.VISIBLE);

        chargeDetailArea.setVisibility(View.GONE);

        setLabel();

        getOrderDetails();

    }

    public void setLabel() {
        billTitleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_BILL_DETAILS"));
        totalHText.setText(generalFunc.retrieveLangLBl("", "LBL_TOTAL_TXT"));
        destAddressHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_DELIVERY_ADDRESS"));
        helpTextView.setText(generalFunc.retrieveLangLBl("", "LBL_HELP"));
        btn_type2.setText(generalFunc.retrieveLangLBl("", "LBL_REORDER"));
        viewPrescTxtView.setText(generalFunc.retrieveLangLBl("View Prescription", "LBL_VIEW_PRESCRIPTION"));
    }


    public void getOrderDetails() {

        mProgressBar.setVisibility(View.VISIBLE);
        contentView.setVisibility(View.GONE);
        helpTextView.setVisibility(View.GONE);

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", " GetOrderDetailsRestaurant");
        parameters.put("iOrderId", iOrderId);
        parameters.put("UserType", Utils.userType);
        parameters.put("eSystem", Utils.eSystem_Type);

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), false, generalFunc);
        exeWebServer.setDataResponseListener(responseString -> {

            if (responseString != null && !responseString.equals("")) {

                boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

                if (isDataAvail) {

                    JSONObject message = generalFunc.getJsonObject(Utils.message_str, responseString);

                    iCompanyId = generalFunc.getJsonValueStr("iCompanyId", message);
                    currencySymbol = generalFunc.getJsonValueStr("currencySymbol", message);
                    fMinOrderValue = generalFunc.getJsonValueStr("fMinOrderValue", message);
                    vCompany = generalFunc.getJsonValueStr("vCompany", message);
                    iOrderId = generalFunc.getJsonValueStr("iOrderId", message);
                    iServiceId = generalFunc.getJsonValueStr("iServiceId", message);

                    if (generalFunc.getJsonValueStr("DisplayReorder", message).equalsIgnoreCase("Yes")) {
                        btnarea.setVisibility(View.VISIBLE);
                    } else {
                        btnarea.setVisibility(View.GONE);
                    }

                    resturantAddressTxt.setText(generalFunc.getJsonValueStr("vRestuarantLocation", message));
                    deliveryaddressTxt.setText(generalFunc.getJsonValueStr("DeliveryAddress", message));
                    ordertitleTxt.setText("#" + generalFunc.convertNumberWithRTL(generalFunc.getJsonValueStr("vOrderNo", message)) + "\n" + generalFunc.convertNumberWithRTL(generalFunc.getDateFormatedType(generalFunc.getJsonValueStr("tOrderRequestDate_Org", message), Utils.OriginalDateFormate, Utils.TrackDateFormatewithTime)));
                    resturantAddressHTxt.setText(generalFunc.getJsonValueStr("vCompany", message));
                    JSONArray itemListArr = null;
                    itemListArr = generalFunc.getJsonArray("itemlist", message);
                    if (billDetails.getChildCount() > 0) {
                        billDetails.removeAllViewsInLayout();
                    }
                    addItemDetailLayout(itemListArr);

                    JSONArray PrescriptionImages = generalFunc.getJsonArray("PrescriptionImages", message);

                    if (PrescriptionImages != null && !PrescriptionImages.equals("")) {
                        viewPrescTxtView.setVisibility(View.VISIBLE);

                        for (int i = 0; i < PrescriptionImages.length(); i++) {
                            HashMap<String, String> map = new HashMap<>();
                            map.put("vImage", generalFunc.getJsonValue(PrescriptionImages, i).toString());
                            imageList.add(map);

                        }

                    }


                    String LBL_PAID_VIA=generalFunc.retrieveLangLBl("", "LBL_PAID_VIA");
                    if (generalFunc.getJsonValueStr("ePaymentOption", message).equalsIgnoreCase("Cash")) {
                        paidviaTextH.setText(LBL_PAID_VIA + " " + generalFunc.retrieveLangLBl("", "LBL_CASH_TXT"));
                    } else if (generalFunc.getJsonValueStr("ePaymentOption", message).equalsIgnoreCase("Card")) {
                        if (!SYSTEM_PAYMENT_FLOW.equalsIgnoreCase("Method-1")) {
                            paidviaTextH.setText(generalFunc.retrieveLangLBl("", "LBL_PAID_VIA_WALLET"));
                        } else {
                            paidviaTextH.setText(LBL_PAID_VIA + " " + generalFunc.retrieveLangLBl("", "LBL_CARD"));
                        }
                    }


                    JSONArray FareDetailsArr = null;
                    FareDetailsArr = generalFunc.getJsonArray("FareDetailsArr", message);

                    if (generalFunc.getJsonValueStr("iStatusCode", message).equalsIgnoreCase("6") && generalFunc.getJsonValueStr("ePaid", message).equals("Yes")) {
                        ePaid = true;
                        ePaymentOption = generalFunc.getJsonValueStr("ePaymentOption", message);
                        deliveryDetails.setVisibility(View.VISIBLE);
                        paidviaTextH.setVisibility(View.GONE);
                        deliveryStatusView.setVisibility(View.VISIBLE);

                        deliverystatusTxt.setText(Html.fromHtml(generalFunc.getJsonValueStr("OrderStatustext", message)));


                        JSONArray OrderDataArray = generalFunc.getJsonArray("DataReorder", message);

                        if (OrderDataArray != null) {
                            for (int i = 0; i < OrderDataArray.length(); i++) {
                                JSONObject orderObj = generalFunc.getJsonObject(OrderDataArray, i);
                                HashMap<String, String> map = new HashMap<>();
                                map.put("Qty", generalFunc.getJsonValueStr("iQty", orderObj));
                                map.put("vItemType", generalFunc.getJsonValueStr("MenuItem", orderObj));
                                map.put("vImage", generalFunc.getJsonValueStr("vImage", orderObj));
                                map.put("fDiscountPrice", generalFunc.getJsonValueStr("fPrice", orderObj));

                                map.put("eFoodType", generalFunc.getJsonValueStr("eFoodType", orderObj));
                                map.put("iFoodMenuId", generalFunc.getJsonValueStr("iFoodMenuId", orderObj));
                                map.put("iCompanyId", iCompanyId);
                                map.put("vCompany", vCompany);
                                optionId = generalFunc.getJsonValueStr("vOptionId", orderObj);
                                iMenuItemId = generalFunc.getJsonValueStr("iMenuItemId", orderObj);
                                map.put("iMenuItemId", iMenuItemId);
                                map.put("optionId", optionId);
                                map.put("ispriceshow", generalFunc.getJsonValue("ispriceshow", responseString));

                                JSONArray toppingArray = generalFunc.getJsonArray("AddOnItemArr", orderObj);

                                if (toppingArray != null) {
                                    for (int j = 0; j < toppingArray.length(); j++) {

                                        JSONObject toppingObject = generalFunc.getJsonObject(toppingArray, j);

                                        if (toppingId.equals("")) {
                                            toppingId = generalFunc.getJsonValueStr("vAddonId", toppingObject);
                                        } else {
                                            toppingId = toppingId + "," + generalFunc.getJsonValueStr("vAddonId", toppingObject);
                                        }


                                    }
                                }

                                map.put("iToppingId", toppingId);
                                cartList.add(map);
                            }

                            JSONArray optionArray = generalFunc.getJsonArray("options", message);
                            for (int i = 0; i < optionArray.length(); i++) {
                                isOption = "Yes";
                                JSONObject optionObject = generalFunc.getJsonObject(optionArray, i);

                                Options optionsObj = new Options();
                                optionsObj.setfPrice(generalFunc.getJsonValueStr("fPrice", optionObject));
                                optionsObj.setfUserPrice(generalFunc.getJsonValueStr("fUserPrice", optionObject));
                                optionsObj.setfUserPriceWithSymbol(generalFunc.getJsonValueStr("fUserPriceWithSymbol", optionObject));
                                optionsObj.setiFoodMenuId(generalFunc.getJsonValueStr("iFoodMenuId", optionObject));
                                optionsObj.setiMenuItemId(generalFunc.getJsonValueStr("iMenuItemId", optionObject));
                                optionsObj.setvOptionName(generalFunc.getJsonValueStr("vOptionName", optionObject));
                                optionsObj.setiOptionId(generalFunc.getJsonValueStr("iOptionId", optionObject));
                                optionsObj.seteDefault(generalFunc.getJsonValueStr("eDefault", optionObject));
                                realmOptionsList.add(optionsObj);
                            }

                            JSONArray addOnArray = generalFunc.getJsonArray("addon", message);
                            for (int i = 0; i < addOnArray.length(); i++) {
                                isTooping = "Yes";
                                JSONObject topingObject = generalFunc.getJsonObject(addOnArray, i);
                                Topping topppingObj = new Topping();
                                topppingObj.setfPrice(generalFunc.getJsonValueStr("fPrice", topingObject));
                                topppingObj.setfUserPrice(generalFunc.getJsonValueStr("fUserPrice", topingObject));
                                topppingObj.setfUserPriceWithSymbol(generalFunc.getJsonValueStr("fUserPriceWithSymbol", topingObject));
                                topppingObj.setiFoodMenuId(generalFunc.getJsonValueStr("iFoodMenuId", topingObject));
                                topppingObj.setiMenuItemId(generalFunc.getJsonValueStr("iMenuItemId", topingObject));
                                topppingObj.setvOptionName(generalFunc.getJsonValueStr("vOptionName", topingObject));
                                topppingObj.setiOptionId(generalFunc.getJsonValueStr("iOptionId", topingObject));
                                realmToppingList.add(topppingObj);
                            }
                        }

                    } else if (generalFunc.getJsonValueStr("iStatusCode", message).equalsIgnoreCase("8")) {
                        deliveryCancelDetails.setVisibility(View.VISIBLE);
                        deliverycanclestatusTxt.setText(generalFunc.getJsonValueStr("OrderStatustext", message));
                        imagecancel.setVisibility(View.VISIBLE);
                        if (!generalFunc.getJsonValueStr("OrderMessage", message).equals("") && generalFunc.getJsonValueStr("OrderMessage", message) != null) {
                            oredrstatusTxt.setVisibility(View.VISIBLE);
                            oredrstatusTxt.setText(generalFunc.getJsonValueStr("OrderMessage", message));
                        }
                    } else if (generalFunc.getJsonValueStr("iStatusCode", message).equalsIgnoreCase("7")) {
                        deliveryCancelDetails.setVisibility(View.VISIBLE);
                        cancelArea.setVisibility(View.GONE);
                        if (!generalFunc.getJsonValueStr("OrderMessage", message).equals("") && generalFunc.getJsonValueStr("OrderMessage", message) != null) {
                            oredrstatusTxt.setVisibility(View.VISIBLE);
                            oredrstatusTxt.setText(generalFunc.getJsonValueStr("OrderMessage", message));
                        }

                    } else {
                        deliveryDetails.setVisibility(View.GONE);
                        paidviaTextH.setVisibility(View.GONE);
                        deliveryStatusView.setVisibility(View.GONE);
                    }
                    setChargeDetails(FareDetailsArr);

                    helpTextView.setVisibility(View.VISIBLE);
                    contentView.setVisibility(View.VISIBLE);
                } else {
                    generalFunc.showError(true);
                }
            } else {
                generalFunc.showError(true);
            }

            mProgressBar.setVisibility(View.GONE);

        });
        exeWebServer.execute();
    }

    private void addItemDetailLayout(JSONArray jobjArray) {


        for (int i = 0; i < jobjArray.length(); i++) {
            JSONObject jobject = generalFunc.getJsonObject(jobjArray, i);
            try {
                additemDetailRow(jobject.getString("MenuItem"), jobject.getString("SubTitle"), jobject.getString("fTotPrice"), " x " + jobject.get("iQty"), jobject.getString("TotalDiscountPrice"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }


    private void setChargeDetails(JSONArray fareArr) {

        if (chargeDetailArea.getChildCount() > 0) {
            chargeDetailArea.removeAllViewsInLayout();
        }

        if (chargeDetailTitleArea.getChildCount() > 0) {
            chargeDetailTitleArea.removeAllViewsInLayout();
            isExpanded = false;
        }

        fareList.clear();
        chargeDetailArea.removeAllViews();
        chargeDetailTitleArea.removeAllViews();

        for (int i = 0; i < fareArr.length(); i++) {
            HashMap<String, String> mapData = new HashMap<>();
            JSONObject obj_tmp = generalFunc.getJsonObject(fareArr, i);

            try {
                mapData.put("Name", obj_tmp.names().getString(0));
                mapData.put("Amount", obj_tmp.get(obj_tmp.names().getString(0)).toString());
            } catch (JSONException e) {
                e.printStackTrace();
                mapData.put("Name", "");
                mapData.put("Amount", "");
            }

            fareList.add(mapData);
        }


        List<HashMap<String, String>> answer = new ArrayList<HashMap<String, String>>();

        for (int i = 0; i < fareList.size(); i++) {
            if (i == fareList.size() - 1) {
                setChildData(i, true);
            } else {
                answer.add(fareList.get(i));
                setChildData(i, false);
            }
        }
    }


    private void setChildData(int i, boolean isLast) {
        LayoutInflater infalInflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View convertView = infalInflater.inflate(R.layout.charge_header_cell, null);

        TextView itemNameTxt = (TextView) convertView.findViewById(R.id.itemNameTxt);
        LinearLayout orderDetailArea = (LinearLayout) convertView.findViewById(R.id.orderDetailArea);
        TextView fareTxt = (TextView) convertView.findViewById(R.id.fareTxt);
        TextView paymentTxt = (TextView) convertView.findViewById(R.id.paymentTxt);
        View shadowView = (View) convertView.findViewById(R.id.shadowView);
        itemNameTxt.setText(fareList.get(i).get("Name"));
        fareTxt.setText(generalFunc.convertNumberWithRTL(fareList.get(i).get("Amount")));

        final ImageView indicatorImg = (ImageView) convertView.findViewById(R.id.indicatorImg);

        Typeface fontLight = Typeface.createFromAsset(getAssets(), getActContext().getResources().getString(R.string.robotolightFont));
        Typeface fontMedium = Typeface.createFromAsset(getAssets(), getActContext().getResources().getString(R.string.robotomediumFont));
        Typeface fontBold = Typeface.createFromAsset(getAssets(), getActContext().getResources().getString(R.string.robotobold));

        if (i == 0) {
            shadowView.setVisibility(View.GONE);
            indicatorImg.setVisibility(View.GONE);

            convertView.setOnClickListener(view -> {
                if (chargeDetailArea.getVisibility() == View.VISIBLE) {
                    chargeDetailArea.setVisibility(View.GONE);
                    bgView.setVisibility(View.GONE);

                    View totalChargeView = chargeDetailTitleArea.getChildAt(0);
                    ((ImageView) totalChargeView.findViewById(R.id.indicatorImg)).setVisibility(View.VISIBLE);
                    (totalChargeView.findViewById(R.id.shadowView)).setVisibility(View.VISIBLE);
                }
            });
            chargeDetailArea.addView(convertView);

            int color=Color.parseColor("#272727");

            itemNameTxt.setTypeface(fontLight);
            itemNameTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            itemNameTxt.setTextColor(color);

            fareTxt.setTypeface(fontMedium);

            fareTxt.setTextColor(color);
            fareTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

        } else if (i == (fareList.size() - 1)) {
            shadowView.setVisibility(View.VISIBLE);
            itemNameTxt.setTypeface(fontMedium);
            fareTxt.setTypeface(fontBold);

            itemNameTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

            itemNameTxt.setTextColor(Color.parseColor("#4a4a4a"));

            fareTxt.setTextColor(Color.parseColor("#000000"));
            fareTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);


            if (isExpanded) {
                indicatorImg.setVisibility(View.INVISIBLE);
            } else {
                indicatorImg.setVisibility(View.VISIBLE);
            }
            indicatorImg.setImageResource(R.mipmap.ic_arrow_up);


            String LBL_PAY_TYPE=generalFunc.retrieveLangLBl("", "LBL_PAY_TYPE");
            if (ePaid) {
                paymentTxt.setVisibility(View.VISIBLE);
                if (ePaymentOption.equalsIgnoreCase("Cash")) {
                    paymentTxt.setText(LBL_PAY_TYPE + " " + generalFunc.retrieveLangLBl("", "LBL_CASH_TXT"));

                } else if (ePaymentOption.equalsIgnoreCase("Card")) {
                    paymentTxt.setText(LBL_PAY_TYPE + " " + generalFunc.retrieveLangLBl("", "LBL_CARD"));

                    if (!SYSTEM_PAYMENT_FLOW.equalsIgnoreCase("Method-1")) {
                        paymentTxt.setText(LBL_PAY_TYPE + " " + generalFunc.retrieveLangLBl("", "LBL_PAID_VIA_WALLET"));
                    } else {
                        paymentTxt.setText(LBL_PAY_TYPE + " " + generalFunc.retrieveLangLBl("", "LBL_CARD"));
                    }

                }
                orderDetailArea.getLayoutParams().height = Utils.dpToPx(getActContext(), 60);
            } else {
                paymentTxt.setVisibility(View.GONE);

                orderDetailArea.getLayoutParams().height = Utils.dpToPx(getActContext(), 40);
            }

            chargeDetailTitleArea.addView(convertView);

            chargeDetailTitleArea.setOnClickListener(new setOnClickList());
        } else {
            int color=Color.parseColor("#272727");
            itemNameTxt.setTypeface(fontLight);
            itemNameTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            itemNameTxt.setTextColor(color);

            fareTxt.setTypeface(fontMedium);

            fareTxt.setTextColor(color);
            fareTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

            shadowView.setVisibility(View.GONE);
            indicatorImg.setVisibility(View.INVISIBLE);

            chargeDetailArea.addView(convertView);
        }

    }


    private void additemDetailRow(String menuitemName, String subMenuName, String itemPrice, String qty, String discountprice) {
        final LayoutInflater inflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_select_bill_design, null);

        TextView billItems = (TextView) view.findViewById(R.id.billItems);
        TextView billItemsQty = (TextView) view.findViewById(R.id.billItemsQty);
        TextView serviceTypeNameTxtView = (TextView) view.findViewById(R.id.serviceTypeNameTxtView);
        TextView strikeoutbillAmount = (TextView) view.findViewById(R.id.strikeoutbillAmount);

        final TextView billAmount = (TextView) view.findViewById(R.id.billAmount);


        billAmount.setText(generalFunc.convertNumberWithRTL(itemPrice));
        billItemsQty.setText(generalFunc.convertNumberWithRTL(qty));

        billItems.setText(menuitemName);
        if (!subMenuName.equalsIgnoreCase("")) {
            serviceTypeNameTxtView.setVisibility(View.VISIBLE);
            serviceTypeNameTxtView.setText(subMenuName);
        } else {
            serviceTypeNameTxtView.setVisibility(View.GONE);
        }

        if (discountprice != null && !discountprice.equals("")) {
            billAmount.setTextColor(getActContext().getResources().getColor(R.color.gray));

            SpannableStringBuilder spanBuilder = new SpannableStringBuilder();
            SpannableString origSpan = new SpannableString(billAmount.getText());

            origSpan.setSpan(new StrikethroughSpan(), 0, billAmount.getText().length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

            spanBuilder.append(origSpan);

            String priceStr = "\n" + generalFunc.convertNumberWithRTL(discountprice);

            SpannableString discountSpan = new SpannableString(priceStr);
            discountSpan.setSpan(new ForegroundColorSpan(Color.parseColor("#272727")), 0, priceStr.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            spanBuilder.append(discountSpan);

            billAmount.setText(spanBuilder);
        } else {

            billAmount.setTextColor(Color.parseColor("#272727"));
            billAmount.setPaintFlags(billAmount.getPaintFlags());
        }


        billDetails.addView(view);
    }


    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Utils.hideKeyboard(getActContext());
            int i = view.getId();
            if (i == R.id.backImgView) {
                onBackPressed();
            } else if (i == chargeDetailTitleArea.getId()) {

                if (chargeDetailArea.getVisibility() == View.GONE) {
                    chargeDetailArea.setVisibility(View.VISIBLE);
                    bgView.setVisibility(View.VISIBLE);

                    View totalChargeView = chargeDetailTitleArea.getChildAt(0);
                    ((ImageView) totalChargeView.findViewById(R.id.indicatorImg)).setVisibility(View.INVISIBLE);
                    (totalChargeView.findViewById(R.id.shadowView)).setVisibility(View.GONE);

                    View firstChargeView = chargeDetailArea.getChildAt(0);
                    ((ImageView) firstChargeView.findViewById(R.id.indicatorImg)).setVisibility(View.VISIBLE);
                    (firstChargeView.findViewById(R.id.shadowView)).setVisibility(View.VISIBLE);
                }

            } else if (i == bgView.getId()) {
                if (chargeDetailArea.getVisibility() == View.VISIBLE) {
                    chargeDetailArea.setVisibility(View.GONE);
                    bgView.setVisibility(View.GONE);

                    View totalChargeView = chargeDetailTitleArea.getChildAt(0);
                    ((ImageView) totalChargeView.findViewById(R.id.indicatorImg)).setVisibility(View.VISIBLE);
                    (totalChargeView.findViewById(R.id.shadowView)).setVisibility(View.VISIBLE);
                }
            } else if (i == helpTextView.getId()) {
                Bundle bn = new Bundle();
                bn.putString("iOrderId", iOrderId);
                new StartActProcess(getActContext()).startActWithData(Help_MainCategory.class, bn);
            } else if (i == btn_type2.getId()) {
                manageReorder();
            } else if (view.getId() == viewPrescTxtView.getId()) {

                Bundle bn = new Bundle();
                bn.putSerializable("imageList", imageList);
                bn.putBoolean("isOrder", true);
                (new StartActProcess(getActContext())).startActWithData(PrescriptionActivity.class, bn);

            }
        }
    }


    public void addDataToList(Realm realm) {
        ArrayList<String> removeData=new ArrayList<>();
        removeData.add(Utils.COMPANY_ID);
        removeData.add(Utils.COMPANY_MINIMUM_ORDER);
        generalFunc.removeValue(removeData);
        generalFunc.removeAllRealmData(realm);

        if ((realmOptionsList != null && realmOptionsList.size() > 0) || (realmToppingList != null && realmToppingList.size() > 0)) {
            storeAllOptionsToRealm();
        }

        setRealmdData();
    }

    public void storeAllOptionsToRealm() {
        Realm realm = MyApp.getRealmInstance();
        realm.beginTransaction();
        realm.insertOrUpdate(realmToppingList);
        realm.insertOrUpdate(realmOptionsList);
        realm.commitTransaction();
    }


    boolean isCartNull;

    public void setRealmdData() {

        Realm realm = MyApp.getRealmInstance();

        for (int i = 0; i < cartList.size(); i++) {

            HashMap<String, String> cartData = cartList.get(i);
            realm.beginTransaction();
            //if (cart == null) {
            isCartNull = true;
            Cart cart = new Cart();
            cart.setvItemType(cartData.get("vItemType"));
            cart.setvImage(cartData.get("vImage"));
            cart.setfDiscountPrice(cartData.get("fDiscountPrice"));
            cart.setiMenuItemId(cartData.get("iMenuItemId"));
            cart.seteFoodType(cartData.get("eFoodType"));
            cart.setiServiceId(iServiceId);
            cart.setiFoodMenuId(cartData.get("iFoodMenuId"));
            cart.setiCompanyId(cartData.get("iCompanyId"));
            cart.setvCompany(cartData.get("vCompany"));
            cart.setCurrencySymbol(currencySymbol);
            cart.setQty(cartData.get("Qty"));
            cart.setIsOption(isOption);
            cart.setIsTooping(isTooping);
            cart.setIspriceshow(cartData.get("ispriceshow"));
            cart.setMilliseconds(Calendar.getInstance().getTimeInMillis());


            cart.setiOptionId(cartData.get("optionId"));
            cart.setiToppingId(cartData.get("iToppingId"));
            if (isCartNull) {
                realm.insert(cart);
            } else {
                realm.insertOrUpdate(cart);
            }

            realm.commitTransaction();
        }

        generalFunc.storeData(Utils.COMPANY_MINIMUM_ORDER, fMinOrderValue);
        generalFunc.storeData(Utils.COMPANY_ID, iCompanyId);

        Bundle bn = new Bundle();
        bn.putBoolean("isRestart", true);
        new StartActProcess(getActContext()).startActWithData(EditCartActivity.class, bn);
    }


    public void manageReorder() {
        Realm realm = MyApp.getRealmInstance();
        cartRealmList = realm.where(Cart.class).findAll();

        if (cartRealmList != null && cartRealmList.size() > 0) {

            GenerateAlertBox generateAlertBox = new GenerateAlertBox(getActContext());
            generateAlertBox.setCancelable(false);
            generateAlertBox.setContentMessage(generalFunc.retrieveLangLBl("", "LBL_UPDATE_CART"), generalFunc.retrieveLangLBl("Are you sure you'd like to change restaurants ? Your order will be lost.", "LBL_ORDER_LOST_ALERT_TXT"));
            generateAlertBox.setNegativeBtn(generalFunc.retrieveLangLBl("", "LBL_CANCEL_TXT"));
            generateAlertBox.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_PROCEED"));
            generateAlertBox.setBtnClickList(btn_id -> {
                if (btn_id == 1) {
                    deleteOptionToRealm();

                    addDataToList(realm);

                } else {
                    generateAlertBox.closeAlertBox();
                }
            });
            generateAlertBox.showAlertBox();

        } else {
            deleteOptionToRealm();
            addDataToList(realm);
        }
    }


    public void deleteOptionToRealm() {
        Realm realm = MyApp.getRealmInstance();
        realm.beginTransaction();
        realm.delete(Options.class);
        realm.commitTransaction();

        Realm realmTopping = MyApp.getRealmInstance();
        realmTopping.beginTransaction();
        realmTopping.delete(Topping.class);
        realmTopping.commitTransaction();
    }

    public Context getActContext() {
        return OrderDetailsActivity.this;
    }
}
