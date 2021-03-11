package com.fragments;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import com.TimeDistanceFare;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

import com.adapter.files.CabTypeAdapter;
import com.adapter.files.PoolSeatsSelectionAdapter;
import com.google.gson.JsonArray;
import com.servo.user.BusinessSelectPaymentActivity;
import com.servo.user.CouponActivity;
import com.servo.user.FareBreakDownActivity;
import com.servo.user.MainActivity;
import com.servo.user.MyWalletActivity;
import com.servo.user.R;
import com.servo.user.RentalDetailsActivity;
import com.drawRoute.DirectionsJSONParser;
import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.general.files.MapAnimator;
import com.general.files.StartActProcess;
import com.general.files.StopOverPointsDataParser;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.maps.android.SphericalUtil;
import com.model.ContactModel;
import com.model.Stop_Over_Points_Data;
import com.squareup.picasso.Picasso;
import com.utils.CommonUtilities;
import com.utils.Logger;
import com.utils.Utils;
import com.view.CreateRoundedView;
import com.GenerateAlertBox;
import com.view.MButton;
import android.widget.TextView;
import android.widget.Toast;

import com.view.MaterialRippleLayout;
import com.view.SelectableRoundedImageView;
import com.view.anim.loader.AVLoadingIndicatorView;
import com.view.editBox.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Retrofit;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class CabSelectionFragment extends Fragment implements CabTypeAdapter.OnItemClickList, ViewTreeObserver.OnGlobalLayoutListener, PoolSeatsSelectionAdapter.OnItemClickList {

    private final static double DEFAULT_CURVE_ROUTE_CURVATURE = 0.5f;
    private final static int DEFAULT_CURVE_POINTS = 60;


    static MainActivity mainAct;
    static GeneralFunctions generalFunc;
    static TextView payTypeTxt;
    static RadioButton cardRadioBtn;
    static ImageView payImgView;
    public MButton ride_now_btn;
    public int currentPanelDefaultStateHeight = 100;
    public String currentCabGeneralType = "";
    public CabTypeAdapter adapter;
    public ArrayList<HashMap<String, String>> cabTypeList;
    public ArrayList<HashMap<String, String>> rentalTypeList;
    public ArrayList<HashMap<String, String>> tempCabTypeList = new ArrayList<>();
    public String app_type = "Ride";
    public ImageView img_ridelater;
    //    public int isSelcted = -1;
    public boolean isclickableridebtn = false;
    public boolean isroutefound = false;
    public int selpos = 0;
    public View view = null;
    public boolean isCardValidated = true;
    public boolean isSkip = false;
    public LatLng sourceLocation = null;
    public LatLng destLocation = null;
    public Marker sourceMarker, destMarker, sourceDotMarker, destDotMarker;

    LinearLayout imageLaterarea;
    String userProfileJson = "";
    RecyclerView carTypeRecyclerView;
    String currency_sign = "";
    boolean isKilled = false;
    LinearLayout paymentArea;
    LinearLayout promoArea;
    View payTypeSelectArea;
    String appliedPromoCode = "";
    RadioButton cashRadioBtn;
    LinearLayout casharea;
    LinearLayout cardarea;
    LinearLayout cashcardarea;
    public String distance = "";
    public String time = "";
    AVLoadingIndicatorView loaderView;
    TextView noServiceTxt;
    boolean isCardnowselcted = false;
    boolean isCardlaterselcted = false;
    String RideDeliveryType = "";
    TextView promoTxt;
    int i = 0;
    ExecuteWebServerUrl estimateFareTask;
    Polyline route_polyLine;
    boolean isRouteFail = false;
    int height = 0;
    int width = 0;

    MarkerOptions source_dot_option, dest_dot_option;
    String required_str = "";
    ProgressBar mProgressBar;
    androidx.appcompat.app.AlertDialog outstanding_dialog;

    //#UberPool
    /*UberPool Related Declaration Start*/
    public MButton confirm_seats_btn;
    public ImageView poolBackImage;
    public PoolSeatsSelectionAdapter seatsSelectionAdapter;
    public TextView poolTxt, poolFareTxt, poolnoseatsTxt, poolnoteTxt;
    RecyclerView poolSeatsRecyclerView;
    LinearLayout cashCardArea, poolArea, mainContentArea;
    public int seatsSelectpos = 0;

    String routeDrawResponse = "";
    public ArrayList<String> poolSeatsList = new ArrayList<>();
    public boolean isPoolCabTypeIdSelected = false;
    /*UberPool Related Declaration End*/

    public ImageView rentalBackImage;
    TextView rentalPkg;
    SelectableRoundedImageView rentPkgImage, rentBackPkgImage;
    public RelativeLayout rentalarea;
    public TextView rentalPkgDesc;
    public static int RENTAL_REQ_CODE = 1234;
    public String iRentalPackageId = "";

    View marker_view;
    TextView addressTxt, etaTxt;
    boolean isRental = false;
    int lstSelectpos = 0;

    public int fragmentWidth = 0;
    public int fragmentHeight = 0;

    LinearLayout organizationArea;
    TextView organizationTxt;
    int noOfSeats = 2;
    public boolean isCardSelect = false;
    ArrayList<Marker> markerArrayList = new ArrayList<>();  // list of markers on map


    ArrayList<Stop_Over_Points_Data> wayPointslist = new ArrayList<>();  // List of Way Points/ middle points
    ArrayList<Stop_Over_Points_Data> destPointlist = new ArrayList<>();  // destination Points
    ArrayList<Stop_Over_Points_Data> finalPointlist = new ArrayList<>();  // final Points list with time & distance & based on shortest location first algorithm
    ArrayList<Stop_Over_Points_Data> stop_Over_Points_Temp_Array = new ArrayList<Stop_Over_Points_Data>();
    LatLngBounds.Builder builder = new LatLngBounds.Builder();
    private String APP_PAYMENT_MODE = "";
    private String APP_PAYMENT_METHOD = "";
    private String SYSTEM_PAYMENT_FLOW = "";
    int DRIVER_ARRIVED_MIN_TIME_PER_MINUTE = 3;


    public static void setCardSelection() {
        if (generalFunc == null) {
            generalFunc = mainAct.generalFunc;
        }


        payTypeTxt.setText(generalFunc.retrieveLangLBl("", "LBL_CARD"));
        mainAct.setCashSelection(false);
        cardRadioBtn.setChecked(true);
        payImgView.setImageResource(R.drawable.money_green);
    }

    public static void setWalletSelection() {
        if (generalFunc == null) {
            generalFunc = mainAct.generalFunc;
        }

        payTypeTxt.setText(generalFunc.retrieveLangLBl("", "LBL_PAY_BY_WALLET_TXT"));
        mainAct.setCashSelection(false);
        cardRadioBtn.setChecked(true);
        payImgView.setImageResource(R.drawable.green_wallet);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (view != null) {
            return view;
        }


        view = inflater.inflate(R.layout.fragment_new_cab_selection, container, false);
        mainAct = (MainActivity) getActivity();
        generalFunc = mainAct.generalFunc;
        DRIVER_ARRIVED_MIN_TIME_PER_MINUTE = generalFunc.parseIntegerValue(3, generalFunc.getJsonValue("DRIVER_ARRIVED_MIN_TIME_PER_MINUTE", mainAct.userProfileJson));

        //#UberPool
        /* Pool related views declaration started */
        poolBackImage = (ImageView) view.findViewById(R.id.poolBackImage);
        poolFareTxt = (TextView) view.findViewById(R.id.poolFareTxt);
        poolTxt = (TextView) view.findViewById(R.id.poolTxt);
        poolnoseatsTxt = (TextView) view.findViewById(R.id.poolnoseatsTxt);
        poolnoteTxt = (TextView) view.findViewById(R.id.poolnoteTxt);
        poolSeatsRecyclerView = (RecyclerView) view.findViewById(R.id.poolSeatsRecyclerView);
        cashCardArea = (LinearLayout) view.findViewById(R.id.cashcardarea);
        poolArea = (LinearLayout) view.findViewById(R.id.poolArea);
        mainContentArea = (LinearLayout) view.findViewById(R.id.mainContentArea);
        poolBackImage.setOnClickListener(new setOnClickList());
        /* Pool related views declaration ended */

        rentalBackImage = (ImageView) view.findViewById(R.id.rentalBackImage);
        rentalarea = (RelativeLayout) view.findViewById(R.id.rentalarea);
        organizationArea = (LinearLayout) view.findViewById(R.id.organizationArea);
        rentPkgImage = (SelectableRoundedImageView) view.findViewById(R.id.rentPkgImage);
        rentBackPkgImage = (SelectableRoundedImageView) view.findViewById(R.id.rentBackPkgImage);
        rentalPkg = (TextView) view.findViewById(R.id.rentalPkg);
        rentalPkgDesc = (TextView) view.findViewById(R.id.rentalPkgDesc);
        organizationTxt = (TextView) view.findViewById(R.id.organizationTxt);
        rentalBackImage.setOnClickListener(new setOnClickList());
        rentalPkg.setOnClickListener(new setOnClickList());
        rentPkgImage.setOnClickListener(new setOnClickList());
        img_ridelater = (ImageView) view.findViewById(R.id.img_ridelater);
        organizationArea.setOnClickListener(new setOnClickList());
        if (generalFunc.isRTLmode()) {
            rentalBackImage.setRotation(180);
            img_ridelater.setRotationY(180);
            poolBackImage.setRotation(180);
        }

        for (int i = 0; i < noOfSeats; i++) {
            poolSeatsList.add("" + (i + 1));
        }

        poolnoseatsTxt.setText(generalFunc.retrieveLangLBl("How Many seats do you need?", "LBL_POOL_SEATS"));
        poolnoteTxt.setText(generalFunc.retrieveLangLBl("This fare is based on our estimation. This may vary during trip and final fare.", "LBL_GENERAL_NOTE_FARE_EST"));
        poolTxt.setText(generalFunc.retrieveLangLBl("Pool", "LBL_POOL"));
        if (mainAct.eShowOnlyMoto != null && mainAct.eShowOnlyMoto.equalsIgnoreCase("Yes")) {
            rentalPkg.setText(generalFunc.retrieveLangLBl("", "LBL_RENT_MOTO_TITLE_TXT"));
            rentalPkgDesc.setText(generalFunc.retrieveLangLBl("", "LBL_RENT_MOTO_PKG_INFO"));
        } else {
            rentalPkg.setText(generalFunc.retrieveLangLBl("", "LBL_RENT_A_CAR"));
            rentalPkgDesc.setText(generalFunc.retrieveLangLBl("", "LBL_RENT_PKG_INFO"));
        }

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager()
                .getDefaultDisplay()
                .getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;


        height = displayMetrics.heightPixels - Utils.dpToPx(getActContext(), 300);

        ride_now_btn = ((MaterialRippleLayout) view.findViewById(R.id.ride_now_btn)).getChildView();

        confirm_seats_btn = ((MaterialRippleLayout) view.findViewById(R.id.confirm_seats_btn)).getChildView();
        ride_now_btn.setId(Utils.generateViewId());
        confirm_seats_btn.setId(Utils.generateViewId());
        confirm_seats_btn.setAllCaps(true);

        mProgressBar = (ProgressBar) view.findViewById(R.id.mProgressBar);
        mProgressBar.getIndeterminateDrawable().setColorFilter(
                getActContext().getResources().getColor(R.color.appThemeColor_2), android.graphics.PorterDuff.Mode.SRC_IN);
        findRoute("--");
        RideDeliveryType = getArguments().getString("RideDeliveryType");

        carTypeRecyclerView = (RecyclerView) view.findViewById(R.id.carTypeRecyclerView);
        loaderView = (AVLoadingIndicatorView) view.findViewById(R.id.loaderView);
        payTypeSelectArea = view.findViewById(R.id.payTypeSelectArea);
        payTypeTxt = (TextView) view.findViewById(R.id.payTypeTxt);
        promoTxt = (TextView) view.findViewById(R.id.promoTxt);
        promoTxt.setText(generalFunc.retrieveLangLBl("", "LBL_COUPON"));


        imageLaterarea = (LinearLayout) view.findViewById(R.id.imageLaterarea);
        noServiceTxt = (TextView) view.findViewById(R.id.noServiceTxt);


        casharea = (LinearLayout) view.findViewById(R.id.casharea);
        cardarea = (LinearLayout) view.findViewById(R.id.cardarea);

        casharea.setOnClickListener(new setOnClickList());
        cardarea.setOnClickListener(new setOnClickList());

        img_ridelater.setOnClickListener(new setOnClickList());

        if (generalFunc.getJsonValueStr("ENABLE_CORPORATE_PROFILE", mainAct.obj_userProfile).equalsIgnoreCase("Yes") && mainAct.getCurrentCabGeneralType().equalsIgnoreCase("Ride")) {
            organizationArea.setVisibility(View.VISIBLE);
            organizationTxt.setText(generalFunc.retrieveLangLBl("", "LBL_PERSONAL"));
            LinearLayout.LayoutParams organizationLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            organizationLayoutParams.setMargins(0, 0, 0, -Utils.dpToPx(getActContext(), 5));
            organizationArea.setLayoutParams(organizationLayoutParams);
        }


        new CreateRoundedView(getActContext().getResources().getColor(R.color.white), Utils.dipToPixels(getActContext(), 14), 1,
                getActContext().getResources().getColor(R.color.gray), rentBackPkgImage);
        new CreateRoundedView(getActContext().getResources().getColor(R.color.appThemeColor_1), Utils.dipToPixels(getActContext(), 12), 0,
                getActContext().getResources().getColor(R.color.appThemeColor_2), rentPkgImage);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_car);
        Drawable d = new BitmapDrawable(getResources(), bitmap);
        d.setColorFilter(getActContext().getResources().getColor(R.color.appThemeColor_TXT_1), PorterDuff.Mode.MULTIPLY);
        rentPkgImage.setImageDrawable(d);

        paymentArea = (LinearLayout) view.findViewById(R.id.paymentArea);
        promoArea = (LinearLayout) view.findViewById(R.id.promoArea);
        promoArea.setOnClickListener(new setOnClickList());
        paymentArea.setOnClickListener(new setOnClickList());
        cashRadioBtn = (RadioButton) view.findViewById(R.id.cashRadioBtn);
        cardRadioBtn = (RadioButton) view.findViewById(R.id.cardRadioBtn);

        payImgView = (ImageView) view.findViewById(R.id.payImgView);

        cashcardarea = (LinearLayout) view.findViewById(R.id.cashcardarea);

        getUserProfileJson(mainAct.userProfileJson);

        currency_sign = generalFunc.getJsonValue("CurrencySymbol", userProfileJson);
        app_type = generalFunc.getJsonValue("APP_TYPE", userProfileJson);

        showBookingLaterArea();

        if (mainAct.isDeliver(mainAct.getCurrentCabGeneralType())) {
            img_ridelater.setImageResource(R.mipmap.ride_later_delivery);
        }

        if (app_type.equalsIgnoreCase(Utils.CabGeneralTypeRide_Delivery_UberX)) {
            app_type = "Ride";
        }

        if (app_type.equals(Utils.CabGeneralType_UberX)) {
            view.setVisibility(View.GONE);
            return view;
        }

        isKilled = false;

        if (mainAct.isMultiDelivery()) {

            FrameLayout.LayoutParams params = new
                    FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            // Set the height by params

            params.height = Utils.dpToPx(getActContext(), 118);
            // set height of RecyclerView

            cashcardarea.setVisibility(View.GONE);
            view.findViewById(R.id.belowShadow).setVisibility(View.GONE);

            // set margin top for higher devices

            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) view.findViewById(R.id.sendRequestArea).getLayoutParams();
            mlp.setMargins(mlp.leftMargin, 10, mlp.rightMargin, mlp.bottomMargin);

        } else {

            cashRadioBtn.setText(generalFunc.retrieveLangLBl("", "LBL_CASH_TXT"));
            cardRadioBtn.setText(generalFunc.retrieveLangLBl("", "LBL_CARD"));
            setCashSelection();
            if (APP_PAYMENT_MODE.equalsIgnoreCase("Cash")) {
                cashRadioBtn.setVisibility(View.VISIBLE);
                cardRadioBtn.setVisibility(View.GONE);
            } else if (APP_PAYMENT_MODE.equalsIgnoreCase("Card")) {
                cashRadioBtn.setVisibility(View.GONE);
                cardRadioBtn.setVisibility(View.VISIBLE);
                isCardValidated = true;
                if (SYSTEM_PAYMENT_FLOW.equalsIgnoreCase("Method-1")) {
                    setCardSelection();
                } else {
                    setWalletSelection();
                }
                isCardValidated = false;
            } else if (SYSTEM_PAYMENT_FLOW.equalsIgnoreCase("Method-1")) {
                cardRadioBtn.setText(generalFunc.retrieveLangLBl("", "LBL_CARD"));
                cashRadioBtn.setVisibility(View.VISIBLE);
                cardRadioBtn.setVisibility(View.VISIBLE);
            } else if (!SYSTEM_PAYMENT_FLOW.equalsIgnoreCase("Method-1")) {
                cardRadioBtn.setText(generalFunc.retrieveLangLBl("Pay by Wallet", "LBL_PAY_BY_WALLET_TXT") + "(" + generalFunc.convertNumberWithRTL(generalFunc.getJsonValue("user_available_balance", userProfileJson) + ")"));
                cashRadioBtn.setVisibility(View.VISIBLE);
                cardRadioBtn.setVisibility(View.VISIBLE);
            }
        }


        setLabels(true);

        ride_now_btn.setOnClickListener(new setOnClickList());
        confirm_seats_btn.setOnClickListener(new setOnClickList());


        configRideLaterBtnArea(false);

        addGlobalLayoutListner();

        seatsSelectionAdapter = new PoolSeatsSelectionAdapter(getActContext(), poolSeatsList, generalFunc);
        seatsSelectionAdapter.setSelectedSeat(seatsSelectpos);
        poolSeatsRecyclerView.setAdapter(seatsSelectionAdapter);
        seatsSelectionAdapter.notifyDataSetChanged();
        seatsSelectionAdapter.setOnItemClickList(this);

        return view;
    }

    private void showBookingLaterArea() {
        if (generalFunc.getJsonValue("RIDE_LATER_BOOKING_ENABLED", userProfileJson).equalsIgnoreCase("Yes") && !mainAct.isMultiDelivery()) {

            if (isPoolCabTypeIdSelected) {
                imageLaterarea.setVisibility(View.GONE);
            } else if (app_type.equalsIgnoreCase(Utils.CabGeneralTypeRide_Delivery_UberX) && !mainAct.iscubejekRental) {
                imageLaterarea.setVisibility(View.VISIBLE);

            } else if (mainAct.iscubejekRental || mainAct.isRental) {
                imageLaterarea.setVisibility(View.GONE);
            } else {
                imageLaterarea.setVisibility(View.VISIBLE);
            }


        } else {
            imageLaterarea.setVisibility(View.GONE);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        addGlobalLayoutListner();
    }

    private void addGlobalLayoutListner() {

        if (getView() != null) {
            getView().getViewTreeObserver().removeGlobalOnLayoutListener(this);
        }
        if (view != null) {
            view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
        }

        if (getView() != null) {

            getView().getViewTreeObserver().addOnGlobalLayoutListener(this);
        } else if (view != null) {
            view.getViewTreeObserver().addOnGlobalLayoutListener(this);
        }
    }

    public void showLoader() {
        try {
            loaderView.setVisibility(View.VISIBLE);
            closeNoServiceText();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showNoServiceText() {
        noServiceTxt.setVisibility(View.VISIBLE);
    }

    public void closeNoServiceText() {
        noServiceTxt.setVisibility(View.GONE);
    }

    public void closeLoader() {
        try {
            loaderView.setVisibility(View.GONE);
            if (mainAct.cabTypesArrList.size() == 0) {
                showNoServiceText();
            } else {
                closeNoServiceText();
            }
        } catch (Exception e) {

        }
    }

    public void setUserProfileJson() {
        getUserProfileJson(mainAct.userProfileJson);
    }

    public void getUserProfileJson(String userProfileJsonStr) {
        userProfileJson = userProfileJsonStr;
        APP_PAYMENT_MODE = generalFunc.getJsonValue("APP_PAYMENT_MODE", userProfileJson);
        APP_PAYMENT_METHOD = generalFunc.getJsonValue("APP_PAYMENT_METHOD", userProfileJson);
        SYSTEM_PAYMENT_FLOW = generalFunc.getJsonValue("SYSTEM_PAYMENT_FLOW", userProfileJson);
    }

    public void checkCardConfig() {
        setUserProfileJson();


        if (APP_PAYMENT_METHOD.equalsIgnoreCase("Stripe")) {
            String vStripeCusId = generalFunc.getJsonValue("vStripeCusId", userProfileJson);
            if (vStripeCusId.equals("")) {
                mainAct.OpenCardPaymentAct(true);
            } else {
                showPaymentBox(false, false, "", new Intent());
            }
        }

    }

    public void checkCardConfig(boolean isOutstanding, boolean isReqNow, String responseString, Intent data) {
        setUserProfileJson();

        if (APP_PAYMENT_METHOD.equalsIgnoreCase("Stripe")) {
            String vStripeCusId = generalFunc.getJsonValue("vStripeCusId", userProfileJson);
            if (vStripeCusId.equals("")) {
                mainAct.OpenCardPaymentAct(true);
            } else {
                showPaymentBox(isOutstanding, isReqNow, responseString, data);
            }
        }

    }

    public void showPaymentBox(boolean isOutstanding, boolean isReqNow, String responseString, Intent data) {
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
            if (isOutstanding) {
                callOutStandingPayAmout(responseString, data);
            } else {
                checkPaymentCard();
            }
        });
        builder.setNeutralButton(generalFunc.retrieveLangLBl("Change", "LBL_CHANGE"), (dialog, which) -> {
            dialog.cancel();
            mainAct.OpenCardPaymentAct(true);
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

    public void setCashSelection() {
        isCardSelect = false;

        payTypeTxt.setText(generalFunc.retrieveLangLBl("", "LBL_CASH_TXT"));

        isCardValidated = false;
        mainAct.setCashSelection(true);
        cashRadioBtn.setChecked(true);

        payImgView.setImageResource(R.drawable.money_green);
    }

    public void checkPaymentCard() {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "CheckCard");
        parameters.put("iUserId", generalFunc.getMemberId());

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(responseString -> {

            if (responseString != null && !responseString.equals("")) {

                String action = generalFunc.getJsonValue(Utils.action_str, responseString);
                if (action.equals("1")) {

                    if (mainAct.pickUpLocation == null) {
                        return;
                    }
                    isCardValidated = true;
                    setCardSelection();

                    if (isCardnowselcted) {
                        isCardnowselcted = false;


                        if (mainAct.isDeliver(mainAct.getCurrentCabGeneralType())) {
                            if (!mainAct.getDestinationStatus() && !mainAct.isMultiDelivery()) {
                                generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("Please add your destination location " +
                                        "to deliver your package.", "LBL_ADD_DEST_MSG_DELIVER_ITEM"));
                                return;
                            }
                            mainAct.continueDeliveryProcess();
                            return;
                        } else {
                            if (!mainAct.getCabReqType().equals(Utils.CabReqType_Later)) {

                                if (cabTypeList.get(selpos).get("eRental") != null && !cabTypeList.get(selpos).get("eRental").equalsIgnoreCase("") &&
                                        cabTypeList.get(selpos).get("eRental").equalsIgnoreCase("Yes")) {

                                    Bundle bn = new Bundle();
                                    bn.putString("address", mainAct.pickUpLocationAddress);
                                    bn.putString("vVehicleType", cabTypeList.get(selpos).get("vRentalVehicleTypeName"));
                                    bn.putString("iVehicleTypeId", cabTypeList.get(selpos).get("iVehicleTypeId"));
                                    bn.putString("vLogo", cabTypeList.get(selpos).get("vLogo1"));
                                    bn.putString("eta", etaTxt.getText().toString());
                                    bn.putString("eMoto", mainAct.eShowOnlyMoto);
                                    bn.putString("PromoCode", appliedPromoCode);


                                    new StartActProcess(getActContext()).startActForResult(
                                            RentalDetailsActivity.class, bn, RENTAL_REQ_CODE);
                                    return;

                                }
                                mainAct.continuePickUpProcess();
                            } else {
                                mainAct.setRideSchedule();
                            }

                        }
                    }

                    if (isCardlaterselcted) {
                        isCardlaterselcted = false;
                        mainAct.chooseDateTime();
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

    public void setLabels(boolean isCallGenerateType) {

        if (mainAct == null) {
            return;
        }

        if (generalFunc == null) {
            generalFunc = mainAct.generalFunc;
        }

        required_str = generalFunc.retrieveLangLBl("", "LBL_FEILD_REQUIRD_ERROR_TXT");
        noServiceTxt.setText(generalFunc.retrieveLangLBl("service not available in this location", "LBL_NO_SERVICE_AVAILABLE_TXT"));

        if (mainAct.currentLoadedDriverList == null || mainAct.currentLoadedDriverList.size() < 1) {
            ride_now_btn.setText(generalFunc.retrieveLangLBl("No Car available.", "LBL_NO_CARS"));
            if (isCallGenerateType) {
                generateCarType();
            }
            return;
        }


        if (app_type.equalsIgnoreCase(Utils.CabGeneralType_UberX)) {
            currentCabGeneralType = Utils.CabGeneralType_UberX;
        } else {
            String type = mainAct.isDeliver(app_type) || mainAct.isDeliver(RideDeliveryType) ? "Deliver" : Utils.CabGeneralType_Ride;
            if (type.equals("Deliver")) {
                if (mainAct.getCabReqType().equals(Utils.CabReqType_Now)) {
                    ride_now_btn.setText(generalFunc.retrieveLangLBl("", "LBL_BTN_NEXT_TXT"));
                } else if (mainAct.getCabReqType().equals(Utils.CabReqType_Later)) {
                    ride_now_btn.setText(generalFunc.retrieveLangLBl("", "LBL_BTN_NEXT_TXT"));
                }
            } else {
                ride_now_btn.setText(generalFunc.retrieveLangLBl("Request Now", "LBL_REQUEST_NOW"));
            }

            if (generalFunc.getSelectedCarTypeData(mainAct.getSelectedCabTypeId(), mainAct.cabTypesArrList, "ePoolStatus").equalsIgnoreCase("Yes")) {
                ride_now_btn.setText(generalFunc.retrieveLangLBl("", "LBL_CONFIRM_SEATS"));
            }

        }
        confirm_seats_btn.setText(generalFunc.retrieveLangLBl("Confirm Seats", "LBL_CONFIRM_SEATS"));
        if (isCallGenerateType) {
            generateCarType();
        }

    }

    public void releaseResources() {
        isKilled = true;
    }

    public void changeCabGeneralType(String currentCabGeneralType) {
        this.currentCabGeneralType = currentCabGeneralType;
    }

    public String getCurrentCabGeneralType() {
        return this.currentCabGeneralType;
    }

    public void configRideLaterBtnArea(boolean isGone) {
        if (mainAct.isMultiDelivery()) {
            mainAct.setPanelHeight(185);
            currentPanelDefaultStateHeight = 185;
        } else {
            if (isGone || app_type.equalsIgnoreCase("Ride-Delivery")) {
                mainAct.setPanelHeight(237);
                if (!app_type.equalsIgnoreCase("Ride-Delivery")) {
                    mainAct.setUserLocImgBtnMargin(105);
                }
                return;
            }
            if (!generalFunc.getJsonValue("RIIDE_LATER", userProfileJson).equalsIgnoreCase("YES") && !app_type.equalsIgnoreCase("Ride-Delivery")) {
                mainAct.setUserLocImgBtnMargin(105);
                mainAct.setPanelHeight(237);
            } else {


                mainAct.setPanelHeight(237);
                currentPanelDefaultStateHeight = 237;
                mainAct.setUserLocImgBtnMargin(164);
            }
        }
    }

    public void generateCarType() {
        if (getActContext() == null) {
            return;
        }

        if (cabTypeList == null) {
            cabTypeList = new ArrayList<>();
            rentalTypeList = new ArrayList<>();
            if (adapter == null) {
                adapter = new CabTypeAdapter(getActContext(), cabTypeList, generalFunc);
                adapter.setSelectedVehicleTypeId(mainAct.getSelectedCabTypeId());
                carTypeRecyclerView.setAdapter(adapter);
                adapter.setOnItemClickList(this);
            } else {
                adapter.notifyDataSetChanged();
            }
        } else {
            cabTypeList.clear();
            rentalTypeList.clear();
        }

        if (mainAct.isDeliver(currentCabGeneralType)) {
            this.currentCabGeneralType = "Deliver";
        }

        String APP_TYPE = generalFunc.getJsonValue("APP_TYPE", userProfileJson);
        for (int i = 0; i < mainAct.cabTypesArrList.size(); i++) {

            HashMap<String, String> map = new HashMap<>();
            String iVehicleTypeId = mainAct.cabTypesArrList.get(i).get("iVehicleTypeId");

            String vVehicleType = mainAct.cabTypesArrList.get(i).get("vVehicleType");
            String vRentalVehicleTypeName = mainAct.cabTypesArrList.get(i).get("vRentalVehicleTypeName");
            String fPricePerKM = mainAct.cabTypesArrList.get(i).get("fPricePerKM");
            String fPricePerMin = mainAct.cabTypesArrList.get(i).get("fPricePerMin");
            String iBaseFare = mainAct.cabTypesArrList.get(i).get("iBaseFare");
            String fCommision = mainAct.cabTypesArrList.get(i).get("fCommision");
            String iPersonSize = mainAct.cabTypesArrList.get(i).get("iPersonSize");
            String vLogo = mainAct.cabTypesArrList.get(i).get("vLogo");
            String vLogo1 = mainAct.cabTypesArrList.get(i).get("vLogo1");
            String eType = mainAct.cabTypesArrList.get(i).get("eType");
            String fPoolPercentage = mainAct.cabTypesArrList.get(i).get("fPoolPercentage");

            String eRental = mainAct.cabTypesArrList.get(i).get("eRental");
            String ePoolStatus = mainAct.cabTypesArrList.get(i).get("ePoolStatus");

            if (!eType.equalsIgnoreCase(currentCabGeneralType)) {
                continue;
            }

            map.put("iVehicleTypeId", iVehicleTypeId);
            map.put("vVehicleType", vVehicleType);
            map.put("vRentalVehicleTypeName", vRentalVehicleTypeName);
            map.put("fPricePerKM", fPricePerKM);
            map.put("fPricePerMin", fPricePerMin);
            map.put("iBaseFare", iBaseFare);
            map.put("fCommision", fCommision);
            map.put("iPersonSize", iPersonSize);
            map.put("vLogo", vLogo);
            map.put("vLogo1", vLogo1);
            map.put("fPoolPercentage", fPoolPercentage);
            //#UberPool Change
            map.put("ePoolStatus", ePoolStatus);

            if (((APP_TYPE.equalsIgnoreCase(Utils.CabGeneralTypeRide_Delivery)) || (APP_TYPE.equalsIgnoreCase(Utils.CabGeneralTypeRide_Delivery_UberX)) || (APP_TYPE.equalsIgnoreCase(Utils.CabGeneralType_Ride))) && (mainAct.iscubejekRental || mainAct.isRental)) {
                map.put("eRental", eRental);
            } else {
                map.put("eRental", "No");
            }

            if (i == 0) {
                adapter.setSelectedVehicleTypeId(iVehicleTypeId);
            }

            cabTypeList.add(map);

            if (eRental != null && eRental.equalsIgnoreCase("Yes")) {
                HashMap<String, String> rentalmap = (HashMap<String, String>) map.clone();
                rentalmap.put("eRental", "Yes");
                rentalTypeList.add(rentalmap);
            }
        }


        manageRentalArea();

        mainAct.setCabTypeList(cabTypeList);
        adapter.notifyDataSetChanged();

        if (cabTypeList.size() > 0) {
            onItemClick(0);
        }
    }

    public void manageRentalArea() {
        if (rentalarea != null && rentalBackImage != null && mainAct.isMultiStopOverEnabled()) {
            // Show or Hide Rental - if MultiStop Over Added
            int rentalArea = rentalarea.getVisibility();
            int rentalBackImgArea = rentalBackImage.getVisibility();

            if (mainAct.stopOverPointsList.size() > 2) {
                hideRentalArea();
            } else if ((rentalArea == View.GONE && rentalBackImgArea == View.GONE)) {
                showRentalArea();
            }
        } else {
            showRentalArea();
        }

    }

    public void showRentalArea() {
        if (mainAct != null && !mainAct.iscubejekRental) {
            if (rentalTypeList != null && rentalTypeList.size() > 0) {
                String APP_TYPE = generalFunc.getJsonValue("APP_TYPE", userProfileJson);

                if (APP_TYPE.equalsIgnoreCase(Utils.CabGeneralType_Ride) || APP_TYPE.equalsIgnoreCase("Ride-Delivery") || (RideDeliveryType.equalsIgnoreCase(Utils.CabGeneralType_Ride) && !mainAct.iscubejekRental)) {
                    rentalPkg.setVisibility(View.VISIBLE);
                    rentalarea.setVisibility(View.VISIBLE);
                    rentPkgImage.setVisibility(View.VISIBLE);
                    rentBackPkgImage.setVisibility(View.VISIBLE);


                    Runnable r = new Runnable() {

                        @Override
                        public void run() {
                            try {
                                mainAct.setPanelHeight(280);
                                /*RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) (mainAct.userLocBtnImgView).getLayoutParams();
                                params.bottomMargin = Utils.dipToPixels(getActContext(), 300);
*/
                            } catch (Exception e2) {

                                new Handler().postDelayed(this, 20);
                            }
                        }
                    };
                    new Handler().postDelayed(r, 20);
                    setShadow();
                }

            }
        }
    }

    public void closeLoadernTxt() {
        loaderView.setVisibility(View.GONE);
        closeNoServiceText();

    }

    public void setShadow() {
        (view.findViewById(R.id.shadowView)).setVisibility(View.GONE);
    }

    public Context getActContext() {
        return mainAct.getActContext();
    }

    @Override
    public void onItemClick(int position) {

        String iVehicleTypeId = cabTypeList.get(position).get("iVehicleTypeId");
        String ePoolStatus = cabTypeList.get(position).get("ePoolStatus");

        if (ePoolStatus.equalsIgnoreCase("Yes") && mainAct.stopOverPointsList.size() > 2) {
            generalFunc.showMessage(carTypeRecyclerView, generalFunc.retrieveLangLBl("", "LBL_REMOVE_MULTI_STOP_OVER_TXT"));
            return;
        }


        selpos = position;

        isPoolCabTypeIdSelected = ePoolStatus.equalsIgnoreCase("Yes");
        mainAct.isPoolCabTypeIdSelected = isPoolCabTypeIdSelected;

        showBookingLaterArea();

        if (!iVehicleTypeId.equals(mainAct.getSelectedCabTypeId())) {
            mainAct.selectedCabTypeId = iVehicleTypeId;
            adapter.setSelectedVehicleTypeId(iVehicleTypeId);
            adapter.notifyDataSetChanged();
            mainAct.changeCabType(iVehicleTypeId);

            if (cabTypeList.get(position).get("eFlatTrip") != null &&
                    (!cabTypeList.get(position).get("eFlatTrip").equalsIgnoreCase(""))
                    && cabTypeList.get(position).get("eFlatTrip").equalsIgnoreCase("Yes")) {
                mainAct.isFixFare = true;
            } else {
                mainAct.isFixFare = false;
            }
            //#UberPool Change
            if (ePoolStatus.equalsIgnoreCase("Yes")) {
                mainAct.loadAvailCabs.checkAvailableCabs();
                ride_now_btn.setText(generalFunc.retrieveLangLBl("", "LBL_CONFIRM_SEATS"));
            } else {
                String type = mainAct.isDeliver(app_type) || mainAct.isDeliver(RideDeliveryType) ? "Deliver" : Utils.CabGeneralType_Ride;
                if (type.equals("Deliver")) {
                    if (mainAct.getCabReqType().equals(Utils.CabReqType_Now)) {
                        ride_now_btn.setText(generalFunc.retrieveLangLBl("", "LBL_BTN_NEXT_TXT"));
                    } else if (mainAct.getCabReqType().equals(Utils.CabReqType_Later)) {
                        ride_now_btn.setText(generalFunc.retrieveLangLBl("", "LBL_BTN_NEXT_TXT"));
                    }
                } else {
                    ride_now_btn.setText(generalFunc.retrieveLangLBl("Request Now", "LBL_REQUEST_NOW"));
                }

            }
        } else {
            openFareDetailsDilaog(position);
        }

        poolFareTxt.setText(cabTypeList.get(position).get("total_fare"));

        if (isPoolCabTypeIdSelected) {
            if (Utils.checkText(routeDrawResponse)) {
                routeDraw();
            } else {
                findRoute("--");
            }
        } else {
            if (routeDrawResponse != null && !routeDrawResponse.equalsIgnoreCase("")) {
                PolylineOptions lineOptions = null;

                if (isPoolCabTypeIdSelected && sourceLocation != null && destLocation != null) {
                    lineOptions = createCurveRoute(new LatLng(sourceLocation.latitude, sourceLocation.longitude), new LatLng(destLocation.latitude, destLocation.longitude));

                } else if (!isPoolCabTypeIdSelected && Utils.checkText(routeDrawResponse)) {
                    if (mainAct.stopOverPointsList.size() > 2) {
                        lineOptions = getGoogleRouteOptions(routeDrawResponse, Utils.dipToPixels(getActContext(), 5), getActContext().getResources().getColor(R.color.gray), getActContext(), mainAct.stopOverPointsList, wayPointslist, destPointlist, finalPointlist, mainAct.getMap(), builder);

                    } else {
                        lineOptions = getGoogleRouteOptions(routeDrawResponse, Utils.dipToPixels(getActContext(), 5), getActContext().getResources().getColor(android.R.color.darker_gray));
                    }
                }


                if (lineOptions != null) {
                    if (route_polyLine != null) {
                        route_polyLine.remove();
                        route_polyLine = null;

                    }
                   // lineOptions.color(Color.GREEN);
                    route_polyLine = mainAct.getMap().addPolyline(lineOptions);//black
                    route_polyLine.remove();
                }


                DisplayMetrics metrics = new DisplayMetrics();
                mainAct.getWindowManager().getDefaultDisplay().getMetrics(metrics);

                if (isSkip) {
                    MapAnimator.getInstance().stopRouteAnim();
                    if (route_polyLine != null) {
                        route_polyLine.remove();
                        route_polyLine = null;
                    }
                    return;
                }
                if (route_polyLine != null && route_polyLine.getPoints().size() > 1) {
                    // my1
                    MapAnimator.getInstance().animateRoute(mainAct.getMap(), route_polyLine.getPoints(), getActContext());
                }
            }
        }


    }

    public void routeDraw() {
        if (isSkip) {
            if (route_polyLine != null) {
                route_polyLine.remove();
                route_polyLine = null;
            }
            return;
        }
        PolylineOptions lineOptions = null;

        if (isPoolCabTypeIdSelected && sourceLocation != null && destLocation != null) {
            lineOptions = createCurveRoute(new LatLng(sourceLocation.latitude, sourceLocation.longitude), new LatLng(destLocation.latitude, destLocation.longitude));

        } else if (!isPoolCabTypeIdSelected && Utils.checkText(routeDrawResponse)) {
            if (mainAct.stopOverPointsList.size() > 2) {
                lineOptions = getGoogleRouteOptions(routeDrawResponse, Utils.dipToPixels(getActContext(), 5), getActContext().getResources().getColor(R.color.gray), getActContext(), mainAct.stopOverPointsList, wayPointslist, destPointlist, finalPointlist, mainAct.getMap(), builder);

            } else {
                lineOptions = getGoogleRouteOptions(routeDrawResponse, Utils.dipToPixels(getActContext(), 5), getActContext().getResources().getColor(android.R.color.darker_gray));
            }

        }

        if (lineOptions != null) {
            if (route_polyLine != null) {
                route_polyLine.remove();
                route_polyLine = null;
            }

            if (MapAnimator.getInstance() != null) {
                MapAnimator.getInstance().stopRouteAnim();
            }

            //Draw polyline
            route_polyLine = mainAct.getMap().addPolyline(lineOptions);//black

            if (isPoolCabTypeIdSelected) {
                route_polyLine.setColor(Color.parseColor("#cecece"));
                route_polyLine.setStartCap(new RoundCap());
                route_polyLine.setEndCap(new RoundCap());
            }


            if (route_polyLine != null && route_polyLine.getPoints().size() > 1) {
                MapAnimator.getInstance().animateRoute(mainAct.getMap(), route_polyLine.getPoints(), getActContext());
            }

            route_polyLine.remove();
        }

    }

    public void openFareEstimateDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getActContext());
        builder.setTitle("");

        LayoutInflater inflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.fare_detail_design, null);
        builder.setView(dialogView);

        ((TextView) dialogView.findViewById(R.id.fareDetailHTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_FARE_DETAIL_TXT"));
        ((TextView) dialogView.findViewById(R.id.baseFareHTxt)).setText(" " + generalFunc.retrieveLangLBl("", "LBL_BASE_FARE_TXT"));
        ((TextView) dialogView.findViewById(R.id.parMinHTxt)).setText(" / " + generalFunc.retrieveLangLBl("", "LBL_MIN_TXT"));
        ((TextView) dialogView.findViewById(R.id.parMinHTxt)).setVisibility(View.GONE);
        ((TextView) dialogView.findViewById(R.id.andTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_AND_TXT"));
        ((TextView) dialogView.findViewById(R.id.parKmHTxt)).setText(" / " + generalFunc.retrieveLangLBl("", "LBL_KM_TXT"));
        ((TextView) dialogView.findViewById(R.id.parKmHTxt)).setVisibility(View.GONE);

        ((TextView) dialogView.findViewById(R.id.baseFareVTxt)).setText(currency_sign + " " +
                generalFunc.getSelectedCarTypeData(mainAct.getSelectedCabTypeId(), mainAct.cabTypesArrList, "iBaseFare"));

        ((TextView) dialogView.findViewById(R.id.parMinVTxt)).setText(currency_sign + " " +
                generalFunc.getSelectedCarTypeData(mainAct.getSelectedCabTypeId(), mainAct.cabTypesArrList, "fPricePerMin") + " / " + generalFunc.retrieveLangLBl("", "LBL_MIN_TXT"));

        ((TextView) dialogView.findViewById(R.id.parKmVTxt)).setText(currency_sign + " " +
                generalFunc.getSelectedCarTypeData(mainAct.getSelectedCabTypeId(), mainAct.cabTypesArrList, "fPricePerKM") + " / " + generalFunc.retrieveLangLBl("", "LBL_KM_TXT"));

        builder.show();
    }

    public void hidePayTypeSelectionArea() {
        payTypeSelectArea.setVisibility(View.GONE);
        cashcardarea.setVisibility(View.VISIBLE);
        mainAct.setPanelHeight(240 - 10);

        if (!mainAct.iscubejekRental) {

            if (rentalTypeList.size() > 0) {
                Runnable r = new Runnable() {

                    @Override
                    public void run() {
                        try {
                            mainAct.setPanelHeight(290 - 10);
                        } catch (Exception e2) {
                            new Handler().postDelayed(this, 20);
                        }
                    }
                };
                new Handler().postDelayed(r, 20);
            }
        }
    }

    public void buildNoCabMessage(String message, String positiveBtn) {

        if (mainAct.noCabAvailAlertBox != null) {
            mainAct.noCabAvailAlertBox.closeAlertBox();
            mainAct.noCabAvailAlertBox = null;
        }

        final GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
        generateAlert.setCancelable(true);
        generateAlert.setBtnClickList(btn_id -> generateAlert.closeAlertBox());
        generateAlert.setContentMessage("", message);
        generateAlert.setPositiveBtn(positiveBtn);
        generateAlert.showAlertBox();

        mainAct.noCabAvailAlertBox = generateAlert;
    }

    public String getAppliedPromoCode() {
        return this.appliedPromoCode;
    }

    public void findRoute(String etaVal) {

        if (mainAct != null && mainAct.isMultiDelivery()) {
            if (mainAct.getMap() != null) {
                CameraPosition cameraPosition = new CameraPosition.Builder().target(
                        new LatLng(mainAct.pickUpLocation.getLatitude(), mainAct.pickUpLocation.getLongitude()))
                        .zoom(Utils.defaultZomLevel).build();
                mainAct.getMap().moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }

            if (mainAct.loadAvailCabs != null) {
                mainAct.loadAvailCabs.changeCabs();
            }

            return;
        }


        try {

            String serverKey = generalFunc.retrieveValue(Utils.GOOGLE_SERVER_ANDROID_PASSENGER_APP_KEY);
            String parameters = "";

            if (mainAct.stopOverPointsList.size() > 2 && mainAct.isMultiStopOverEnabled()) {
                // Origin of route
                String str_origin = "origin=" + mainAct.stopOverPointsList.get(0).getDestLat() + "," + mainAct.stopOverPointsList.get(0).getDestLong();


                String str_dest = "";
                String waypoints = "";
                wayPointslist = new ArrayList<>();      // List of Way Points
                destPointlist = new ArrayList<>();      // destination Points
                finalPointlist = new ArrayList<>();     // final Points list with time & distance & based on shortest location first algorithm
                stop_Over_Points_Temp_Array = new ArrayList<>(); // temp list of all points

                stop_Over_Points_Temp_Array = new ArrayList<Stop_Over_Points_Data>(mainAct.stopOverPointsList.subList(1, mainAct.stopOverPointsList.size()));

                // Retrive middle & destination points

                if (stop_Over_Points_Temp_Array.size() > 0) {
                    String lastAddress = "";
                    for (int i = 0; i < stop_Over_Points_Temp_Array.size(); i++) {

                        if (i == stop_Over_Points_Temp_Array.size() - 1) {
                            str_dest = "destination=" + stop_Over_Points_Temp_Array.get(stop_Over_Points_Temp_Array.size() - 1).getDestLat() + "," + stop_Over_Points_Temp_Array.get(stop_Over_Points_Temp_Array.size() - 1).getDestLong();
                            stop_Over_Points_Temp_Array.get(i).setDestination(true);
                            destPointlist.add(stop_Over_Points_Temp_Array.get(i));
                        } else if (i == stop_Over_Points_Temp_Array.size() - 2) {
                            wayPointslist.add(stop_Over_Points_Temp_Array.get(i));
                            lastAddress = stop_Over_Points_Temp_Array.get(i).getDestLat() + "," + stop_Over_Points_Temp_Array.get(i).getDestLong();

                        } else {
                            wayPointslist.add(stop_Over_Points_Temp_Array.get(i));
                            waypoints = waypoints + stop_Over_Points_Temp_Array.get(i).getDestLat() + "," + stop_Over_Points_Temp_Array.get(i).getDestLong() + "|";
                        }

                    }
                    waypoints = "waypoints=optimize:true|" + waypoints + lastAddress;
                } else {
                    str_dest = "destination=" + stop_Over_Points_Temp_Array.get(stop_Over_Points_Temp_Array.size() - 1).getDestLat() + "," + stop_Over_Points_Temp_Array.get(stop_Over_Points_Temp_Array.size() - 1).getDestLong();
                    destPointlist.add(stop_Over_Points_Temp_Array.get(stop_Over_Points_Temp_Array.size() - 1));
                }

                // Building the parameters to the web service
                if (stop_Over_Points_Temp_Array.size() > 1) {
                    parameters = str_origin + "&" + str_dest + "&" + waypoints;

                } else {
                    parameters = str_origin + "&" + str_dest;

                }

            } else {

                wayPointslist.clear();

                String originLoc = mainAct.getPickUpLocation().getLatitude() + "," + mainAct.getPickUpLocation().getLongitude();
                String destLoc = null;
                if (mainAct.destLocation != null) {
                    destLoc = mainAct.getDestLocLatitude() + "," + mainAct.getDestLocLongitude();
                } else {
                    destLoc = mainAct.getPickUpLocation().getLatitude() + "," + mainAct.getPickUpLocation().getLongitude();

                }

                parameters = "origin=" + originLoc + "&destination=" + destLoc;

            }

            String url = "https://maps.googleapis.com/maps/api/directions/json?" + parameters + "&key=" + serverKey + "&language=" + generalFunc.retrieveValue(Utils.GOOGLE_MAP_LANGUAGE_CODE_KEY) + "&sensor=true";

            mProgressBar.setIndeterminate(true);
            mProgressBar.setVisibility(View.VISIBLE);


            Logger.d("Api", "directUrl_value True");

            ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), url, true);
            exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
                @Override
                public void setResponse(String responseString) {

                    mProgressBar.setIndeterminate(false);
                    mProgressBar.setVisibility(View.INVISIBLE);

                    if (responseString != null && !responseString.equals("")) {

                        String status = generalFunc.getJsonValue("status", responseString);

                        if (status.equals("OK")) {
                            isRouteFail = false;
                            JSONArray obj_routes = generalFunc.getJsonArray("routes", responseString);
                            if (obj_routes != null && obj_routes.length() > 0) {


                                if (mainAct.stopOverPointsList.size() > 2 && mainAct.isMultiStopOverEnabled()) {

                                    if (finalPointlist.size() > 0) {
                                        ArrayList<Stop_Over_Points_Data> finalAllPointlist = new ArrayList<>();
                                        finalAllPointlist = new ArrayList<>();
                                        finalAllPointlist.add(mainAct.stopOverPointsList.get(0));
                                        finalAllPointlist.addAll(finalPointlist);
                                        mainAct.stopOverPointsList.clear();
                                        mainAct.stopOverPointsList.addAll(finalAllPointlist);
                                    }


                                    sourceLocation = mainAct.stopOverPointsList.get(0).getDestLatLong();
                                    destLocation = mainAct.stopOverPointsList.get(mainAct.stopOverPointsList.size() - 1).getDestLatLong();

                                    StopOverPointsDataParser parser = new StopOverPointsDataParser(getActContext(), mainAct.stopOverPointsList, wayPointslist, destPointlist, finalPointlist, mainAct.getMap(), builder);
                                    parser.getDistanceArray(generalFunc.getJsonObject(responseString));
                                    List<List<HashMap<String, String>>> routes_list = parser.parse(generalFunc.getJsonObject(responseString));

                                    distance = parser.distance;
                                    time = parser.time;

                                } else {

                                    JSONObject obj_legs = generalFunc.getJsonObject(generalFunc.getJsonArray("legs", generalFunc.getJsonObject(obj_routes, 0).toString()), 0);


                                    distance = "" + (GeneralFunctions.parseDoubleValue(0, generalFunc.getJsonValue("value",
                                            generalFunc.getJsonValue("distance", obj_legs.toString()).toString())));

                                    time = "" + (GeneralFunctions.parseDoubleValue(0, generalFunc.getJsonValue("value",
                                            generalFunc.getJsonValue("duration", obj_legs.toString()).toString())));

                                    sourceLocation = new LatLng(GeneralFunctions.parseDoubleValue(0.0, generalFunc.getJsonValue("lat", generalFunc.getJsonValue("start_location", obj_legs.toString()))),
                                            GeneralFunctions.parseDoubleValue(0.0, generalFunc.getJsonValue("lng", generalFunc.getJsonValue("start_location", obj_legs.toString()))));

                                    destLocation = new LatLng(GeneralFunctions.parseDoubleValue(0.0, generalFunc.getJsonValue("lat", generalFunc.getJsonValue("end_location", obj_legs.toString()))),
                                            GeneralFunctions.parseDoubleValue(0.0, generalFunc.getJsonValue("lng", generalFunc.getJsonValue("end_location", obj_legs.toString()))));

                                }

                                if (getActivity() != null) {
                                    estimateFare(distance, time);
                                }


                                //temp animation test
                                handleMapAnimation(responseString, sourceLocation, destLocation, etaVal);

                            }


                        } else {


                            isRouteFail = true;
                            if (!isSkip) {
                                GenerateAlertBox alertBox = new GenerateAlertBox(getActContext());
                                alertBox.setContentMessage("", generalFunc.retrieveLangLBl("Route not found", "LBL_DEST_ROUTE_NOT_FOUND"));
                                alertBox.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"));
                                alertBox.setBtnClickList(btn_id -> {
                                    alertBox.closeAlertBox();
                                    mainAct.userLocBtnImgView.performClick();

                                });
                                alertBox.showAlertBox();
                            }

                            if (isSkip) {
                                isRouteFail = false;
                                if (mainAct.destLocation != null && mainAct.pickUpLocation != null) {
                                    handleMapAnimation(responseString, new LatLng(mainAct.pickUpLocation.getLatitude(), mainAct.pickUpLocation.getLongitude()), new LatLng(mainAct.destLocation.getLatitude(), mainAct.destLocation.getLongitude()), "--");
                                }
                            } else {
                                mainAct.userLocBtnImgView.performClick();
                            }

                            isSkip = true;
                            if (getActivity() != null) {
                                estimateFare(null, null);
                            }
                        }

                    }
                }
            });


            if (generalFunc.retrieveValue("ENABLE_GOOGLE_API_OPTIMIZATION_ON_FARE_EST").equalsIgnoreCase("Yes")) {
                managePath();
            } else {
                exeWebServer.execute();
            }

        } catch (
                Exception e) {


        }
    }

    public void managePath() {
        Logger.d("ManagePath", "::called");
        mProgressBar.setIndeterminate(false);
        mProgressBar.setVisibility(View.INVISIBLE);
        isRouteFail = false;

        Location srcLoc = new Location("Source");
        Location destLoc = new Location("Destination");

        if (mainAct.stopOverPointsList.size() > 2 && mainAct.isMultiStopOverEnabled()) {
            // Origin of route
            // String str_origin = "origin=" + mainAct.stopOverPointsList.get(0).getDestLat() + "," + mainAct.stopOverPointsList.get(0).getDestLong();
            srcLoc.setLatitude(mainAct.stopOverPointsList.get(0).getDestLat());
            srcLoc.setLongitude(mainAct.stopOverPointsList.get(0).getDestLong());


            //  String str_dest = "";
            String waypoints = "";
            wayPointslist = new ArrayList<>();      // List of Way Points
            destPointlist = new ArrayList<>();      // destination Points
            finalPointlist = new ArrayList<>();     // final Points list with time & distance & based on shortest location first algorithm
            stop_Over_Points_Temp_Array = new ArrayList<>(); // temp list of all points

            stop_Over_Points_Temp_Array = new ArrayList<Stop_Over_Points_Data>(mainAct.stopOverPointsList.subList(1, mainAct.stopOverPointsList.size()));

            // Retrive middle & destination points

            if (stop_Over_Points_Temp_Array.size() > 0) {
                String lastAddress = "";
                for (int i = 0; i < stop_Over_Points_Temp_Array.size(); i++) {

                    if (i == stop_Over_Points_Temp_Array.size() - 1) {
                        // str_dest = "destination=" + stop_Over_Points_Temp_Array.get(stop_Over_Points_Temp_Array.size() - 1).getDestLat() + "," + stop_Over_Points_Temp_Array.get(stop_Over_Points_Temp_Array.size() - 1).getDestLong();

                        destLoc.setLatitude(stop_Over_Points_Temp_Array.get(stop_Over_Points_Temp_Array.size() - 1).getDestLat());
                        destLoc.setLongitude(stop_Over_Points_Temp_Array.get(stop_Over_Points_Temp_Array.size() - 1).getDestLong());
                        stop_Over_Points_Temp_Array.get(i).setDestination(true);
                        destPointlist.add(stop_Over_Points_Temp_Array.get(i));
                        Logger.d("ManagePath", "::called_1::" + stop_Over_Points_Temp_Array.get(stop_Over_Points_Temp_Array.size() - 1).getDestLatLong());

                    } else if (i == stop_Over_Points_Temp_Array.size() - 2) {
                        wayPointslist.add(stop_Over_Points_Temp_Array.get(i));
                        lastAddress = stop_Over_Points_Temp_Array.get(i).getDestLat() + "," + stop_Over_Points_Temp_Array.get(i).getDestLong();
                        Logger.d("ManagePath", "::called_2::" + stop_Over_Points_Temp_Array.get(i).getDestLatLong());


                    } else {
                        wayPointslist.add(stop_Over_Points_Temp_Array.get(i));
                        waypoints = waypoints + stop_Over_Points_Temp_Array.get(i).getDestLat() + "," + stop_Over_Points_Temp_Array.get(i).getDestLong() + "|";
                        Logger.d("ManagePath", "::called_3::" + stop_Over_Points_Temp_Array.get(i).getDestLatLong());
                    }

                }
                waypoints = "waypoints=optimize:true|" + waypoints + lastAddress;
            } else {
                // str_dest = "destination=" + stop_Over_Points_Temp_Array.get(stop_Over_Points_Temp_Array.size() - 1).getDestLat() + "," + stop_Over_Points_Temp_Array.get(stop_Over_Points_Temp_Array.size() - 1).getDestLong();
                destPointlist.add(stop_Over_Points_Temp_Array.get(stop_Over_Points_Temp_Array.size() - 1));

                destLoc.setLatitude(stop_Over_Points_Temp_Array.get(stop_Over_Points_Temp_Array.size() - 1).getDestLat());
                destLoc.setLongitude(stop_Over_Points_Temp_Array.get(stop_Over_Points_Temp_Array.size() - 1).getDestLong());
            }

            // Building the parameters to the web service
//            if (stop_Over_Points_Temp_Array.size() > 1) {
//                parameters = str_origin + "&" + str_dest + "&" + waypoints;
//
//            } else {
//                parameters = str_origin + "&" + str_dest;
//
//            }

        } else {

            // String originLoc = mainAct.getPickUpLocation().getLatitude() + "," + mainAct.getPickUpLocation().getLongitude();
            srcLoc.setLatitude(mainAct.getPickUpLocation().getLatitude());
            srcLoc.setLongitude(mainAct.getPickUpLocation().getLongitude());

            //String destLoc = null;
            if (mainAct.getDestLocLatitude() != null) {
                //  destLoc = mainAct.getDestLocLatitude() + "," + mainAct.getDestLocLongitude();
                destLoc.setLatitude(GeneralFunctions.parseDoubleValue(0, mainAct.getDestLocLatitude()));
                destLoc.setLongitude(GeneralFunctions.parseDoubleValue(0, mainAct.getDestLocLongitude()));
            } else {
                //  destLoc = mainAct.getPickUpLocation().getLatitude() + "," + mainAct.getPickUpLocation().getLongitude();

                destLoc.setLatitude(mainAct.getPickUpLocation().getLatitude());
                destLoc.setLongitude(mainAct.getPickUpLocation().getLongitude());
            }

            //  parameters = "origin=" + originLoc + "&destination=" + destLoc;

        }

        sendAndRequestResponsetime(srcLoc.getLatitude(), srcLoc.getLongitude(),
                destLoc.getLatitude(), destLoc.getLongitude());




    }

    public void setEta(String time) {
        if (etaTxt != null) {
            etaTxt.setText(time);
        }


    }

    public void mangeMrakerPostion() {
        try {

            if (mainAct.pickUpLocation != null) {
                Point PickupPoint = mainAct.getMap().getProjection().toScreenLocation(new LatLng(mainAct.pickUpLocation.getLatitude(), mainAct.pickUpLocation.getLongitude()));
                if (sourceMarker != null) {
                    sourceMarker.setAnchor(PickupPoint.x < Utils.dpToPx(getActContext(), 200) ? 0.00f : 1.00f, PickupPoint.y < Utils.dpToPx(getActContext(), 100) ? 0.20f : 1.20f);
                }
            }
            if (destLocation != null) {
                Point DestinationPoint = mainAct.getMap().getProjection().toScreenLocation(destLocation);
                //dest
                if (destMarker != null) {
                    destMarker.setAnchor(DestinationPoint.x < Utils.dpToPx(getActContext(), 200) ? 0.00f : 1.00f, DestinationPoint.y < Utils.dpToPx(getActContext(), 100) ? 0.20f : 1.20f);
                }
            }
        } catch (Exception e) {

        }


    }

    public void handleSourceMarker(String etaVal) {
        try {
            if (!isSkip) {
                if (mainAct.pickUpLocation == null) {
                    return;
                }
            }

            if (marker_view == null) {
                marker_view = ((LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                        .inflate(R.layout.custom_marker, null);
                addressTxt = (TextView) marker_view
                        .findViewById(R.id.addressTxt);
                etaTxt = (TextView) marker_view.findViewById(R.id.etaTxt);
            }

            if (marker_view != null) {
                etaTxt = (TextView) marker_view.findViewById(R.id.etaTxt);
            }

            addressTxt.setTextColor(getActContext().getResources().getColor(R.color.sourceAddressTxt));

            LatLng fromLnt;
            if (isSkip) {
                estimateFare(null, null);
                if (destMarker != null) {
                    destMarker.remove();
                }
                if (destDotMarker != null) {
                    destDotMarker.remove();
                }
                if (route_polyLine != null) {
                    route_polyLine.remove();
                }

                destLocation = null;
                mainAct.destLocation = null;

                fromLnt = new LatLng(mainAct.pickUpLocation.getLatitude(), mainAct.pickUpLocation.getLongitude());

            } else {
                fromLnt = new LatLng(mainAct.pickUpLocation.getLatitude(), mainAct.pickUpLocation.getLongitude());

                if (sourceLocation != null) {
                    fromLnt = sourceLocation;
                }


            }


            etaTxt.setVisibility(View.VISIBLE);
            etaTxt.setText(etaVal);

            if (sourceMarker != null) {
                sourceMarker.remove();
                sourceMarker = null;
            }

            if (source_dot_option != null) {
                sourceDotMarker.remove();
                sourceDotMarker = null;
                source_dot_option = null;
            }

            source_dot_option = new MarkerOptions().position(fromLnt).icon(BitmapDescriptorFactory.fromResource(R.mipmap.dot));

            if (mainAct.getMap() != null) {
                sourceDotMarker = mainAct.getMap().addMarker(source_dot_option);
            }

            String name = "";
            if (generalFunc.retrieveValue(Utils.BOOK_FOR_ELSE_ENABLE_KEY).equalsIgnoreCase("yes") && getCurrentCabGeneralType().equalsIgnoreCase(Utils.CabGeneralType_Ride)) {

                if (generalFunc.containsKey(Utils.BFSE_SELECTED_CONTACT_KEY) && Utils.checkText(generalFunc.retrieveValue(Utils.BFSE_SELECTED_CONTACT_KEY))) {
                    Gson gson = new Gson();
                    String data1 = generalFunc.retrieveValue(Utils.BFSE_SELECTED_CONTACT_KEY);
                    ContactModel contactdetails = gson.fromJson(data1, new TypeToken<ContactModel>() {
                            }.getType()
                    );


                    if (Utils.checkText(contactdetails.name) && !contactdetails.name.equalsIgnoreCase("ME")) {
                        int n = 5;
                        String upToNCharacters = contactdetails.name.substring(0, Math.min(contactdetails.name.length(), n)) + (contactdetails.name.length() > n ? "..." : "");
                        name = "<b><font color=" + getActContext().getResources().getColor(R.color.black) + ">" + "@" + upToNCharacters + "</font><b>" + " - ";
                    }
                }
            }

            addressTxt.setText(Html.fromHtml(name + mainAct.pickUpLocationAddress));
            MarkerOptions marker_opt_source = new MarkerOptions().position(fromLnt).icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(getActContext(), marker_view))).anchor(0.00f, 0.20f);
            if (mainAct.getMap() != null) {
                sourceMarker = mainAct.getMap().addMarker(marker_opt_source);
                sourceMarker.setTag("1");
            }

            buildBuilder();

            if (isSkip) {
         /*   if (mainAct.getMap() != null) {
                CameraPosition cameraPosition = new CameraPosition.Builder().target(
                        new LatLng(mainAct.pickUpLocation.getLatitude(), mainAct.pickUpLocation.getLongitude()))
                        .zoom(Utils.defaultZomLevel).build();
                mainAct.getMap().moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }*/
            }

        } catch (Exception e) {
            // Backpress done by user then app crashes
            Logger.d("ManagePathExe", "::" + e.toString());
            e.printStackTrace();
        }
    }

    public void handleMapAnimation(String responseString, LatLng sourceLocation, LatLng destLocation, String etaVal) {
        try {
            if (mainAct == null) {
                return;
            }

            //    mainAct.getMap().clear();
            if (mainAct.cabSelectionFrag == null) {
                return;
            }

            if (isSkip) {
                MapAnimator.getInstance().stopRouteAnim();
                if (route_polyLine != null) {
                    route_polyLine.remove();
                    route_polyLine = null;
                }
                return;
            }

            MapAnimator.getInstance().stopRouteAnim();

            LatLng fromLnt = new LatLng(sourceLocation.latitude, sourceLocation.longitude);
            LatLng toLnt = new LatLng(destLocation.latitude, destLocation.longitude);


            if (marker_view == null) {

                marker_view = ((LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                        .inflate(R.layout.custom_marker, null);
                addressTxt = (TextView) marker_view
                        .findViewById(R.id.addressTxt);
                etaTxt = (TextView) marker_view.findViewById(R.id.etaTxt);
            }

            addressTxt.setTextColor(getActContext().getResources().getColor(R.color.destAddressTxt));


            addressTxt.setText(mainAct.destAddress + " " + (mainAct.stopOverPointsList.size() >= 3 ? ">" : ""));

            MarkerOptions marker_opt_dest = new MarkerOptions().position(toLnt);
            etaTxt.setVisibility(View.GONE);

            marker_opt_dest.icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(getActContext(), marker_view))).anchor(0.00f, 0.20f);
            if (dest_dot_option != null) {
                destDotMarker.remove();
            }
            dest_dot_option = new MarkerOptions().position(toLnt).icon(BitmapDescriptorFactory.fromResource(R.mipmap.dot));
            destDotMarker = mainAct.getMap().addMarker(dest_dot_option);

            if (destMarker != null) {
                destMarker.remove();
            }
            destMarker = mainAct.getMap().addMarker(marker_opt_dest);
            destMarker.setTag("2");
       /* LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(fromLnt);
        builder.include(toLnt);*/

            handleSourceMarker(etaVal);

            JSONArray obj_routes1 = generalFunc.getJsonArray("routes", responseString);


            if (obj_routes1 != null && obj_routes1.length() > 0) {
                routeDrawResponse = responseString;
                PolylineOptions lineOptions = null;

                if (isPoolCabTypeIdSelected && sourceLocation != null && destLocation != null) {
                    lineOptions = createCurveRoute(new LatLng(sourceLocation.latitude, sourceLocation.longitude), new LatLng(destLocation.latitude, destLocation.longitude));

                } else if (!isPoolCabTypeIdSelected && Utils.checkText(routeDrawResponse)) {

                    if (mainAct.stopOverPointsList.size() > 2) {
                        lineOptions = getGoogleRouteOptions(responseString, Utils.dipToPixels(getActContext(), 5), getActContext().getResources().getColor(R.color.gray), getActContext(), mainAct.stopOverPointsList, wayPointslist, destPointlist, finalPointlist, mainAct.getMap(), builder);

                    } else {
                        lineOptions = getGoogleRouteOptions(responseString, Utils.dipToPixels(getActContext(), 5), getActContext().getResources().getColor(android.R.color.background_light));
                    }
                }

                if (lineOptions != null) {
                    if (route_polyLine != null) {
                        route_polyLine.remove();
                        route_polyLine = null;

                    }
                    route_polyLine = mainAct.getMap().addPolyline(lineOptions);//black
                    route_polyLine.remove();
                }
            }

            DisplayMetrics metrics = new DisplayMetrics();
            mainAct.getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int width = metrics.widthPixels;
//        mainAct.getMap().moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), width - Utils.dpToPx(getActContext(), 80), metrics.heightPixels - Utils.dipToPixels(getActContext(), 300), 0));

            if (route_polyLine != null && route_polyLine.getPoints().size() > 1) {
                MapAnimator.getInstance().animateRoute(mainAct.getMap(), route_polyLine.getPoints(), getActContext());
            }

            mainAct.getMap().setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
                @Override
                public void onCameraMove() {

                    DisplayMetrics displaymetrics = new DisplayMetrics();
                    mainAct.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                    int height = displaymetrics.heightPixels;
                    int width = displaymetrics.widthPixels;


                }
            });


//        mainAct.getMap().setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
//            @Override
//            public boolean onMarkerClick(Marker marker) {
//                if (marker == null) {
//                    return false;
//                }
//
//                if (marker.getTag().equals("1")) {
//                    if (mainAct.mainHeaderFrag != null) {
//                        mainAct.mainHeaderFrag.pickupLocArea1.performClick();
//                    }
//
//                } else if (marker.getTag().equals("2")) {
//                    if (mainAct.mainHeaderFrag != null) {
//                        mainAct.mainHeaderFrag.destarea.performClick();
//                    }
//
//                }
//
//                return false;
//            }
//        });


            if (mainAct.loadAvailCabs != null) {
                mainAct.loadAvailCabs.changeCabs();
            }


        } catch (Exception e) {
            // Backpress done by user then app crashes
            Logger.d("ExcpetionCurve", "aa::" + e.toString());
            e.printStackTrace();
        }


    }

    public void handleMapCurveAnimation(LatLng sourceLocation, LatLng destLocation, String etaVal) {

        Logger.d("ManagePath", "::" + sourceLocation + "::" + destLocation);

        try {
            if (mainAct == null) {
                return;
            }

            //  mainAct.getMap().clear();
            if (mainAct.cabSelectionFrag == null) {
                return;
            }

            if (isSkip) {
                MapAnimator.getInstance().stopRouteAnim();
                if (route_polyLine != null) {
                    route_polyLine.remove();
                    route_polyLine = null;
                }

                return;
            }

            MapAnimator.getInstance().stopRouteAnim();

            LatLng fromLnt = new LatLng(sourceLocation.latitude, sourceLocation.longitude);
            LatLng toLnt = new LatLng(destLocation.latitude, destLocation.longitude);

            if (marker_view == null) {

                marker_view = ((LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                        .inflate(R.layout.custom_marker, null);
                addressTxt = (TextView) marker_view
                        .findViewById(R.id.addressTxt);
                etaTxt = (TextView) marker_view.findViewById(R.id.etaTxt);
            }

            addressTxt.setTextColor(getActContext().getResources().getColor(R.color.destAddressTxt));

            Logger.d("ManagePath", "::" + mainAct.destAddress + "::");


            addressTxt.setText(mainAct.destAddress + " " + (mainAct.stopOverPointsList.size() >= 3 ? ">" : ""));

            MarkerOptions marker_opt_dest = new MarkerOptions().position(toLnt);
            etaTxt.setVisibility(View.GONE);

            marker_opt_dest.icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(getActContext(), marker_view))).anchor(0.00f, 0.20f);
            if (dest_dot_option != null) {
                destDotMarker.remove();
            }
            dest_dot_option = new MarkerOptions().position(toLnt).icon(BitmapDescriptorFactory.fromResource(R.mipmap.dot));
            destDotMarker = mainAct.getMap().addMarker(dest_dot_option);

            if (destMarker != null) {
                destMarker.remove();
            }
            destMarker = mainAct.getMap().addMarker(marker_opt_dest);
            destMarker.setTag("2");
       /* LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(fromLnt);
        builder.include(toLnt);*/


            handleSourceMarker(etaVal);


            PolylineOptions lineOptions = null;

            lineOptions = createCurveRoute(fromLnt, toLnt);


            if (lineOptions != null) {
                if (route_polyLine != null) {
                    route_polyLine.remove();
                    route_polyLine = null;

                }
                route_polyLine = mainAct.getMap().addPolyline(lineOptions);//black
                route_polyLine.remove();
            }


            DisplayMetrics metrics = new DisplayMetrics();
            mainAct.getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int width = metrics.widthPixels;
            //  mainAct.getMap().moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), width - Utils.dpToPx(getActContext(), 80), metrics.heightPixels - Utils.dipToPixels(getActContext(), 300), 0));

            if (route_polyLine != null && route_polyLine.getPoints().size() > 1) {
                MapAnimator.getInstance().animateRoute(mainAct.getMap(), route_polyLine.getPoints(), getActContext());
            }


            mainAct.getMap().setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
                @Override
                public void onCameraMove() {

                    DisplayMetrics displaymetrics = new DisplayMetrics();
                    mainAct.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                    int height = displaymetrics.heightPixels;
                    int width = displaymetrics.widthPixels;


                }
            });


//        mainAct.getMap().setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
//            @Override
//            public boolean onMarkerClick(Marker marker) {
//                if (marker == null) {
//                    return false;
//                }
//
//                if (marker.getTag().equals("1")) {
//                    if (mainAct.mainHeaderFrag != null) {
//                        mainAct.mainHeaderFrag.pickupLocArea1.performClick();
//                    }
//
//                } else if (marker.getTag().equals("2")) {
//                    if (mainAct.mainHeaderFrag != null) {
//                        mainAct.mainHeaderFrag.destarea.performClick();
//                    }
//
//                }
//
//                return false;
//            }
//        });


        } catch (Exception e) {
            // Backpress done by user then app crashes

            e.printStackTrace();
        }

    }

    public void buildBuilder() {
        if (mainAct == null) {
            return;
        }
        if (sourceMarker != null && (destMarker == null || isSkip)) {


            builder = new LatLngBounds.Builder();

            builder.include(sourceMarker.getPosition());

            DisplayMetrics metrics = new DisplayMetrics();
            mainAct.getWindowManager().getDefaultDisplay().getMetrics(metrics);

            int width = metrics.widthPixels;
            int height = metrics.heightPixels;
            int padding = (mainAct != null && mainAct.isMultiDelivery()) ? (width != 0 ? (int) (width / 5) : 0) : 0; // offset from edges of the map in pixels

            LatLngBounds bounds = builder.build();
            LatLng center = bounds.getCenter();
            LatLng northEast = SphericalUtil.computeOffset(center, 30 * Math.sqrt(2.0), SphericalUtil.computeHeading(center, bounds.northeast));
            LatLng southWest = SphericalUtil.computeOffset(center, 30 * Math.sqrt(2.0), (180 + (180 + SphericalUtil.computeHeading(center, bounds.southwest))));
            builder.include(southWest);
            builder.include(northEast);

            mainAct.getMap().moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), width - Utils.dipToPixels(getActContext(), 80), height - ((fragmentHeight + 5) + Utils.dipToPixels(getActContext(), 60)), padding));

        } else if (mainAct.map != null && mainAct.map.getView().getViewTreeObserver().isAlive()) {

            mainAct.map.getView().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @SuppressLint("NewApi") // We check which build version we are using.
                @Override
                public void onGlobalLayout() {

                    boolean isBoundIncluded = false;

                    LatLngBounds.Builder builder = new LatLngBounds.Builder();

                    if (sourceMarker != null) {
                        isBoundIncluded = true;
                        builder.include(sourceMarker.getPosition());
                    }


                    if (destMarker != null) {
                        isBoundIncluded = true;
                        builder.include(destMarker.getPosition());
                    }


                    if (isBoundIncluded) {

                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                            mainAct.map.getView().getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        } else {
                            mainAct.map.getView().getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }


                        LatLngBounds bounds = builder.build();


                        LatLng center = bounds.getCenter();

                        LatLng northEast = SphericalUtil.computeOffset(center, 10 * Math.sqrt(2.0), SphericalUtil.computeHeading(center, bounds.northeast));
                        LatLng southWest = SphericalUtil.computeOffset(center, 10 * Math.sqrt(2.0), (180 + (180 + SphericalUtil.computeHeading(center, bounds.southwest))));

                        builder.include(southWest);
                        builder.include(northEast);

                        /*  Method 1 */
//                            mainAct.getMap().moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));

                        DisplayMetrics metrics = new DisplayMetrics();
                        mainAct.getWindowManager().getDefaultDisplay().getMetrics(metrics);

                        int width = metrics.widthPixels;
                        int height = metrics.heightPixels;
                        // Set Padding according to included bounds

                        int padding = (int) (width * 0.25); // offset from edges of the map 10% of screen


                        /*  Method 2 */
                            /*Logger.e("MapHeight","newLatLngZoom");
                            mainAct.getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(builder.build().getCenter(),16));*/

                        try {
                            /*  Method 3 */
                            int screenWidth = getResources().getDisplayMetrics().widthPixels;
                            int screenHeight = getResources().getDisplayMetrics().heightPixels;
                            padding = (height - ((fragmentHeight + 5) + Utils.dipToPixels(getActContext(), 60))) / 3;
                            Logger.e("MapHeight", "cameraUpdate" + padding);
                            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(builder.build(),
                                    screenWidth, screenHeight, padding);
                            mainAct.getMap().animateCamera(cameraUpdate);
                        } catch (Exception e) {
                            e.printStackTrace();

                            /*  Method 1 */
                            //  mainAct.getMap().moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), width - Utils.dipToPixels(getActContext(), 80), height - ((fragmentHeight + 5) + Utils.dipToPixels(getActContext(), 60)), padding));

                        }


                    }

                }
            });
        }
    }

    public static Bitmap createDrawableFromView(Context context, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }

    // add route polyline line
    public PolylineOptions getGoogleRouteOptions(String directionJson, int width, int color, Context mContext, ArrayList<Stop_Over_Points_Data> list, ArrayList<Stop_Over_Points_Data> wayPointslist, ArrayList<Stop_Over_Points_Data> destPointlist, ArrayList<Stop_Over_Points_Data> finalPointlist, GoogleMap gMap, LatLngBounds.Builder builder) {
        PolylineOptions lineOptions = new PolylineOptions();


        StopOverPointsDataParser parser = new StopOverPointsDataParser(mContext, list, wayPointslist, destPointlist, finalPointlist, gMap, builder);
        List<List<HashMap<String, String>>> routes_list = parser.parse(generalFunc.getJsonObject(directionJson));
        ArrayList<LatLng> points = new ArrayList<LatLng>();

        if (routes_list.size() > 0) {
            // Fetching i-th route
            List<HashMap<String, String>> path = routes_list.get(0);

            // Fetching all the points in i-th route
            for (int j = 0; j < path.size(); j++) {
                HashMap<String, String> point = path.get(j);

                double lat = Double.parseDouble(point.get("lat"));
                double lng = Double.parseDouble(point.get("lng"));
                LatLng position = new LatLng(lat, lng);
                points.add(position);

            }

            lineOptions.addAll(points);
            lineOptions.width(width);
            lineOptions.color(color);

            return lineOptions;
        } else {
            return null;
        }
    }

    public PolylineOptions getGoogleRouteOptions(String directionJson, int width, int color) {
        PolylineOptions lineOptions = new PolylineOptions();

        try {
            DirectionsJSONParser parser = new DirectionsJSONParser();
            List<List<HashMap<String, String>>> routes_list = parser.parse(new JSONObject(directionJson));

            ArrayList<LatLng> points = new ArrayList<LatLng>();

            if (routes_list.size() > 0) {
                // Fetching i-th route
                List<HashMap<String, String>> path = routes_list.get(0);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);

                }

                lineOptions.addAll(points);
                lineOptions.width(width);
                lineOptions.color(color);

                return lineOptions;
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    public String getAvailableCarTypesIds() {
        String carTypesIds = "";
        for (int i = 0; i < mainAct.cabTypesArrList.size(); i++) {
            String iVehicleTypeId = mainAct.cabTypesArrList.get(i).get("iVehicleTypeId");

            carTypesIds = carTypesIds.equals("") ? iVehicleTypeId : (carTypesIds + "," + iVehicleTypeId);
        }
        return carTypesIds;
    }

    public void estimateFare(final String distance, String time) {

        //  loaderView.setVisibility(View.VISIBLE);

        if (estimateFareTask != null) {
            estimateFareTask.cancel(true);
            estimateFareTask = null;
        }
        if (distance == null && time == null) {
            //  mainAct.noCabAvail = false;
            // isroutefound = false;

        } else {
            if (mainAct.loadAvailCabs != null) {
                if (mainAct.loadAvailCabs.isAvailableCab) {
                    isroutefound = true;
                    if (!mainAct.timeval.equalsIgnoreCase("\n" + "--")) {
                        mainAct.noCabAvail = false;
                    }
                }
            }

        }

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "estimateFareNew");
        parameters.put("iUserId", generalFunc.getMemberId());
        parameters.put("SelectedCarTypeID", getAvailableCarTypesIds());
        if (distance != null && time != null) {
            parameters.put("distance", distance);
            parameters.put("time", time);
        }
        parameters.put("SelectedCar", mainAct.getSelectedCabTypeId());
        parameters.put("PromoCode", getAppliedPromoCode());

        if (mainAct.getPickUpLocation() != null) {
            parameters.put("StartLatitude", "" + mainAct.getPickUpLocation().getLatitude());
            parameters.put("EndLongitude", "" + mainAct.getPickUpLocation().getLongitude());
        }

        if (mainAct.getDestLocLatitude() != null && !mainAct.getDestLocLatitude().equalsIgnoreCase("")) {
            parameters.put("DestLatitude", "" + mainAct.getDestLocLatitude());
            parameters.put("DestLongitude", "" + mainAct.getDestLocLongitude());
        }

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        estimateFareTask = exeWebServer;
        exeWebServer.setDataResponseListener(responseString -> {

            if (responseString != null && !responseString.equals("")) {

                boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

                if (isDataAvail == true) {

                    JSONArray vehicleTypesArr = generalFunc.getJsonArray(Utils.message_str, responseString);
                    String APP_TYPE = generalFunc.getJsonValue("APP_TYPE", userProfileJson);

                    for (int i = 0; i < vehicleTypesArr.length(); i++) {

                        JSONObject obj_temp = generalFunc.getJsonObject(vehicleTypesArr, i);

                        if (distance != null) {

                            String type = mainAct.getCurrentCabGeneralType();
                            if (type.equalsIgnoreCase("rental")) {
                                type = Utils.CabGeneralType_Ride;
                            }

                            if (generalFunc.getJsonValueStr("eType", obj_temp).contains(type)) {

                                if (cabTypeList != null) {
                                    for (int k = 0; k < cabTypeList.size(); k++) {
                                        HashMap<String, String> map = cabTypeList.get(k);

                                        if (map.get("iVehicleTypeId").equalsIgnoreCase(generalFunc.getJsonValueStr("iVehicleTypeId", obj_temp))) {

                                            String totalfare = "";

                                            if (APP_TYPE.equalsIgnoreCase(Utils.CabGeneralTypeRide_Delivery_UberX) ||
                                                    (APP_TYPE.equalsIgnoreCase(Utils.CabGeneralTypeRide_Delivery) ||
                                                            (APP_TYPE.equalsIgnoreCase(Utils.CabGeneralType_Ride)))) {
                                                if (map.get("eRental").equalsIgnoreCase("Yes") && (mainAct.iscubejekRental || mainAct.isRental)) {
                                                    totalfare = generalFunc.getJsonValueStr("eRental_total_fare", obj_temp);
                                                } else {
                                                    totalfare = generalFunc.getJsonValueStr("total_fare", obj_temp);
                                                }
                                            } else {
                                                totalfare = generalFunc.getJsonValueStr("total_fare", obj_temp);
                                            }

                                            if (totalfare != null && !totalfare.equals("")) {
                                                map.put("total_fare", totalfare);
                                                map.put("FinalFare", generalFunc.getJsonValueStr("FinalFare", obj_temp));
                                                map.put("currencySymbol", generalFunc.getJsonValueStr("currencySymbol", obj_temp));
                                                map.put("eFlatTrip", generalFunc.getJsonValueStr("eFlatTrip", obj_temp));
                                                Logger.e("FinalFare", "::" + generalFunc.getJsonValueStr("FinalFare", obj_temp));
                                                cabTypeList.set(k, map);

                                            } else {
                                                map.put("eFlatTrip", generalFunc.getJsonValueStr("eFlatTrip", obj_temp));
                                                cabTypeList.set(k, map);
                                            }
                                        }

                                    }
                                }

                                if (rentalTypeList != null) {
                                    for (int k = 0; k < rentalTypeList.size(); k++) {
                                        HashMap<String, String> map = rentalTypeList.get(k);

                                        if (/*map.get("vVehicleType").equalsIgnoreCase(generalFunc.getJsonValueStr()("vVehicleType", obj_temp))
                                                && */map.get("iVehicleTypeId").equalsIgnoreCase(generalFunc.getJsonValueStr("iVehicleTypeId", obj_temp))) {

                                            String totalfare = generalFunc.getJsonValueStr("eRental_total_fare", obj_temp);
                                            if (totalfare != null && !totalfare.equals("")) {
                                                map.put("total_fare", totalfare);
                                                map.put("eFlatTrip", generalFunc.getJsonValueStr("eFlatTrip", obj_temp));
                                                rentalTypeList.set(k, map);
                                            } else {
                                                map.put("eFlatTrip", generalFunc.getJsonValueStr("eFlatTrip", obj_temp));
                                                rentalTypeList.set(k, map);
                                            }
                                        }
                                    }
                                }


                            }
                        } else {


                            if (generalFunc.getJsonValueStr("eType", obj_temp).equalsIgnoreCase(mainAct.getCurrentCabGeneralType())) {

                                for (int k = 0; k < cabTypeList.size(); k++) {
                                    HashMap<String, String> map = cabTypeList.get(k);

                                    if (mainAct.iscubejekRental || mainAct.isRental) {
                                        if (/*map.get("vVehicleType").equalsIgnoreCase(generalFunc.getJsonValueStr()("vVehicleType", obj_temp))
                                            &&*/ map.get("iVehicleTypeId").equalsIgnoreCase(generalFunc.getJsonValueStr("iVehicleTypeId", obj_temp))) {
                                            String totalfare = generalFunc.getJsonValueStr("eRental_total_fare", obj_temp);
                                            if (totalfare != null && !totalfare.equals("")) {
                                                map.put("total_fare", totalfare);
                                                map.put("eFlatTrip", generalFunc.getJsonValueStr("eFlatTrip", obj_temp));
                                                rentalTypeList.set(k, map);
                                            } else {
                                                map.put("eFlatTrip", generalFunc.getJsonValueStr("eFlatTrip", obj_temp));
                                                rentalTypeList.set(k, map);
                                            }

                                        }

                                    } else {

                                        if (/*map.get("vVehicleType").equalsIgnoreCase(generalFunc.getJsonValueStr()("vVehicleType", obj_temp))
                                            &&*/ map.get("iVehicleTypeId").equalsIgnoreCase(generalFunc.getJsonValueStr("iVehicleTypeId", obj_temp))) {
                                            map.put("total_fare", "");
                                            cabTypeList.set(k, map);
                                        }
                                    }
                                }

                                if (rentalTypeList != null) {
                                    for (int k = 0; k < rentalTypeList.size(); k++) {
                                        HashMap<String, String> map = rentalTypeList.get(k);

                                        if (/*map.get("vVehicleType").equalsIgnoreCase(generalFunc.getJsonValueStr()("vVehicleType", obj_temp))
                                                && */map.get("iVehicleTypeId").equalsIgnoreCase(generalFunc.getJsonValueStr("iVehicleTypeId", obj_temp))) {

                                            String totalfare = generalFunc.getJsonValueStr("eRental_total_fare", obj_temp);
                                            if (totalfare != null && !totalfare.equals("")) {
                                                map.put("total_fare", totalfare);
                                                map.put("eFlatTrip", generalFunc.getJsonValueStr("eFlatTrip", obj_temp));
                                                rentalTypeList.set(k, map);
                                            } else {
                                                map.put("eFlatTrip", generalFunc.getJsonValueStr("eFlatTrip", obj_temp));
                                                rentalTypeList.set(k, map);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });
        exeWebServer.execute();
    }

    @SuppressLint("WrongViewCast")
    public void openFareDetailsDilaog(final int pos) {

        if (mainAct.isMultiDelivery()) {
            return;
        }


        // if (cabTypeList.get(pos).get("total_fare") != null && !cabTypeList.get(pos).get("total_fare").equalsIgnoreCase("")) {
        if (cabTypeList.get(pos).get("total_fare") != null) {
            String vehicleIconPath = CommonUtilities.SERVER_URL + "webimages/icons/VehicleType/";
            String vehicleDefaultIconPath = CommonUtilities.SERVER_URL + "webimages/icons/DefaultImg/";
            final BottomSheetDialog faredialog = new BottomSheetDialog(getActContext());

            View contentView = View.inflate(getContext(), R.layout.dailog_faredetails, null);
            faredialog.setContentView(contentView);
            BottomSheetBehavior mBehavior = BottomSheetBehavior.from((View) contentView.getParent());
            mBehavior.setPeekHeight(1500);
            View bottomSheetView = faredialog.getWindow().getDecorView().findViewById(R.id.design_bottom_sheet);
            BottomSheetBehavior.from(bottomSheetView).setHideable(false);
            setCancelable(faredialog, false);

            ImageView imagecar;
            final TextView carTypeTitle, capacityHTxt, capacityVTxt, fareHTxt, fareVTxt, mordetailsTxt, farenoteTxt, pkgMsgTxt;
            MButton btn_type2;
            int submitBtnId;
            imagecar = (ImageView) faredialog.findViewById(R.id.imagecar);
            carTypeTitle = (TextView) faredialog.findViewById(R.id.carTypeTitle);
            capacityHTxt = (TextView) faredialog.findViewById(R.id.capacityHTxt);
            capacityVTxt = (TextView) faredialog.findViewById(R.id.capacityVTxt);
            fareHTxt = (TextView) faredialog.findViewById(R.id.fareHTxt);
            fareVTxt = (TextView) faredialog.findViewById(R.id.fareVTxt);
            mordetailsTxt = (TextView) faredialog.findViewById(R.id.mordetailsTxt);
            farenoteTxt = (TextView) faredialog.findViewById(R.id.farenoteTxt);
            pkgMsgTxt = (TextView) faredialog.findViewById(R.id.pkgMsgTxt);

            btn_type2 = ((MaterialRippleLayout) faredialog.findViewById(R.id.btn_type2)).getChildView();
            submitBtnId = Utils.generateViewId();
            btn_type2.setId(submitBtnId);


            capacityHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_CAPACITY"));
            fareHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_FARE_TXT"));
            mordetailsTxt.setText(generalFunc.retrieveLangLBl("", "LBL_MORE_DETAILS"));

            if (mainAct.isFixFare) {
                farenoteTxt.setText(generalFunc.retrieveLangLBl("", "LBL_GENERAL_NOTE_FLAT_FARE_EST"));
            } else {
                farenoteTxt.setText(generalFunc.retrieveLangLBl("", "LBL_GENERAL_NOTE_FARE_EST"));
            }
            btn_type2.setText(generalFunc.retrieveLangLBl("", "LBL_DONE"));

            if (cabTypeList.get(pos).get("eRental") != null && cabTypeList.get(pos).get("eRental").equalsIgnoreCase("Yes")) {
                mordetailsTxt.setVisibility(View.GONE);
                pkgMsgTxt.setVisibility(View.VISIBLE);
                fareHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_PKG_STARTING_AT"));

                if (mainAct.eShowOnlyMoto != null && mainAct.eShowOnlyMoto.equalsIgnoreCase("Yes")) {
                    pkgMsgTxt.setText(generalFunc.retrieveLangLBl("", "LBL_RENT_MOTO_PKG_MSG"));
                } else {
                    pkgMsgTxt.setText(generalFunc.retrieveLangLBl("", "LBL_RENT_PKG_MSG"));
                }
                farenoteTxt.setText(generalFunc.retrieveLangLBl("", "LBL_RENT_PKG_DETAILS"));
            }


            if (!cabTypeList.get(pos).get("eRental").equals("") && cabTypeList.get(pos).get("eRental").equals("Yes")) {
                carTypeTitle.setText(cabTypeList.get(pos).get("vRentalVehicleTypeName"));
            } else {
                carTypeTitle.setText(cabTypeList.get(pos).get("vVehicleType"));
            }
            if (cabTypeList.get(pos).get("total_fare") != null && !cabTypeList.get(pos).get("total_fare").equalsIgnoreCase("")) {
                fareVTxt.setText(generalFunc.convertNumberWithRTL(cabTypeList.get(pos).get("total_fare")));
            } else {
                fareVTxt.setText("--");
            }
            if (mainAct.getCurrentCabGeneralType().equals(Utils.CabGeneralType_Ride)) {
                capacityVTxt.setText(generalFunc.convertNumberWithRTL(cabTypeList.get(pos).get("iPersonSize")) + " " + generalFunc.retrieveLangLBl("", "LBL_PEOPLE_TXT"));

            } else {
                capacityVTxt.setText("---");
            }

            String imgName = cabTypeList.get(pos).get("vLogo1");
            if (imgName.equals("")) {
                imgName = vehicleDefaultIconPath + "hover_ic_car.png";
            } else {
                imgName = vehicleIconPath + cabTypeList.get(pos).get("iVehicleTypeId") + "/android/" + "xxxhdpi_" +
                        cabTypeList.get(pos).get("vLogo1");


            }

            Picasso.with(getActContext())
                    .load(imgName)
                    .into(imagecar, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                        }
                    });


            btn_type2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    faredialog.dismiss();

                }
            });

            mordetailsTxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    dialogShowOnce = true;
                    Bundle bn = new Bundle();
                    bn.putString("SelectedCar", cabTypeList.get(pos).get("iVehicleTypeId"));
                    bn.putString("iUserId", generalFunc.getMemberId());
                    bn.putString("distance", distance);
                    bn.putString("time", time);
                    bn.putString("PromoCode", appliedPromoCode);
                    if (cabTypeList.get(pos).get("eRental").equals("yes")) {
                        bn.putString("vVehicleType", cabTypeList.get(pos).get("vRentalVehicleTypeName"));
                    } else {
                        bn.putString("vVehicleType", cabTypeList.get(pos).get("vVehicleType"));
                    }
                    bn.putBoolean("isSkip", isSkip);
                    if (mainAct.getPickUpLocation() != null) {
                        bn.putString("picupLat", mainAct.getPickUpLocation().getLatitude() + "");
                        bn.putString("pickUpLong", mainAct.getPickUpLocation().getLongitude() + "");
                    }
                    if (mainAct.destLocation != null) {
                        bn.putString("destLat", mainAct.destLocLatitude + "");
                        bn.putString("destLong", mainAct.destLocLongitude + "");
                    }
                    if (mainAct.isFixFare) {
                        bn.putBoolean("isFixFare", true);
                    } else {
                        bn.putBoolean("isFixFare", false);
                    }

                    new StartActProcess(getActContext()).startActWithData(FareBreakDownActivity.class, bn);
                    faredialog.dismiss();
                }
            });


            faredialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                }
            });
            faredialog.show();
        }


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

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        releseInstances();
    }

    private void releseInstances() {
        Utils.hideKeyboard(getActContext());
        if (estimateFareTask != null) {
            estimateFareTask.cancel(true);
            estimateFareTask = null;
        }
    }

    public void Checkpickupdropoffrestriction() {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "Checkpickupdropoffrestriction");
        parameters.put("iUserId", generalFunc.getMemberId());
        parameters.put("PickUpLatitude", "" + mainAct.getPickUpLocation().getLatitude());
        parameters.put("PickUpLongitude", "" + mainAct.getPickUpLocation().getLongitude());
        parameters.put("DestLatitude", mainAct.getDestLocLatitude());
        parameters.put("DestLongitude", mainAct.getDestLocLongitude());
        parameters.put("UserType", Utils.userType);

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(String responseString) {

                String message = generalFunc.getJsonValue(Utils.message_str, responseString);
                if (responseString != null && !responseString.equals("")) {
                    if (generalFunc.getJsonValue("Action", responseString).equalsIgnoreCase("0")) {
                        if (message.equalsIgnoreCase("LBL_DROP_LOCATION_NOT_ALLOW")) {
                            generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_DROP_LOCATION_NOT_ALLOW"));
                        } else if (message.equalsIgnoreCase("LBL_PICKUP_LOCATION_NOT_ALLOW")) {
                            generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_PICKUP_LOCATION_NOT_ALLOW"));
                        }
                    } else if (generalFunc.getJsonValue("Action", responseString).equalsIgnoreCase("1")) {
                        mainAct.continueDeliveryProcess();
                    }

                } else {
                    generalFunc.showError();
                }
            }
        });
        exeWebServer.execute();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releseInstances();
    }

    public void outstandingDialog(/*boolean isReqNow*/String responseString, Intent data) {

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getActContext());

        LayoutInflater inflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dailog_outstanding, null);

        final TextView outStandingTitle = (TextView) dialogView.findViewById(R.id.outStandingTitle);
        final TextView outStandingValue = (TextView) dialogView.findViewById(R.id.outStandingValue);
        final TextView cardtitleTxt = (TextView) dialogView.findViewById(R.id.cardtitleTxt);
        final TextView adjustTitleTxt = (TextView) dialogView.findViewById(R.id.adjustTitleTxt);
        final LinearLayout cardArea = (LinearLayout) dialogView.findViewById(R.id.cardArea);
        final LinearLayout adjustarea = (LinearLayout) dialogView.findViewById(R.id.adjustarea);
        outStandingTitle.setText(generalFunc.retrieveLangLBl("", "LBL_OUTSTANDING_AMOUNT_TXT"));
        String type = mainAct.getCurrentCabGeneralType();
        if (SYSTEM_PAYMENT_FLOW.equalsIgnoreCase("Method-3")) {
            adjustarea.setVisibility(View.GONE);
        }
        adjustTitleTxt.setText(generalFunc.retrieveLangLBl("Adjust in Your trip", "LBL_ADJUST_OUT_AMT_RIDE_TXT"));
        if (type.equalsIgnoreCase("Ride")) {
            adjustTitleTxt.setText(generalFunc.retrieveLangLBl("Adjust in Your trip", "LBL_ADJUST_OUT_AMT_RIDE_TXT"));
        } else if (type.equalsIgnoreCase("Deliver")) {
            adjustTitleTxt.setText(generalFunc.retrieveLangLBl("Adjust in Your trip", "LBL_ADJUST_OUT_AMT_DELIVERY_TXT"));
        }
//        outStandingValue.setText(generalFunc.getJsonValue("fOutStandingAmountWithSymbol", userProfileJson));
        outStandingValue.setText(generalFunc.convertNumberWithRTL(generalFunc.getJsonValue("fOutStandingAmountWithSymbol", responseString)));


        cardtitleTxt.setText(generalFunc.retrieveLangLBl("Pay Now", "LBL_PAY_NOW"));

        if (SYSTEM_PAYMENT_FLOW.equalsIgnoreCase("Method-1")) {
            if (APP_PAYMENT_MODE.equalsIgnoreCase("Cash-Card") ||
                    APP_PAYMENT_MODE.equalsIgnoreCase("Card")) {
                cardArea.setVisibility(View.VISIBLE);

            }


            if (!generalFunc.getJsonValue("ShowPayNow", responseString).equalsIgnoreCase("") && generalFunc.getJsonValue("ShowPayNow", responseString).equalsIgnoreCase("NO")) {
                cardArea.setVisibility(View.GONE);
            }
        } else if (!SYSTEM_PAYMENT_FLOW.equalsIgnoreCase("Method-1")) {

            cardtitleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_PAY_BY_WALLET_TXT"));
            cardArea.setVisibility(View.VISIBLE);
        }

        cardArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                outstanding_dialog.dismiss();

                if (SYSTEM_PAYMENT_FLOW.equalsIgnoreCase("Method-1")) {
                    checkCardConfig(true, mainAct.getCabReqType().equals(Utils.CabReqType_Now), responseString, data);
                } else if (!SYSTEM_PAYMENT_FLOW.equalsIgnoreCase("Method-1")) {
                    getUserProfileJson(generalFunc.retrieveValue(Utils.USER_PROFILE_JSON));
                    callOutStandingPayAmout(responseString, data);


                }

            }
        });

        adjustarea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                outstanding_dialog.dismiss();

//                if (isReqNow) {
//                    isOutStandingDailogShow = true;
//                    ride_now_btn.performClick();
//                } else {
//                    isOutStandingDailogShow = true;
//                    img_ridelater.performClick();
//
//                }

                mainAct.continueSurgeChargeExecution(responseString, data);
            }
        });

        if (generalFunc.isRTLmode()) {
            ((ImageView) dialogView.findViewById(R.id.cardimagearrow)).setRotationY(180);
            ((ImageView) dialogView.findViewById(R.id.adjustimagearrow)).setRotationY(180);
        }


        @SuppressLint("WrongViewCast") MButton btn_type2 = ((MaterialRippleLayout) dialogView.findViewById(R.id.btn_type2)).getChildView();
        int submitBtnId = Utils.generateViewId();
        btn_type2.setId(submitBtnId);
        btn_type2.setText(generalFunc.retrieveLangLBl("", "LBL_CANCEL_TXT"));
        btn_type2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                outstanding_dialog.dismiss();
            }
        });

        builder.setView(dialogView);
        outstanding_dialog = builder.create();
        if (generalFunc.isRTLmode() == true) {
            generalFunc.forceRTLIfSupported(outstanding_dialog);
        }
        outstanding_dialog.setCancelable(false);
        outstanding_dialog.show();
    }

    public void callOutStandingPayAmout(/*boolean isReqNow*/ String responseStr, Intent data) {

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "ChargePassengerOutstandingAmount");
        parameters.put("iMemberId", generalFunc.getMemberId());
        parameters.put("UserType", Utils.app_type);
        parameters.put("ePaymentBy", mainAct.ePaymentBy);
        parameters.put("iUserProfileId", mainAct.iUserProfileId);
        parameters.put("iOrganizationId", mainAct.iOrganizationId);
        parameters.put("vProfileEmail", mainAct.vProfileEmail);

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setCancelAble(false);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(responseString -> {

            if (responseString != null && !responseString.equals("")) {
                boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

                if (isDataAvail == true) {
                    String message = generalFunc.getJsonValue(Utils.message_str, responseString);
                    generalFunc.storeData(Utils.USER_PROFILE_JSON, message);
                    getUserProfileJson(generalFunc.retrieveValue(Utils.USER_PROFILE_JSON));
                    mainAct.userProfileJson = userProfileJson;
                    final GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
                    generateAlert.setCancelable(false);
                    generateAlert.setBtnClickList(new GenerateAlertBox.HandleAlertBtnClick() {
                        @Override
                        public void handleBtnClick(int btn_id) {

                            /*if (isReqNow) {
                                isOutStandingDailogShow = true;
                                ride_now_btn.performClick();
                            } else {
                                isOutStandingDailogShow = true;
                                img_ridelater.performClick();
                            }*/

                            mainAct.continueSurgeChargeExecution(responseString, data);
                        }
                    });
                    generateAlert.setContentMessage("", generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str_one, responseString)));
                    generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"));
                    generateAlert.showAlertBox();
                } else {

                    if (generalFunc.getJsonValue(Utils.message_str, responseString).equalsIgnoreCase("LOW_WALLET_AMOUNT")) {
                        String walletMsg = "";

                        String low_balance_content_msg = generalFunc.getJsonValue("low_balance_content_msg", responseString);

                        if (low_balance_content_msg != null && !low_balance_content_msg.equalsIgnoreCase("")) {
                            walletMsg = low_balance_content_msg;
                        } else {
                            walletMsg = generalFunc.retrieveLangLBl("", "LBL_WALLET_LOW_AMOUNT_MSG_TXT");
                        }
                        generalFunc.showGeneralMessage(generalFunc.retrieveLangLBl("", "LBL_LOW_WALLET_BALANCE"), walletMsg, generalFunc.retrieveLangLBl("", "LBL_CANCEL_TXT"), generalFunc.retrieveLangLBl("", "LBL_ADD_MONEY"), button_Id -> {
                            if (button_Id == 1) {

                                new StartActProcess(getActContext()).startAct(MyWalletActivity.class);
                            } else {

                            }
                        });
                    }
                }
            } else {
                generalFunc.showGeneralMessage("",
                        generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)));
            }
        });
        exeWebServer.execute();

    }

    @Override
    public void onGlobalLayout() {
        boolean heightChanged = false;
        if (getView() != null || view != null) {
            if (getView() != null) {

                if (getView().getHeight() != 0 && getView().getHeight() != fragmentHeight) {
                    heightChanged = true;
                }
                fragmentWidth = getView().getWidth();
                fragmentHeight = getView().getHeight();
            } else if (view != null) {

                if (view.getHeight() != 0 && view.getHeight() != fragmentHeight) {
                    heightChanged = true;
                }

                fragmentWidth = view.getWidth();
                fragmentHeight = view.getHeight();
            }

            Logger.e("FragHeight", "is :::" + fragmentHeight + "\n" + "Frag Width is :::" + fragmentWidth);

            if (heightChanged && fragmentWidth != 0 && fragmentHeight != 0) {
                mainAct.setPanelHeight(fragmentHeight);
            }
        }
    }

    @Override
    public void onItemClick(int position, String selectedType) {

        seatsSelectpos = position;
        double totalFare = GeneralFunctions.parseDoubleValue(0, cabTypeList.get(selpos).get("FinalFare"));
        double seatVal = GeneralFunctions.parseDoubleValue(1, poolSeatsList.get(position));

        if (seatVal > 1) {
            double res = (totalFare / 100.0f) * GeneralFunctions.parseDoubleValue(0, mainAct.cabTypesArrList.get(selpos).get("fPoolPercentage"));
            res = res + totalFare;
            poolFareTxt.setText(cabTypeList.get(selpos).get("currencySymbol") + " " + String.format("%.2f", (float) res));
        } else {
            poolFareTxt.setText(cabTypeList.get(selpos).get("total_fare"));
        }

        if (seatsSelectionAdapter != null) {
            seatsSelectionAdapter.setSelectedSeat(seatsSelectpos);
            seatsSelectionAdapter.notifyDataSetChanged();
        }
    }

    public void hideRentalArea() {
        rentalPkg.setVisibility(View.GONE);
        rentalarea.setVisibility(View.GONE);
        rentPkgImage.setVisibility(View.GONE);
        rentBackPkgImage.setVisibility(View.GONE);
    }


    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            int i = view.getId();
            Utils.hideKeyboard(getActContext());
            if (i == R.id.minFareArea) {
                openFareEstimateDialog();
            } else if (i == ride_now_btn.getId()) {

                if (mProgressBar.getVisibility() == View.VISIBLE) {
                    generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("Route not found", "LBL_DEST_ROUTE_NOT_FOUND"));
                    return;
                }
                if ((mainAct.currentLoadedDriverList != null && mainAct.currentLoadedDriverList.size() < 1) || mainAct.currentLoadedDriverList == null || (cabTypeList != null && cabTypeList.size() < 1) || cabTypeList == null) {

                    buildNoCabMessage(generalFunc.retrieveLangLBl("", "LBL_NO_CARS_AVAIL_IN_TYPE"),
                            generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"));
                    return;
                }

                if (cabTypeList.get(selpos).get("ePoolStatus").equalsIgnoreCase("Yes") && !mainAct.isDestinationAdded) {
                    generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_DESTINATION_REQUIRED_POOL"));
                    return;

                }


                if (isRouteFail && !mainAct.isMultiDelivery()) {
                    generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("Route not found", "LBL_DEST_ROUTE_NOT_FOUND"));
                    return;
                }

                // if (!ridenowclick) {
//                if (!isOutStandingDailogShow) {
////
////                    if (generalFunc.getJsonValue("fOutStandingAmount", userProfileJson) != null &&
////                            GeneralFunctions.parseDoubleValue(0, generalFunc.getJsonValue("fOutStandingAmount", userProfileJson)) > 0) {
////                        outstandingDialog(true);
////                        //  ridenowclick = true;
////                        return;
////
////                    }
////                }
                //  }


                // isOutStandingDailogShow = false;


                // if (!ridenowclick) {

                mainAct.setCabReqType(Utils.CabReqType_Now);


//                    if (mainAct.getDestinationStatus()) {
//                        String destLocAdd = mainAct != null ? (mainAct.getDestAddress().equals(
//                                generalFunc.retrieveLangLBl("", "LBL_SELECTING_LOCATION_TXT")) ? "" : mainAct.getDestAddress()) : "";
//                        if (destLocAdd.equals("")) {
//                            return;
//                        }
//                    }

                if (!isCardValidated && APP_PAYMENT_MODE.equalsIgnoreCase("Card") &&
                        SYSTEM_PAYMENT_FLOW.equalsIgnoreCase("Method-1")) {
                    isCardnowselcted = true;
                    isCardlaterselcted = false;
                    checkCardConfig();
                    return;
                }


                if (mainAct.isDeliver(mainAct.getCurrentCabGeneralType())) {
                    if (!mainAct.getDestinationStatus() && !mainAct.isMultiDelivery()) {
                        generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("Please add your destination location " +
                                "to deliver your package.", "LBL_ADD_DEST_MSG_DELIVER_ITEM"));
                        return;
                    }
                    Checkpickupdropoffrestriction();
                    // mainAct.setDeliverySchedule();
                    return;
                }

                // ridenowclick = true;

                if (cabTypeList.get(selpos).get("ePoolStatus").equalsIgnoreCase("Yes")) {
                    poolArea.setVisibility(View.VISIBLE);
                    mainContentArea.setVisibility(View.GONE);

                    double totalFare = GeneralFunctions.parseDoubleValue(0, cabTypeList.get(selpos).get("FinalFare"));
                    double seatVal = GeneralFunctions.parseDoubleValue(1, poolSeatsList.get(seatsSelectpos));
                    if (seatVal > 1) {
                        double res = (totalFare / 100.0f) * GeneralFunctions.parseDoubleValue(0, mainAct.cabTypesArrList.get(selpos).get("fPoolPercentage"));
                        res = res + totalFare;
                        poolFareTxt.setText(cabTypeList.get(selpos).get("currencySymbol") + " " + String.format("%.2f", (float) res));
                    } else {
                        poolFareTxt.setText(cabTypeList.get(selpos).get("total_fare"));
                    }

                    return;
                }

                if (!mainAct.getCabReqType().equals(Utils.CabReqType_Later)) {
                    //  mainAct.requestPickUp();


                    if (cabTypeList.get(selpos).get("eRental") != null && !cabTypeList.get(selpos).get("eRental").equalsIgnoreCase("") &&
                            cabTypeList.get(selpos).get("eRental").equalsIgnoreCase("Yes")) {

                        Bundle bn = new Bundle();
                        bn.putString("address", mainAct.pickUpLocationAddress);
                        bn.putString("vVehicleType", cabTypeList.get(selpos).get("vRentalVehicleTypeName"));
                        bn.putString("iVehicleTypeId", cabTypeList.get(selpos).get("iVehicleTypeId"));
                        bn.putString("vLogo", cabTypeList.get(selpos).get("vLogo1"));
                        bn.putString("eta", etaTxt.getText().toString());
                        bn.putString("eMoto", mainAct.eShowOnlyMoto);
                        bn.putString("PromoCode", appliedPromoCode);


                        new StartActProcess(getActContext()).startActForResult(
                                RentalDetailsActivity.class, bn, RENTAL_REQ_CODE);
                        return;

                    }

                    // ride_now_btn.setEnabled(false);
                    //  ride_now_btn.setClickable(false);


                    mainAct.continuePickUpProcess();
                } else {
                    //   ride_now_btn.setEnabled(false);
                    // ride_now_btn.setClickable(false);


                    mainAct.setRideSchedule();
                }

//                    Handler handler = new Handler();
//                    handler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            ridenowclick = false;
//                        }
//                    }, 500);
                // }
            } else if (i == img_ridelater.getId()) {
                try {

                    if (mainAct.stopOverPointsList.size() > 2) {
                        generalFunc.showMessage(carTypeRecyclerView, generalFunc.retrieveLangLBl("", "LBL_REMOVE_MULTI_STOP_OVER_TXT"));
                        return;
                    }


                    if (mProgressBar.getVisibility() == View.VISIBLE) {
                        generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("Route not found", "LBL_DEST_ROUTE_NOT_FOUND"));
                        return;
                    }


                    if (!cabTypeList.get(selpos).get("eRental").equalsIgnoreCase("Yes")) {
                        if (mainAct.destAddress == null || mainAct.destAddress.equalsIgnoreCase("")) {
                            generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("Destination is required to create scheduled booking.", "LBL_DEST_REQ_FOR_LATER"));

                            return;
                        }
                    }

                    if (isRouteFail && !mainAct.isMultiDelivery()) {
                        generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("Route not found", "LBL_DEST_ROUTE_NOT_FOUND"));
                        return;
                    }
//                    if (!isOutStandingDailogShow) {
//                        if (generalFunc.getJsonValue("fOutStandingAmount", userProfileJson) != null &&
//                                GeneralFunctions.parseIntegerValue(0, generalFunc.getJsonValue("fOutStandingAmount", userProfileJson)) > 0) {
//                            outstandingDialog(false);
//                            return;
//
//                        }
//                    }


//                if (!ridelaterclick) {
//                    ridelaterclick = true;
                    if (cabTypeList.size() > 0) {
                        if (!isCardValidated && APP_PAYMENT_MODE.equalsIgnoreCase("Card") && !mainAct.isMultiDelivery()) {
                            isCardlaterselcted = true;
                            isCardnowselcted = false;
                            checkCardConfig();
                            return;
                        }
                        //  ride_now_btn.setEnabled(false);
                        // ride_now_btn.setTextColor(Color.parseColor("#BABABA"));
                        //  ride_now_btn.setClickable(false);
                        mainAct.chooseDateTime();
                        //      }
//                    Handler handler = new Handler();
//                    handler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            ridelaterclick = false;
//                        }
//                    }, 200);
                    }
                } catch (Exception e) {

                }
            } else if (i == R.id.organizationArea) {
                paymentArea.performClick();
            } else if (i == R.id.paymentArea) {

                if (generalFunc.getJsonValueStr("ENABLE_CORPORATE_PROFILE", mainAct.obj_userProfile).equalsIgnoreCase("Yes") && mainAct.getCurrentCabGeneralType().equalsIgnoreCase("Ride")) {

                    Bundle bn = new Bundle();
                    bn.putString("isWallet", mainAct.eWalletDebitAllow);
                    bn.putBoolean("isCash", mainAct.isCashSelected);
                    bn.putString("ePaymentBy", mainAct.ePaymentBy);
                    bn.putString("selectReasonId", mainAct.selectReasonId);
                    bn.putString("vReasonTitle", mainAct.vReasonTitle);
                    bn.putString("iUserProfileId", mainAct.iUserProfileId);
                    bn.putString("iOrganizationId", mainAct.iOrganizationId);
                    bn.putString("vProfileEmail", mainAct.vProfileEmail);
                    bn.putString("vProfileName", mainAct.vProfileName);
                    bn.putString("vReasonName", mainAct.vReasonName);
                    bn.putInt("selectPos", mainAct.selectPos);
                    bn.putString("vImage", mainAct.vImage);
                    new StartActProcess(getActContext()).startActForResult(BusinessSelectPaymentActivity.class, bn, Utils.SELECT_ORGANIZATION_PAYMENT_CODE);
                    return;
                }

                if (payTypeSelectArea.getVisibility() == View.VISIBLE) {
                    hidePayTypeSelectionArea();
                } else {
                    if (APP_PAYMENT_MODE.equalsIgnoreCase("Cash-Card")) {

                        if (rentalTypeList.size() > 0 && !mainAct.iscubejekRental) {
                            Runnable r = new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        if (rentPkgImage.getVisibility() == View.VISIBLE) {
                                            mainAct.setPanelHeight(335);
                                        } else {
                                            mainAct.setPanelHeight(280);

                                        }
                                    } catch (Exception e2) {
                                        new Handler().postDelayed(this, 20);
                                    }
                                }
                            };
                            new Handler().postDelayed(r, 20);
                        } else {
                            mainAct.setPanelHeight(283);
                        }
                        payTypeSelectArea.setVisibility(View.VISIBLE);
                        cashcardarea.setVisibility(View.GONE);
                    } else {


                        if (rentalTypeList.size() > 0 && !mainAct.iscubejekRental) {
                            Runnable r = new Runnable() {

                                @Override
                                public void run() {
                                    try {
                                        mainAct.setPanelHeight(335 - 55);
                                    } catch (Exception e2) {
                                        new Handler().postDelayed(this, 20);
                                    }
                                }
                            };
                            new Handler().postDelayed(r, 20);
                        } else {
                            mainAct.setPanelHeight(283 - 48);
                        }
                    }
                }

            } else if (i == R.id.promoArea) {
                // showPromoBox();
                Bundle bn = new Bundle();
                bn.putString("CouponCode", appliedPromoCode);
                bn.putString("eType", mainAct.getCurrentCabGeneralType());
                new StartActProcess(getActContext()).startActForResult(CabSelectionFragment.this, CouponActivity.class, Utils.SELECT_COUPON_REQ_CODE, bn);
            } else if (i == R.id.cardarea) {
                hidePayTypeSelectionArea();
                if (SYSTEM_PAYMENT_FLOW.equalsIgnoreCase("Method-1")) {
                    setCashSelection();
                    checkCardConfig();
                } else {
                    isCardSelect = true;
                    setWalletSelection();

                }
                //   }

            } else if (i == R.id.casharea) {
                isCardSelect = false;
                hidePayTypeSelectionArea();
                setCashSelection();
            } else if (i == R.id.rentalBackImage) {

                mainAct.isRental = false;
                mainAct.iscubejekRental = false;

                if (mainAct.loadAvailCabs != null) {
                    mainAct.loadAvailCabs.checkAvailableCabs();
                }
                selpos = 0;
                iRentalPackageId = "";
                lstSelectpos = 0;
                cabTypeList = (ArrayList<HashMap<String, String>>) tempCabTypeList.clone();
                mainAct.setCabTypeList(cabTypeList);
                tempCabTypeList.clear();
                tempCabTypeList = (ArrayList<HashMap<String, String>>) cabTypeList.clone();
                isRental = false;
                if (cabTypeList.size() > 0) {
                    adapter.setSelectedVehicleTypeId(cabTypeList.get(0).get("iVehicleTypeId"));
                    mainAct.selectedCabTypeId = cabTypeList.get(0).get("iVehicleTypeId");
                    adapter.setRentalItem(cabTypeList);
                    adapter.notifyDataSetChanged();
                }
                rentalBackImage.setVisibility(View.GONE);
                rentalPkgDesc.setVisibility(View.GONE);


                rentalPkg.setVisibility(View.VISIBLE);
                rentalarea.setVisibility(View.VISIBLE);
                rentPkgImage.setVisibility(View.VISIBLE);
                rentBackPkgImage.setVisibility(View.VISIBLE);
                android.view.animation.Animation bottomUp = AnimationUtils.loadAnimation(getActContext(),
                        R.anim.slide_up_anim);
                carTypeRecyclerView.startAnimation(bottomUp);

                showBookingLaterArea();

                if (!mainAct.iscubejekRental) {
                    Runnable r = new Runnable() {

                        @Override
                        public void run() {
                            try {
                                mainAct.setPanelHeight(280);

                               /* RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) (mainAct.userLocBtnImgView).getLayoutParams();
                                params.bottomMargin = Utils.dipToPixels(getActContext(), 300);*/
                            } catch (Exception e2) {
                                new Handler().postDelayed(this, 20);
                            }
                        }
                    };
                    new Handler().postDelayed(r, 20);
                }

            } else if (i == R.id.rentalPkg) {


                mainAct.isRental = true;
                mainAct.iscubejekRental = true;

                if (mainAct.loadAvailCabs != null) {
                    mainAct.loadAvailCabs.checkAvailableCabs();
                }

                selpos = 0;
                iRentalPackageId = "";
                lstSelectpos = 1;
                tempCabTypeList.clear();
                tempCabTypeList = (ArrayList<HashMap<String, String>>) cabTypeList.clone();
                cabTypeList.clear();
                cabTypeList = (ArrayList<HashMap<String, String>>) rentalTypeList.clone();
                adapter.setRentalItem(cabTypeList);
                isRental = true;
                if (cabTypeList.size() > 0) {
                    adapter.setSelectedVehicleTypeId(cabTypeList.get(0).get("iVehicleTypeId"));
                    mainAct.selectedCabTypeId = cabTypeList.get(0).get("iVehicleTypeId");
                    adapter.notifyDataSetChanged();
                }
                rentalPkgDesc.setVisibility(View.VISIBLE);

                rentalBackImage.setVisibility(View.VISIBLE);
                rentalPkg.setVisibility(View.GONE);
                rentalarea.setVisibility(View.GONE);
                rentPkgImage.setVisibility(View.GONE);
                rentBackPkgImage.setVisibility(View.GONE);

                android.view.animation.Animation bottomUp = AnimationUtils.loadAnimation(getActContext(),
                        R.anim.slide_up_anim);
                carTypeRecyclerView.startAnimation(bottomUp);

                imageLaterarea.setVisibility(View.GONE);

                Runnable r = new Runnable() {

                    @Override
                    public void run() {
                        try {
                            mainAct.setPanelHeight(270);
                        } catch (Exception e2) {
                            new Handler().postDelayed(this, 20);
                        }
                    }
                };
                new Handler().postDelayed(r, 20);
            } else if (i == R.id.rentPkgImage) {
                rentalPkg.performClick();
            } else if (i == R.id.poolBackImage) {
                poolArea.setVisibility(View.GONE);
                mainContentArea.setVisibility(View.VISIBLE);
                if (seatsSelectionAdapter != null) {
                    seatsSelectpos = 0;

                    seatsSelectionAdapter.setSelectedSeat(seatsSelectpos);
                    seatsSelectionAdapter.notifyDataSetChanged();

                    if (cabTypeList != null && cabTypeList.get(selpos).get("total_fare") != null && !cabTypeList.get(selpos).get("total_fare").equalsIgnoreCase("")) {
                        poolFareTxt.setText(cabTypeList.get(selpos).get("total_fare"));
                    }

                }
                /*cashCardArea.setVisibility(View.VISIBLE);
                carTypeRecyclerView.setVisibility(View.VISIBLE);*/
            } else if (i == confirm_seats_btn.getId()) {


                mainAct.continuePickUpProcess();
            }

        }

    }

    public void setOrganizationName(String name, boolean isOrganization) {
        organizationTxt.setText(name);
        if (!isOrganization) {
            LinearLayout.LayoutParams organizationLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            organizationLayoutParams.setMargins(0, 0, 0, -Utils.dpToPx(getActContext(), 5));
            organizationArea.setLayoutParams(organizationLayoutParams);
            organizationTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
        } else {
            LinearLayout.LayoutParams organizationLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            organizationLayoutParams.setMargins(0, 0, 0, 0);
            organizationArea.setLayoutParams(organizationLayoutParams);

            organizationTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            cashRadioBtn.setVisibility(View.GONE);
            payTypeTxt.setText(generalFunc.retrieveLangLBl("", "LBL_CASH_TXT"));
            cardRadioBtn.setVisibility(View.GONE);
            payTypeTxt.setVisibility(View.GONE);
            cashRadioBtn.setVisibility(View.GONE);
            isCardValidated = true;
            payImgView.setImageResource(R.drawable.ic_business_pay);
            payImgView.setColorFilter(getResources().getColor(R.color.businesspay), PorterDuff.Mode.SRC_IN);

        }
    }

    public void setPaymentType(String type) {
        payTypeTxt.setVisibility(View.VISIBLE);
        if (type.equalsIgnoreCase("Cash")) {
            cashRadioBtn.setVisibility(View.VISIBLE);
            cardRadioBtn.setVisibility(View.GONE);
            setCashSelection();
        } else if (type.equalsIgnoreCase("Card")) {

            if (!SYSTEM_PAYMENT_FLOW.equalsIgnoreCase("Method-1")) {
                cashRadioBtn.setVisibility(View.GONE);
                cardRadioBtn.setVisibility(View.VISIBLE);
                setWalletSelection();
                isCardValidated = true;
            } else {
                cashRadioBtn.setVisibility(View.GONE);
                cardRadioBtn.setVisibility(View.VISIBLE);
                setCardSelection();
                isCardValidated = true;
            }
        }
    }


    public boolean handleRnetalView(String selectedTime) {
        if (cabTypeList.get(selpos).get("eRental") != null && !cabTypeList.get(selpos).get("eRental").equalsIgnoreCase("") &&
                cabTypeList.get(selpos).get("eRental").equalsIgnoreCase("Yes")) {

            Bundle bn = new Bundle();
            bn.putString("address", mainAct.pickUpLocationAddress);
            bn.putString("vVehicleType", cabTypeList.get(selpos).get("vRentalVehicleTypeName"));
            bn.putString("iVehicleTypeId", cabTypeList.get(selpos).get("iVehicleTypeId"));
            bn.putString("vLogo", cabTypeList.get(selpos).get("vLogo1"));
            bn.putString("eta", etaTxt.getText().toString());
            bn.putString("selectedTime", selectedTime);
            bn.putString("eMoto", mainAct.eShowOnlyMoto);
            bn.putString("PromoCode", appliedPromoCode);
            new StartActProcess(getActContext()).startActForResult(
                    RentalDetailsActivity.class, bn, RENTAL_REQ_CODE);
            return true;


        }
        return false;
    }

    //    public PolylineOptions createCurveRoute(LatLng origin, LatLng dest) {
//
//        double distance = SphericalUtil.computeDistanceBetween(origin, dest);
//        double heading = SphericalUtil.computeHeading(origin, dest);
//        double halfDistance = distance > 0 ? (distance / 2) : (distance * DEFAULT_CURVE_ROUTE_CURVATURE);
//
//        // Calculate midpoint position
//        LatLng midPoint = SphericalUtil.computeOffset(origin, halfDistance, heading);
//
//        // Calculate position of the curve center point
//        double sqrCurvature = DEFAULT_CURVE_ROUTE_CURVATURE * DEFAULT_CURVE_ROUTE_CURVATURE;
//        double extraParam = distance / (4 * DEFAULT_CURVE_ROUTE_CURVATURE);
//        double midPerpendicularLength = (1 - sqrCurvature) * extraParam;
//        double r = (1 + sqrCurvature) * extraParam;
//
//        LatLng circleCenterPoint = SphericalUtil.computeOffset(midPoint, midPerpendicularLength, heading + 90.0);
//
//        // Calculate heading between circle center and two points
//        double headingToOrigin = SphericalUtil.computeHeading(circleCenterPoint, origin);
//
//        // Calculate positions of points on the curve
//        double step = Math.toDegrees(Math.atan(halfDistance / midPerpendicularLength)) * 2 / DEFAULT_CURVE_POINTS;
//        //Polyline options
//        PolylineOptions options = new PolylineOptions();
//
//        for (int i = 0; i < DEFAULT_CURVE_POINTS; ++i) {
//            LatLng pi = SphericalUtil.computeOffset(circleCenterPoint, r, headingToOrigin + i * step);
//            options.add(pi);
//        }
//        return options;
//    }
    public PolylineOptions createCurveRoute(LatLng srcLoc, LatLng destLoc) {
        //Polyline options
        PolylineOptions options = new PolylineOptions();


        if (wayPointslist.size() > 0) {
            for (int j = 0; j < wayPointslist.size(); j++) {

                if (j == 0) {
                    getCurveRouteOptions(srcLoc, wayPointslist.get(j).getDestLatLong(), options);

                } else if (j == wayPointslist.size() - 1) {
                    getCurveRouteOptions(wayPointslist.get(j).getDestLatLong(), destLoc, options);
                } else {
                    getCurveRouteOptions(wayPointslist.get(j - 1).getDestLatLong(), wayPointslist.get(j).getDestLatLong(), options);
                }

                if (wayPointslist.size() == 1) {
                    getCurveRouteOptions(wayPointslist.get(j).getDestLatLong(), destLoc, options);
                }

                String pinText;
                int pinIcon = R.mipmap.dot_filled;

                pinText = "" + (j + 1);


                Bitmap marker_time_ic = generalFunc.writeTextOnDrawable(getActContext(), pinIcon, pinText, false, R.string.defaultFont);
                Marker dest_marker = mainAct.getMap().addMarker(
                        new MarkerOptions().position(wayPointslist.get(j).getDestLatLong())
                                .icon(BitmapDescriptorFactory.fromBitmap(marker_time_ic)));
                builder.include(dest_marker.getPosition());
                markerArrayList.add(dest_marker);


            }

        } else {
            getCurveRouteOptions(srcLoc, destLoc, options);
        }
        return options;


    }

    public PolylineOptions getCurveRouteOptions(LatLng origin, LatLng dest, PolylineOptions options) {

        double distance = SphericalUtil.computeDistanceBetween(origin, dest);
        double heading = SphericalUtil.computeHeading(origin, dest);
        double halfDistance = distance > 0 ? (distance / 2) : (distance * DEFAULT_CURVE_ROUTE_CURVATURE);

        // Calculate midpoint position
        LatLng midPoint = SphericalUtil.computeOffset(origin, halfDistance, heading);

        // Calculate position of the curve center point
        double sqrCurvature = DEFAULT_CURVE_ROUTE_CURVATURE * DEFAULT_CURVE_ROUTE_CURVATURE;
        double extraParam = distance / (4 * DEFAULT_CURVE_ROUTE_CURVATURE);
        double midPerpendicularLength = (1 - sqrCurvature) * extraParam;
        double r = (1 + sqrCurvature) * extraParam;

        LatLng circleCenterPoint = SphericalUtil.computeOffset(midPoint, midPerpendicularLength, heading + 90.0);

        // Calculate heading between circle center and two points
        double headingToOrigin = SphericalUtil.computeHeading(circleCenterPoint, origin);

        // Calculate positions of points on the curve
        double step = Math.toDegrees(Math.atan(halfDistance / midPerpendicularLength)) * 2 / DEFAULT_CURVE_POINTS;

        for (int i = 0; i < DEFAULT_CURVE_POINTS; ++i) {
            LatLng pi = SphericalUtil.computeOffset(circleCenterPoint, r, headingToOrigin + i * step);
            options.add(pi);

        }
      //  options.width(4).color(getActContext().getResources().getColor(R.color.green));
        return options;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Utils.SELECT_COUPON_REQ_CODE && resultCode == Activity.RESULT_OK) {
            String couponCode = data.getStringExtra("CouponCode");
            if (couponCode == null) {
                couponCode = "";
            }
            appliedPromoCode = couponCode;

            Logger.d("ManagePath", "::onActivityResult()");
            findRoute("--");
        }
    }

    public void manageisRentalValue() {
        if (rentalarea.getVisibility() == View.VISIBLE || rentalBackImage.getVisibility() == View.VISIBLE) {
            mainAct.isRental = false;
            mainAct.iscubejekRental = false;

        }
    }



    private TimeDistanceFare sendAndRequestResponsetime(double srtLat,double strtlang,double endlat,double endlang) {

        TimeDistanceFare timeDistanceFare = new TimeDistanceFare();

        RequestQueue mRequestQueue;
        StringRequest mStringRequest;
        String url = "https://maps.googleapis.com/maps/api/distancematrix/json?origins="+srtLat+","+strtlang+"+&destinations=+"+endlat+","+endlang+"&mode=driving&language=en-EN&sensor=false&key=AIzaSyAMXLhiO0qGD0ZtVMpl3ML_OzqWxocX4Sc";
        mRequestQueue = Volley.newRequestQueue(getActContext());
        mStringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray obj_routes = generalFunc.getJsonArray("rows", response);
                    JSONObject obj_temp = generalFunc.getJsonObject(obj_routes, 0);
                    JSONArray jsonArray= generalFunc.getJsonArray("elements",obj_temp);
                    JSONObject obj_temp1 = generalFunc.getJsonObject(jsonArray, 0);
                    JSONObject jsonArray1= generalFunc.getJsonObject("distance",obj_temp1);
                    String googledistance= generalFunc.getJsonValueStr("value",jsonArray1);
                    JSONObject jsonArray2= generalFunc.getJsonObject("duration",obj_temp1);
                    String googletime= generalFunc.getJsonValueStr("value",jsonArray2);

                    timeDistanceFare.setDistance(Integer.parseInt(googledistance));
                    timeDistanceFare.setTime(Integer.parseInt(googletime));

                    settime(Double.parseDouble(googletime),Double.parseDouble(googledistance),srtLat,strtlang,endlat,endlang);



                }
                catch (Exception e){
                    e.printStackTrace();
                    Log.i("hhhhhhhhhhh","Exception:" + e);

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.i("hhhhhhhhhhh","Error :" + error.toString());
                sendAndRequestResponsetime(srtLat,strtlang,endlat,endlang);
            }
        });

        mRequestQueue.add(mStringRequest);


        return timeDistanceFare;
    }

    private void settime(double time,double dis,double srtLat,double strtlang,double endlat,double endlang){
        double distance =dis;
        this.distance = ""+distance;
        this.time = ""+time;
        Log.i("hhhhhhhhhhh","googletime :" + this.time+"::"+this.distance);
        if (getActivity() != null) {
            estimateFare(distance + "", time + "");
        }

        handleMapCurveAnimation(new LatLng(srtLat, strtlang),
                new LatLng(endlat, endlang), "--");


    }




}


