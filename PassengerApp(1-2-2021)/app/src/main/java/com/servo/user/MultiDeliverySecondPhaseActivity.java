package com.servo.user;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.adapter.files.MultiDestinationItemAdapter;
import com.fragments.MultiScrollSupportMapFragment;
import com.general.files.DataParser;
import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.general.files.MapComparator;
import com.general.files.MyApp;
import com.general.files.StartActProcess;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.maps.android.SphericalUtil;
import com.model.Delivery_Data;
import com.model.Multi_Delivery_Data;
import com.model.Multi_Dest_Info_Detail_Data;
import com.utils.Logger;
import com.utils.Utils;
import com.view.MButton;
import android.widget.TextView;
import com.view.MaterialRippleLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Created by Esite on 03-04-2018.
 */

public class MultiDeliverySecondPhaseActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnCameraMoveCanceledListener, GoogleMap.OnCameraMoveStartedListener, GoogleMap.OnCameraIdleListener, MultiDestinationItemAdapter.OnItemClickListener, GoogleMap.OnCameraMoveListener {

    private final static double DEFAULT_CURVE_ROUTE_CURVATURE = 0.5f;
    private final static int DEFAULT_CURVE_POINTS = 60;
    public int height = 0;
    TextView titleTxt, addDestinationTitleTxt;
    ImageView backImgView;
    MultiScrollSupportMapFragment map;
    ImageView iv_arrow;
    LinearLayout headerArea;
    FrameLayout bottomArea;
    // Area which going to be hide on map touch
    LinearLayout contentArea;
    LinearLayout innerLayout;
    LinearLayout subLayout;
    RecyclerView destRecyclerView;
    MultiDestinationItemAdapter destAdapter;
    NestedScrollView sv_main;
    boolean arrowUp = true;
    ArrayList<Marker> markerArrayList = new ArrayList<>();  // list of markers on map
    HashMap<String, String> mapData = new HashMap<>();
    ArrayList<Multi_Delivery_Data> listofViews = new ArrayList<>();      // list of main views
    ArrayList<Multi_Delivery_Data> templistofViews = new ArrayList<>();  // temp list of views
    ArrayList<Multi_Delivery_Data> wayPointslist = new ArrayList<>();   // List of Way Points/ middle points
    ArrayList<Multi_Delivery_Data> destPointlist = new ArrayList<>();   // destination Points
    ArrayList<Multi_Delivery_Data> finalPointlist = new ArrayList<>();  // final Points list with time & distance & based on shortest location first algorithm
    ArrayList<Multi_Dest_Info_Detail_Data> dest_details_Array = new ArrayList<Multi_Dest_Info_Detail_Data>();  // store all locations time & distance
    MButton nextbtn;
    String LBL_MULTI_FR_TXT, LBL_MULTI_TO_TXT, LBL_MULTI_ADD_NEW_DESTINATION;
    String serverKey;
    int DRIVER_ARRIVED_MIN_TIME_PER_MINUTE;
    LatLngBounds.Builder builder = new LatLngBounds.Builder();

    int maxDestAddAllowedCount = 0;
    boolean isManualCalculation = false;
    ArrayList<Multi_Delivery_Data> destinationsArraylist = new ArrayList<>(); // list contains distance with manual filtered distance data
    ArrayList<Multi_Delivery_Data> filteredDestinationsArraylist = new ArrayList<>(); // temp list used for manual distance calculation
    int count = 0;
    private Animation animShow, animHide;
    private GoogleMap gMap;
    private GeneralFunctions generalFunc;
    private LinearLayout addDestinationArea;
    private Polyline route_polyLine;
    private String time = "";
    private String distance = "";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_second_phase);
        generalFunc = MyApp.getInstance().getGeneralFun(getActContext());
        mapData = (HashMap<String, String>) getIntent().getSerializableExtra("selectedData");
        setSelected("2");
        initView();
        nextbtn.setClickable(false);
        nextbtn.setFocusable(false);
        setlable();
    }

    private void setlable() {
        nextbtn.setText(generalFunc.retrieveLangLBl("Next", "LBL_BTN_NEXT_TXT"));
        titleTxt.setText(generalFunc.retrieveLangLBl("New Booking", "LBL_MULTI_NEW_BOOKING_TXT"));
        addDestinationTitleTxt.setText(generalFunc.retrieveLangLBl("Add Destination", "LBL_MULTI_ADD_DELIVERY"));
        LBL_MULTI_FR_TXT = generalFunc.retrieveLangLBl("FR", "LBL_MULTI_FR_TXT");
        LBL_MULTI_TO_TXT = generalFunc.retrieveLangLBl("TO", "LBL_MULTI_TO_TXT");
        LBL_MULTI_ADD_NEW_DESTINATION = generalFunc.retrieveLangLBl("", "LBL_MULTI_ADD_NEW_DESTINATION");
        DRIVER_ARRIVED_MIN_TIME_PER_MINUTE = generalFunc.parseIntegerValue(3, generalFunc.getJsonValue("DRIVER_ARRIVED_MIN_TIME_PER_MINUTE", generalFunc.retrieveValue(Utils.USER_PROFILE_JSON)));
        serverKey = generalFunc.retrieveValue(Utils.GOOGLE_SERVER_ANDROID_PASSENGER_APP_KEY);
        maxDestAddAllowedCount = generalFunc.parseIntegerValue(0, generalFunc.retrieveValue(Utils.MAX_ALLOW_NUM_DESTINATION_MULTI_KEY));
    }

    public void addEmptyDestination(boolean addEmptyDest) {
        if (addEmptyDest) {
            Multi_Delivery_Data mt = new Multi_Delivery_Data();
            mt.setHintLable(LBL_MULTI_ADD_NEW_DESTINATION);
            mt.setFRLable(LBL_MULTI_FR_TXT);
            mt.setTOLable(LBL_MULTI_TO_TXT);
            listofViews.add(mt);
        }
        showAddDestinationArea();
        setScrollviewHeight();
    }

    private void setDestinationView(boolean addEmptyDest) {
        addEmptyDestination(addEmptyDest);

        destAdapter = new MultiDestinationItemAdapter(getActContext(), listofViews, generalFunc, false);
        destRecyclerView.setAdapter(destAdapter);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        destRecyclerView.setItemAnimator(new DefaultItemAnimator());

        destRecyclerView.setLayoutManager(llm);
        destRecyclerView.setNestedScrollingEnabled(false);
        destAdapter.setOnItemClickListener(this);
        destAdapter.notifyDataSetChanged();

        destRecyclerView.smoothScrollToPosition(destRecyclerView.getAdapter().getItemCount());

        try {
            sv_main.post(new Runnable() {
                @Override
                public void run() {
                    sv_main.fullScroll(NestedScrollView.FOCUS_DOWN);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void setScrollviewHeight() {
        int margin = (Utils.dpToPx(getActContext(), 0));
        int recyclerViewitemHeight = (Utils.dpToPx(getActContext(), 50));

        Display mDisplay = this.getWindowManager().getDefaultDisplay();
        final int width = mDisplay.getWidth();
        final int height = mDisplay.getHeight();

        // set innerLayout to Default

        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) innerLayout.getLayoutParams();
        layoutParams2.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams2.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        innerLayout.setLayoutParams(layoutParams2);
        innerLayout.requestLayout();

        // set Recyclerview height according child
        ViewGroup.LayoutParams paramsRV = destRecyclerView.getLayoutParams();
        paramsRV.width = ViewGroup.LayoutParams.MATCH_PARENT;
        paramsRV.height = (recyclerViewitemHeight * listofViews.size());
        destRecyclerView.setLayoutParams(paramsRV);
        destRecyclerView.requestLayout();


        LinearLayout.LayoutParams layoutParams1 = (LinearLayout.LayoutParams) subLayout.getLayoutParams();
        layoutParams1.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams1.height = ViewGroup.LayoutParams.MATCH_PARENT;
        subLayout.setLayoutParams(layoutParams1);
        subLayout.requestLayout();

        // set SubLayout to default
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) subLayout
                .getLayoutParams();

        layoutParams.setMargins(0, 0, 0, 0);
        subLayout.setLayoutParams(layoutParams);
        subLayout.requestLayout();


        if (arrowUp == false) {

            // set margin to SubLayout due to innerLayout's match parent changes

            LinearLayout.LayoutParams sl = (LinearLayout.LayoutParams) subLayout.getLayoutParams();
            sl.width = ViewGroup.LayoutParams.MATCH_PARENT;
            sl.height = ViewGroup.LayoutParams.MATCH_PARENT;
            sl.setMargins(0, margin, 0, 0);
            subLayout.setLayoutParams(sl);
            subLayout.requestLayout();


            // set innerLayout to match parent

            FrameLayout.LayoutParams lp1 = (FrameLayout.LayoutParams) innerLayout.getLayoutParams();
            lp1.width = ViewGroup.LayoutParams.MATCH_PARENT;
            lp1.height = ViewGroup.LayoutParams.MATCH_PARENT;
            innerLayout.setLayoutParams(lp1);
            innerLayout.requestLayout();

            if (listofViews.size() < 3) {
                iv_arrow.performClick();
            }


            return;
        }

        if (listofViews.size() > 3) {
            iv_arrow.setVisibility(View.VISIBLE);
            ViewGroup.LayoutParams params123 = subLayout.getLayoutParams();
            params123.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params123.height = (int) (height * 0.45);
            subLayout.setLayoutParams(params123);
            subLayout.requestLayout();

            return;
        } else {
            iv_arrow.setVisibility(View.GONE);
            destRecyclerView.scrollToPosition(0);
        }


    }

    private void initView() {
        titleTxt = (TextView) findViewById(R.id.titleTxt);
        addDestinationTitleTxt = (TextView) findViewById(R.id.addDestinationTitleTxt);
        backImgView = (ImageView) findViewById(R.id.backImgView);
        sv_main = (NestedScrollView) findViewById(R.id.sv_main);
        addDestinationArea = (LinearLayout) findViewById(R.id.addDestinationArea);
        contentArea = (LinearLayout) findViewById(R.id.contentArea);
        innerLayout = (LinearLayout) findViewById(R.id.innerLayout);
        subLayout = (LinearLayout) findViewById(R.id.subLayout);
        headerArea = (LinearLayout) findViewById(R.id.headerArea);
        bottomArea = (FrameLayout) findViewById(R.id.bottomArea);
        iv_arrow = (ImageView) findViewById(R.id.iv_arrow);
        destRecyclerView = (RecyclerView) findViewById(R.id.destRecyclerView);
        nextbtn = ((MaterialRippleLayout) findViewById(R.id.nextbtn)).getChildView();

        nextbtn.setId(Utils.generateViewId());
        nextbtn.setOnClickListener(new setOnClickList());
        backImgView.setOnClickListener(new setOnClickList());
        iv_arrow.setOnClickListener(new setOnClickList());
        addDestinationArea.setOnClickListener(new setOnClickList());

        /*Map Data*/
        map = (MultiScrollSupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapV2);

        map.getMapAsync(this);

        map.setListener(new MultiScrollSupportMapFragment.OnTouchListener() {
            @Override
            public void onTouch(boolean b) {

            }
        });
    }

    private void setSelected(String selected) {

        if (selected.equalsIgnoreCase("2")) {
            if (generalFunc.isRTLmode()) {
                ((ImageView) findViewById(R.id.iv1)).setRotation(-180);
            }

            ((ImageView) findViewById(R.id.iv1)).setImageResource(R.drawable.multi_arrow_shape_normal);
            ((TextView) findViewById(R.id.tv1)).setBackgroundColor(getResources().getColor(R.color.multi_tab_light_color));
            ((TextView) findViewById(R.id.tv1)).setTextColor(getResources().getColor(R.color.appThemeColor_TXT_1));
            ((TextView) findViewById(R.id.tv1)).setText(generalFunc.retrieveLangLBl("", "LBL_MULTI_VEHICLE_TYPE"));

            ((LinearLayout) findViewById(R.id.tabArea1)).setBackgroundColor(getResources().getColor(R.color.multi_tab_dark_color));
            if (generalFunc.isRTLmode()) {
                ((ImageView) findViewById(R.id.iv2)).setRotation(-180);
            }
            ((ImageView) findViewById(R.id.iv2)).setImageResource(R.drawable.multi_arrow_shape_dark);
            ((View) findViewById(R.id.lineView)).setBackgroundColor(getResources().getColor(R.color.multi_tab_dark_color));
            ((TextView) findViewById(R.id.tv2)).setBackgroundColor(getResources().getColor(R.color.multi_tab_dark_color));
            ((LinearLayout) findViewById(R.id.tabArea2)).setBackgroundColor(getResources().getColor(R.color.multi_tab_light_color));
            ((TextView) findViewById(R.id.tv2)).setTextColor(getResources().getColor(R.color.appThemeColor_TXT_1));
            ((TextView) findViewById(R.id.tv2)).setText(generalFunc.retrieveLangLBl("", "LBL_MULTI_ROUTE"));


            ((LinearLayout) findViewById(R.id.tabArea3)).setBackgroundColor(getResources().getColor(R.color.multi_tab_light_color));
            ((TextView) findViewById(R.id.tv3)).setTextColor(getResources().getColor(R.color.appThemeColor_TXT_1));
            ((TextView) findViewById(R.id.tv3)).setText(generalFunc.retrieveLangLBl("", "LBL_MULTI_PRICE"));

            findViewById(R.id.headerArea).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.gMap = googleMap;
        setListner(this);
        if (generalFunc.checkLocationPermission(true) == true) {
            getMap().setMyLocationEnabled(true);
        }

        try {
            boolean success = gMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.map_styler));

            if (!success) {
                Log.e("!!!!!!!!!!!", "Style parsing failed.");
            }
        } catch (Exception e) {

        }

        getMap().getUiSettings().setTiltGesturesEnabled(false);
        getMap().getUiSettings().setCompassEnabled(false);
        getMap().getUiSettings().setMyLocationButtonEnabled(false);

        double pickUpLat = generalFunc.parseDoubleValue(0.0, mapData.get("pickUpLocLattitude"));
        double pickUpLong = generalFunc.parseDoubleValue(0.0, mapData.get("pickUpLocLongitude"));
        setCameraPosition(new LatLng(pickUpLat, pickUpLong));


        Multi_Delivery_Data mt = new Multi_Delivery_Data();
        mt.setDestLat(pickUpLat);
        mt.setDestLong(pickUpLong);
        mt.setDestLatLong(new LatLng(pickUpLat, pickUpLong));
        mt.setHintLable(LBL_MULTI_ADD_NEW_DESTINATION);
        mt.setFRLable(LBL_MULTI_FR_TXT);
        mt.setTOLable(LBL_MULTI_TO_TXT);
        mt.setDestAddress(mapData.get("pickUpLocAddress"));
        mt.setIsFromLoc("Yes");

        ArrayList<Delivery_Data> dt = new ArrayList<Delivery_Data>();
        Delivery_Data frData = new Delivery_Data();
        frData.setDestLat(pickUpLat);
        frData.setDestLong(pickUpLong);
        frData.setDestLatLong(new LatLng(pickUpLat, pickUpLong));
        frData.setDestAddress(mapData.get("pickUpLocAddress"));
        frData.setIsFromLoc("Yes");
        dt.add(0, frData);
        mt.setDt(dt);

        boolean hasDetails = false;
        getStoredDetails(); // Get stored list of locations
        if (templistofViews.size() == 0) {
            listofViews.add(mt); // add pickup/source location
        } else {
            hasDetails = true;
            listofViews.add(mt); // add pickup/source location
            listofViews.addAll(templistofViews);  // add stored locations

        }

        setDestinationView(listofViews.size() < 2 ? true : false);
        addSourceMarker(listofViews.get(0).getDestLat(), listofViews.get(0).getDestLong());

        if (hasDetails) {
            sortArrayByDistance(true);

        }
    }

    private ArrayList<Multi_Delivery_Data> getStoredDetails() {
        templistofViews = new ArrayList<>();

        if (Utils.checkText(generalFunc.retrieveValue(Utils.MUTLI_DELIVERY_LIST_JSON_DETAILS_KEY))) {

            Gson gson = new Gson();
            String data1 = generalFunc.retrieveValue(Utils.MUTLI_DELIVERY_LIST_JSON_DETAILS_KEY);
            ArrayList<Multi_Delivery_Data> listofViewsData = gson.fromJson(data1, new TypeToken<ArrayList<Multi_Delivery_Data>>() {
                    }.getType()
            );

            if (templistofViews == null) {
                templistofViews = new ArrayList<>();
            }

            for (int i = 0; i < listofViewsData.size(); i++) {
                if (listofViewsData.get(i).isDetailsAdded() == true) {
                    if (generalFunc.retrieveValue(Utils.ALLOW_MULTIPLE_DEST_ADD_KEY).equalsIgnoreCase("No") && templistofViews.size() == 1) {
                        break;
                    } else {
                        templistofViews.add(listofViewsData.get(i));
                    }
                }
            }

        }

        return templistofViews;
    }

    private void addSourceMarker(double pickUpLat, double pickUpLong) {
        gMap.clear();
        markerArrayList.clear();
        builder = new LatLngBounds.Builder();

        int pinIcon = R.drawable.pin_dest_green;
        String pinText = LBL_MULTI_FR_TXT;
        Bitmap marker_time_ic = generalFunc.writeTextOnDrawable(getActContext(), pinIcon, pinText, true, R.string.defaultFont);

        Marker source_marker = gMap.addMarker(
                new MarkerOptions().position(new LatLng(pickUpLat, pickUpLong))
                        .icon(BitmapDescriptorFactory.fromBitmap(marker_time_ic)));
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(pickUpLat, pickUpLong), 14));
        builder.include(source_marker.getPosition());
        markerArrayList.add(source_marker);

    }

    private void setListner(MultiDeliverySecondPhaseActivity multiDeliverySecondPhaseActivity) {
        gMap.setOnCameraIdleListener(multiDeliverySecondPhaseActivity);
        gMap.setOnCameraMoveCanceledListener(multiDeliverySecondPhaseActivity);
        gMap.setOnCameraMoveStartedListener(multiDeliverySecondPhaseActivity);
        gMap.setOnCameraMoveListener(multiDeliverySecondPhaseActivity);
    }

    private void setCameraPosition(LatLng location) {
        CameraPosition cameraPosition = new CameraPosition.Builder().target(
                new LatLng(location.latitude,
                        location.longitude))
                .zoom(Utils.defaultZomLevel).build();
        gMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    public GoogleMap getMap() {
        return this.gMap;
    }

    @Override
    public void onCameraMoveCanceled() {
        showOrHideViews(true);
    }

    @Override
    public void onCameraMoveStarted(int i) {
        showOrHideViews(false);
    }

    @Override
    public void onCameraIdle() {
        showOrHideViews(true);
    }

    void showOrHideViews(boolean show) {
        int bottomAreaState = innerLayout.getVisibility();
        int headerAreaState = headerArea.getVisibility();
        if (show && (bottomAreaState == 0 && headerAreaState == 0)) {
            return;
        } else if (!show && (bottomAreaState == 8 && headerAreaState == 8)) {
            return;
        }
        animateView(innerLayout, show);
        animateView(headerArea, show);
    }

    public void animateView(View view, boolean expand) {
        animShow = AnimationUtils.loadAnimation(this, R.anim.view_show);
        animHide = AnimationUtils.loadAnimation(this, R.anim.view_hide);

        if (expand) {
            view.setVisibility(View.VISIBLE);
            view.startAnimation(animShow);
        } else {
            view.startAnimation(animHide);
            view.setVisibility(View.GONE);
        }
    }

    @Override
    public void onItemClickList(String type, int position) {
        //Item Clicked

        if (type.equalsIgnoreCase("Remove")) {
            boolean isDestDetailsAdded = false;
            if (listofViews.get(position).isDestination()) {
                isDestDetailsAdded = true;
            }
            listofViews.remove(position);

            showAddDestinationArea();

            destAdapter.notifyDataSetChanged();
            if (arrowUp) {
                setScrollviewHeight();
            } else if (listofViews.size() < 4) {
                setScrollviewHeight();
            }
            if (isDestDetailsAdded) {
                sortArrayByDistance(true);
            }


        } else if (type.equalsIgnoreCase("Select")) {

            if (listofViews.get(position).getIsFromLoc().equalsIgnoreCase("Yes")) {
                return;
            }

            addOrEditDeliveryDetails(position);

        }
    }

    private void showAddDestinationArea() {
        addDestinationArea.setVisibility(View.GONE);
        String ALLOW_MULTIPLE_DEST_ADD_KEY = generalFunc.prefHasKey(Utils.ALLOW_MULTIPLE_DEST_ADD_KEY) ? generalFunc.retrieveValue(Utils.ALLOW_MULTIPLE_DEST_ADD_KEY) : "";
        if (listofViews.size() > 1 && listofViews.get(1).isDetailsAdded() && !ALLOW_MULTIPLE_DEST_ADD_KEY.equalsIgnoreCase("No")) {
            addDestinationArea.setVisibility(View.VISIBLE);
        }
    }

    private void addOrEditDeliveryDetails(int position) {
        if (listofViews.size() - 1 >= maxDestAddAllowedCount && position == -1) {
            generalFunc.showGeneralMessage("", "you can not add destinations more than" + maxDestAddAllowedCount);
            return;
        }
        Bundle bn = new Bundle();
        bn.putString("isDeliverNow", mapData.get("isDeliverNow"));
        bn.putInt("selectedPos", position);
        String json = "";
        if (position != -1) {
            Gson gson = new Gson();
            json = gson.toJson(listofViews.get(position).getDt());
        }
        bn.putString("selectedDetails", json);
        bn.putBoolean("isDestAdded", position != -1 ? listofViews.get(position).isDetailsAdded() : false);
        bn.putBoolean("isEdit", position != -1 ? false : true);
        bn.putBoolean("isFromMulti", true);
        bn.putDouble("lat", listofViews.get(0).getDestLat());
        bn.putDouble("long", listofViews.get(0).getDestLong());
        bn.putString("address", listofViews.get(0).getDestAddress());

        new StartActProcess(getActContext()).startActForResult(EnterMultiDeliveryDetailsActivity.class, bn, Utils.MULTI_DELIVERY_DETAILS_ADD_CODE);
    }

    @Override
    public void onCameraMove() {
        showOrHideViews(false);
    }

    private Activity getActContext() {
        return MultiDeliverySecondPhaseActivity.this;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Utils.MULTI_DELIVERY_DETAILS_ADD_CODE && resultCode == RESULT_OK && data != null) {

            int listPos = data.getIntExtra("selectedPos", -1);


            if (data.hasExtra("selectedDetails")) {

                if (listPos == -1) {
                    addEmptyDestination(true);
                    listPos = listofViews.size() - 1;
                }

                Gson gson = new Gson();
                String data1 = data.getStringExtra("selectedDetails");
                ArrayList<Delivery_Data> dataMap = gson.fromJson(data1, new TypeToken<ArrayList<Delivery_Data>>() {
                        }.getType()
                );
                if (dataMap != null) {
                    listofViews.get(listPos).setDt(dataMap);

                    for (int i = 0; i < dataMap.size(); i++) {
                        // set inside delivery Address details
                        if (dataMap.get(i).geteInputType().equalsIgnoreCase("SelectAddress")) {
                            listofViews.get(listPos).setDestAddress(dataMap.get(i).getDestAddress());
                            listofViews.get(listPos).setDestLong(dataMap.get(i).getDestLong());
                            listofViews.get(listPos).setDestLat(dataMap.get(i).getDestLat());
                            listofViews.get(listPos).setDestLatLong(dataMap.get(i).getDestLatLong());
                            listofViews.get(listPos).setDetailsAdded(true);
                            break;
                        }
                    }

                    if (listofViews.get(listPos).getIsFromLoc().equalsIgnoreCase("No")) {
                        sortArrayByDistance(true);
                    } else {
                        findRoute();
                    }
                }

            }

        } else if (requestCode == Utils.ALL_DELIVERY_DETAILS_ADDED_CODE && resultCode == RESULT_OK && data != null) {

            Gson gson = new Gson();
            ArrayList<Multi_Delivery_Data> list = new ArrayList<>();

            list = gson.fromJson(data.getStringExtra("list"), new TypeToken<ArrayList<Multi_Delivery_Data>>() {
                    }.getType()
            );

            storeDetails(list, false);

            Bundle bn = new Bundle();
            bn.putSerializable("deliveries", generalFunc.retrieveValue(Utils.MUTLI_DELIVERY_JSON_DETAILS_KEY));
            bn.putString("isMulti", "true");
            bn.putString("isCashPayment", data.getStringExtra("isCashPayment"));
            bn.putString("paymentMethod", data.getStringExtra("paymentMethod"));
            bn.putString("promocode", data.getStringExtra("promocode"));

            bn.putString("total_del_dist", data.getStringExtra("total_del_dist"));
            bn.putString("total_del_time", data.getStringExtra("total_del_time"));
            bn.putString("totalFare", data.getStringExtra("totalFare"));

            Logger.d("Api", "second last screen bundle data" + bn.toString());
            (new StartActProcess(getActContext())).setOkResult(bn);
            finish();

        }
    }

    // Sort array based on selected method - Manual calculation or normal
    private void sortArrayByDistance(boolean b) {
        if (generalFunc.retrieveValue(Utils.ENABLE_ROUTE_OPTIMIZE_MULTI_KEY).equalsIgnoreCase("Yes")) {
            // Manual Calculation
            if (count == 0) {
                // compare from source to dest1 n dest1 to dest2 with all destinations
                destinationsArraylist.clear();
                filteredDestinationsArraylist.clear();
                filteredDestinationsArraylist.add(listofViews.get(0));
                destinationsArraylist.addAll(listofViews);
            }

            Logger.d("Api", "loop main" + count);

            if (destinationsArraylist.size() > 1) {
                Logger.d("Api", "mainArraylist size()" + destinationsArraylist.size());
                for (int i = 1; i < destinationsArraylist.size(); i++) {
                    calculateManualDistance(i, count, ((i + 1) == destinationsArraylist.size()), b);
                }
            } else {

                listofViews = new ArrayList<>();
                listofViews.addAll(filteredDestinationsArraylist);
                count = 0;
                if (b) {
                    findRoute();
                }
            }
        } else {
            // compare from source to each destination once
            ArrayList<Multi_Delivery_Data> mainArraylist = new ArrayList<>();

            for (int i = 1; i < listofViews.size(); i++) {
                calculateDistance(listofViews.get(i), mainArraylist);
            }

            Collections.sort(mainArraylist, new MapComparator(""));

            ArrayList<Multi_Delivery_Data> tempArrayList = new ArrayList<>();
            tempArrayList.add(listofViews.get(0));
            tempArrayList.addAll(mainArraylist);

            listofViews = new ArrayList<>();
            listofViews.addAll(tempArrayList);

            if (b) {
                findRoute();
            }
        }
    }

    // Store Details
    private void storeDetails(ArrayList<Multi_Delivery_Data> array, boolean setFocusable) {

        nextbtn.setClickable(setFocusable);
        nextbtn.setFocusable(setFocusable);

        ArrayList<Multi_Delivery_Data> finalAllDetailsArray = new ArrayList<Multi_Delivery_Data>();

        JSONArray jaStore = new JSONArray();
        JSONArray jaStore1 = new JSONArray();
        JSONArray mainJaStore = new JSONArray();
        JSONArray mainAllJaStore = new JSONArray();

        if (setFocusable) {
            for (int i = 0; i < array.size(); i++) {
                storeDetails(i, jaStore, jaStore1, finalAllDetailsArray, array, setFocusable);
            }
        } else {
            for (int i = 1; i < array.size(); i++) {
                storeDetails(i, jaStore, jaStore1, finalAllDetailsArray, array, setFocusable);
            }
        }

        try {

            mainJaStore.put(0, jaStore1);
            mainAllJaStore.put(0, jaStore);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        generalFunc.removeValue(Utils.MUTLI_DELIVERY_JSON_DETAILS_KEY);
        generalFunc.storeData(Utils.MUTLI_DELIVERY_JSON_DETAILS_KEY, jaStore1.toString());

        Gson gson = new Gson();
        String json = gson.toJson(finalAllDetailsArray);

        generalFunc.removeValue(Utils.MUTLI_DELIVERY_LIST_JSON_DETAILS_KEY);
        generalFunc.storeData(Utils.MUTLI_DELIVERY_LIST_JSON_DETAILS_KEY, json);


    }

    private JSONObject storeDetails(int pos, JSONArray jaStore, JSONArray jaStore1, ArrayList<Multi_Delivery_Data> finalAllDetailsArray, ArrayList<Multi_Delivery_Data> mainArray, boolean setFocusable) {
        JSONObject deliveriesObj = new JSONObject();
        JSONObject deliveriesObjall = new JSONObject();
        ArrayList<Delivery_Data> dt = mainArray.get(pos).getDt();
        Multi_Delivery_Data details = mainArray.get(pos);


        for (int i = 0; i < dt.size(); i++) {
            try {

                if (dt.get(i).geteInputType().equalsIgnoreCase("SelectAddress")) {
                    deliveriesObjall.put("DestAddress", dt.get(i).getDestAddress());
                    deliveriesObjall.put("DestLat", dt.get(i).getDestLat());
                    deliveriesObjall.put("DestLong", dt.get(i).getDestLong());
                } else {
                    deliveriesObjall.put("vFieldValue", dt.get(i).getvFieldValue());
                    deliveriesObjall.put("iDeliveryFieldId", dt.get(i).getiDeliveryFieldId());
                    deliveriesObjall.put("eInputType", dt.get(i).geteInputType());
                    deliveriesObjall.put("selectedOptionID", dt.get(i).getSelectedOptionID());
                    deliveriesObjall.put("itemID", dt.get(i).getItemID());
                    deliveriesObjall.put("ePaymentByReceiver", dt.get(i).getPaymentDoneBy());
                }


                if (dt.get(i).geteInputType().equals("Select")) {
                    deliveriesObj.put(dt.get(i).getiDeliveryFieldId(), dt.get(i).getSelectedOptionID());
                } else if (dt.get(i).geteInputType().equalsIgnoreCase("SelectAddress")) {
                    deliveriesObj.put("vReceiverAddress", dt.get(i).getDestAddress());
                    deliveriesObj.put("vReceiverLatitude", dt.get(i).getDestLat());
                    deliveriesObj.put("vReceiverLongitude", dt.get(i).getDestLong());
                } else {
                    deliveriesObj.put(dt.get(i).getiDeliveryFieldId(), dt.get(i).getvFieldValue());
                }


                deliveriesObj.put("ePaymentByReceiver", mainArray.get(pos).getePaymentByReceiver());

                jaStore.put(i, deliveriesObjall);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (setFocusable) {
            finalAllDetailsArray.add(pos, details);
            try {
                jaStore1.put(pos, deliveriesObj);
            } catch (JSONException e) {
                e.printStackTrace();
            }


        } else {
            finalAllDetailsArray.add(details);
            jaStore1.put(deliveriesObj);
        }


        return deliveriesObj;
    }

    // Compare with all entered destinations and sort all points according distance
    private void calculateManualDistance(int loopCount, int compareCount, boolean isLast, boolean b) {

        Multi_Delivery_Data deliveryDetailsObj = destinationsArraylist.get(loopCount);
        Multi_Delivery_Data deliveryDetailsObjSource = destinationsArraylist.get(0);
        Location loc1 = new Location("");
        loc1.setLatitude(deliveryDetailsObjSource.getDestLat());
        loc1.setLongitude(deliveryDetailsObjSource.getDestLong());

        Location loc2 = new Location("");
        loc2.setLatitude(deliveryDetailsObj.getDestLat());
        loc2.setLongitude(deliveryDetailsObj.getDestLong());

        Random r = new Random();
        int randomNo = r.nextInt(1000 + 1);
        long distanceInMeters = (long) loc1.distanceTo(loc2);  // direct distance
        int lowestTime = ((int) (((distanceInMeters / 1000) * DRIVER_ARRIVED_MIN_TIME_PER_MINUTE) * 60)); // estimated Time
        deliveryDetailsObj.setDistance(0);
        deliveryDetailsObj.setTime(lowestTime);
        deliveryDetailsObj.setDistance(distanceInMeters);
        deliveryDetailsObj.setID("" + randomNo);
        destinationsArraylist.set(loopCount, deliveryDetailsObj);

        if (isLast) {
            Collections.sort(destinationsArraylist, new MapComparator(""));
            filteredDestinationsArraylist.add(destinationsArraylist.get(0 + 1));
            destinationsArraylist.remove(0 + 1);
            count = compareCount + 1;

            if (listofViews.size() == count + 1) {
                listofViews = new ArrayList<>();
                listofViews.addAll(filteredDestinationsArraylist);
                count = 0;
                if (b) {
                    findRoute();
                }
            } else {
                sortArrayByDistance(b);
            }
        }


    }

    // Find and sort all points distance
    private void calculateDistance(Multi_Delivery_Data deliveryDetailsObj, ArrayList<Multi_Delivery_Data> mainArraylist) {

        Location loc1 = new Location("");
        loc1.setLatitude(listofViews.get(0).getDestLat());
        loc1.setLongitude(listofViews.get(0).getDestLong());

        Location loc2 = new Location("");
        loc2.setLatitude(deliveryDetailsObj.getDestLat());
        loc2.setLongitude(deliveryDetailsObj.getDestLong());

        Random r = new Random();
        int randomNo = r.nextInt(1000 + 1);
        long distanceInMeters = (long) loc1.distanceTo(loc2);  // direct distance
        int lowestTime = ((int) ((distanceInMeters / 1000) * DRIVER_ARRIVED_MIN_TIME_PER_MINUTE) * 60); // estimated Time
        deliveryDetailsObj.setDistance(0);
        deliveryDetailsObj.setTime(lowestTime);
        deliveryDetailsObj.setDistance(distanceInMeters);
        deliveryDetailsObj.setID("" + randomNo);
        mainArraylist.add(deliveryDetailsObj);
    }


    Location srcLoc = new Location("Source");
    Location destLoc = new Location("Destination");

    // Sort list data based on distance
    public void findRoute() {

        addSourceMarker(listofViews.get(0).getDestLat(), listofViews.get(0).getDestLong());

        if (generalFunc.retrieveValue(Utils.ENABLE_ROUTE_OPTIMIZE_MULTI_KEY).equalsIgnoreCase("Yes")) {
            // do manual calculation of time & Distance n add pins on map
            long distanceVal = 0;
            long timeVal = 0;
            dest_details_Array = new ArrayList<>();
            for (int i = 1; i < listofViews.size(); i++) {
                if (listofViews.get(i).isDetailsAdded() == true) {
                    int pinIcon = R.drawable.pin_dest_yellow;
                    String pinText = listofViews.size() > 2 ? ("" + i) : LBL_MULTI_TO_TXT;
                    Bitmap marker_time_ic = generalFunc.writeTextOnDrawable(getActContext(), pinIcon, pinText, false, R.string.defaultFont);
                    Marker dest_marker = gMap.addMarker(new MarkerOptions().position(listofViews.get(i).getDestLatLong()).icon(BitmapDescriptorFactory.fromBitmap(marker_time_ic)));
                    markerArrayList.add(dest_marker);
                    builder.include(dest_marker.getPosition());
                    listofViews.get(i).setDestination(true);
                    long origTime = listofViews.get(i).getTime();
                    long origDistance = listofViews.get(i).getDistance();
                    timeVal = timeVal + origTime;
                    distanceVal = distanceVal + origDistance;

                    Multi_Dest_Info_Detail_Data dt = new Multi_Dest_Info_Detail_Data();
                    dt.setDistance(origDistance);
                    dt.setTime(origTime);

                    distance = "" + (distanceVal);
                    time = "" + (timeVal);
                    isManualCalculation = true;
                    dest_details_Array.add(dt);
                }
            }

            setDestinationView(false);
            storeDetails(listofViews, true);

            if (listofViews.size() > 1) {
                bindAllMarkers();
            }
            return;
        }


        // Origin of route
        String str_origin = "origin=" + listofViews.get(0).getDestLat() + "," + listofViews.get(0).getDestLong();
        srcLoc.setLatitude(listofViews.get(0).getDestLat());
        srcLoc.setLongitude(listofViews.get(0).getDestLong());


        String str_dest = "";
        String waypoints = "";
        wayPointslist = new ArrayList<>();
        destPointlist = new ArrayList<>();
        finalPointlist = new ArrayList<>();
        dest_details_Array = new ArrayList<>();

        templistofViews = new ArrayList<>();
        isManualCalculation = false;

        for (int i = 0; i < listofViews.size(); i++) {
            if (listofViews.get(i).isDetailsAdded() == true) {
                templistofViews.add(listofViews.get(i));
            }
        }

        if (templistofViews.size() == 0) {

            if (route_polyLine != null) {
                route_polyLine.remove();
            }
            return;
        }


        if (templistofViews.size() > 0) {
            String lastAddress = "";
            for (int i = 0; i < templistofViews.size(); i++) {

                if (i == templistofViews.size() - 1) {
                    str_dest = "destination=" + templistofViews.get(templistofViews.size() - 1).getDestLat() + "," + templistofViews.get(templistofViews.size() - 1).getDestLong();
                    destLoc.setLatitude(templistofViews.get(templistofViews.size() - 1).getDestLat());
                    destLoc.setLongitude(templistofViews.get(templistofViews.size() - 1).getDestLong());
                    templistofViews.get(i).setDestination(true);
                    destPointlist.add(templistofViews.get(i));
                } else if (i == templistofViews.size() - 2) {
                    wayPointslist.add(templistofViews.get(i));
                    lastAddress = templistofViews.get(i).getDestLat() + "," + templistofViews.get(i).getDestLong();

                } else {
                    wayPointslist.add(templistofViews.get(i));
                    waypoints = waypoints + templistofViews.get(i).getDestLat() + "," + templistofViews.get(i).getDestLong() + "|";
                }

            }
            waypoints = "waypoints=optimize:true|" + waypoints + lastAddress;
        } else {
            str_dest = "destination=" + templistofViews.get(templistofViews.size() - 1).getDestLat() + "," + templistofViews.get(templistofViews.size() - 1).getDestLong();
            destLoc.setLatitude(templistofViews.get(templistofViews.size() - 1).getDestLat());
            destLoc.setLongitude(templistofViews.get(templistofViews.size() - 1).getDestLong());
            destPointlist.add(templistofViews.get(templistofViews.size() - 1));
        }

        String parameters = "";
        // Building the parameters to the web service
        if (templistofViews.size() > 1) {
            parameters = str_origin + "&" + str_dest + "&" + waypoints;

        } else {
            parameters = str_origin + "&" + str_dest;

        }
        String url = "https://maps.googleapis.com/maps/api/directions/json?" + parameters + "&key=" + serverKey + "&language=" + generalFunc.retrieveValue(Utils.GOOGLE_MAP_LANGUAGE_CODE_KEY) + "&sensor=true";

        Logger.d("Api", "directUrl_value True");

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), url, true);

        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(String responseString) {

                if (responseString != null && !responseString.equals("")) {

                    String status = generalFunc.getJsonValue("status", responseString);

                    if (status.equals("OK")) {

                        JSONArray obj_routes = generalFunc.getJsonArray("routes", responseString);
                        if (obj_routes != null && obj_routes.length() > 0) {


                            PolylineOptions lineOptions = getGoogleRouteOptions(responseString, Utils.dipToPixels(getActContext(), 5), getActContext().getResources().getColor(R.color.black), getActContext(), listofViews, wayPointslist, destPointlist, finalPointlist, dest_details_Array, markerArrayList, gMap, builder);

                            if (lineOptions != null) {
                                if (route_polyLine != null) {
                                    route_polyLine.remove();
                                }
                                route_polyLine = gMap.addPolyline(lineOptions);
                            }

                            if (finalPointlist.size() > 0) {
                                ArrayList<Multi_Delivery_Data> finalAllPointlist = new ArrayList<>();

                                finalAllPointlist = new ArrayList<>();
                                finalAllPointlist.add(listofViews.get(0));
                                finalAllPointlist.addAll(finalPointlist);
                               /*
                               // Re-add blank added destinations
                               if ((listofViews.size()) > finalAllPointlist.size()) {
                                    for (int i = 0; i < (listofViews.size() - finalAllPointlist.size()); i++) {
                                        Multi_Delivery_Data mt = new Multi_Delivery_Data();
                                        mt.setHintLable(generalFunc.retrieveLangLBl("", "LBL_MULTI_ADD_NEW_DESTINATION"));
                                        mt.setFRLable(LBL_MULTI_FR_TXT);
                                        mt.setTOLable(LBL_MULTI_TO_TXT);
                                        finalAllPointlist.add(mt);
                                    }
                                }
                               */
                                listofViews.clear();
                                listofViews.addAll(finalAllPointlist);
                            }

                            setDestinationView(false);
                            storeDetails(listofViews, true);
                            bindAllMarkers();
                        }

                    } else {
                        generalFunc.showGeneralMessage(generalFunc.retrieveLangLBl("", "LBL_ERROR_TXT"),
                                generalFunc.retrieveLangLBl("", "LBL_GOOGLE_DIR_NO_ROUTE"));
                    }

                } else {
                    generalFunc.showError();
                }
            }
        });


        if (generalFunc.retrieveValue("ENABLE_GOOGLE_API_OPTIMIZATION").equalsIgnoreCase("Yes")) {

            managePath();
        } else {
            exeWebServer.execute();
        }
    }


    public void managePath() {


        PolylineOptions lineOptions = getGoogleCurveRouteOptions();

        if (wayPointslist.size() == 0) {
            lineOptions = getGoogleCurveRouteOptions();
        }


        if (lineOptions != null) {
            if (route_polyLine != null) {
                route_polyLine.remove();
            }
            route_polyLine = gMap.addPolyline(lineOptions);
        }

        if (finalPointlist.size() > 0) {
            ArrayList<Multi_Delivery_Data> finalAllPointlist = new ArrayList<>();

            finalAllPointlist = new ArrayList<>();
            finalAllPointlist.add(listofViews.get(0));
            finalAllPointlist.addAll(finalPointlist);
                               /*
                               // Re-add blank added destinations
                               if ((listofViews.size()) > finalAllPointlist.size()) {
                                    for (int i = 0; i < (listofViews.size() - finalAllPointlist.size()); i++) {
                                        Multi_Delivery_Data mt = new Multi_Delivery_Data();
                                        mt.setHintLable(generalFunc.retrieveLangLBl("", "LBL_MULTI_ADD_NEW_DESTINATION"));
                                        mt.setFRLable(LBL_MULTI_FR_TXT);
                                        mt.setTOLable(LBL_MULTI_TO_TXT);
                                        finalAllPointlist.add(mt);
                                    }
                                }
                               */
            listofViews.clear();
            listofViews.addAll(finalAllPointlist);
        }
        String pinText = generalFunc.retrieveLangLBl("TO", "LBL_MULTI_TO_TXT");
        int pinIcon = R.drawable.pin_dest_yellow;
//        if (jLegs.length() > 1) {
//            pinText = "" + (j + 1);
//        }

        Bitmap marker_time_ic = generalFunc.writeTextOnDrawable(getActContext(), pinIcon, pinText, false, R.string.defaultFont);
        Marker dest_marker = gMap.addMarker(
                new MarkerOptions().position(new LatLng(destLoc.getLatitude(), destLoc.getLongitude()))
                        .icon(BitmapDescriptorFactory.fromBitmap(marker_time_ic)));
        builder.include(dest_marker.getPosition());
        markerArrayList.add(dest_marker);

        setDestinationView(false);
        storeDetails(listofViews, true);
        bindAllMarkers();


    }


    // Add Markers
    private void bindAllMarkers() {
        try {
            if (builder != null && getMap() != null) {
                LatLngBounds bounds = builder.build();
                LatLng center = bounds.getCenter();

                LatLng northEast = SphericalUtil.computeOffset(center, 30 * Math.sqrt(2.0), SphericalUtil.computeHeading(center, bounds.northeast));
                LatLng southWest = SphericalUtil.computeOffset(center, 30 * Math.sqrt(2.0), (180 + (180 + SphericalUtil.computeHeading(center, bounds.southwest))));

                builder.include(southWest);
                builder.include(northEast);

                int screenWidth = getResources().getDisplayMetrics().widthPixels;
                int screenHeight = getResources().getDisplayMetrics().heightPixels;
                int reScreenHeight = screenHeight - (arrowUp ? (headerArea.getMeasuredHeight() + (innerLayout.getMeasuredHeight() + 5) + Utils.dipToPixels(getActContext(), 60)) : Utils.dipToPixels(getActContext(), 300));
                int padding = (int) (screenWidth * 0.25); // offset from edges of the map 10% of screen
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(builder.build(),
                        screenWidth, reScreenHeight, padding);
                float maxZoomLevel = gMap.getMaxZoomLevel();
                gMap.setMaxZoomPreference(maxZoomLevel);
                getMap().animateCamera(cameraUpdate);
            }
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    private void setBounceAnimation(View view, BounceAnimListener bounceAnimListener) {
        Animation anim = AnimationUtils.loadAnimation(getActContext(), R.anim.bounce_interpolator);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                if (bounceAnimListener != null) {
                    bounceAnimListener.onAnimationFinished();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(anim);
    }

    // add route polyline line
    public PolylineOptions getGoogleRouteOptions(String directionJson, int width, int color, Context mContext, ArrayList<Multi_Delivery_Data> list, ArrayList<Multi_Delivery_Data> wayPointslist, ArrayList<Multi_Delivery_Data> destPointlist, ArrayList<Multi_Delivery_Data> finalPointlist, ArrayList<Multi_Dest_Info_Detail_Data> dest_details_array, ArrayList<Marker> markerArrayList, GoogleMap gMap, LatLngBounds.Builder builder) {
        PolylineOptions lineOptions = new PolylineOptions();

        try {
            DataParser parser = new DataParser(mContext, list, wayPointslist, destPointlist, finalPointlist, dest_details_array, markerArrayList, gMap, builder);
            List<List<HashMap<String, String>>> routes_list = parser.parse(new JSONObject(directionJson));
            distance = parser.distance;
            time = parser.time;
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

    public PolylineOptions getGoogleCurveRouteOptions() {
        PolylineOptions options = createCurveRoute();
        try {

            distance = SphericalUtil.computeDistanceBetween(new LatLng(srcLoc.getLatitude(), srcLoc.getLongitude()), new LatLng(destLoc.getLatitude(), destLoc.getLongitude())) + "";
            time = (int) ((GeneralFunctions.parseDoubleValue(0, distance) / 1000) * DRIVER_ARRIVED_MIN_TIME_PER_MINUTE) * 60 + "";
            if (GeneralFunctions.parseIntegerValue(0, time) < 1) {
                time = 1 + "";
            }

//            ArrayList<LatLng> points = new ArrayList<LatLng>();
//
//           // points.add(new LatLng(srcLoc.getLatitude(), srcLoc.getLongitude()));
//            points.add(new LatLng(destLoc.getLatitude(), destLoc.getLongitude()));
//
//
//            lineOptions.addAll(points);
//            lineOptions.width(width);
//            lineOptions.color(color);

            return options;

        } catch (Exception e) {
            return null;
        }
    }

    public PolylineOptions createCurveRoute() {
        //Polyline options
        PolylineOptions options = new PolylineOptions();

        if (wayPointslist.size() > 0) {
            for (int j = 0; j < wayPointslist.size(); j++) {

                if (j == 0) {
                    getCurveRouteOptions(new LatLng(srcLoc.getLatitude(), srcLoc.getLongitude()), wayPointslist.get(j).getDestLatLong(), options);

                } else if (j == wayPointslist.size() - 1) {
                    getCurveRouteOptions(wayPointslist.get(j).getDestLatLong(), new LatLng(destLoc.getLatitude(), destLoc.getLongitude()), options);
                } else {
                    getCurveRouteOptions(wayPointslist.get(j - 1).getDestLatLong(), wayPointslist.get(j).getDestLatLong(), options);
                }

                if (wayPointslist.size() == 1) {
                    getCurveRouteOptions(wayPointslist.get(j).getDestLatLong(), new LatLng(destLoc.getLatitude(), destLoc.getLongitude()), options);
                }

                String pinText;
                int pinIcon = R.drawable.pin_dest_yellow;

                pinText = "" + (j + 1);


                Bitmap marker_time_ic = generalFunc.writeTextOnDrawable(getActContext(), pinIcon, pinText, false, R.string.defaultFont);
                Marker dest_marker = gMap.addMarker(
                        new MarkerOptions().position(wayPointslist.get(j).getDestLatLong())
                                .icon(BitmapDescriptorFactory.fromBitmap(marker_time_ic)));
                builder.include(dest_marker.getPosition());
                markerArrayList.add(dest_marker);


            }

        } else {
            getCurveRouteOptions(new LatLng(srcLoc.getLatitude(), srcLoc.getLongitude()), new LatLng(destLoc.getLatitude(), destLoc.getLongitude()), options);
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
        return options;
    }

    private interface BounceAnimListener {
        void onAnimationFinished();
    }

    // draw RouteLine

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            int i = view.getId();
            Utils.hideKeyboard(getActContext());
            if (i == R.id.backImgView) {
                MultiDeliverySecondPhaseActivity.super.onBackPressed();
            } else if (i == nextbtn.getId()) {

                boolean detailsAdded = false;
                for (int j = 0; j < listofViews.size(); j++) {
                    if (listofViews.get(j).isDetailsAdded() || listofViews.get(j).getIsFromLoc().equalsIgnoreCase("Yes")) {
                        detailsAdded = true;
                    } else {
                        detailsAdded = false;
                    }
                }

                // Check all details added or not ?
                if (detailsAdded == false) {
                    generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("Please add all location's details", "LBL_MULTI_AD_ALL_DEST_TXT"));
                    return;
                }

                if (Utils.checkText(time) && Utils.checkText(distance)) {

                    Bundle bn = new Bundle();
                    mapData.put("time", time);
                    mapData.put("distance", distance);
                    bn.putSerializable("selectedData", mapData);
                    Gson gson = new Gson();
                    String json = gson.toJson(listofViews);
                    String jaStore = gson.toJson(dest_details_Array);
                    // String json1 = gson.toJson(jaStore);
                    bn.putString("selectedDetails", json);
                    bn.putSerializable("timeAndDistArr", jaStore);
                    bn.putBoolean("isFromMulti", true);
                    bn.putBoolean("isManualCalculation", true);
                    new StartActProcess(getActContext()).startActForResult(MultiDeliveryThirdPhaseActivity.class, bn, Utils.ALL_DELIVERY_DETAILS_ADDED_CODE);
                }


            } else if (i == R.id.iv_arrow) {
                if (arrowUp) {
                    iv_arrow.setImageResource(R.mipmap.arrow_sliding_down);
                    arrowUp = false;
                    setScrollviewHeight();
                } else {
                    iv_arrow.setImageResource(R.mipmap.arrow_sliding_up);
                    arrowUp = true;
                    setScrollviewHeight();
                }
            } else if (i == R.id.addDestinationArea) {
                setBounceAnimation(addDestinationArea, () -> {
                    addOrEditDeliveryDetails(-1);
                });
//                setDestinationView(false);

            }
        }
    }

}
