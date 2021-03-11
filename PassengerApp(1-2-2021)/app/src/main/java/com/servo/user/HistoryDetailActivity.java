package com.servo.user;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.core.view.GravityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.text.InputType;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;

import com.general.files.CustomHorizontalScrollView;
import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.general.files.MyApp;
import com.general.files.OnSwipeTouchListener;
import com.general.files.SetOnTouchList;
import com.general.files.SlideAnimationUtil;
import com.general.files.StartActProcess;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.kyleduo.switchbutton.SwitchButton;
import com.model.Delivery_Data;
import com.model.Trip_Status;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.utils.CommonUtilities;
import com.utils.Utils;
import com.view.CreateRoundedView;
import com.view.ErrorView;
import com.GenerateAlertBox;
import com.view.MButton;
import android.widget.TextView;
import com.view.MaterialRippleLayout;
import com.view.SelectableRoundedImageView;
import com.view.editBox.MaterialEditText;
import com.view.simpleratingbar.SimpleRatingBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.servo.user.R.id.ratingBar;

public class HistoryDetailActivity extends AppCompatActivity implements OnMapReadyCallback {

    public GeneralFunctions generalFunc;
    TextView titleTxt;
    TextView subTitleTxt;
    ImageView backImgView;
    GoogleMap gMap;
    LinearLayout fareDetailDisplayArea;
    View convertView = null;

    LinearLayout beforeServiceArea, afterServiceArea;
    String before_serviceImg_url = "";
    String after_serviceImg_url = "";
    String isRatingDone = "";
    MButton btn_type2;
    String userProfileJson;
    TextView ratingDriverHTxt;
    LinearLayout profilebgarea;
    TextView cartypeTxt;
    TextView ufxratingDriverHTxt;
    SimpleRatingBar ufxratingBar;

    TextView tipHTxt, tipamtTxt, tipmsgTxt;
    CardView tiparea;
    private int rateBtnId;
    private MaterialEditText commentBox;
    TextView helpTxt;
    ImageView tipPluseImage;
    TextView vReasonTitleTxt;

    private String tripData = "";
    /*Multi Delivery Rlated fields*/
    private ArrayList<Trip_Status> recipientDetailList = new ArrayList<>();
    private Dialog signatureImageDialog;
    private String senderImage;
    String iTripId = "";
    private View signatureArea;

    ProgressBar loading;
    ErrorView errorView;
    RelativeLayout container;

    TextView viewReqServicesTxtView;

    /* Fav Driver variable declaration start */
    LinearLayout favArea;
    LinearLayout lineArea;
    TextView favDriverTitleTxt;
    SwitchButton favSwitch;
    CustomHorizontalScrollView hScrollView;
    private boolean isAnimated = false;


    int width;
    int width_D;
    int width_;

    /* Fav Driver variable declaration end */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_detail);


        generalFunc = MyApp.getInstance().getGeneralFun(getActContext());
        userProfileJson = generalFunc.retrieveValue(Utils.USER_PROFILE_JSON);


        helpTxt = (TextView) findViewById(R.id.helpTxt);
        titleTxt = (TextView) findViewById(R.id.titleTxt);
        subTitleTxt = (TextView) findViewById(R.id.subTitleTxt);
        commentBox = (MaterialEditText) findViewById(R.id.commentBox);
        vReasonTitleTxt = (TextView) findViewById(R.id.vReasonTitleTxt);
        signatureArea = (LinearLayout) findViewById(R.id.signatureArea);
        viewReqServicesTxtView = (TextView) findViewById(R.id.viewReqServicesTxtView);

        View commentArea = findViewById(R.id.commentArea);
        tipPluseImage = (ImageView) findViewById(R.id.tipPluseImage);
        commentBox.setInputType(InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE);
        commentBox.setSingleLine(false);
        commentBox.setHideUnderline(true);
        commentBox.setGravity(GravityCompat.START | Gravity.TOP);
        commentBox.setLines(5);

        commentBox.setBothText("", generalFunc.retrieveLangLBl("", "LBL_WRITE_COMMENT_HINT_TXT"));

        new CreateRoundedView(Color.parseColor("#FFFFFF"), 0, Utils.dipToPixels(getActContext(), 1), Color.parseColor("#F2F2F2"), commentArea);

        backImgView = (ImageView) findViewById(R.id.backImgView);
        fareDetailDisplayArea = (LinearLayout) findViewById(R.id.fareDetailDisplayArea);
        afterServiceArea = (LinearLayout) findViewById(R.id.afterServiceArea);
        beforeServiceArea = (LinearLayout) findViewById(R.id.beforeServiceArea);
        ratingDriverHTxt = (TextView) findViewById(R.id.ratingDriverHTxt);
        profilebgarea = (LinearLayout) findViewById(R.id.profilebgarea);
        cartypeTxt = (TextView) findViewById(R.id.cartypeTxt);

        ufxratingDriverHTxt = (TextView) findViewById(R.id.ufxratingDriverHTxt);
        ufxratingBar = (SimpleRatingBar) findViewById(R.id.ufxratingBar);


        tipHTxt = (TextView) findViewById(R.id.tipHTxt);
        tipamtTxt = (TextView) findViewById(R.id.tipamtTxt);
        tipmsgTxt = (TextView) findViewById(R.id.tipmsgTxt);

        loading = (ProgressBar) findViewById(R.id.loading);
        errorView = (ErrorView) findViewById(R.id.errorView);
        container = (RelativeLayout) findViewById(R.id.container);

        tiparea = (CardView) findViewById(R.id.tiparea);


        btn_type2 = ((MaterialRippleLayout) findViewById(R.id.btn_type2)).getChildView();
        rateBtnId = Utils.generateViewId();
        btn_type2.setId(rateBtnId);

        btn_type2.setOnClickListener(new setOnClickList());
        viewReqServicesTxtView.setOnClickListener(new setOnClickList());


        if (getIntent().hasExtra("iTripId")) {
            iTripId = getIntent().getStringExtra("iTripId");
            getBookingsHistory(false);
        } else {
            tripData = getIntent().getStringExtra("TripData");
            setLabels(tripData);
            setData(tripData);
        }

       /* setLabels();
        setData();*/


        commentBox.setTextColor(getResources().getColor(R.color.mdtp_transparent_black));

        backImgView.setOnClickListener(new setOnClickList());
        subTitleTxt.setOnClickListener(new setOnClickList());
        afterServiceArea.setOnClickListener(new setOnClickList());
        beforeServiceArea.setOnClickListener(new setOnClickList());
        helpTxt.setOnClickListener(new setOnClickList());
        commentBox.setOnTouchListener((v, event) -> {
            ((NestedScrollView) findViewById(R.id.scrollContainer)).requestDisallowInterceptTouchEvent(true);
            return false;
        });
    }

    private void scrollWithanimation() {
        if (!ufxratingBar.isFocusable()) {
            lineArea.performClick();
        }else
        {
            showSetAsFavArea(true);
        }
    }



    private void showSetAsFavArea(boolean show) {
        if (show) {

            if (generalFunc.isRTLmode()) {
                SlideAnimationUtil.slideInFromLeft(getActContext(), favArea);
            } else {
                SlideAnimationUtil.slideOutToRight(getActContext(), favArea);
            }

            if (!isAnimated) {
                hScrollView.setScrollingEnabled(true);

                ufxratingBar.animate().translationXBy(generalFunc.isRTLmode()?width_:-width_);

                ufxratingBar.setIndicator(true);
                ufxratingBar.setFocusable(false);

                favArea.setVisibility(View.VISIBLE);
                favArea.animate().translationXBy(generalFunc.isRTLmode()? width_D : -width_D) ;

                isAnimated = true;

                hScrollView.setScrollingEnabled(false);
            }

        } else {
//

            if (generalFunc.isRTLmode()) {
                SlideAnimationUtil.slideInFromRight(getActContext(), ufxratingBar);
            } else {
                SlideAnimationUtil.slideOutToLeft(getActContext(), ufxratingBar);
            }

            if (isAnimated) {
                hScrollView.setScrollingEnabled(true);

                ufxratingBar.animate().translationXBy(generalFunc.isRTLmode()?-width_:width_);

                ufxratingBar.setIndicator(false);
                ufxratingBar.setFocusable(true);

                favArea.animate().translationXBy(generalFunc.isRTLmode()?-width_D:width_D);
                favArea.setVisibility(View.GONE);

                isAnimated = false;

                hScrollView.setScrollingEnabled(false);
            }


        }
//        favArea.setVisibility(show?View.VISIBLE:View.GONE);
//        ratingBar.setVisibility(show?View.GONE:View.VISIBLE);
        lineArea.setOnClickListener(show ? new setOnClickList() : null);
    }

    private void getBookingsHistory(boolean isLoadMore) {
        if (errorView.getVisibility() == View.VISIBLE) {
            errorView.setVisibility(View.GONE);
        }
        if (container.getVisibility() == View.VISIBLE) {
            container.setVisibility(View.GONE);
        }
        if (loading.getVisibility() != View.VISIBLE) {
            loading.setVisibility(View.VISIBLE);
        }


        final HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "getRideHistory");
        parameters.put("iUserId", generalFunc.getMemberId());
        parameters.put("UserType", Utils.app_type);
        if (isLoadMore) {
            parameters.put("page", "" + 1);
        }

        if (getIntent().hasExtra("iTripId")) {
            parameters.put("iTripId", getIntent().getStringExtra("iTripId"));
        }


        final ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setDataResponseListener(responseString -> {
            JSONObject responseObj=generalFunc.getJsonObject(responseString);

            if (responseObj != null && !responseObj.equals("")) {
                closeLoader();

                if (GeneralFunctions.checkDataAvail(Utils.action_str, responseObj)) {

                    String nextPage = generalFunc.getJsonValueStr("NextPage", responseObj);
                    JSONArray arr_rides = generalFunc.getJsonArray(Utils.message_str, responseObj);

                    if (arr_rides != null && arr_rides.length() > 0) {
                        for (int i = 0; i < arr_rides.length(); i++) {
                            JSONObject obj_temp = generalFunc.getJsonObject(arr_rides, i);

                            HashMap<String, String> map = new HashMap<String, String>();

                            map.put("tTripRequestDateOrig", generalFunc.getJsonValueStr("tTripRequestDateOrig", obj_temp));
                            map.put("CurrencySymbol", generalFunc.getJsonValueStr("CurrencySymbol", obj_temp));
                            map.put("tSaddress", generalFunc.getJsonValueStr("tSaddress", obj_temp));
                            map.put("tDaddress", generalFunc.getJsonValueStr("tDaddress", obj_temp));
                            map.put("vRideNo", generalFunc.getJsonValueStr("vRideNo", obj_temp));

                            map.put("LBL_BOOKING_NO", generalFunc.retrieveLangLBl("", "LBL_BOOKING"));
                            map.put("LBL_Status", generalFunc.retrieveLangLBl("", "LBL_Status"));
                            map.put("is_rating", generalFunc.getJsonValueStr("is_rating", obj_temp));
                            map.put("iTripId", generalFunc.getJsonValueStr("iTripId", obj_temp));
                            if (generalFunc.getJsonValueStr("eType", obj_temp).equalsIgnoreCase("deliver") || generalFunc.getJsonValue("eType", tripData).equals(Utils.eType_Multi_Delivery)) {
                                map.put("eType", generalFunc.retrieveLangLBl("Delivery", "LBL_DELIVERY"));
                                map.put("LBL_PICK_UP_LOCATION", generalFunc.retrieveLangLBl("Sender Location", "LBL_SENDER_LOCATION"));
                                map.put("LBL_DEST_LOCATION", generalFunc.retrieveLangLBl("Receiver's Location", "LBL_RECEIVER_LOCATION"));
                            } else {
                                map.put("LBL_PICK_UP_LOCATION", generalFunc.retrieveLangLBl("", "LBL_PICK_UP_LOCATION"));
                                map.put("eType", generalFunc.getJsonValueStr("eType", obj_temp));
                                map.put("LBL_DEST_LOCATION", generalFunc.retrieveLangLBl("", "LBL_DEST_LOCATION"));
                            }
                            map.put("eFareType", generalFunc.getJsonValueStr("eFareType", obj_temp));


                            map.put("appType", generalFunc.getJsonValue("APP_TYPE", userProfileJson));
                            map.put("LBL_JOB_LOCATION_TXT", generalFunc.retrieveLangLBl("", "LBL_JOB_LOCATION_TXT"));


                            if (generalFunc.getJsonValueStr("eCancelled", obj_temp).equals("Yes")) {
                                map.put("iActive", generalFunc.retrieveLangLBl("", "LBL_CANCELED_TXT"));
                            } else {
                                if (generalFunc.getJsonValueStr("iActive", obj_temp).equals("Canceled")) {
                                    map.put("iActive", generalFunc.retrieveLangLBl("", "LBL_CANCELED_TXT"));
                                } else if (generalFunc.getJsonValueStr("iActive", obj_temp).equals("Finished")) {
                                    map.put("iActive", generalFunc.retrieveLangLBl("", "LBL_FINISHED_TXT"));
                                } else {
                                    map.put("iActive", generalFunc.getJsonValueStr("iActive", obj_temp));
                                }
                            }


                            map.put("LBL_BOOKING_NO", generalFunc.retrieveLangLBl("Delivery No", "LBL_DELIVERY_NO"));
                            map.put("LBL_CANCEL_BOOKING", generalFunc.retrieveLangLBl("Cancel Delivery", "LBL_CANCEL_DELIVERY"));

                            if (generalFunc.retrieveValue(Utils.APP_DESTINATION_MODE).equalsIgnoreCase(Utils.NONE_DESTINATION)) {
                                map.put("DESTINATION", "No");
                            } else {
                                map.put("DESTINATION", "Yes");
                            }


                            map.put("JSON", obj_temp.toString());
                            map.put("APP_TYPE", generalFunc.getJsonValue("APP_TYPE", userProfileJson));

                            if (generalFunc.getJsonValueStr("eType", obj_temp).equals(Utils.CabGeneralType_UberX) &&
                                    !generalFunc.getJsonValueStr("eFareType", obj_temp).equalsIgnoreCase(Utils.CabFaretypeRegular)) {

                                map.put("SelectedVehicle", generalFunc.getJsonValueStr("carTypeName", obj_temp));
                                map.put("SelectedCategory", generalFunc.getJsonValueStr("vVehicleCategory", obj_temp));


                            }
                            map.put("moreServices", generalFunc.getJsonValueStr("moreServices", obj_temp));
                            if (generalFunc.getJsonValueStr("eFareType", obj_temp).equalsIgnoreCase(Utils.CabFaretypeFixed) && generalFunc.getJsonValueStr("moreServices", obj_temp).equalsIgnoreCase("No")) {
                                map.put("SelectedCategory", generalFunc.getJsonValueStr("vCategory", obj_temp));

                            }

                            if (map != null) {
                                tripData = map.get("JSON");
                                setLabels(tripData);
                                setData(tripData);
                            }

                        }
                    }


                } else {
                    //generalFunc.showError();
                    generateErrorView(isLoadMore);
                }
            } else {
//                    generalFunc.showError();
                generateErrorView(isLoadMore);
            }
        });
        exeWebServer.execute();
    }

    public void closeLoader() {
        if (loading.getVisibility() == View.VISIBLE) {
            loading.setVisibility(View.GONE);
        }

        if (container.getVisibility() == View.GONE) {
            container.setVisibility(View.VISIBLE);
        }
    }

    public void generateErrorView(boolean isLoadMore) {

        closeLoader();

        generalFunc.generateErrorView(errorView, "LBL_ERROR_TXT", "LBL_NO_INTERNET_TXT");

        if (errorView.getVisibility() != View.VISIBLE) {
            errorView.setVisibility(View.VISIBLE);
        }

        if (container.getVisibility() == View.VISIBLE) {
            container.setVisibility(View.GONE);
        }

        errorView.setOnRetryListener(() -> getBookingsHistory(isLoadMore));
    }


    String headerLable = "", noVal = "", driverhVal = "";

    public void setLabels(String tripData) {
//        String tripData = getIntent().getStringExtra("TripData");

        /*Multi related new lable*/
        ((TextView) findViewById(R.id.passengerSignTxt)).setText(generalFunc.retrieveLangLBl("View Signature", "LBL_VIEW_MULTI_SENDER_SIGN"));

        helpTxt.setText(generalFunc.retrieveLangLBl("Help?", "LBL_NEED_HELP")); //LBL_NEED_HELP_TXT

        titleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_RECEIPT_HEADER_TXT"));
        subTitleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_GET_RECEIPT_TXT"));
        viewReqServicesTxtView.setText(generalFunc.retrieveLangLBl("", "LBL_VIEW_REQUESTED_SERVICES"));


        if (generalFunc.getJsonValue("eType", tripData).equals(Utils.CabGeneralType_UberX)) {
            headerLable = generalFunc.retrieveLangLBl("", "LBL_THANKS_JOB_TXT");
            noVal = generalFunc.retrieveLangLBl("", "LBL_SERVICES") + "#";
            driverhVal = generalFunc.retrieveLangLBl("", "LBL_SERVICE_PROVIDER_TXT");
        } else if (generalFunc.getJsonValue("eType", tripData).equals("Deliver") || generalFunc.getJsonValue("eType", tripData).equals(Utils.eType_Multi_Delivery)) {
            headerLable = generalFunc.retrieveLangLBl("", "LBL_THANKS_DELIVERY_TXT");
            noVal = generalFunc.retrieveLangLBl("", "LBL_DELIVERY") + "#";
            driverhVal = generalFunc.retrieveLangLBl("", "LBL_CARRIER");
        } else {
            headerLable = generalFunc.retrieveLangLBl("", "LBL_THANKS_RIDING_TXT");
            noVal = generalFunc.retrieveLangLBl("", "LBL_RIDE") + "#";
            driverhVal = generalFunc.retrieveLangLBl("", "LBL_DRIVER");
        }

        ((TextView) findViewById(R.id.headerTxt)).setText(generalFunc.retrieveLangLBl("", headerLable));


        ((TextView) findViewById(R.id.rideNoHTxt)).setText(noVal);
        ((TextView) findViewById(R.id.ratingHTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_RATING"));
        ((TextView) findViewById(R.id.driverHTxt)).setText(driverhVal);
        String dateLable = "";
        String pickupHval = "";

        if (generalFunc.getJsonValue("eType", tripData).equals(Utils.CabGeneralType_UberX)) {
            dateLable = generalFunc.retrieveLangLBl("", "LBL_JOB_REQ_DATE");
            pickupHval = generalFunc.retrieveLangLBl("", "LBL_JOB_LOCATION_TXT");
        } else if (generalFunc.getJsonValue("eType", tripData).equals("Deliver") || generalFunc.getJsonValue("eType", tripData).equals(Utils.eType_Multi_Delivery)) {
            dateLable = generalFunc.retrieveLangLBl("", "LBL_DELIVERY_REQUEST_DATE");
            pickupHval = generalFunc.retrieveLangLBl("", "LBL_SENDER_LOCATION");
        } else {
            dateLable = generalFunc.retrieveLangLBl("", "LBL_TRIP_REQUEST_DATE_TXT");
            pickupHval = generalFunc.retrieveLangLBl("", "LBL_PICKUP_LOCATION_TXT");
        }


        ((TextView) findViewById(R.id.tripdateHTxt)).setText(generalFunc.retrieveLangLBl("", dateLable));


        ((TextView) findViewById(R.id.pickUpHTxt)).setText(pickupHval);
        if (generalFunc.getJsonValue("eType", tripData).equals("Deliver") || generalFunc.getJsonValue("eType", tripData).equals(Utils.eType_Multi_Delivery)) {
            ((TextView) findViewById(R.id.dropOffHTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_DELIVERY_DETAILS_TXT"));
        } else if (generalFunc.getJsonValue("eType", tripData).equals(Utils.CabGeneralType_Ride)) {
            ((TextView) findViewById(R.id.dropOffHTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_DEST_LOCATION"));
        } else {
            ((TextView) findViewById(R.id.dropOffHTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_DEST_LOCATION"));

        }

        ((TextView) findViewById(R.id.chargesHTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_CHARGES_TXT"));
        btn_type2.setText(generalFunc.retrieveLangLBl("Rate", "LBL_RATE_DRIVER_TXT"));
        ratingDriverHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_RATING"));

        if (generalFunc.getJsonValue("eType", tripData).equals(Utils.eType_Multi_Delivery)) {
            ufxratingDriverHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_RATE_HEADING_CARRIER"));
        } else {
            ufxratingDriverHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_RATE_HEADING_DRIVER_TXT"));
        }

        tipHTxt.setText(generalFunc.retrieveLangLBl("Tip Amount", "LBL_TIP_AMOUNT"));
        tipmsgTxt.setText(generalFunc.retrieveLangLBl("Thank you for giving tip for this trip.", "LBL_TIP_INFO_SHOW_RIDER"));


    }

    public void setData(String tripData) {
//        String tripData = getIntent().getStringExtra("TripData");

        if (generalFunc.getJsonValue("vReasonTitle", tripData) != null && !generalFunc.getJsonValue("vReasonTitle", tripData).equalsIgnoreCase("")) {
            vReasonTitleTxt.setVisibility(View.VISIBLE);
            vReasonTitleTxt.setText(generalFunc.getJsonValue("vReasonTitle", tripData));
        }


        if (generalFunc.retrieveValue(Utils.ENABLE_FAVORITE_DRIVER_MODULE_KEY).equalsIgnoreCase("Yes")) {

            /*Fav Driver Feature Start*/

            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            width = displayMetrics.widthPixels;
            width_D = (int) (width * 0.369);
            width_ = (int) (width * 0.397);


            favSwitch = (SwitchButton) findViewById(R.id.favSwitch);
            lineArea = (LinearLayout) findViewById(R.id.lineArea);
            favArea = (LinearLayout) findViewById(R.id.favArea);
            hScrollView = (CustomHorizontalScrollView) findViewById(R.id.hScrollView);
            favDriverTitleTxt = (TextView) findViewById(R.id.favDriverTitleTxt);
            favDriverTitleTxt.setText(generalFunc.retrieveLangLBl("save as Favorite Driver", "LBL_SAVE_AS_FAV_DRIVER"));

            lineArea.setOnTouchListener(new SetOnTouchList());

            favSwitch.setOnCheckedChangeListener((compoundButton, b) -> {

                if (b == true) {
                    favSwitch.setThumbColorRes(R.color.Green);
                } else {
                    favSwitch.setThumbColorRes(android.R.color.holo_red_dark);
                }


            });

            String eFavDriver = generalFunc.getJsonValue("eFavDriver", tripData);
            favSwitch.setChecked(eFavDriver.equalsIgnoreCase("Yes"));

            hScrollView.setOnTouchListener(new SetOnTouchList());
            hScrollView.setOnClickListener(new setOnClickList());

            ufxratingBar.setOnRatingBarChangeListener(new SimpleRatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(SimpleRatingBar simpleRatingBar, float rating, boolean fromUser) {

                    if (favArea.getVisibility() == View.GONE) {
                        showSetAsFavArea(true);

                    }
                }
            });


            hScrollView.setOnTouchListener(new OnSwipeTouchListener(getActContext()) {
                public void onSwipeTop() {

                }

                public void onSwipeRight() {
                    scrollWithanimation();
                }

                public void onSwipeLeft() {
                    scrollWithanimation();
                }

                public void onSwipeBottom() {

                }

            });

            /*Fav Driver Feature End*/

        }



        ((TextView) findViewById(R.id.rideNoVTxt)).setText(generalFunc.convertNumberWithRTL(generalFunc.getJsonValue("vRideNo", tripData)));
        if (generalFunc.getJsonValue("eChargeViewShow", tripData) != null && generalFunc.getJsonValue("eChargeViewShow", tripData).equalsIgnoreCase("No")) {
            ((TextView) findViewById(R.id.chargesHTxt)).setVisibility(View.GONE);
            ((TextView) findViewById(R.id.headerTxt)).setVisibility(View.GONE);
            ((CardView) findViewById(R.id.chargeArea)).setVisibility(View.GONE);
            ((LinearLayout) findViewById(R.id.paymentarea)).setVisibility(View.GONE);
            ((TextView) findViewById(R.id.rideNoVTxt)).setText(generalFunc.convertNumberWithRTL(generalFunc.getJsonValue("vRideNo", tripData)));
            LinearLayout.LayoutParams txtParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            txtParam.setMargins(2, 10, 2, 0);
            ((TextView) findViewById(R.id.rideNoVTxt)).setLayoutParams(txtParam);
            ((TextView) findViewById(R.id.rideNoHTxt)).setLayoutParams(txtParam);

        }


        ((TextView) findViewById(R.id.nameDriverVTxt)).setText(generalFunc.getJsonValue("vName", generalFunc.getJsonValue("DriverDetails", tripData)) + " " +
                generalFunc.getJsonValue("vLastName", generalFunc.getJsonValue("DriverDetails", tripData)));
        ((TextView) findViewById(R.id.tripdateVTxt)).setText(generalFunc.getDateFormatedType(generalFunc.getJsonValue("tTripRequestDateOrig", tripData), Utils.OriginalDateFormate, Utils.getDetailDateFormat(getActContext())));

        ((TextView) findViewById(R.id.pickUpVTxt)).setText(generalFunc.getJsonValue("tSaddress", tripData));

        if (generalFunc.getJsonValue("eType", tripData).equals("Deliver")) {

            ((TextView) findViewById(R.id.dropOffVTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_RECEIVER_NAME") + ": " + generalFunc.getJsonValue("vReceiverName", tripData) + "\n\n" +
                    generalFunc.retrieveLangLBl("", "LBL_RECEIVER_LOCATION") + ": " + generalFunc.getJsonValue("tDaddress", tripData) + "\n\n" +
                    generalFunc.retrieveLangLBl("", "LBL_PACKAGE_TYPE_TXT") + ": " + generalFunc.getJsonValue("PackageType", tripData) + "\n\n" +
                    generalFunc.retrieveLangLBl("", "LBL_PACKAGE_DETAILS") + ": " + generalFunc.getJsonValue("tPackageDetails", tripData)
            );
        } else {
            ((TextView) findViewById(R.id.dropOffVTxt)).setText(generalFunc.getJsonValue("tDaddress", tripData));
        }

        if (generalFunc.getJsonValue("tDaddress", tripData).equals("")) {
            (findViewById(R.id.dropOffVTxt)).setVisibility(View.GONE);
            (findViewById(R.id.dropOffHTxt)).setVisibility(View.GONE);
        }


        if (!generalFunc.getJsonValue("fTipPrice", tripData).equals("0") && !generalFunc.getJsonValue("fTipPrice", tripData).equals("0.0") &&
                !generalFunc.getJsonValue("fTipPrice", tripData).equals("0.00") &&
                !generalFunc.getJsonValue("fTipPrice", tripData).equals("")) {
            tiparea.setVisibility(View.VISIBLE);
            tipPluseImage.setVisibility(View.VISIBLE);

            tipamtTxt.setText(generalFunc.getJsonValue("fTipPrice", tripData));

        } else {
            tiparea.setVisibility(View.GONE);
            tipPluseImage.setVisibility(View.GONE);
        }

        cartypeTxt.setText(generalFunc.getJsonValue("vServiceDetailTitle", tripData));

        String trip_status_str = generalFunc.getJsonValue("iActive", tripData);

        isRatingDone = generalFunc.getJsonValue("is_rating", tripData);

        if (isRatingDone.equalsIgnoreCase("No") && trip_status_str.contains("Finished")) {
            findViewById(R.id.rateDriverArea).setVisibility(View.VISIBLE);
            findViewById(R.id.rateCardDriverArea).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.rateDriverArea).setVisibility(View.GONE);
            findViewById(R.id.rateCardDriverArea).setVisibility(View.GONE);
        }

        if (trip_status_str.contains("Canceled")) {

            String cancelLable = "";
            String cancelableReason = generalFunc.getJsonValue("vCancelReason", tripData);

            if (generalFunc.getJsonValue("eCancelledBy", tripData).equalsIgnoreCase("DRIVER")) {

                if (generalFunc.getJsonValue("eType", tripData).equals("Deliver") || generalFunc.getJsonValue("eType", tripData).equals(Utils.eType_Multi_Delivery)) {
                    cancelLable = generalFunc.retrieveLangLBl("", "LBL_PREFIX_DELIVERY_CANCEL_DRIVER") + " " + cancelableReason;
                } else if (generalFunc.getJsonValue("eType", tripData).equals(Utils.CabGeneralType_UberX)) {
                    cancelLable = generalFunc.retrieveLangLBl("", "LBL_PREFIX_JOB_CANCEL_PROVIDER") + " " + cancelableReason;
                } else {
                    cancelLable = generalFunc.retrieveLangLBl("", "LBL_PREFIX_TRIP_CANCEL_DRIVER") + " " + cancelableReason;
                }

            } else {

                if (generalFunc.getJsonValue("eType", tripData).equals(Utils.CabGeneralType_UberX)) {
                    cancelLable = generalFunc.retrieveLangLBl("", "LBL_CANCELED_JOB");
                } else if (generalFunc.getJsonValue("eType", tripData).equals("Deliver") || generalFunc.getJsonValue("eType", tripData).equals(Utils.eType_Multi_Delivery)) {
                    cancelLable = generalFunc.retrieveLangLBl("", "LBL_CANCELED_DELIVERY_TXT");
                } else {
                    cancelLable = generalFunc.retrieveLangLBl("", "LBL_CANCELED_TRIP_TXT");
                }
            }

            ((TextView) findViewById(R.id.tripStatusTxt)).setText(generalFunc.retrieveLangLBl("", cancelLable));
            (findViewById(R.id.tripDetailArea)).setVisibility(View.VISIBLE);
        } else if (trip_status_str.contains("Finished")) {

            String finishLable = "";
            if (generalFunc.getJsonValue("eType", tripData).equals(Utils.CabGeneralType_UberX)) {
                finishLable = generalFunc.retrieveLangLBl("", "LBL_FINISHED_JOB_TXT");
            } else if (generalFunc.getJsonValue("eType", tripData).equals("Deliver") || generalFunc.getJsonValue("eType", tripData).equals(Utils.eType_Multi_Delivery)) {
                finishLable = generalFunc.retrieveLangLBl("", "LBL_FINISHED_DELIVERY_TXT");
            } else {
                finishLable = generalFunc.retrieveLangLBl("", "LBL_FINISHED_TRIP_TXT");
            }

            ((TextView) findViewById(R.id.tripStatusTxt)).setText(generalFunc.retrieveLangLBl("", finishLable));

            (findViewById(R.id.tripDetailArea)).setVisibility(View.VISIBLE);
            subTitleTxt.setVisibility(View.VISIBLE);
        } else {
            ((TextView) findViewById(R.id.tripStatusTxt)).setText(trip_status_str);

        }

        if (generalFunc.getJsonValue("vTripPaymentMode", tripData).equals("Cash")) {
            ((TextView) findViewById(R.id.paymentTypeTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_CASH_PAYMENT_TXT"));
        } else {
            ((TextView) findViewById(R.id.paymentTypeTxt)).setText(generalFunc.retrieveLangLBl("Card Payment", "LBL_CARD_PAYMENT"));
            ((ImageView) findViewById(R.id.paymentTypeImgeView)).setImageResource(R.mipmap.ic_card_new);
        }

        if (generalFunc.getJsonValue("ePayWallet", tripData).equals("Yes")) {
            ((TextView) findViewById(R.id.paymentTypeTxt)).setText(generalFunc.retrieveLangLBl("Paid By Wallet", "LBL_PAID_VIA_WALLET"));
            ((ImageView) findViewById(R.id.paymentTypeImgeView)).setImageResource(R.mipmap.ic_menu_wallet);
        }


        if (generalFunc.getJsonValue("vTripPaymentMode", tripData).equalsIgnoreCase("Organization")) {
            ((TextView) findViewById(R.id.paymentTypeTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_PAYMENT_BY_TXT") + " " + generalFunc.getJsonValue("OrganizationName", tripData));
            ((ImageView) findViewById(R.id.paymentTypeImgeView)).setImageResource(R.drawable.ic_business_pay);
            ((ImageView) findViewById(R.id.paymentTypeImgeView)).setColorFilter(getResources().getColor(R.color.appThemeColor_1), PorterDuff.Mode.SRC_IN);
        }

        if (generalFunc.getJsonValue("eCancelled", tripData).equals("Yes")) {

            String cancelledLable = "";
            String cancelableReason = generalFunc.getJsonValue("vCancelReason", tripData);

            if (generalFunc.getJsonValue("eCancelledBy", tripData).equalsIgnoreCase("DRIVER")) {

                if (generalFunc.getJsonValue("eType", tripData).equals(Utils.CabGeneralType_UberX)) {
                    cancelledLable = generalFunc.retrieveLangLBl("", "LBL_PREFIX_JOB_CANCEL_PROVIDER") + " " + cancelableReason;
                } else if (generalFunc.getJsonValue("eType", tripData).equals("Deliver") || generalFunc.getJsonValue("eType", tripData).equals(Utils.eType_Multi_Delivery)) {
                    cancelledLable = generalFunc.retrieveLangLBl("", "LBL_PREFIX_DELIVERY_CANCEL_DRIVER") + " " + cancelableReason;
                } else {
                    cancelledLable = generalFunc.retrieveLangLBl("", "LBL_PREFIX_TRIP_CANCEL_DRIVER") + " " + cancelableReason;
                }

            } else {

                if (generalFunc.getJsonValue("eType", tripData).equals(Utils.CabGeneralType_UberX)) {
                    cancelledLable = generalFunc.retrieveLangLBl("", "LBL_CANCELED_JOB");
                } else if (generalFunc.getJsonValue("eType", tripData).equals("Deliver") || generalFunc.getJsonValue("eType", tripData).equals(Utils.eType_Multi_Delivery)) {
                    cancelledLable = generalFunc.retrieveLangLBl("", "LBL_CANCELED_DELIVERY_TXT");
                } else {
                    cancelledLable = generalFunc.retrieveLangLBl("", "LBL_CANCELED_TRIP_TXT");
                }
            }

            ((TextView) findViewById(R.id.tripStatusTxt)).setText(generalFunc.retrieveLangLBl("", cancelledLable));
        }

        ((SimpleRatingBar) findViewById(ratingBar)).setRating(GeneralFunctions.parseFloatValue(0, generalFunc.getJsonValue("TripRating", tripData)));


        final ImageView profilebackImage = (ImageView) findViewById(R.id.profileimageback);
        final ImageView driverImageview = (SelectableRoundedImageView) findViewById(R.id.driverImgView);

        driverImageview.setImageResource(R.mipmap.ic_no_pic_user);

        Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                if (profilebackImage != null) {
                    Utils.setBlurImage(bitmap, profilebackImage);
                }
                driverImageview.setImageBitmap(bitmap);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };

        String driverImageName = generalFunc.getJsonValue("vImage", generalFunc.getJsonValue("DriverDetails", tripData));
        if (driverImageName == null || driverImageName.equals("") || driverImageName.equals("NONE")) {
            (driverImageview).setImageResource(R.mipmap.ic_no_pic_user);
        } else {
            String image_url = CommonUtilities.PROVIDER_PHOTO_PATH + generalFunc.getJsonValue("iDriverId", tripData) + "/"
                    + driverImageName;
            Picasso.with(getActContext())
                    .load(image_url)
                    .placeholder(R.mipmap.ic_no_pic_user)
                    .error(R.mipmap.ic_no_pic_user)
                    .into(target);
        }

        if (generalFunc.getJsonValue("eType", tripData).equalsIgnoreCase("UberX") && generalFunc.getJsonValue("SERVICE_PROVIDER_FLOW", userProfileJson).equalsIgnoreCase("Provider")) {
            viewReqServicesTxtView.setVisibility(View.VISIBLE);
        }

        if (generalFunc.getJsonValue("eType", tripData).equalsIgnoreCase("UberX") || generalFunc.getJsonValue("eFareType", tripData).equalsIgnoreCase("Fixed")) {
            findViewById(R.id.service_area).setVisibility(View.GONE);
            findViewById(R.id.serviceHTxt).setVisibility(View.GONE);
            findViewById(R.id.photoArea).setVisibility(View.VISIBLE);

            ((TextView) findViewById(R.id.beforeImgHTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_BEFORE_SERVICE"));
            ((TextView) findViewById(R.id.afterImgHTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_AFTER_SERVICE"));

            if (!TextUtils.isEmpty(generalFunc.getJsonValue("vBeforeImage", tripData))) {
                findViewById(R.id.beforeServiceArea).setVisibility(View.VISIBLE);
                before_serviceImg_url = generalFunc.getJsonValue("vBeforeImage", tripData);

                String vBeforeImage = Utils.getResizeImgURL(getActContext(), before_serviceImg_url, getResources().getDimensionPixelSize(R.dimen.before_after_img_size), getResources().getDimensionPixelSize(R.dimen.before_after_img_size));

                displayPic(vBeforeImage, (ImageView) findViewById(R.id.iv_before_img), "before");

                findViewById(R.id.iv_before_img).setOnClickListener(v -> (new StartActProcess(getActContext())).openURL(before_serviceImg_url));
            } else {
                findViewById(R.id.beforeServiceArea).setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(generalFunc.getJsonValue("vAfterImage", tripData))) {
                findViewById(R.id.afterServiceArea).setVisibility(View.VISIBLE);
                after_serviceImg_url = generalFunc.getJsonValue("vAfterImage", tripData);

                String vAfterImage = Utils.getResizeImgURL(getActContext(), after_serviceImg_url, getResources().getDimensionPixelSize(R.dimen.before_after_img_size), getResources().getDimensionPixelSize(R.dimen.before_after_img_size));
                displayPic(vAfterImage, (ImageView) findViewById(R.id.iv_after_img), "after");

                findViewById(R.id.iv_after_img).setOnClickListener(v -> (new StartActProcess(getActContext())).openURL(after_serviceImg_url));
            } else {
                findViewById(R.id.afterServiceArea).setVisibility(View.GONE);
            }

            if (TextUtils.isEmpty(generalFunc.getJsonValue("vBeforeImage", tripData)) && TextUtils.isEmpty(generalFunc.getJsonValue("vAfterImage", tripData))) {

                findViewById(R.id.photoArea).setVisibility(View.GONE);

            }
            ((TextView) findViewById(R.id.pickUpVTxt)).setText(generalFunc.getJsonValue("tSaddress", tripData));
//            ((TextView) findViewById(R.id.serviceTypeVTxt)).setText(generalFunc.getJsonValue("vVehicleCategory", tripData) + " - " + generalFunc.getJsonValue("vVehicleType", tripData));
//            ((TextView) findViewById(R.id.serviceTypeHTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_Car_Type"));


        } else {
            findViewById(R.id.tripDetailArea).setVisibility(View.VISIBLE);
            findViewById(R.id.service_area).setVisibility(View.GONE);
            findViewById(R.id.serviceHTxt).setVisibility(View.GONE);
            findViewById(R.id.photoArea).setVisibility(View.GONE);
        }

        /*Show Multi Delivery Details*/
        if (generalFunc.getJsonValue("eType", tripData).equals(Utils.eType_Multi_Delivery)) {
            (findViewById(R.id.tripDetailArea)).setVisibility(View.VISIBLE);
            (findViewById(R.id.dropOffVTxt)).setVisibility(View.GONE);

            getTripDeliveryLocations(tripData);

            if (trip_status_str.contains("Canceled") && recipientDetailList.size() > 0) {
               /* (findViewById(R.id.tripDetailArea)).setVisibility(View.GONE);
                (findViewById(R.id.dropOffHTxt)).setVisibility(View.GONE);
                (findViewById(R.id.dropOffVTxt)).setVisibility(View.GONE);*/
            }
        }

        boolean FareDetailsArrNew = generalFunc.isJSONkeyAvail("HistoryFareDetailsNewArr", tripData);

        JSONArray FareDetailsArrNewObj = null;
        if (FareDetailsArrNew == true) {
            FareDetailsArrNewObj = generalFunc.getJsonArray("HistoryFareDetailsNewArr", tripData);
        }
        if (FareDetailsArrNewObj != null) {
            addFareDetailLayout(FareDetailsArrNewObj);
        }
    }

    /*Start of Multi Delivery Data*/

    public void getTripDeliveryLocations(String tripData) {

        final HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "getTripDeliveryDetails");
        parameters.put("iTripId", generalFunc.getJsonValue("iTripId", tripData));
        parameters.put("iCabBookingId", "");
        parameters.put("userType", Utils.app_type);


        final ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(this, parameters);
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(String responseString) {

                if (responseString != null && !responseString.equals("")) {

                    String msg_str = generalFunc.getJsonValue(Utils.message_str, responseString);

                    if (GeneralFunctions.checkDataAvail(Utils.action_str, responseString)) {

                        String paymentDoneByDetail = generalFunc.getJsonValue("PaymentPerson", responseString);

                        if (Utils.checkText(paymentDoneByDetail)) {
                            if (generalFunc.getJsonValue("ePayWallet", tripData).equals("Yes")) {
                                ((TextView) findViewById(R.id.paymentTypeTxt)).setText(generalFunc.retrieveLangLBl("Paid By Wallet", "LBL_PAY_BY_WALLET_TXT"));
                                ((ImageView) findViewById(R.id.paymentTypeImgeView)).setImageResource(R.mipmap.ic_menu_wallet);
                            }
                            ((TextView) findViewById(R.id.paymentTypeTxt)).append(" " + generalFunc.retrieveLangLBl("Paid By", "LBL_PAID_BY_TXT") + " " + paymentDoneByDetail);
                        }


                        if (Utils.checkText(msg_str)) {


                            JSONObject jobject = generalFunc.getJsonObject("MemberDetails", msg_str);

                            if (jobject != null) {
                                senderImage = generalFunc.getJsonValue("Sender_Signature", jobject.toString());


                            }

                            JSONArray tripLocations = generalFunc.getJsonArray("Deliveries", msg_str);
                            if (tripLocations != null) {


                                String LBL_RECIPIENT = "",LBL_Status="",LBL_CANCELED_TRIP_TXT="",LBL_FINISHED_TXT="",LBL_DROP_OFF_LOCATION_TXT="",LBL_PICK_UP_INS="",LBL_DELIVERY_INS="",LBL_PACKAGE_DETAILS="",LBL_CALL_TXT="",LBL_VIEW_SIGN_TXT="",LBL_MESSAGE_ACTIVE_TRIP="",LBL_RESPONSIBLE_FOR_PAYMENT_TXT="",LBL_DELIVERY_STATUS_TXT="";

                                if (tripLocations.length() > 0) {
                                    LBL_RECIPIENT = generalFunc.retrieveLangLBl("", "LBL_RECIPIENT");
                                    LBL_Status = generalFunc.retrieveLangLBl("", "LBL_Status");
                                    LBL_CANCELED_TRIP_TXT = generalFunc.retrieveLangLBl("", "LBL_CANCELED_TRIP_TXT");
                                    LBL_FINISHED_TXT = generalFunc.retrieveLangLBl("", "LBL_FINISHED_TXT");
                                    LBL_DROP_OFF_LOCATION_TXT = generalFunc.retrieveLangLBl("", "LBL_DROP_OFF_LOCATION_TXT");
                                    LBL_PICK_UP_INS = generalFunc.retrieveLangLBl("", "LBL_PICK_UP_INS");
                                    LBL_DELIVERY_INS = generalFunc.retrieveLangLBl("", "LBL_DELIVERY_INS");
                                    LBL_PACKAGE_DETAILS = generalFunc.retrieveLangLBl("", "LBL_PACKAGE_DETAILS");
                                    LBL_CALL_TXT = generalFunc.retrieveLangLBl("", "LBL_CALL_TXT");
                                    LBL_VIEW_SIGN_TXT = generalFunc.retrieveLangLBl("", "LBL_VIEW_SIGN_TXT");
                                    LBL_MESSAGE_ACTIVE_TRIP = generalFunc.retrieveLangLBl("", LBL_MESSAGE_ACTIVE_TRIP);
                                    LBL_RESPONSIBLE_FOR_PAYMENT_TXT=generalFunc.retrieveLangLBl("Responsible for payment","LBL_RESPONSIBLE_FOR_PAYMENT_TXT");
                                    LBL_DELIVERY_STATUS_TXT = generalFunc.retrieveLangLBl("", LBL_DELIVERY_STATUS_TXT);
                                }



                                for (int i = 0; i < tripLocations.length(); i++) {
                                    Trip_Status recipientDetailMap1 = new Trip_Status();
                                    JSONArray jsonArray1 = generalFunc.getJsonArray(tripLocations, i);

                                    ArrayList<Delivery_Data> subrecipientDetailList = new ArrayList<>();

                                    for (int j = 0; j < jsonArray1.length(); j++) {
                                        JSONObject jobject1 = generalFunc.getJsonObject(jsonArray1, j);
                                        Delivery_Data recipientDetailMap = new Delivery_Data();

                                        String vValue=generalFunc.getJsonValueStr("vValue", jobject1);
                                        String vFieldName = generalFunc.getJsonValueStr("vFieldName", jobject1);

                                        recipientDetailMap.setvValue(vValue);

                                        /*if (generalFunc.getJsonValueStr()("vFieldName", jobject1).equalsIgnoreCase("Recepient Name")) {*/
                                        if (vFieldName.equalsIgnoreCase("Recepient Name") || (generalFunc.getJsonValueStr("iDeliveryFieldId", jobject1).equalsIgnoreCase("2"))) {
                                            recipientDetailMap1.setRecepientName(vValue);
                                        } /*else if (generalFunc.getJsonValueStr()("vFieldName", jobject1).equalsIgnoreCase("Mobile Number")) {*/ else if (vFieldName.equalsIgnoreCase("Mobile Number") || (generalFunc.getJsonValueStr("iDeliveryFieldId", jobject1).equalsIgnoreCase("3"))) {
                                            recipientDetailMap1.setRecepientNum(vValue);
                                            recipientDetailMap1.setRecepientMaskNum(generalFunc.getJsonValueStr("vMaskValue", jobject1));
                                        } else if (vFieldName.equalsIgnoreCase("Address")) {
                                            recipientDetailMap1.setePaymentByReceiver(generalFunc.getJsonValueStr("ePaymentByReceiver", jobject1));
                                            recipientDetailMap1.setRecepientAddress(Html.fromHtml(generalFunc.getJsonValue("tDaddress", jobject1.toString())).toString());

                                            recipientDetailMap1.setReceipent_Signature(generalFunc.getJsonValueStr("Receipent_Signature", jobject1));

                                            recipientDetailMap1.setiTripDeliveryLocationId(generalFunc.getJsonValueStr("iTripDeliveryLocationId", jobject1));

                                            recipientDetailMap1.setiActive(generalFunc.getJsonValueStr("iActive", jobject1));

                                            recipientDetailMap.setvValue(generalFunc.getJsonValueStr("tDaddress", jobject1));
                                            recipientDetailMap.setiTripDeliveryLocationId(Html.fromHtml(generalFunc.getJsonValue("iTripDeliveryLocationId", jobject1.toString())).toString());


                                        }

                                        recipientDetailMap.setvFieldName(vFieldName);

                                        recipientDetailMap.setiDeliveryFieldId(generalFunc.getJsonValueStr("iDeliveryFieldId", jobject1));

                                        recipientDetailMap.settSaddress(generalFunc.getJsonValueStr("tSaddress", jobject1));

                                        recipientDetailMap.settStartLat(GeneralFunctions.parseDoubleValue(0.0, generalFunc.getJsonValueStr("tStartLat", jobject1)));

                                        recipientDetailMap.settStartLong(GeneralFunctions.parseDoubleValue(0.0, generalFunc.getJsonValueStr("tStartLong", jobject1)));


                                        recipientDetailMap.settDaddress(generalFunc.getJsonValueStr("tDaddress", jobject1));


                                        recipientDetailMap.settDestLat(GeneralFunctions.parseDoubleValue(0.0, generalFunc.getJsonValueStr("tEndLat", jobject1)));

                                        recipientDetailMap.settDestLong(GeneralFunctions.parseDoubleValue(0.0, generalFunc.getJsonValueStr("tEndLong", jobject1)));

                                        recipientDetailMap.setePaymentByReceiver(generalFunc.getJsonValueStr("ePaymentByReceiver", jobject1));

                                       /* if (!vFieldName.equalsIgnoreCase("Mobile Number") && !vFieldName.equalsIgnoreCase("Recepient Name") && Utils.checkText(vValue)) {
                                            subrecipientDetailList.add(recipientDetailMap);
                                        }*/

                                        if ((!vFieldName.equalsIgnoreCase("Mobile Number") && !(generalFunc.getJsonValueStr("iDeliveryFieldId", jobject1).equalsIgnoreCase("3"))) && (!vFieldName.equalsIgnoreCase("Recepient Name") && !(generalFunc.getJsonValueStr("iDeliveryFieldId", jobject1).equalsIgnoreCase("2")))/*&& Utils.checkText(vValue)*/) {
                                            subrecipientDetailList.add(recipientDetailMap);
                                        }
                                    }

                                    String status = getIntent().hasExtra("Status") ? getIntent().getStringExtra("Status") : "";

                                    if (status.equalsIgnoreCase("activeTrip")) {
                                        recipientDetailMap1.setShowUpcomingLocArea("Yes");
                                    } else {
                                        recipientDetailMap1.setShowUpcomingLocArea("No");
                                    }
                                    if (status.equalsIgnoreCase("cabRequestScreen")) {
                                        recipientDetailMap1.setShowMobile("No");
                                    } else {
                                        recipientDetailMap1.setShowMobile("Yes");
                                    }

                                    recipientDetailMap1.setLBL_RECIPIENT(LBL_RECIPIENT);
                                    recipientDetailMap1.setLBL_Status(LBL_Status);
                                    recipientDetailMap1.setLBL_CANCELED_TRIP_TXT(LBL_CANCELED_TRIP_TXT);
                                    recipientDetailMap1.setLBL_FINISHED_TRIP_TXT(LBL_FINISHED_TXT);
                                    recipientDetailMap1.setLBL_DROP_OFF_LOCATION_TXT(LBL_DROP_OFF_LOCATION_TXT);
                                    recipientDetailMap1.setLBL_PACKAGE_DETAILS(LBL_PICK_UP_INS);
                                    recipientDetailMap1.setLBL_DELIVERY_INS(LBL_DELIVERY_INS);
                                    recipientDetailMap1.setLBL_PACKAGE_DETAILS(LBL_PACKAGE_DETAILS);
                                    recipientDetailMap1.setLBL_CALL_TXT(LBL_CALL_TXT);
                                    recipientDetailMap1.setLBL_VIEW_SIGN_TXT(LBL_VIEW_SIGN_TXT);
                                    recipientDetailMap1.setLBL_MESSAGE_ACTIVE_TRIP(LBL_MESSAGE_ACTIVE_TRIP);
                                    recipientDetailMap1.setLBL_RESPONSIBLE_FOR_PAYMENT_TXT(LBL_RESPONSIBLE_FOR_PAYMENT_TXT);
                                    recipientDetailMap1.setLBL_DELIVERY_STATUS_TXT(LBL_DELIVERY_STATUS_TXT);

                                    recipientDetailMap1.setListOfDeliveryItems(subrecipientDetailList);
                                    recipientDetailList.add(recipientDetailMap1);

                                    setRecyclerView();
                                }
                            }
                        } else {
                            generalFunc.showGeneralMessage(generalFunc.retrieveLangLBl("Error", "LBL_ERROR_TXT"),
                                    generalFunc.retrieveLangLBl("", msg_str));

                        }
                    }
                }
            }
        });
        exeWebServer.execute();

    }

    private void setRecyclerView() {
        {
            if (((LinearLayout) findViewById(R.id.deliveryArea)).getChildCount() > 0) {
                ((LinearLayout) findViewById(R.id.deliveryArea)).removeAllViewsInLayout();
            }

            if (Utils.checkText(senderImage)) {
                signatureArea.setVisibility(View.VISIBLE);
                signatureArea.setOnClickListener(new setOnClickList());
            }


            for (int i = 0; i < recipientDetailList.size(); i++) {

                LayoutInflater infalInflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.multi_history_recipient_list_design, null);

                TextView recipientNoTxt, recipeientMobileTxt, recipeientNameTxt, dropOffVTxt;
                TextView dropOffHTxt, ricipientSignTxt, tripStatusLblTxt, tripStatusTxt;
                RecyclerView deliveryDetailsList;
                LinearLayout deliveryDetailsArea;
                View line;

                recipientNoTxt = (TextView) convertView.findViewById(R.id.recipientNoTxt);
                recipeientNameTxt = (TextView) convertView.findViewById(R.id.recipeientNameTxt);
                recipeientMobileTxt = (TextView) convertView.findViewById(R.id.recipeientMobileTxt);
                dropOffVTxt = (TextView) convertView.findViewById(R.id.dropOffVTxt);
                dropOffHTxt = (TextView) convertView.findViewById(R.id.dropOffHTxt);
                ricipientSignTxt = (TextView) convertView.findViewById(R.id.ricipientSignTxt);
                deliveryDetailsList = (RecyclerView) convertView.findViewById(R.id.deliveryDetailsList);
                tripStatusLblTxt = (TextView) convertView.findViewById(R.id.tripStatusLblTxt);
                tripStatusTxt = (TextView) convertView.findViewById(R.id.tripStatusTxt);
                deliveryDetailsArea = (LinearLayout) convertView.findViewById(R.id.deliveryDetailsArea);
                line = (View) convertView.findViewById(R.id.line);

                final Trip_Status item = recipientDetailList.get(i);


                if (i == recipientDetailList.size() - 1) {
                    line.setVisibility(View.GONE);
                } else {
                    line.setVisibility(View.VISIBLE);
                }

                recipientNoTxt.setText("" + item.getLBL_RECIPIENT() + " " + (i + 1));
                recipeientNameTxt.setText(item.getRecepientName());
                recipeientMobileTxt.setText(item.getRecepientMaskNum());

                tripStatusLblTxt.setText(item.getLBL_DELIVERY_STATUS_TXT());
                tripStatusTxt.setText(item.getiActive());
                ricipientSignTxt.setText(item.getLBL_VIEW_SIGN_TXT());
                ricipientSignTxt.setTag(i);
                if (Utils.checkText(item.getReceipent_Signature())) {
                    ricipientSignTxt.setVisibility(View.VISIBLE);
                } else {
                    ricipientSignTxt.setVisibility(View.GONE);
                }

                ricipientSignTxt.setOnClickListener(view -> showSignatureImage(generalFunc.retrieveLangLBl("", "LBL_RECIPIENT_NAME_HEADER_TXT") + " : " + recipientDetailList.get((int) view.getTag()).getRecepientName(), item.getReceipent_Signature(), false));

                dropOffHTxt.setText(item.getLBL_DROP_OFF_LOCATION_TXT());
                dropOffVTxt.setText(item.getRecepientAddress());

                if (deliveryDetailsArea.getChildCount() <= 0) {

                    ArrayList<Delivery_Data> listOfData = recipientDetailList.get(i).getListOfDeliveryItems();
                    for (int i1 = 0; i1 < listOfData.size(); i1++) {
                        if (listOfData.get(i1).getvFieldName().equalsIgnoreCase("Address")) {
                            dropOffHTxt.setText(item.getLBL_DROP_OFF_LOCATION_TXT());
                            dropOffVTxt.setText(listOfData.get(i1).gettDaddress());

                        } else {
                            setdeliveriesDetails(listOfData.get(i1).getvFieldName(), listOfData.get(i1).getvValue(), i, i1, listOfData.size(), deliveryDetailsArea);
                        }
                    }
                }


                if (convertView != null)
                    ((LinearLayout) findViewById(R.id.deliveryArea)).addView(convertView);

            }


            ((LinearLayout) findViewById(R.id.deliveryArea)).setVisibility(View.VISIBLE);

            findViewById(R.id.deliveryItemListRecycleview).setVisibility(View.GONE);

        }
    }

    private void setdeliveriesDetails(String vFieldName, String vValue, int checkItemPos, int listSize, int noRecipient, LinearLayout deliveryDetailsArea) {
        LayoutInflater infalInflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = infalInflater.inflate(R.layout.multi_delivery_details_design, null);

        TextView itemTitleTxt, itemValueTxt;
        LinearLayout itemDetailArea;
        View bottomLine;

        itemTitleTxt = (TextView) v.findViewById(R.id.itemTitleTxt);
        itemDetailArea = (LinearLayout) v.findViewById(R.id.itemDetailArea);
        itemValueTxt = (TextView) v.findViewById(R.id.itemValueTxt);
        bottomLine = (View) v.findViewById(R.id.bottomLine);

        itemDetailArea.setTag(noRecipient); // This line is important.
        itemTitleTxt.setText(vFieldName);
        itemValueTxt.setText(Utils.checkText(vValue) ? vValue : "--");

        if (noRecipient == listSize - 1) {
            bottomLine.setVisibility(View.GONE);
        } else {
            bottomLine.setVisibility(View.GONE);
        }

        if (v != null)
            deliveryDetailsArea.addView(v);
    }

    public void showSignatureImage(String Name, String image_url, boolean isSender) {
        signatureImageDialog = new Dialog(getActContext(), R.style.Theme_Dialog);
        signatureImageDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        signatureImageDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        signatureImageDialog.setContentView(R.layout.multi_show_sign_design);

        final ProgressBar LoadingProgressBar = ((ProgressBar) signatureImageDialog.findViewById(R.id.LoadingProgressBar));

        ((TextView) signatureImageDialog.findViewById(R.id.nameTxt)).setText(" " + Name);

        if (isSender) {
            ((TextView) signatureImageDialog.findViewById(R.id.passengerDTxt)).setText(generalFunc.retrieveLangLBl("Sender Signature", "LBL_SENDER_SIGN"));
            ((TextView) signatureImageDialog.findViewById(R.id.nameTxt)).setVisibility(View.GONE);

        } else {
            ((TextView) signatureImageDialog.findViewById(R.id.passengerDTxt)).setText(generalFunc.retrieveLangLBl("Receiver Signature", "LBL_RECEIVER_SIGN"));
            ((TextView) signatureImageDialog.findViewById(R.id.nameTxt)).setVisibility(View.VISIBLE);

        }

        if (Utils.checkText(image_url)) {

            Picasso.with(getActContext())
                    .load(image_url)
                    .into(((ImageView) signatureImageDialog.findViewById(R.id.passengerImgView)), new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            LoadingProgressBar.setVisibility(View.GONE);
                            ((ImageView) signatureImageDialog.findViewById(R.id.passengerImgView)).setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError() {
                            LoadingProgressBar.setVisibility(View.VISIBLE);
                            ((ImageView) signatureImageDialog.findViewById(R.id.passengerImgView)).setVisibility(View.GONE);

                        }
                    });
        } else {
            LoadingProgressBar.setVisibility(View.VISIBLE);
            ((ImageView) signatureImageDialog.findViewById(R.id.passengerImgView)).setVisibility(View.GONE);

        }
        (signatureImageDialog.findViewById(R.id.closeImg)).setOnClickListener(view -> {

            if (signatureImageDialog != null) {
                signatureImageDialog.dismiss();
            }
        });

        signatureImageDialog.setCancelable(false);
        signatureImageDialog.setCanceledOnTouchOutside(false);

        if (generalFunc.isRTLmode()) {
            generalFunc.forceRTLIfSupported(signatureImageDialog);
        }
        signatureImageDialog.show();

    }

    /*End of Multi Delivery Data*/

    public void displayPic(String image_url, ImageView view, final String imgType) {

        Picasso.with(getActContext())
                .load(image_url)
                .placeholder(R.mipmap.ic_no_icon)
                .into(view, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        if (imgType.equalsIgnoreCase("before")) {
                            findViewById(R.id.before_loading).setVisibility(View.GONE);
                            findViewById(R.id.iv_before_img).setVisibility(View.VISIBLE);
                        } else if (imgType.equalsIgnoreCase("after")) {
                            findViewById(R.id.after_loading).setVisibility(View.GONE);
                            findViewById(R.id.iv_after_img).setVisibility(View.VISIBLE);
                        }

                    }

                    @Override
                    public void onError() {
                        if (imgType.equalsIgnoreCase("before")) {
                            findViewById(R.id.before_loading).setVisibility(View.VISIBLE);
                            findViewById(R.id.iv_before_img).setVisibility(View.GONE);
                        } else if (imgType.equalsIgnoreCase("after")) {
                            findViewById(R.id.after_loading).setVisibility(View.VISIBLE);
                            findViewById(R.id.iv_after_img).setVisibility(View.GONE);

                        }
                    }
                });

    }

    private void addFareDetailLayout(JSONArray jobjArray) {

        if (fareDetailDisplayArea.getChildCount() > 0) {
            fareDetailDisplayArea.removeAllViewsInLayout();
        }

        for (int i = 0; i < jobjArray.length(); i++) {
            JSONObject jobject = generalFunc.getJsonObject(jobjArray, i);
            try {
                addFareDetailRow(jobject.names().getString(0), jobject.get(jobject.names().getString(0)).toString(), (jobjArray.length() - 1) == i ? true : false);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private void addFareDetailRow(String row_name, String row_value, boolean isLast) {
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

            titleHTxt.setText(generalFunc.convertNumberWithRTL(row_name));
            titleVTxt.setText(generalFunc.convertNumberWithRTL(row_value));

            titleHTxt.setTextColor(Color.parseColor("#303030"));
            titleVTxt.setTextColor(Color.parseColor("#111111"));
        }

        fareDetailDisplayArea.addView(convertView);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.gMap = googleMap;

//        String tripData = getIntent().getStringExtra("TripData");

        String tStartLat = generalFunc.getJsonValue("tStartLat", tripData);
        String tStartLong = generalFunc.getJsonValue("tStartLong", tripData);
        String tEndLat = generalFunc.getJsonValue("tEndLat", tripData);
        String tEndLong = generalFunc.getJsonValue("tEndLong", tripData);

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        Marker pickUpMarker = null;
        Marker destMarker = null;
        if (!tStartLat.equals("") && !tStartLat.equals("0.0") && !tStartLong.equals("") && !tStartLong.equals("0.0")) {
            LatLng pickUpLoc = new LatLng(GeneralFunctions.parseDoubleValue(0.0, tStartLat), GeneralFunctions.parseDoubleValue(0.0, tStartLong));
            MarkerOptions marker_opt = new MarkerOptions().position(pickUpLoc);
            marker_opt.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_source_marker)).anchor(0.5f, 0.5f);
            pickUpMarker = this.gMap.addMarker(marker_opt);

            builder.include(pickUpLoc);

            gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pickUpLoc, 10));
        }

        if (generalFunc.getJsonValue("iActive", tripData).equals("Finished")) {
            if (!tEndLat.equals("") && !tEndLat.equals("0.0") && !tEndLong.equals("") && !tEndLong.equals("0.0")) {
                LatLng destLoc = new LatLng(GeneralFunctions.parseDoubleValue(0.0, tEndLat), GeneralFunctions.parseDoubleValue(0.0, tEndLong));
                MarkerOptions marker_opt = new MarkerOptions().position(destLoc);
                marker_opt.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_dest_marker)).anchor(0.5f, 0.5f);
                destMarker = this.gMap.addMarker(marker_opt);

                builder.include(destLoc);

                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destLoc, 10));
            }
        }


        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;

        gMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), width, Utils.dipToPixels(getActContext(), 200), 100));
        gMap.setOnMarkerClickListener(marker -> {

            marker.hideInfoWindow();

            return true;
        });

        if (pickUpMarker != null && destMarker != null) {
            drawRoute(pickUpMarker.getPosition(), destMarker.getPosition());
        }

    }

    public void drawRoute(LatLng pickUpLoc, LatLng destinationLoc) {
        String originLoc = pickUpLoc.latitude + "," + pickUpLoc.longitude;
        String destLoc = destinationLoc.latitude + "," + destinationLoc.longitude;
        String serverKey = generalFunc.retrieveValue(Utils.GOOGLE_SERVER_ANDROID_PASSENGER_APP_KEY);
        String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" + originLoc + "&destination=" + destLoc + "&sensor=true&key=" + serverKey + "&language=" + generalFunc.retrieveValue(Utils.GOOGLE_MAP_LANGUAGE_CODE_KEY) + "&sensor=true";

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), url, true);

        exeWebServer.setDataResponseListener(responseString -> {

            if (responseString != null && !responseString.equals("")) {

                String status = generalFunc.getJsonValue("status", responseString);

                if (status.equals("OK")) {

                    JSONArray obj_routes = generalFunc.getJsonArray("routes", responseString);
                    if (obj_routes != null && obj_routes.length() > 0) {

                        PolylineOptions lineOptions = generalFunc.getGoogleRouteOptions(responseString, Utils.dipToPixels(getActContext(), 5), getActContext().getResources().getColor(R.color.black));

                        if (lineOptions != null) {
                            gMap.addPolyline(lineOptions);
                        }
                    }

                }

            }
        });
        exeWebServer.execute();
    }

    public void sendReceipt() {

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "getReceipt");
        parameters.put("UserType", Utils.app_type);
        parameters.put("iTripId", generalFunc.getJsonValue("iTripId", tripData));

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(responseString -> {

            if (responseString != null && !responseString.equals("")) {

                boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

                if (isDataAvail) {
                    generalFunc.showGeneralMessage("",
                            generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)));
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

    public Context getActContext() {
        return HistoryDetailActivity.this;
    }

    public void submitRating() {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "submitRating");
        parameters.put("iMemberId", generalFunc.getMemberId());
        parameters.put("iGeneralUserId", generalFunc.getMemberId());
        parameters.put("tripID", generalFunc.getJsonValue("iTripId", tripData));
        parameters.put("rating", "" + ufxratingBar.getRating());
        parameters.put("message", Utils.getText(commentBox));
        parameters.put("UserType", Utils.app_type);
        if (favSwitch!=null) {
            parameters.put("eFavDriver", favSwitch.isChecked() ? "Yes" : "No");
        }

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(responseString -> {

            if (responseString != null && !responseString.equals("")) {

                boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

                if (isDataAvail) {

                    final GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
                    generateAlert.setCancelable(true);
                    generateAlert.setBtnClickList(btn_id -> {
                        generateAlert.closeAlertBox();

                        Intent returnIntent = new Intent();
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();
                    });
                    generateAlert.setContentMessage("", generalFunc.retrieveLangLBl("", "LBL_SUCCESS_RATING_SUBMIT_TXT"));
                    generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"));
                    generateAlert.showAlertBox();
                    generateAlert.setCancelable(false);


                } else {
                    resetRatingData();
                    generalFunc.showGeneralMessage("",
                            generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)));
                }
            } else {
                generalFunc.showError();
            }
        });
        exeWebServer.execute();
    }

    private void resetRatingData() {
        commentBox.setText("");
        ((RatingBar) findViewById(ratingBar)).setRating(0);
    }

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Utils.hideKeyboard(getActContext());
            Bundle bn = new Bundle();

            switch (view.getId()) {
                case R.id.backImgView:
                    HistoryDetailActivity.super.onBackPressed();
                    break;

                case R.id.subTitleTxt:
                    sendReceipt();
                    break;

                case R.id.beforeServiceArea:
                    new StartActProcess(getActContext()).openURL(before_serviceImg_url);
                    break;

                case R.id.afterServiceArea:
                    new StartActProcess(getActContext()).openURL(after_serviceImg_url);
                    break;
                case R.id.viewReqServicesTxtView:
                    bn.putString("iTripId", generalFunc.getJsonValue("iTripId", tripData));
                    new StartActProcess(getActContext()).startActWithData(MoreServiceInfoActivity.class, bn);
                    break;
                case R.id.helpTxt:
                    bn.putString("iTripId", generalFunc.getJsonValue("iTripId", tripData));
                    new StartActProcess(getActContext()).startActWithData(Help_MainCategory.class, bn);
                    break;

                case R.id.signatureArea:
                    //new StartActProcess(getActContext()).startActWithData(UberXSelectServiceActivity.class, bundle);
                    showSignatureImage(generalFunc.getJsonValue("vName", tripData) + " " +
                            generalFunc.getJsonValue("vLastName", tripData), senderImage, true);
                    break;
                case R.id.lineArea:
                    showSetAsFavArea(false);
                    break;
                case R.id.hScrollView:
                    if (!ufxratingBar.isFocusable()) {
                        lineArea.performClick();
                    }
                    break;
            }

            if (view.getId() == rateBtnId) {
                if (((SimpleRatingBar) findViewById(R.id.ufxratingBar)).getRating() < 1) {
                    generalFunc.showMessage(generalFunc.getCurrentView(HistoryDetailActivity.this), generalFunc.retrieveLangLBl("", "LBL_ERROR_RATING_DIALOG_TXT"));
                    return;
                }
                submitRating();
            }
        }
    }
}
