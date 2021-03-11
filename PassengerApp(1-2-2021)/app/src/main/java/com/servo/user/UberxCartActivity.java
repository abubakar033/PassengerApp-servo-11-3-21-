package com.servo.user;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Html;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.adapter.files.CategoryListItem;
import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.general.files.MyApp;
import com.realmModel.CarWashCartData;
import com.utils.Logger;
import com.utils.Utils;
import com.view.MButton;
import android.widget.TextView;
import com.view.MaterialRippleLayout;
import com.view.editBox.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.HashMap;

import io.realm.Realm;
import io.realm.RealmResults;

public class UberxCartActivity extends AppCompatActivity {

    public GeneralFunctions generalFunc;
    TextView titleTxt, headingTxt, commentHname;
    ImageView backImgView;
    String userProfileJson;
    CategoryListItem categoryListItem;
    MaterialEditText commentBox;
    LinearLayout fareDetailDisplayArea;
    MButton btn_type2;
    int submitBtnId;
    ImageView minusImgView, addImgView;
    TextView QTYNumberTxtView;
    String iMaxQty = "";
    String eAllowQty = "";
    String vSymbol = "";
    LinearLayout qtyArea;
    RealmResults<CarWashCartData> realmCartList;
    TextView removeCartTxt;

    String finalTotal = "";
    int qty = 1;
    CarWashCartData cart = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uberx_cart);

        generalFunc = MyApp.getInstance().getGeneralFun(getActContext());
        userProfileJson = generalFunc.retrieveValue(Utils.USER_PROFILE_JSON);
        categoryListItem = (CategoryListItem) getIntent().getSerializableExtra("data");
        realmCartList = getCartData();
        Realm realm = MyApp.getRealmInstance();
        cart = checksameRecordExist(realm);

        btn_type2 = ((MaterialRippleLayout) findViewById(R.id.btn_type2)).getChildView();
        removeCartTxt = (TextView) findViewById(R.id.removeCartTxt);
        removeCartTxt.setText(generalFunc.retrieveLangLBl("Remove From Cart", "LBL_UFX_REMOVE_FROM_CART"));
        removeCartTxt.setOnClickListener(new setOnClickList());
        btn_type2.setText(generalFunc.retrieveLangLBl("", "LBL_ADD_ITEM"));
        commentHname = (TextView) findViewById(R.id.commentHname);
        commentBox = (MaterialEditText) findViewById(R.id.commentBox);
        if (cart != null) {
            qty = GeneralFunctions.parseIntegerValue(0, cart.getItemCount());
            btn_type2.setText(generalFunc.retrieveLangLBl("", "LBL_UFX_EDIT_CART"));
            commentBox.setText(cart.getSpecialInstruction());
            removeCartTxt.setVisibility(View.VISIBLE);
        }
        titleTxt = (TextView) findViewById(R.id.titleTxt);
        headingTxt = (TextView) findViewById(R.id.headingTxt);

        backImgView = (ImageView) findViewById(R.id.backImgView);

        minusImgView = (ImageView) findViewById(R.id.minusImgView);
        addImgView = (ImageView) findViewById(R.id.addImgView);
        QTYNumberTxtView = (TextView) findViewById(R.id.QTYNumberTxtView);
        QTYNumberTxtView.setText(qty + "");
        qtyArea = (LinearLayout) findViewById(R.id.qtyArea);
        minusImgView.setOnClickListener(new setOnClickList());
        addImgView.setOnClickListener(new setOnClickList());
        fareDetailDisplayArea = (LinearLayout) findViewById(R.id.fareDetailDisplayArea);
        commentBox.setInputType(InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE);
        commentBox.setSingleLine(false);
        commentBox.setHideUnderline(true);
        commentBox.setGravity(Gravity.START | Gravity.TOP);
        commentBox.setLines(3);
        commentBox.setBackgroundDrawable(getResources().getDrawable(R.drawable.login_fb_border));
        commentBox.setPaddings(10, 5, 0, 5);

        submitBtnId = Utils.generateViewId();
        btn_type2.setId(submitBtnId);
        btn_type2.setOnClickListener(new setOnClickList());
        backImgView.setOnClickListener(new setOnClickList());
        titleTxt.setText(generalFunc.retrieveLangLBl("Cart", "LBL_UFX_CART"));
        commentHname.setText(generalFunc.retrieveLangLBl("", "LBL_INS_PROVIDER_BELOW"));

        headingTxt.setText(categoryListItem.getvTitle());

        boolean viewMoreSet = false;
        if (!categoryListItem.getvDesc().trim().equals("")) {
            try {
                String des = Html.fromHtml(categoryListItem.getvDesc()) + "";
                ((TextView) findViewById(R.id.descTxt)).setText(Html.fromHtml(des));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            if (viewMoreSet == false) {
                generalFunc.makeTextViewResizable((TextView) findViewById(R.id.descTxt), 2, "...\n+ " + generalFunc.retrieveLangLBl("View More", "LBL_VIEW_MORE_TXT"), true, R.color.appThemeColor_1, R.dimen.txt_size_16);
                viewMoreSet = true;
            }
        } else {
            (findViewById(R.id.descTxt)).setVisibility(View.GONE);
        }


        getDetails();

    }

    public RealmResults<CarWashCartData> getCartData() {
        try {
            Realm realm = MyApp.getRealmInstance();
            return realm.where(CarWashCartData.class).findAll();
        } catch (Exception e) {
            Logger.d("RealmException", "::" + e.toString());

        }
        return null;
    }

    private void addFareDetailLayout(JSONArray jobjArray, int qty) {

        if (FareDetailsArrNewObj == null) {
            return;
        }

        if (fareDetailDisplayArea.getChildCount() > 0) {
            fareDetailDisplayArea.removeAllViewsInLayout();
        }

        for (int i = 0; i < jobjArray.length(); i++) {
            JSONObject jobject = generalFunc.getJsonObject(jobjArray, i);
            try {
                String data = jobject.names().getString(0);

                addFareDetailRow(data, jobject.get(data).toString(), (jobjArray.length() - 1) == i ? true : false, qty);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private void addFareDetailRow(String row_name, String row_value, boolean isLast, int qty) {
        View convertView = null;
        if (row_name.equalsIgnoreCase("eDisplaySeperator")) {
            convertView = new View(getActContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Utils.dipToPixels(getActContext(), 1));
            params.setMarginStart(Utils.dipToPixels(getActContext(), 10));
            params.setMarginEnd(Utils.dipToPixels(getActContext(), 10));
            convertView.setBackgroundColor(Color.parseColor("#dedede"));
            convertView.setLayoutParams(params);
        } else {
            LayoutInflater infalInflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.design_fare_deatil_row, null);

            convertView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            convertView.setPaddingRelative(Utils.dipToPixels(getActContext(), 10), 0, Utils.dipToPixels(getActContext(), 10), 0);

            convertView.setMinimumHeight(Utils.dipToPixels(getActContext(), 40));

            TextView titleHTxt = (TextView) convertView.findViewById(R.id.titleHTxt);
            TextView titleVTxt = (TextView) convertView.findViewById(R.id.titleVTxt);

            double qtyWiseVal = GeneralFunctions.parseDoubleValue(0, row_value.replace(vSymbol, "")) * qty;


            titleHTxt.setText(generalFunc.convertNumberWithRTL(row_name));
            if (eAllowQty.equalsIgnoreCase("Yes")) {
                titleVTxt.setText(generalFunc.convertNumberWithRTL(vSymbol + " " + generalFunc.convertNumberWithRTL(GeneralFunctions.convertDecimalPlaceDisplay(qtyWiseVal))));
            } else {
                titleVTxt.setText(row_value);
            }

            titleHTxt.setTextColor(Color.parseColor("#303030"));
            titleVTxt.setTextColor(Color.parseColor("#111111"));

            if (isLast) {
                titleHTxt.setTextColor(getActContext().getResources().getColor(R.color.appThemeColor_1));
                titleVTxt.setTextColor(getActContext().getResources().getColor(R.color.appThemeColor_1));

            }
           /* if (isLast) {
                finalTotal = vSymbol + " " + qtyWiseVal;
            }*/
            String qtyWiseValNw = "" + new BigDecimal(qtyWiseVal).setScale(2,
                    BigDecimal.ROUND_HALF_UP);
            if (isLast) {
                finalTotal = vSymbol + qtyWiseValNw;
            }
        }

        if (convertView != null)
            fareDetailDisplayArea.addView(convertView);
    }

    JSONArray FareDetailsArrNewObj = null;

    public void getDetails() {


        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "getVehicleTypeDetails");
        parameters.put("iMemberId", generalFunc.getMemberId());
        parameters.put("SelectedCabType", Utils.CabGeneralType_UberX);
        parameters.put("iVehicleTypeId", categoryListItem.getiVehicleTypeId());
        parameters.put("iDriverId", getIntent().getStringExtra("iDriverId"));

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setCancelAble(false);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(responseString -> {

            if (responseString != null && !responseString.equals("")) {

                boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);
                if (isDataAvail) {

                    String message = generalFunc.getJsonValue(Utils.message_str, responseString);
                    iMaxQty = generalFunc.getJsonValue("iMaxQty", message);
                    eAllowQty = generalFunc.getJsonValue("eAllowQty", message);
                    vSymbol = generalFunc.getJsonValue("vSymbol", message);
                    if (eAllowQty.equalsIgnoreCase("Yes")) {
                        qtyArea.setVisibility(View.VISIBLE);
                    }


                    FareDetailsArrNewObj = generalFunc.getJsonArray("fareDetails", message);


                    addFareDetailLayout(FareDetailsArrNewObj, qty);


                } else {
                    generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", generalFunc.getJsonValue("message", responseString)));
                }

            } else {
                generalFunc.showError();
            }
        });
        exeWebServer.execute();
    }

    public Context getActContext() {
        return UberxCartActivity.this;
    }

    boolean isCartNull;

    public void setRealmData() {
        Realm realm = MyApp.getRealmInstance();
        CarWashCartData cart = checksameRecordExist(realm);

        realm.beginTransaction();
        if (cart == null) {
            isCartNull = true;
            cart = new CarWashCartData();
            cart.setCategoryListItem(categoryListItem);
            cart.setDriverId(getIntent().getStringExtra("iDriverId"));
            cart.setSpecialInstruction(Utils.getText(commentBox));
            cart.setItemCount(QTYNumberTxtView.getText().toString().trim());
            cart.setFinalTotal(finalTotal);
            cart.setvSymbol(vSymbol);
            if (isCartNull) {
                realm.insert(cart);
            } else {
                realm.insertOrUpdate(cart);
            }

        } else {
            cart.setItemCount(QTYNumberTxtView.getText().toString().trim());
            cart.setFinalTotal(finalTotal);
            realm.insertOrUpdate(cart);
        }
        realm.commitTransaction();
        finish();
    }

    public CarWashCartData checksameRecordExist(Realm realm) {
        CarWashCartData cart = null;

        RealmResults<CarWashCartData> cartlist = realm.where(CarWashCartData.class).findAll();

        if (cartlist != null && cartlist.size() > 0)

            for (int i = 0; i < realmCartList.size(); i++) {

                if (categoryListItem.getiVehicleTypeId().equalsIgnoreCase(realmCartList.get(i).getCategoryListItem().getiVehicleTypeId())) {
                    return realmCartList.get(i);
                }
            }


        return cart;
    }


    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Utils.hideKeyboard(UberxCartActivity.this);
            if (view == backImgView) {
                onBackPressed();
            } else if (view == btn_type2) {
                setRealmData();

            } else if (view == addImgView) {


                int QUANTITY = Integer.parseInt(QTYNumberTxtView.getText().toString());


                if (QUANTITY < GeneralFunctions.parseIntegerValue(0, iMaxQty)) {

                    if (QUANTITY >= 1) {
                        QUANTITY = QUANTITY + 1;

                        QTYNumberTxtView.setText(generalFunc.convertNumberWithRTL("" + QUANTITY));
                        addFareDetailLayout(FareDetailsArrNewObj, QUANTITY);
                    }
                } else {
                    generalFunc.showMessage(backImgView, generalFunc.retrieveLangLBl("", "LBL_QUANTITY_CLOSED_TXT"));
                }

            } else if (view == minusImgView) {

                int QUANTITY = Integer.parseInt(QTYNumberTxtView.getText().toString());
                if (QUANTITY > 1) {
                    QUANTITY = QUANTITY - 1;
                    QTYNumberTxtView.setText(generalFunc.convertNumberWithRTL("" + QUANTITY));

                    addFareDetailLayout(FareDetailsArrNewObj, QUANTITY);
                }

            } else if (view == removeCartTxt) {
                Realm realm = MyApp.getRealmInstance();
                realm.beginTransaction();
                cart.deleteFromRealm();
                realm.commitTransaction();
                finish();


            }
        }

    }
}
