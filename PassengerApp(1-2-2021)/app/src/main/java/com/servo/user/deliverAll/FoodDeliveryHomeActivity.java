package com.servo.user.deliverAll;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.JResponseClasses.ApiInterface;
import com.JResponseClasses.ApiStoreInterface;
import com.JResponseClasses.BannerResponse;
import com.JResponseClasses.ResturentBannerInterface;
import com.adapter.JDemoAdapter;
import com.adapter.ResBannerResponce;
import com.adapter.RsBannnerModelClass;
import com.adapter.files.deliverAll.FoodDeliveryHomeAdapter;
import com.adapter.files.deliverAll.RestaurantAdapter;
import com.jAdapter.ResturantBannerAdapter;
import com.junaid_condition_sets.StaticClass;
import com.servo.user.R;
import com.servo.user.SearchLocationActivity;
import com.general.files.AddDrawer;
import com.general.files.AppFunctions;
import com.general.files.ConfigPubNub;
import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.general.files.GetAddressFromLocation;
import com.general.files.GetLocationUpdates;
import com.general.files.InternetConnection;
import com.general.files.MyApp;
import com.general.files.OpenAdvertisementDialog;
import com.general.files.StartActProcess;
import com.general.files.UpdateFrequentTask;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.realmModel.Cart;
import com.squareup.picasso.Picasso;
import com.utils.Logger;
import com.utils.Utils;
import com.view.CounterFab;
import com.view.ErrorView;
import com.GenerateAlertBox;
import com.view.LoopingCirclePageIndicator;
import com.view.MButton;

import com.view.MaterialRippleLayout;
import com.MyProgressDialog;
import com.view.SelectableRoundedImageView;
import com.view.simpleratingbar.SimpleRatingBar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE;

public class FoodDeliveryHomeActivity extends AppCompatActivity implements UpdateFrequentTask.OnTaskRunCalled,
        ViewPager.OnPageChangeListener, GetAddressFromLocation.AddressFound, GetLocationUpdates.LocationUpdates, ResturentBannerInterface,
        RestaurantAdapter.RestaurantOnClickListener, AppBarLayout.OnOffsetChangedListener, JDemoAdapter.newIntercaeBannerClick {

    int selected_cat = 0;
    StaticClass st;
    public LinearLayout noloactionview;
    public ImageView nolocmenuImgView;
    public ImageView nolocbackImgView;
    public String userProfileJson = "";
    public JSONObject obj_userProfile;
    int BANNER_AUTO_ROTATE_INTERVAL = 4000;
    RecyclerView dataListRecyclerView;
    View bannerArea;
    ViewPager bannerViewPager;
    LoopingCirclePageIndicator bannerCirclePageIndicator;
    FoodDeliveryHomeAdapter bannerAdapter;
    GeneralFunctions generalFunc;

    TextView headerLocAddressTxt, LocStaticTxt, noOfServiceTxt;
    TextView headerTxt;
    String headerAddress = "";
    ArrayList<HashMap<String, String>> generalCategoryList = new ArrayList<>();
    ArrayList<HashMap<String, String>> mainCategoryList = new ArrayList<>();
    ArrayList<HashMap<String, String>> subCategoryList = new ArrayList<>();
    ArrayList<HashMap<String, String>> filterList = new ArrayList<>();
    //String CAT_TYPE_MODE = "0";
    String latitude = "0.0";
    String longitude = "0.0";
    UpdateFrequentTask updateBannerChangeFreqTask;
    int currentBannerPosition = 0;
    ImageView backImgView, menuImgView;
    String address;
    GetLocationUpdates getLastLocation;
    boolean isUfxaddress = false;
    GetAddressFromLocation getAddressFromLocation;
    boolean isgpsview = false;
    TextView noLocMsgTxt, deliveryAreaTxt;
    TextView noLocTitleTxt;

    ConfigPubNub configPubNub;
    boolean isArrivedPopup = false;
    boolean isstartPopup = false;
    boolean isEndpopup = false;
    boolean isCancelpopup = false;
    MButton noThanksBtn;
    TextView outAreaTitle;
    int numberOfRestaurant = 0;

    Location currentLocation;

    RecyclerView restaurantListRecycView;
    // fixme resturant adapter
    RestaurantAdapter restaurantAdapter;
    ArrayList<HashMap<String, String>> restaurantArr_List = new ArrayList<>();
    ArrayList<HashMap<String, String>> sortby_List = new ArrayList<>();

    TextView filterTxtView, relevenceTxt;

    RecyclerView j_banner_recycler;

    RelativeLayout filter_lay;


    ImageView filterImage;
    MButton changeLocBtn, editLocationBtn;
    LinearLayout locationPopup;
    ImageView menuSearchView;

    LinearLayout searchLAy;

    Location filterLocation;
    String isOfferCheck = "No";
    String isFavCheck = "No";
    String selectedFilterId = "";
    boolean filterClick = false;
    String selectedSort = "relevance";


    public AddDrawer addDrawer;

    //public DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    SwipeRefreshLayout mSwipeRefreshLayout;

    private AppBarLayout app_bar_layout;
    LinearLayout ratingArea;

    TextView orderHotelName;
    ImageView ratingCancel;
    SimpleRatingBar ratingBar;

    LinearLayout errorViewArea;
    ErrorView errorView;

    InternetConnection internetConnection;
    String isOfferApply = "No";
    RealmResults<Cart> realmCartList;
    CounterFab fabcartIcon;
    String next_page_str = "";
    boolean mIsLoading = false;
    boolean isNextPageAvailable = false;
    TextView NoDataTxt;
    boolean isSecondTime = false;


    TabLayout category_restaurant_tablayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_delivery_home);

        st = new StaticClass(this);

        category_restaurant_tablayout = findViewById(R.id.category_restaurant_tablayout);

        j_banner_recycler = findViewById(R.id.j_banner_recycler);


        generalFunc = MyApp.getInstance().getGeneralFun(getActContext());
        internetConnection = new InternetConnection(getActContext());
        isUfxaddress = false;
        userProfileJson = generalFunc.retrieveValue(Utils.USER_PROFILE_JSON);
        obj_userProfile = generalFunc.getJsonObject(userProfileJson);

        if (obj_userProfile != null) {
            JSONArray serviceArray = generalFunc.getJsonArray("ServiceCategories", obj_userProfile);

            if (serviceArray != null && serviceArray.length() > 1 && generalFunc.isAnyDeliverOptionEnabled()) {
                JSONObject advertise_banner_data = generalFunc.getJsonObject("advertise_banner_data", obj_userProfile);
                if (advertise_banner_data != null && advertise_banner_data.length() > 0) {

                    String image_url = generalFunc.getJsonValueStr("image_url", advertise_banner_data);
                    if (image_url != null && !image_url.equalsIgnoreCase("")) {
                        HashMap<String, String> map = new HashMap<>();
                        map.put("image_url", generalFunc.getJsonValueStr("image_url", advertise_banner_data));
                        map.put("tRedirectUrl", generalFunc.getJsonValueStr("tRedirectUrl", advertise_banner_data));
                        map.put("vImageWidth", generalFunc.getJsonValueStr("vImageWidth", advertise_banner_data));
                        map.put("vImageHeight", generalFunc.getJsonValueStr("vImageHeight", advertise_banner_data));
                        new OpenAdvertisementDialog(getActContext(), map, generalFunc);
                    }
                }
            }
        }


        fabcartIcon = (CounterFab) findViewById(R.id.fabcartIcon);


        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeToRefresh);
        app_bar_layout = (AppBarLayout) findViewById(R.id.app_bar_layout);
        ratingArea = (LinearLayout) findViewById(R.id.ratingArea);
        orderHotelName = (TextView) findViewById(R.id.orderHotelName);
        ratingCancel = (ImageView) findViewById(R.id.ratingCancel);
        ratingBar = (SimpleRatingBar) findViewById(R.id.ratingBar);
        errorViewArea = (LinearLayout) findViewById(R.id.errorViewArea);
        errorView = (ErrorView) findViewById(R.id.errorView);
        NoDataTxt = (TextView) findViewById(R.id.NoDataTxt);

        ratingCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ratingArea.setVisibility(View.GONE);
            }
        });

        ratingBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bn = new Bundle();

                bn.putFloat("rating", ratingBar.getRating());

                new StartActProcess(getActContext()).startActWithData(FoodRatingActivity.class, bn);
                ratingArea.setVisibility(View.GONE);
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                //  getFilterList();

                generateErrorView();
                if (filterLocation != null) {
                    getRestaurantListnew(filterLocation.getLatitude(), filterLocation.getLongitude(), false);

                    getFilterList(filterLocation.getLatitude(), filterLocation.getLongitude());
                }
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
        fabcartIcon.setOnClickListener(new setOnClickLst());
        // abubakar rating
        String Ratings_From_DeliverAll = generalFunc.getJsonValueStr("Ratings_From_DeliverAll", obj_userProfile);


        if (Ratings_From_DeliverAll != null && !Ratings_From_DeliverAll.equalsIgnoreCase("") &&
                !Ratings_From_DeliverAll.equalsIgnoreCase("Done")) {
            String lastOrder = generalFunc.getJsonValueStr("LastOrderCompanyName", obj_userProfile);
            Log.d("LastOrderCompanyName", "" + generalFunc.getJsonValueStr("LastOrderCompanyName", obj_userProfile));

            ratingArea.setVisibility(View.VISIBLE);
            orderHotelName.setText(generalFunc.getJsonValueStr("LastOrderCompanyName", obj_userProfile));

            // abubakar pending work rating
        }

        getLastLocation = new GetLocationUpdates(getActContext(), Utils.LOCATION_UPDATE_MIN_DISTANCE_IN_MITERS, false, this);

        try {
            address = getIntent().getStringExtra("uberXAddress");
        } catch (Exception e) {
        }

        setSortList();
        latitude = getIntent().getDoubleExtra("uberXlat", 0.0) + "";
        longitude = getIntent().getDoubleExtra("uberXlong", 0.0) + "";

        boolean isback = getIntent().getBooleanExtra("isback", false);

        dataListRecyclerView = (RecyclerView) findViewById(R.id.dataListRecyclerView);
        restaurantListRecycView = (RecyclerView) findViewById(R.id.restaurantListRecycView);
        bannerArea = findViewById(R.id.bannerArea);
        bannerViewPager = (ViewPager) findViewById(R.id.bannerViewPager);
        bannerCirclePageIndicator = (LoopingCirclePageIndicator) findViewById(R.id.bannerCirclePageIndicator);
        headerLocAddressTxt = (TextView) findViewById(R.id.headerLocAddressTxt);
        filterTxtView = (TextView) findViewById(R.id.filterTxtView);
        filterTxtView.setVisibility(View.GONE);
        filter_lay = findViewById(R.id.filter_lay);
        filterImage = (ImageView) findViewById(R.id.filterImage);
        relevenceTxt = (TextView) findViewById(R.id.relevenceTxt);
        relevenceTxt.setText(generalFunc.retrieveLangLBl("", "LBL_RELEVANCE"));
        headerTxt = (TextView) findViewById(R.id.headerTxt);
        LocStaticTxt = (TextView) findViewById(R.id.LocStaticTxt);
        noOfServiceTxt = (TextView) findViewById(R.id.noOfServiceTxt);
        outAreaTitle = (TextView) findViewById(R.id.outAreaTitle);
        backImgView = (ImageView) findViewById(R.id.backImgView);
        menuImgView = (ImageView) findViewById(R.id.menuImgView);
        //menuImgView.setOnClickListener(new setOnClickLst());
        changeLocBtn = (MButton) findViewById(R.id.changeLocBtn);
        locationPopup = (LinearLayout) findViewById(R.id.locationPopup);
        editLocationBtn = (MButton) findViewById(R.id.editLocationBtn);
        noThanksBtn = (MButton) findViewById(R.id.noThanksBtn);
        menuSearchView = (ImageView) findViewById(R.id.menuSearchView);
        menuSearchView.setVisibility(View.GONE);
        searchLAy = (LinearLayout) findViewById(R.id.searchLAy);
        noThanksBtn.setOnClickListener(new setOnClickLst());
        changeLocBtn.setOnClickListener(new setOnClickLst());
        editLocationBtn.setOnClickListener(new setOnClickLst());
        backImgView.setOnClickListener(new setOnClickLst());
        filterTxtView.setOnClickListener(new setOnClickLst());
        filter_lay.setOnClickListener(new setOnClickLst());
        relevenceTxt.setOnClickListener(new setOnClickLst());

        noloactionview = (LinearLayout) findViewById(R.id.noloactionview);
        noLocTitleTxt = (TextView) findViewById(R.id.noLocTitleTxt);
        noLocMsgTxt = (TextView) findViewById(R.id.noLocMsgTxt);
        deliveryAreaTxt = (TextView) findViewById(R.id.deliveryAreaTxt);

        nolocmenuImgView = (ImageView) findViewById(R.id.nolocmenuImgView);
        nolocbackImgView = (ImageView) findViewById(R.id.nolocbackImgView);

        nolocmenuImgView.setOnClickListener(new setOnClickList());
        nolocbackImgView.setOnClickListener(new setOnClickList());

//        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
//
//        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
//                1, 2) {
//
//            /** Called when a drawer has settled in a completely closed state. */
//            public void onDrawerClosed(View view) {
//                super.onDrawerClosed(view);
//                // getActionBar().setTitle("Closed");
//                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
//            }
//
//            /** Called when a drawer has settled in a completely open state. */
//            public void onDrawerOpened(View drawerView) {
//                super.onDrawerOpened(drawerView);
//                // getActionBar().setTitle("Opened");
//                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
//            }
//        };
        // Set the drawer toggle as the DrawerListener
        //mDrawerLayout.setDrawerListener(mDrawerToggle);

        CollapsingToolbarLayout.LayoutParams lyParamsBannerArea = (CollapsingToolbarLayout.LayoutParams) bannerArea.getLayoutParams();
        lyParamsBannerArea.height = Utils.getHeightOfBanner(getActContext(), 0, "16:9");
        bannerArea.setLayoutParams(lyParamsBannerArea);
        addDrawer = new AddDrawer(getActContext(), userProfileJson);

        if (isback) {
            menuImgView.setVisibility(View.GONE);
            backImgView.setVisibility(View.VISIBLE);
            addDrawer.configDrawer(true);
        } else {
            addDrawer.configDrawer(false);
        }
//        ufxCatAdapter = new UberXCategoryAdapter(getActContext(), generalCategoryList, generalFunc);

        dataListRecyclerView.setLayoutManager(new LinearLayoutManager(getActContext()));
        //CAT_TYPE_MODE = "1";

        //  dataListRecyclerView.setAdapter(restaurantAdapter);

        (findViewById(R.id.headerLogo)).setVisibility(View.GONE);
        (findViewById(R.id.uberXHeaderLayout)).setOnClickListener(new setOnClickLst());
        (findViewById(R.id.headerTxt)).setOnClickListener(new setOnClickLst());

        menuSearchView.setOnClickListener(new setOnClickLst());
        searchLAy.setOnClickListener(new setOnClickLst());
        (findViewById(R.id.headerArrow)).setOnClickListener(new setOnClickLst());

        setData();
        getBanners();

        bannerViewPager.addOnPageChangeListener(this);

        Log.d("FoodDeliveryHome+++", "" + restaurantArr_List);

        restaurantAdapter = new RestaurantAdapter(getActContext(), restaurantArr_List, false);
        Log.d("restaurantArr_List", "::" + restaurantArr_List);
        dataListRecyclerView.setAdapter(restaurantAdapter);

        dataListRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                                                     @Override
                                                     public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                                         super.onScrolled(recyclerView, dx, dy);

                                                         int visibleItemCount = recyclerView.getLayoutManager().getChildCount();
                                                         int totalItemCount = recyclerView.getLayoutManager().getItemCount();
                                                         int firstVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();

                                                         int lastInScreen = firstVisibleItemPosition + visibleItemCount;
                                                         if ((lastInScreen == totalItemCount) && !(mIsLoading) && isNextPageAvailable == true) {
                                                             mIsLoading = true;
                                                             restaurantAdapter.addFooterView();

                                                             getRestaurantListnew(filterLocation.getLatitude(), filterLocation.getLongitude(), true);
                                                         } else if (isNextPageAvailable == false) {
                                                             restaurantAdapter.removeFooterView();
                                                         }
                                                     }
                                                 }

        );

        restaurantAdapter.setOnRestaurantItemClick(this);

        //addDrawer.configDrawer(false);

        generateErrorView();

        //  testV();
        ResturentBannerdata();

    }

    //rest calls
    private void testV() {


        // fixme Api banner
        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://www.servo.com.ly/").addConverterFactory(GsonConverterFactory.create()).build();
        Call<BannerResponse> call = retrofit.create(ApiInterface.class).get_j_banners();
        call.enqueue(new Callback<BannerResponse>() {
            @Override
            public void onResponse(Call<BannerResponse> call, Response<BannerResponse> response) {

                if (response != null) {

                    assert response.body() != null;
                    if (response.body().getStatus().equals("success")) {


                        // fixme j banner data
                        //  getCallAdapter(response);
                    } else {
                    }

                } else {
                }
            }

            @Override
            public void onFailure(Call<BannerResponse> call, Throwable t) {
            }
        });

    }

    private void ResturentBannerdata() {


        // fixme Api banner
        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://www.servo.com.ly/").addConverterFactory(GsonConverterFactory.create()).build();
        Call<ResBannerResponce> call = retrofit.create(ApiStoreInterface.class).get_Resturent_banners();
        call.enqueue(new Callback<ResBannerResponce>() {
            @Override
            public void onResponse(Call<ResBannerResponce> call, Response<ResBannerResponce> response) {

                if (response != null) {

                    assert response.body() != null;
                    if (response.body().getStatus().equals("success")) {

                        // fixme j banner data
                        getCallAdapter(response);
                    } else {

                    }

                } else {

                }
            }

            @Override
            public void onFailure(Call<ResBannerResponce> call, Throwable t) {
                Log.d("&&&&&&&&&&&&", "err2::" + t);
            }


        });

    }

    public void getCallAdapter(Response<ResBannerResponce> response) {

        // fixme abubakar banner responce
        ResBannerResponce bannerResponse = response.body();
        List<RsBannnerModelClass> bannerData = bannerResponse.getData();


        if (generalFunc.retrieveLangLBl("Select", "LBL_SELECT_LANGUAGE_HINT_TXT").equalsIgnoreCase("اختار اللغة")) {

            j_banner_recycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));
        } else {
            j_banner_recycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        }
        j_banner_recycler.setAdapter(new ResturantBannerAdapter(FoodDeliveryHomeActivity.this, FoodDeliveryHomeActivity.this, bannerData, restaurantArr_List, this));
    }

    public void addRestTabs(ArrayList<HashMap<String, String>> list_item) {

        category_restaurant_tablayout.removeAllTabs();

        HashMap<String, String> check = new HashMap<>();


        TabLayout.Tab tb = category_restaurant_tablayout.newTab();
        tb.setCustomView(R.layout.restaurant_category);
        category_restaurant_tablayout.addTab(tb);

        for (int i = 0; i < list_item.size(); i++) {

            if (!check.containsValue(list_item.get(i).get("cuisineName"))) {

                check.put(list_item.get(i).get("cuisineName"), list_item.get(i).get("cuisineName"));

                TabLayout.Tab tab = category_restaurant_tablayout.newTab();

                HashMap map = list_item.get(i);

                tab.setCustomView(R.layout.restaurant_category);

                TextView textView = tab.getCustomView().findViewById(R.id.tv_rest_category);
                ImageView imageView = tab.getCustomView().findViewById(R.id.iv_category);

                textView.setText(map.get("cuisineName").toString());

                String vImage = "https://www.servo.com.ly/admin/images/" + map.get("image").toString();

                if (vImage != null && !vImage.trim().equals("")) {
                    Picasso.with(this)
                            .load(vImage).placeholder(R.mipmap.ic_no_icon).error(getResources().getDrawable(R.mipmap.ic_no_icon))
                            .into(imageView);
                } else {
                    imageView.setImageDrawable(getResources().getDrawable(R.mipmap.ic_no_icon));
                }

                category_restaurant_tablayout.addTab(tab);

            }
        }

        // category_restaurant_tablayout.getTabAt(0).select();

        category_restaurant_tablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                TextView textView = tab.getCustomView().findViewById(R.id.tv_rest_category);
                textView.getText().toString();
                ArrayList<HashMap<String, String>> tabList = new ArrayList<>();


                if (tab.getPosition() != 0) {
                    for (int i = 0; i < filterList.size(); i++) {
                        if (filterList.get(i).get("cuisineName").equalsIgnoreCase(textView.getText().toString())) {

                            // selectedFilterId=   tabList.add(filterList.get(i));
                            selectedFilterId = filterList.get(i).get("cuisineId");
                            //Toast.makeText(FoodDeliveryHomeActivity.this, ""+tabList.get(i).toString(), Toast.LENGTH_SHORT).show();
                        }
                    }


                    setTabSelection(tab.getPosition());
                } else {

                    selectedFilterId = "";
                    setTabSelection(tab.getPosition());
                    //  noOfServiceTxt.setText(""+realmCartList.size());
                    // noOfServiceTxt.setText(generalFunc.convertNumberWithRTL(numberOfRestaurant + "") + " " + LBL_RESTAURANTS);

                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        // setTabSelection(0);

    }

    private void setTabSelection(int selected) {

     /*   ArrayList<HashMap<String,String>> mapArrayList= new ArrayList<>();
        if (!cata.equalsIgnoreCase("all")){
        for (HashMap<String,String> e:restaurantArr_List) {

            if (e.get("Restaurant_Cuisine").equalsIgnoreCase(cata)){

                mapArrayList.add(e);
            }

        }
            restaurantAdapter.update(mapArrayList);

        }
        else {
            restaurantAdapter.update(restaurantArr_List);

        }*/

        // fixme abubakar set tab
        for (int i = 0; i < category_restaurant_tablayout.getTabCount(); i++) {
            category_restaurant_tablayout.getTabAt(i).getCustomView().findViewById(R.id.myvi).setVisibility
                    (i == selected ? View.VISIBLE : View.GONE);
        }


        isSecondTime = true;
        getRestaurantListnew(filterLocation.getLatitude(), filterLocation.getLongitude(), false);

        // faredialog.dismiss();
    }

    public void generateErrorView() {

        generalFunc.generateErrorView(errorView, "LBL_ERROR_TXT", "LBL_NO_INTERNET_TXT");


        if (internetConnection.isNetworkConnected()) {

            errorViewArea.setVisibility(View.GONE);
        } else {
            if (errorViewArea.getVisibility() != View.VISIBLE) {
                errorViewArea.setVisibility(View.VISIBLE);
            }
            errorView.setOnRetryListener(new ErrorView.RetryListener() {
                @Override
                public void onRetry() {
                    if (filterLocation != null) {
                        getRestaurantListnew(filterLocation.getLatitude(), filterLocation.getLongitude(), false);
                        getFilterList(filterLocation.getLatitude(), filterLocation.getLongitude());
                    }
                }
            });
        }
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        //The Refresh must be only active when the offset is zero :
        mSwipeRefreshLayout.setEnabled(i == 0);
    }

    public void setSortList() {
        HashMap<String, String> relevanceMap = new HashMap<>();
        relevanceMap.put("label", generalFunc.retrieveLangLBl("", "LBL_RELEVANCE"));
        relevanceMap.put("value", "relevance");

        HashMap<String, String> ratingMap = new HashMap<>();
        ratingMap.put("label", generalFunc.retrieveLangLBl("", "LBL_RATING"));
        ratingMap.put("value", "rating");

        HashMap<String, String> timeMap = new HashMap<>();
        timeMap.put("label", generalFunc.retrieveLangLBl("", "LBL_TIME"));
        timeMap.put("value", "time");

        sortby_List.add(relevanceMap);
        sortby_List.add(ratingMap);
        sortby_List.add(timeMap);
        if (generalFunc.getServiceId().equalsIgnoreCase("1") || generalFunc.getServiceId().equalsIgnoreCase("")) {

            HashMap<String, String> costlTohMap = new HashMap<>();
            costlTohMap.put("label", generalFunc.retrieveLangLBl("", "LBL_COST_LTOH"));
            costlTohMap.put("value", "costlth");

            HashMap<String, String> costhTolMap = new HashMap<>();
            costhTolMap.put("label", generalFunc.retrieveLangLBl("", "LBL_COST_HTOL"));
            costhTolMap.put("value", "costhtl");

            sortby_List.add(costlTohMap);
            sortby_List.add(costhTolMap);
        }
    }


    public boolean isPubNubEnabled() {
        String ENABLE_PUBNUB = generalFunc.getJsonValueStr("ENABLE_PUBNUB", obj_userProfile);

        return ENABLE_PUBNUB.equalsIgnoreCase("Yes");

    }

    public void NoLocationView() {

//        if (!generalFunc.isLocationEnabled()) {
//            noloactionview.setVisibility(View.VISIBLE);
//
//        } else {
//            if (getLastLocation != null) {
//                getLastLocation.stopLocationUpdates();
//                getLastLocation = null;
//            }
//            getLastLocation = new GetLocationUpdates(getActContext(), Utils.LOCATION_UPDATE_MIN_DISTANCE_IN_MITERS, false, this);
//
//            noloactionview.setVisibility(View.GONE);
//        }


    }


    private void setData() {
        headerLocAddressTxt.setHint(generalFunc.retrieveLangLBl("Enter Location...", "LBL_ENTER_LOC_HINT_TXT"));
        LocStaticTxt.setText(generalFunc.retrieveLangLBl("Location For availing Service", "LBL_LOCATION_FOR_AVAILING_TXT"));
        noOfServiceTxt.setText(generalFunc.retrieveLangLBl("Select Service", "LBL_SELECT_SERVICE_TXT"));

        noLocTitleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_LOCATION_CHANGE_TITLE"));
        noLocMsgTxt.setText(generalFunc.retrieveLangLBl("", "LBL_LOCATION_CHANGE_NOTE"));
        noThanksBtn.setText(generalFunc.retrieveLangLBl("", "LBL_NO_THANKS"));
        changeLocBtn.setText(generalFunc.retrieveLangLBl("", "LBL_CHANGE_LOCATION"));
        editLocationBtn.setText(generalFunc.retrieveLangLBl("", "LBL_EDIT_LOCATION"));
        outAreaTitle.setText(generalFunc.retrieveLangLBl("", "LBL_OUT_OF_DELIVERY_AREA"));
        deliveryAreaTxt.setText(generalFunc.retrieveLangLBl("", "LBL_DELIVERY_AREA_NOTE"));
        filterTxtView.setText(generalFunc.retrieveLangLBl("", "LBL_FILTER"));

    }

    public void getBanners() {

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "getBanners");
        parameters.put("iMemberId", generalFunc.getMemberId());
        parameters.put("eSystem", Utils.eSystem_Type);

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(String responseString) {

                if (responseString != null && !responseString.equals("")) {

//                    st.toast("get banners response not null");

                    boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);
                    if (isDataAvail) {
                        JSONArray arr = generalFunc.getJsonArray(Utils.message_str, responseString);

                        if (arr != null) {

                            ArrayList<String> imagesList = new ArrayList<String>();
                            for (int i = 0; i < arr.length(); i++) {
//                            JSONObject obj_temp = generalFunc.getJsonObject(arr, i);
//                            imagesList.add(generalFunc.getJsonValue("vImage", obj_temp.toString()));

                                JSONObject obj_temp = generalFunc.getJsonObject(arr, i);

                                String vImage = generalFunc.getJsonValueStr("vImage", obj_temp);

                                String imageURL = Utils.getResizeImgURL(getActContext(), vImage, Utils.getWidthOfBanner(getActContext(), 0), Utils.getHeightOfBanner(getActContext(), 0, "16:9"));

                                imagesList.add(imageURL);
                            }

                            FoodDeliveryHomeAdapter bannerAdapter = new FoodDeliveryHomeAdapter(getActContext(), imagesList);
                            bannerViewPager.setAdapter(bannerAdapter);
                            FoodDeliveryHomeActivity.this.bannerAdapter = bannerAdapter;
                            bannerCirclePageIndicator.setDataSize(imagesList.size());
                            bannerCirclePageIndicator.setViewPager(bannerViewPager);

//                            st.toast("Size: " + imagesList.size());

                            if (imagesList.size() > 1) {
                                bannerCirclePageIndicator.setVisibility(View.VISIBLE);
                                updateBannerChangeFreqTask = new UpdateFrequentTask(BANNER_AUTO_ROTATE_INTERVAL);
                                updateBannerChangeFreqTask.setTaskRunListener(FoodDeliveryHomeActivity.this);
                                updateBannerChangeFreqTask.avoidFirstRun();
                                updateBannerChangeFreqTask.startRepeatingTask();
                            } else {
                                bannerCirclePageIndicator.setVisibility(View.GONE);
//                                st.toast("bannerCirclePageIndicator hide");
                            }

                        }
                    }
                } else {
//                    st.toast("get banners response not avaialble");
//                    generalFunc.showError();
                }
            }
        });
        exeWebServer.execute();
    }

    public Context getActContext() {
        return FoodDeliveryHomeActivity.this;
    }


    public void getFilterList(final double latitude, final double longitude) {

        Logger.d("TRACKgetFilterList", "getFilterList:1::" + System.currentTimeMillis());

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "getCuisineList");
        parameters.put("PassengerLat", "" + latitude);
        parameters.put("PassengerLon", "" + longitude);
        parameters.put("eSystem", Utils.eSystem_Type);
        Logger.d("TRACKgetFilterList", "getFilterList:2::" + System.currentTimeMillis());

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            // fixme abubakarr chk respoance
            public void setResponse(String responseString) {

                filterList.clear();
                JSONObject responseObj = generalFunc.getJsonObject(responseString);

                Logger.d("TRACKgetFilterList", "getFilterList:3::" + System.currentTimeMillis());


                Log.d("testfilterlist", "responseObj::" + responseString);

                if (responseObj != null && !responseObj.equals("")) {
                    boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseObj);

                    if (isDataAvail == true) {

                        isOfferApply = generalFunc.getJsonValueStr("isOfferApply", responseObj);
                        JSONArray restaurant_Arr = generalFunc.getJsonArray("CuisineList", responseObj);
                        if (restaurant_Arr != null) {
                            for (int i = 0; i < restaurant_Arr.length(); i++) {
                                HashMap<String, String> map = new HashMap<String, String>();

                                JSONObject filter_Obj = generalFunc.getJsonObject(restaurant_Arr, i);
                                map.put("cuisineId", generalFunc.getJsonValueStr("cuisineId", filter_Obj));
                                map.put("cuisineName", generalFunc.getJsonValueStr("cuisineName", filter_Obj));
                                map.put("eStatus", generalFunc.getJsonValueStr("eStatus", filter_Obj));
                                map.put("image", generalFunc.getJsonValueStr("image", filter_Obj));
                                Logger.d("TRACKgetFilterList", "getFilterList:4::" + System.currentTimeMillis());

                                filterList.add(map);
                            }
                            addRestTabs(filterList);
                            Log.d("testfilterlist", "filterList::" + filterList);

//                            filterTxtView.setVisibility(View.VISIBLE);
                        } else {
                            filterTxtView.setVisibility(View.GONE);
                        }
                    }
                }
            }
        });
        exeWebServer.execute();


    }

    private void getRestaurantListnew(final double latitude, final double longitude, boolean isLoadMore) {


        MyProgressDialog myProgressDialog = new MyProgressDialog(getActContext(), false, "Loading");
        if (isSecondTime) {

            myProgressDialog.show();
            isSecondTime= false;
        }

        Logger.d("TRACK", "1::" + System.currentTimeMillis());
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "loadAvailableRestaurantsNew");
        parameters.put("PassengerLat", "" + latitude);
        parameters.put("PassengerLon", "" + longitude);
        parameters.put("fOfferType", isOfferCheck);
        parameters.put("eFavStore", isFavCheck);
        parameters.put("iUserId", generalFunc.getMemberId());
        parameters.put("vLang", generalFunc.retrieveValue(Utils.LANGUAGE_CODE_KEY));

        if (headerTxt != null && headerTxt.getText() != null && !headerTxt.getText().toString().equals("")) {
            parameters.put("vAddress", headerAddress);
        }
        if (selectedFilterId == null) {
            selectedFilterId = "";
        }
        //do by abubakar call api
        parameters.put("cuisineId", selectedFilterId);
        parameters.put("sortby", selectedSort);

        if (isLoadMore == true) {
            parameters.put("page",""+restaurantArr_List.size());
        }
        parameters.put("eSystem", Utils.eSystem_Type);


        Log.d("OpenCatType", "parametersFoodDev::" + parameters);


        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(String responseString) {

                generateErrorView();
                Logger.d("TRACK", "2::" + System.currentTimeMillis());
                JSONObject responseObj = generalFunc.getJsonObject(responseString);
                Logger.d("TRACK", "3::" + System.currentTimeMillis());
                Log.d("restaurant_Arr123456", "" + responseString);

                if (responseObj != null && !responseObj.equals("")) {

                    boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseObj);

                   // String nextPage = generalFunc.getJsonValueStr("NextPage", responseObj);
                    String totalPag = generalFunc.getJsonValueStr("totalPages", responseObj);


                    String LBL_RESTAURANTS = "";
                    LBL_RESTAURANTS = generalFunc.retrieveLangLBl("", "LBL_RESTAURANTS");

                    Log.d("LBL_RESTAURANTS", "LBL_RESTAURANTS::" + LBL_RESTAURANTS);

                    if (isDataAvail == true) {
                        NoDataTxt.setVisibility(View.GONE);
                        if (!isLoadMore) {
                            restaurantArr_List.clear();
                            numberOfRestaurant = 0;
                        }
                        dataListRecyclerView.setVisibility(View.VISIBLE);

                        JSONArray restaurant_Arr = generalFunc.getJsonArray("message", responseObj);

                        Log.d("restaurant_Arr12345", "" + restaurant_Arr);

                        Logger.d("TRACK", "4::" + System.currentTimeMillis());

                        if (restaurant_Arr != null && restaurant_Arr.length() > 0) {
                            String LBL_OPEN_NOW = "", LBL_CLOSED_TXT = "", LBL_NOT_ACCEPT_ORDERS_TXT = "";
                            int dimension = 0;
                            Logger.d("TRACK", "5::" + System.currentTimeMillis());
                            if (restaurant_Arr.length() > 0) {
                                dimension = getActContext().getResources().getDimensionPixelSize(R.dimen.restaurant_img_size_home_screen);
                                LBL_OPEN_NOW = generalFunc.retrieveLangLBl("Open Now", "LBL_OPEN_NOW");
                                LBL_CLOSED_TXT = generalFunc.retrieveLangLBl("close", "LBL_CLOSED_TXT");
                                LBL_NOT_ACCEPT_ORDERS_TXT = generalFunc.retrieveLangLBl("close", "LBL_NOT_ACCEPT_ORDERS_TXT");
                            }
                            Logger.d("TRACK", "6::" + System.currentTimeMillis());


                            for (int i = 0; i < restaurant_Arr.length(); i++) {

                                HashMap<String, String> map = new HashMap<String, String>();
                                JSONObject restaurant_Obj = generalFunc.getJsonObject(restaurant_Arr, i);
                                map.put("vCompany", generalFunc.getJsonValueStr("vCompany", restaurant_Obj));
                                map.put("tClocation", generalFunc.getJsonValueStr("tClocation", restaurant_Obj));
                                map.put("iCompanyId", generalFunc.getJsonValueStr("iCompanyId", restaurant_Obj));
                                map.put("vPhone", generalFunc.getJsonValueStr("vPhone", restaurant_Obj));
                                String vImage = generalFunc.getJsonValueStr("vImage", restaurant_Obj);
                                map.put("vImage", vImage);
                                map.put("vImage", Utils.getResizeImgURL(getActContext(), vImage, dimension, dimension));

                                map.put("vLatitude", generalFunc.getJsonValueStr("vLatitude", restaurant_Obj));
                                map.put("vLongitude", generalFunc.getJsonValueStr("vLongitude", restaurant_Obj));

                                map.put("vFromTimeSlot1", generalFunc.getJsonValueStr("vFromTimeSlot1", restaurant_Obj));
                                map.put("vToTimeSlot1", generalFunc.getJsonValueStr("vToTimeSlot1", restaurant_Obj));
                                map.put("vFromTimeSlot2", generalFunc.getJsonValueStr("vFromTimeSlot2", restaurant_Obj));
                                map.put("vToTimeSlot2", generalFunc.getJsonValueStr("vToTimeSlot2", restaurant_Obj));
                                map.put("fMinOrderValue", generalFunc.getJsonValueStr("fMinOrderValue", restaurant_Obj));
                                map.put("Restaurant_Cuisine", generalFunc.getJsonValueStr("Restaurant_Cuisine", restaurant_Obj));
                                map.put("fPrepareTime", generalFunc.getJsonValueStr("fPrepareTime", restaurant_Obj));

                                String Restaurant_PricePerPerson = generalFunc.getJsonValueStr("Restaurant_PricePerPerson", restaurant_Obj);
                                map.put("Restaurant_PricePerPerson", Restaurant_PricePerPerson);
                                map.put("Restaurant_PricePerPersonConverted", generalFunc.convertNumberWithRTL(Restaurant_PricePerPerson));

                                String Restaurant_OrderPrepareTime = generalFunc.getJsonValueStr("Restaurant_OrderPrepareTime", restaurant_Obj);
                                map.put("Restaurant_OrderPrepareTime", Restaurant_OrderPrepareTime);
                                map.put("Restaurant_OrderPrepareTimeConverted", generalFunc.convertNumberWithRTL(Restaurant_OrderPrepareTime));

                                map.put("Restaurant_Status", generalFunc.getJsonValueStr("restaurantstatus", restaurant_Obj));

                                String Restaurant_Opentime = generalFunc.getJsonValueStr("Restaurant_Opentime", restaurant_Obj);
                                map.put("Restaurant_Opentime", Restaurant_Opentime);
                                map.put("Restaurant_OpentimeConverted", generalFunc.convertNumberWithRTL(Restaurant_Opentime));

                                map.put("Restaurant_Closetime", generalFunc.getJsonValueStr("Restaurant_Closetime", restaurant_Obj));
                                map.put("Restaurant_OfferMessage", generalFunc.getJsonValueStr("Restaurant_OfferMessage", restaurant_Obj));

                                String Restaurant_OfferMessage_short = generalFunc.getJsonValueStr("Restaurant_OfferMessage_short", restaurant_Obj);
                                map.put("Restaurant_OfferMessage_short", Restaurant_OfferMessage_short);
                                map.put("Restaurant_OfferMessage_shortConverted", generalFunc.convertNumberWithRTL(Restaurant_OfferMessage_short));


                                String vAvgRating = generalFunc.getJsonValueStr("vAvgRating", restaurant_Obj);
                                map.put("vAvgRating", vAvgRating);


                                map.put("vAvgRatingConverted", generalFunc.convertNumberWithRTL(vAvgRating));

                                String Restaurant_MinOrderValue = generalFunc.getJsonValueStr("Restaurant_MinOrderValue", restaurant_Obj);
                                map.put("Restaurant_MinOrderValue", Restaurant_MinOrderValue);
                                map.put("Restaurant_MinOrderValueConverted", generalFunc.convertNumberWithRTL(Restaurant_MinOrderValue));

                                map.put("eAvailable", generalFunc.getJsonValueStr("eAvailable", restaurant_Obj));
                                map.put("timeslotavailable", generalFunc.getJsonValueStr("timeslotavailable", restaurant_Obj));
                                map.put("ispriceshow", generalFunc.getJsonValueStr("ispriceshow", responseObj));
                                map.put("eFavStore", generalFunc.getJsonValueStr("eFavStore", restaurant_Obj));
                                map.put("deliveryPrice", generalFunc.getJsonValueStr("deliveryPrice", restaurant_Obj));


                                Log.d("deliveryPricede", "" + generalFunc.getJsonValueStr("deliveryPrice", restaurant_Obj));

                                map.put("LBL_OPEN_NOW", LBL_OPEN_NOW);
                                map.put("LBL_CLOSED_TXT", LBL_CLOSED_TXT);
                                map.put("LBL_NOT_ACCEPT_ORDERS_TXT", LBL_NOT_ACCEPT_ORDERS_TXT);

                                numberOfRestaurant += 1;
                                restaurantArr_List.add(map);

                                Log.d("khanburger", "name::" + map);
                            }

                        }
                        Logger.d("TRACK", "7::" + System.currentTimeMillis());

                        isSecondTime= false;

                        generalFunc.storeData("PassengerLat", latitude + "");
                        generalFunc.storeData("PassengerLon", longitude + "");
                        restaurantAdapter.notifyDataSetChanged();

                        Log.d("LBL_RESTAURANTS", "restaurant_Arr::" + restaurant_Arr.length());
                        Log.d("LBL_RESTAURANTS", "numberOfRestaurant::" + numberOfRestaurant);

                        if (restaurant_Arr != null && restaurant_Arr.length() > 0) {
                            myProgressDialog.close();
                            noOfServiceTxt.setText(generalFunc.convertNumberWithRTL(numberOfRestaurant + "") + " " + LBL_RESTAURANTS);
                        } else {
                            myProgressDialog.close();
                            numberOfRestaurant = 0;
                            noOfServiceTxt.setText(generalFunc.convertNumberWithRTL(numberOfRestaurant + "") + " " + LBL_RESTAURANTS);
                        }


                        if (restaurantArr_List.size() > 0) {
                            noloactionview.setVisibility(View.GONE);
//                            menuSearchView.setVisibility(View.VISIBLE);
                        }


                        /*if (!nextPage.equals("") && !nextPage.equals("0")) {
                            next_page_str = nextPage;
                            isNextPageAvailable = true;
                        }*/
                        if (restaurantArr_List.size()+1<Integer.parseInt(totalPag)) {
                        //if (restaurantArr_List.size()+1<44) {
                            //next_page_str = nextPage;
                            isNextPageAvailable = true;
                        }
                        else {
                            removeNextPageConfig();
                        }
                        mIsLoading = false;

                    } else {

                        String msg_str = generalFunc.getJsonValue("message", responseString);

                        if (!isLoadMore && msg_str.equalsIgnoreCase("LBL_NO_RESTAURANT_FOUND_TXT")) {
                            restaurantArr_List.clear();
                        }


                        NoDataTxt.setVisibility(View.GONE);
                        if (restaurantArr_List.size() == 0) {

                            if (!generalFunc.getJsonValue("message1", responseString).equalsIgnoreCase("")) {
                                dataListRecyclerView.setVisibility(View.GONE);

                                //  NoDataTxt.setVisibility(View.VISIBLE);
                                NoDataTxt.setText(generalFunc.retrieveLangLBl("", generalFunc.getJsonValue("message1", responseString)));

                            } else if (filterImage.getVisibility() == View.GONE) {
                                noloactionview.setVisibility(View.VISIBLE);
                                menuSearchView.setVisibility(View.GONE);
                            }

                            numberOfRestaurant = 0;
                            myProgressDialog.close();

                            noOfServiceTxt.setText(generalFunc.convertNumberWithRTL(numberOfRestaurant + "") + " " + LBL_RESTAURANTS);
                            restaurantArr_List.clear();


                        }

                        restaurantAdapter.notifyDataSetChanged();

                    }

                } else {
                    generalFunc.showError();
                }
            }
        });
        exeWebServer.execute();
    }
    private void getRestaurantListnew1 (final double latitude, final double longitude, boolean isLoadMore) {


        MyProgressDialog myProgressDialog = new MyProgressDialog(getActContext(), false, "Loading");
        if (isSecondTime) {

            myProgressDialog.show();
            isSecondTime= false;
        }

        Logger.d("TRACK", "1::" + System.currentTimeMillis());
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "loadAvailableRestaurantsNew");
        parameters.put("PassengerLat", "" + latitude);
        parameters.put("PassengerLon", "" + longitude);
        parameters.put("fOfferType", isOfferCheck);
        parameters.put("eFavStore", isFavCheck);
        parameters.put("iUserId", generalFunc.getMemberId());
        parameters.put("vLang", generalFunc.retrieveValue(Utils.LANGUAGE_CODE_KEY));

        if (headerTxt != null && headerTxt.getText() != null && !headerTxt.getText().toString().equals("")) {
            parameters.put("vAddress", headerAddress);
        }
        if (selectedFilterId == null) {
            selectedFilterId = "";
        }
        //do by abubakar call api
        parameters.put("cuisineId", selectedFilterId);
        parameters.put("sortby", selectedSort);

        if (isLoadMore == true) {
            parameters.put("page", ""+restaurantArr_List.size());
        }
        parameters.put("eSystem", Utils.eSystem_Type);


        Log.d("OpenCatType", "parametersFoodDev::" + parameters);


        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(String responseString) {

                generateErrorView();
                Logger.d("TRACK", "2::" + System.currentTimeMillis());
                JSONObject responseObj = generalFunc.getJsonObject(responseString);
                Logger.d("TRACK", "3::" + System.currentTimeMillis());


                Log.d("restaurant_Arr123456", "" + responseString);


                if (responseObj != null && !responseObj.equals("")) {

                    boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseObj);

                    String totalPag = generalFunc.getJsonValueStr("totalPages", responseObj);

                    String LBL_RESTAURANTS = "";
                    LBL_RESTAURANTS = generalFunc.retrieveLangLBl("", "LBL_RESTAURANTS");

                    Log.d("LBL_RESTAURANTS", "LBL_RESTAURANTS::" + LBL_RESTAURANTS);

                    if (isDataAvail == true) {
                        NoDataTxt.setVisibility(View.GONE);
                        if (!isLoadMore) {
                            restaurantArr_List.clear();
                            numberOfRestaurant = 0;
                        }
                        dataListRecyclerView.setVisibility(View.VISIBLE);

                        JSONArray restaurant_Arr = generalFunc.getJsonArray("message", responseObj);

                        Log.d("restaurant_Arr12345", "" + restaurant_Arr);

                        Logger.d("TRACK", "4::" + System.currentTimeMillis());

                        if (restaurant_Arr != null && restaurant_Arr.length() > 0) {
                            String LBL_OPEN_NOW = "", LBL_CLOSED_TXT = "", LBL_NOT_ACCEPT_ORDERS_TXT = "";
                            int dimension = 0;
                            Logger.d("TRACK", "5::" + System.currentTimeMillis());
                            if (restaurant_Arr.length() > 0) {
                                dimension = getActContext().getResources().getDimensionPixelSize(R.dimen.restaurant_img_size_home_screen);
                                LBL_OPEN_NOW = generalFunc.retrieveLangLBl("Open Now", "LBL_OPEN_NOW");
                                LBL_CLOSED_TXT = generalFunc.retrieveLangLBl("close", "LBL_CLOSED_TXT");
                                LBL_NOT_ACCEPT_ORDERS_TXT = generalFunc.retrieveLangLBl("close", "LBL_NOT_ACCEPT_ORDERS_TXT");
                            }
                            Logger.d("TRACK", "6::" + System.currentTimeMillis());


                            for (int i = 0; i < restaurant_Arr.length(); i++) {

                                HashMap<String, String> map = new HashMap<String, String>();
                                JSONObject restaurant_Obj = generalFunc.getJsonObject(restaurant_Arr, i);
                                map.put("vCompany", generalFunc.getJsonValueStr("vCompany", restaurant_Obj));
                                map.put("tClocation", generalFunc.getJsonValueStr("tClocation", restaurant_Obj));
                                map.put("iCompanyId", generalFunc.getJsonValueStr("iCompanyId", restaurant_Obj));
                                map.put("vPhone", generalFunc.getJsonValueStr("vPhone", restaurant_Obj));
                                String vImage = generalFunc.getJsonValueStr("vImage", restaurant_Obj);
                                map.put("vImage", vImage);
                                map.put("vImage", Utils.getResizeImgURL(getActContext(), vImage, dimension, dimension));

                                map.put("vLatitude", generalFunc.getJsonValueStr("vLatitude", restaurant_Obj));
                                map.put("vLongitude", generalFunc.getJsonValueStr("vLongitude", restaurant_Obj));

                                map.put("vFromTimeSlot1", generalFunc.getJsonValueStr("vFromTimeSlot1", restaurant_Obj));
                                map.put("vToTimeSlot1", generalFunc.getJsonValueStr("vToTimeSlot1", restaurant_Obj));
                                map.put("vFromTimeSlot2", generalFunc.getJsonValueStr("vFromTimeSlot2", restaurant_Obj));
                                map.put("vToTimeSlot2", generalFunc.getJsonValueStr("vToTimeSlot2", restaurant_Obj));
                                map.put("fMinOrderValue", generalFunc.getJsonValueStr("fMinOrderValue", restaurant_Obj));
                                map.put("Restaurant_Cuisine", generalFunc.getJsonValueStr("Restaurant_Cuisine", restaurant_Obj));
                                map.put("fPrepareTime", generalFunc.getJsonValueStr("fPrepareTime", restaurant_Obj));

                                String Restaurant_PricePerPerson = generalFunc.getJsonValueStr("Restaurant_PricePerPerson", restaurant_Obj);
                                map.put("Restaurant_PricePerPerson", Restaurant_PricePerPerson);
                                map.put("Restaurant_PricePerPersonConverted", generalFunc.convertNumberWithRTL(Restaurant_PricePerPerson));

                                String Restaurant_OrderPrepareTime = generalFunc.getJsonValueStr("Restaurant_OrderPrepareTime", restaurant_Obj);
                                map.put("Restaurant_OrderPrepareTime", Restaurant_OrderPrepareTime);
                                map.put("Restaurant_OrderPrepareTimeConverted", generalFunc.convertNumberWithRTL(Restaurant_OrderPrepareTime));

                                map.put("Restaurant_Status", generalFunc.getJsonValueStr("restaurantstatus", restaurant_Obj));

                                String Restaurant_Opentime = generalFunc.getJsonValueStr("Restaurant_Opentime", restaurant_Obj);
                                map.put("Restaurant_Opentime", Restaurant_Opentime);
                                map.put("Restaurant_OpentimeConverted", generalFunc.convertNumberWithRTL(Restaurant_Opentime));

                                map.put("Restaurant_Closetime", generalFunc.getJsonValueStr("Restaurant_Closetime", restaurant_Obj));
                                map.put("Restaurant_OfferMessage", generalFunc.getJsonValueStr("Restaurant_OfferMessage", restaurant_Obj));

                                String Restaurant_OfferMessage_short = generalFunc.getJsonValueStr("Restaurant_OfferMessage_short", restaurant_Obj);
                                map.put("Restaurant_OfferMessage_short", Restaurant_OfferMessage_short);
                                map.put("Restaurant_OfferMessage_shortConverted", generalFunc.convertNumberWithRTL(Restaurant_OfferMessage_short));


                                String vAvgRating = generalFunc.getJsonValueStr("vAvgRating", restaurant_Obj);
                                map.put("vAvgRating", vAvgRating);


                                map.put("vAvgRatingConverted", generalFunc.convertNumberWithRTL(vAvgRating));

                                String Restaurant_MinOrderValue = generalFunc.getJsonValueStr("Restaurant_MinOrderValue", restaurant_Obj);
                                map.put("Restaurant_MinOrderValue", Restaurant_MinOrderValue);
                                map.put("Restaurant_MinOrderValueConverted", generalFunc.convertNumberWithRTL(Restaurant_MinOrderValue));

                                map.put("eAvailable", generalFunc.getJsonValueStr("eAvailable", restaurant_Obj));
                                map.put("timeslotavailable", generalFunc.getJsonValueStr("timeslotavailable", restaurant_Obj));
                                map.put("ispriceshow", generalFunc.getJsonValueStr("ispriceshow", responseObj));
                                map.put("eFavStore", generalFunc.getJsonValueStr("eFavStore", restaurant_Obj));
                                map.put("deliveryPrice", generalFunc.getJsonValueStr("deliveryPrice", restaurant_Obj));


                                Log.d("deliveryPricede", "" + generalFunc.getJsonValueStr("deliveryPrice", restaurant_Obj));

                                map.put("LBL_OPEN_NOW", LBL_OPEN_NOW);
                                map.put("LBL_CLOSED_TXT", LBL_CLOSED_TXT);
                                map.put("LBL_NOT_ACCEPT_ORDERS_TXT", LBL_NOT_ACCEPT_ORDERS_TXT);

                                numberOfRestaurant += 1;
                                restaurantArr_List.add(map);

                                Log.d("khanburger", "name::" + map);
                            }

                        }
                        Logger.d("TRACK", "7::" + System.currentTimeMillis());

                        isSecondTime= false;

                        generalFunc.storeData("PassengerLat", latitude + "");
                        generalFunc.storeData("PassengerLon", longitude + "");
                        restaurantAdapter.notifyDataSetChanged();

                        Log.d("LBL_RESTAURANTS", "restaurant_Arr::" + restaurant_Arr.length());
                        Log.d("LBL_RESTAURANTS", "numberOfRestaurant::" + numberOfRestaurant);

                        if (restaurant_Arr != null && restaurant_Arr.length() > 0) {
                            myProgressDialog.close();
                            noOfServiceTxt.setText(generalFunc.convertNumberWithRTL(numberOfRestaurant + "") + " " + LBL_RESTAURANTS);
                        } else {
                            myProgressDialog.close();
                            numberOfRestaurant = 0;
                            noOfServiceTxt.setText(generalFunc.convertNumberWithRTL(numberOfRestaurant + "") + " " + LBL_RESTAURANTS);
                        }


                        if (restaurantArr_List.size() > 0) {
                            noloactionview.setVisibility(View.GONE);
//                            menuSearchView.setVisibility(View.VISIBLE);
                        }


                     /*   if (!nextPage.equals("") && !nextPage.equals("0")) {
                            next_page_str = nextPage;
                            isNextPageAvailable = true;*/
                        //}

                        Log.d("sizeeeeeeee","total::"+Integer.parseInt(totalPag)+"size::"+restaurantArr_List.size());
                 //   if (Integer.parseInt(totalPag)>restaurantArr_List.size()+2) {
                    if (restaurantArr_List.size()+1<Integer.parseInt(totalPag)) {
                            //next_page_str = nextPage;
                            isNextPageAvailable = true;
                        } else {
                            removeNextPageConfig();
                        }
                        mIsLoading = false;

                    } else {

                        String msg_str = generalFunc.getJsonValue("message", responseString);

                        if (!isLoadMore && msg_str.equalsIgnoreCase("LBL_NO_RESTAURANT_FOUND_TXT")) {
                            restaurantArr_List.clear();
                        }


                        NoDataTxt.setVisibility(View.GONE);
                        if (restaurantArr_List.size() == 0) {

                            if (!generalFunc.getJsonValue("message1", responseString).equalsIgnoreCase("")) {
                                dataListRecyclerView.setVisibility(View.GONE);

                                //  NoDataTxt.setVisibility(View.VISIBLE);
                                NoDataTxt.setText(generalFunc.retrieveLangLBl("", generalFunc.getJsonValue("message1", responseString)));

                            } else if (filterImage.getVisibility() == View.GONE) {
                                noloactionview.setVisibility(View.VISIBLE);
                                menuSearchView.setVisibility(View.GONE);
                            }

                            numberOfRestaurant = 0;
                            myProgressDialog.close();

                            noOfServiceTxt.setText(generalFunc.convertNumberWithRTL(numberOfRestaurant + "") + " " + LBL_RESTAURANTS);
                            restaurantArr_List.clear();


                        }

                        restaurantAdapter.notifyDataSetChanged();

                    }

                } else {
                    generalFunc.showError();
                }
            }
        });
        exeWebServer.execute();
    }

    public void removeNextPageConfig() {
        next_page_str = "";
        isNextPageAvailable = false;
        mIsLoading = false;
        restaurantAdapter.removeFooterView();
    }

    public RealmResults<Cart> getCartData() {
        try {
            Realm realm = MyApp.getRealmInstance();
            return realm.where(Cart.class).findAll();
        } catch (Exception e) {

        }
        return null;
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void onResume() {
        super.onResume();
        try {

            app_bar_layout.addOnOffsetChangedListener(this);

            realmCartList = getCartData();

            if (realmCartList != null && realmCartList.size() > 0) {
                fabcartIcon.setVisibility(View.VISIBLE);
            } else {
                fabcartIcon.setVisibility(View.GONE);
            }
            if (!isUfxaddress) {
                NoLocationView();
            }

            if (updateBannerChangeFreqTask != null) {
                updateBannerChangeFreqTask.startRepeatingTask();
            }

            if (generalFunc.retrieveValue(Utils.ISWALLETBALNCECHANGE).equalsIgnoreCase("Yes")) {
                getWalletBalDetails();
            } else {
                obj_userProfile = generalFunc.getJsonObject(generalFunc.retrieveValue(Utils.USER_PROFILE_JSON));
                setUserInfo();
            }


            if (addDrawer != null) {
                obj_userProfile = generalFunc.getJsonObject(generalFunc.retrieveValue(Utils.USER_PROFILE_JSON));
                addDrawer.Managemenu();
            }

            if (fabcartIcon != null && fabcartIcon.getVisibility() == View.VISIBLE) {
                if (realmCartList != null) {
                    int cnt = 0;
                    for (int i = 0; i < realmCartList.size(); i++) {
                        cnt = cnt + GeneralFunctions.parseIntegerValue(0, realmCartList.get(i).getQty());
                    }

                    fabcartIcon.setCount(cnt);

                }

            }
        } catch (Exception e) {
        }
    }

    @Override
    public void onTaskRun() {

        if (currentBannerPosition < (bannerAdapter.getCount() - 1)) {
            bannerViewPager.setCurrentItem((bannerViewPager.getCurrentItem() + 1), true);
        } else {
            bannerViewPager.setCurrentItem(0, true);

        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        app_bar_layout.removeOnOffsetChangedListener(this);
        if (updateBannerChangeFreqTask != null) {
            updateBannerChangeFreqTask.stopRepeatingTask();
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        currentBannerPosition = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    public void getWalletBalDetails() {


        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "GetMemberWalletBalance");
        parameters.put("iUserId", generalFunc.getMemberId());
        parameters.put("UserType", Utils.app_type);
        parameters.put("eSystem", Utils.eSystem_Type);

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(final String responseString) {

                if (responseString != null && !responseString.equals("")) {

                    boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

                    if (isDataAvail) {
                        try {
                            generalFunc.storeData(Utils.ISWALLETBALNCECHANGE, "No");
                            String userProfileJson = generalFunc.retrieveValue(Utils.USER_PROFILE_JSON);
                            JSONObject object = generalFunc.getJsonObject(userProfileJson);
                            object.put("user_available_balance", generalFunc.getJsonValue("MemberBalance", responseString));
                            generalFunc.storeData(Utils.USER_PROFILE_JSON, object.toString());

                            userProfileJson = generalFunc.retrieveValue(Utils.USER_PROFILE_JSON);
                            obj_userProfile = generalFunc.getJsonObject(userProfileJson);


                            setUserInfo();
                        } catch (Exception e) {

                        }

                    }


                }
            }
        });
        exeWebServer.execute();
    }


    public void setUserInfo() {
        View view = ((Activity) getActContext()).findViewById(android.R.id.content);
        ((TextView) view.findViewById(R.id.userNameTxt)).setText(generalFunc.getJsonValueStr("vName", obj_userProfile) + " "
                + generalFunc.getJsonValueStr("vLastName", obj_userProfile));
        ((TextView) view.findViewById(R.id.walletbalncetxt)).setText(generalFunc.retrieveLangLBl("", "LBL_WALLET_BALANCE") + ": " + generalFunc.convertNumberWithRTL(generalFunc.getJsonValueStr("user_available_balance", obj_userProfile)));

        (new AppFunctions(getActContext())).checkProfileImage((SelectableRoundedImageView) view.findViewById(R.id.userImgView), obj_userProfile.toString(), "vImgName");
    }

    public void setHeaderTxt(String address) {
        headerAddress = address;
        int textSize1 = getResources().getDimensionPixelSize(R.dimen.txt_size_12);
        int textSize2 = getResources().getDimensionPixelSize(R.dimen.txt_size_14);
        String text1 = generalFunc.retrieveLangLBl("", "LBL_LOCATION_FOR_AVAILING_TXT");
        String text2 = address;

        SpannableString span1 = new SpannableString(text1);
        span1.setSpan(new AbsoluteSizeSpan(textSize1), 0, text1.length(), SPAN_INCLUSIVE_INCLUSIVE);

        SpannableString span2 = new SpannableString(text2);
        span2.setSpan(new AbsoluteSizeSpan(textSize2), 0, text2.length(), SPAN_INCLUSIVE_INCLUSIVE);

// let's put both spans together with a separator and all
        CharSequence finalText = TextUtils.concat(span1, "\n", span2);
        headerTxt.setText(finalText);

    }

    @Override
    public void onAddressFound(String address, double latitude, double longitude, String geocodeobject) {

        if (address != null && !address.equals("")) {
            this.latitude = latitude + "";
            this.longitude = longitude + "";
            headerLocAddressTxt.setText(address);


            //headerTxt.setText(address);
            setHeaderTxt(address);

            //  if (generalFunc.getJsonValue("UserSelectedAddress", userProfileJson).equalsIgnoreCase("")) {
            HashMap<String, String> storeData = new HashMap<>();
            storeData.put(Utils.SELECT_ADDRESSS, address);
            storeData.put(Utils.SELECT_LATITUDE, latitude + "");
            storeData.put(Utils.SELECT_LONGITUDE, longitude + "");

            storeData.put(Utils.CURRENT_ADDRESSS, address);
            storeData.put(Utils.CURRENT_LATITUDE, latitude + "");
            storeData.put(Utils.CURRENT_LONGITUDE, longitude + "");
            generalFunc.storeData(storeData);
//            } else {
//                generalFunc.storeData(Utils.SELECT_ADDRESSS, generalFunc.getJsonValue("UserSelectedAddress", userProfileJson));
//                generalFunc.storeData(Utils.SELECT_LATITUDE, generalFunc.getJsonValue("UserSelectedLatitude", userProfileJson));
//                generalFunc.storeData(Utils.SELECT_LONGITUDE, generalFunc.getJsonValue("UserSelectedLongitude", userProfileJson));
//                generalFunc.storeData(Utils.SELECT_ADDRESS_ID, generalFunc.getJsonValue("UserSelectedAddressId", userProfileJson));
//
//            }

        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Utils.MY_PROFILE_REQ_CODE && resultCode == RESULT_OK && data != null) {
            String userProfileJson = generalFunc.retrieveValue(Utils.USER_PROFILE_JSON);
            obj_userProfile = generalFunc.getJsonObject(generalFunc.retrieveValue(Utils.USER_PROFILE_JSON));
            addDrawer.changeUserProfileJson(userProfileJson);
        } else if (requestCode == Utils.UBER_X_SEARCH_PICKUP_LOC_REQ_CODE && resultCode == RESULT_OK && data != null) {
            headerLocAddressTxt.setText(data.getStringExtra("Address"));
            //headerTxt.setText(data.getStringExtra("Address"));
            setHeaderTxt(data.getStringExtra("Address"));
            this.latitude = data.getStringExtra("Latitude") == null ? "0.0" : data.getStringExtra("Latitude");
            this.longitude = data.getStringExtra("Longitude") == null ? "0.0" : data.getStringExtra("Longitude");

            if (!this.latitude.equalsIgnoreCase("0.0") && !this.longitude.equalsIgnoreCase("0.0")) {
                isUfxaddress = true;
                HashMap<String, String> storeData = new HashMap<>();
                storeData.put(Utils.SELECT_ADDRESSS, data.getStringExtra("Address"));
                storeData.put(Utils.SELECT_LATITUDE, latitude + "");
                storeData.put(Utils.SELECT_LONGITUDE, longitude + "");

                storeData.put(Utils.CURRENT_ADDRESSS, data.getStringExtra("Address"));
                storeData.put(Utils.CURRENT_LATITUDE, latitude + "");
                storeData.put(Utils.CURRENT_LONGITUDE, longitude + "");

                generalFunc.storeData(storeData);

                filterLocation = new Location("filter");
                filterLocation.setLatitude(GeneralFunctions.parseDoubleValue(0.0, latitude));
                filterLocation.setLongitude(GeneralFunctions.parseDoubleValue(0.0, longitude));

                getRestaurantListnew(GeneralFunctions.parseDoubleValue(0.0, latitude), GeneralFunctions.parseDoubleValue(0.0, longitude), false);
                getFilterList(GeneralFunctions.parseDoubleValue(0.0, latitude), GeneralFunctions.parseDoubleValue(0.0, longitude));

                if (currentLocation != null) {
                    double distance = GeneralFunctions.calculationByLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), GeneralFunctions.parseDoubleValue(0.0, latitude), GeneralFunctions.parseDoubleValue(0.0, longitude), "KM");
                    if (distance > 10) {
                        //comment for demo mode
                        //locationPopup.setVisibility(View.VISIBLE);

                        restaurantAdapter.removeFooterView();
                    } else {
                        locationPopup.setVisibility(View.GONE);
                    }
                }
            }


        } else if (requestCode == Utils.CARD_PAYMENT_REQ_CODE && resultCode == RESULT_OK && data != null) {
            String userProfileJson = generalFunc.retrieveValue(Utils.USER_PROFILE_JSON);
            obj_userProfile = generalFunc.getJsonObject(generalFunc.retrieveValue(Utils.USER_PROFILE_JSON));
            addDrawer.changeUserProfileJson(userProfileJson);
        } else if (requestCode == Utils.REQUEST_CODE_GPS_ON) {
            isgpsview = true;

            if (generalFunc.isLocationEnabled()) {
                if (getLastLocation != null) {
                    getLastLocation.stopLocationUpdates();
                    getLastLocation = null;
                }
                getLastLocation = new GetLocationUpdates(getActContext(), Utils.LOCATION_UPDATE_MIN_DISTANCE_IN_MITERS, false, this);

                if (getLastLocation != null) {
                    final Handler handler = new Handler();
                    int delay = 1000; //milliseconds

                    handler.postDelayed(new Runnable() {
                        public void run() {
                            isgpsview = true;
                            //do something
                            if (getLastLocation.getLastLocation() != null) {
                                isgpsview = false;
                                NoLocationView();
                            } else {
                                handler.postDelayed(this, 1000);
                            }
                        }
                    }, delay);
                }
            } else {
                isgpsview = false;
            }

        } else if (requestCode == Utils.SEARCH_PICKUP_LOC_REQ_CODE && resultCode == RESULT_OK && data != null) {
//            mainAct.configPickUpDrag(true, false, false);
            if (resultCode == RESULT_OK) {
                isUfxaddress = true;
                noloactionview.setVisibility(View.GONE);

                Place place = PlaceAutocomplete.getPlace(getActContext(), data);


                LatLng placeLocation = place.getLatLng();

                headerLocAddressTxt.setText(place.getAddress().toString());
                //headerTxt.setText(place.getAddress().toString());
                setHeaderTxt(place.getAddress().toString());
                this.latitude = placeLocation.latitude + "";
                this.longitude = placeLocation.longitude + "";

                HashMap<String, String> storeData = new HashMap<>();
                storeData.put(Utils.SELECT_ADDRESSS, place.getAddress().toString());
                storeData.put(Utils.SELECT_LATITUDE, placeLocation.latitude + "");
                storeData.put(Utils.SELECT_LONGITUDE, placeLocation.longitude + "");


                storeData.put(Utils.CURRENT_ADDRESSS, place.getAddress().toString());
                storeData.put(Utils.CURRENT_LATITUDE, latitude + "");
                storeData.put(Utils.CURRENT_LONGITUDE, longitude + "");

                generalFunc.storeData(storeData);

                if (currentLocation != null) {
                    double distance = GeneralFunctions.calculationByLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), placeLocation.latitude, placeLocation.longitude, "KM");
                    if (distance > 10) {
                        //comment for demo mode
                        locationPopup.setVisibility(View.VISIBLE);
                        restaurantAdapter.removeFooterView();
                    } else {
                        locationPopup.setVisibility(View.GONE);
                    }
                }

                filterLocation = new Location("filter");
                filterLocation.setLatitude(placeLocation.latitude);
                filterLocation.setLongitude(placeLocation.longitude);
                getRestaurantListnew(placeLocation.latitude, placeLocation.longitude, false);
                getFilterList(placeLocation.latitude, placeLocation.longitude);
            }
        } else if (requestCode == 111 && resultCode == RESULT_OK) {
            if (filterLocation != null) {
                getRestaurantListnew(filterLocation.getLatitude(), filterLocation.getLongitude(), false);
            }

        }
    }

    public void pubNubMsgArrived(final String message) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                String msgType = generalFunc.getJsonValue("MsgType", message);
                String iDriverId = generalFunc.getJsonValue("iDriverId", message);

                if (msgType.equals("DriverArrived")) {
                    if (!isArrivedPopup) {
                        isArrivedPopup = true;
                    }
                } else {
                    onGcmMessageArrived(message);
                }
            }
        });
    }

    public void buildMessage(String message, String positiveBtn) {
        final GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
        generateAlert.setCancelable(false);
        generateAlert.setBtnClickList(btn_id -> generateAlert.closeAlertBox());
        generateAlert.setContentMessage("", message);
        generateAlert.setPositiveBtn(positiveBtn);
        generateAlert.showAlertBox();
    }

    public void onGcmMessageArrived(String message) {

        String driverMsg = generalFunc.getJsonValue("Message", message);

        if (driverMsg.equals("TripEnd")) {

// show alert - - not cancabable - ok button only - on click OK finish screen

            if (!isEndpopup) {
                isEndpopup = true;
                // Utils.generateNotification(getActContext(), generalFunc.retrieveLangLBl("", "LBL_END_TRIP_DIALOG_TXT"));
                //   buildMessage(generalFunc.retrieveLangLBl("", "LBL_END_TRIP_DIALOG_TXT"), generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"));
            }

        } else if (driverMsg.equals("TripStarted")) {
            if (!isstartPopup) {
                isstartPopup = true;
                // Utils.generateNotification(getActContext(), generalFunc.retrieveLangLBl("", "LBL_START_TRIP_DIALOG_TXT"));
                // buildMessage(generalFunc.retrieveLangLBl("", "LBL_START_TRIP_DIALOG_TXT"), generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"));
            }

        } else if (driverMsg.equals("TripCancelledByDriver")) {

            if (!isCancelpopup) {
                isCancelpopup = true;
                String reason = generalFunc.getJsonValue("Reason", message);
                //  Utils.generateNotification(getActContext(), generalFunc.retrieveLangLBl("", "LBL_PREFIX_TRIP_CANCEL_DRIVER") + " " + reason);

                // buildMessage((generalFunc.retrieveLangLBl("", generalFunc.retrieveLangLBl("", "LBL_PREFIX_TRIP_CANCEL_DRIVER") )+ " " + reason), generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"));
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (getLastLocation != null) {
            getLastLocation.stopLocationUpdates();
        }
        releaseResources();
    }


    @Override
    public void onBackPressed() {

        if (addDrawer.checkDrawerState(false)) {
            return;
        }
        super.onBackPressed();
    }

    public void releaseResources() {
        try {
            getAddressFromLocation.setAddressList(null);
            getAddressFromLocation = null;
        } catch (Exception e) {

        }
    }

    boolean isFirst = true;

    @Override
    public void onLocationUpdate(Location mLastLocation) {

        latitude = mLastLocation.getLatitude() + "";
        longitude = mLastLocation.getLongitude() + "";

        if (mLastLocation != null && isFirst) {
            currentLocation = mLastLocation;
            filterLocation = currentLocation;
            getRestaurantListnew(mLastLocation.getLatitude(), mLastLocation.getLongitude(), false);
            getFilterList(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            isFirst = false;
        }


        getAddressFromLocation = new GetAddressFromLocation(getActContext(), generalFunc);
        getAddressFromLocation.setLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        getAddressFromLocation.setAddressList(this);
        getAddressFromLocation.execute();
    }

    @Override
    public void setOnRestaurantclick(int position) {


        //junaid restaurant

        if (internetConnection.isNetworkConnected()) {


            Log.d("TTTTTTTTTTTTT", "" + restaurantArr_List.get(position));

            HashMap<String, String> posData = restaurantArr_List.get(position);
            Bundle bn = new Bundle();
            bn.putString("iCompanyId", posData.get("iCompanyId"));
            bn.putString("Restaurant_Status", posData.get("Restaurant_Status"));
            bn.putString("ispriceshow", posData.get("ispriceshow"));
            bn.putString("lat", latitude);
            bn.putString("long", longitude);

            bn.putString("vCompany", posData.get("vCompany"));
            bn.putString("deliveryPrice", posData.get("deliveryPrice"));
            bn.putString("vAvgRating", posData.get("vAvgRating"));
            bn.putString("Restaurant_Cuisine", posData.get("Restaurant_Cuisine"));
            bn.putString("Restaurant_OrderPrepareTimeConverted", posData.get("Restaurant_OrderPrepareTimeConverted"));


            new StartActProcess(getActContext()).startActForResult(RestaurantAllDetailsNewActivity.class, bn, 111);
        } else {
            generalFunc.showMessage(menuImgView, generalFunc.retrieveLangLBl("", "LBL_NO_INTERNET_TXT"));
        }
    }

    @Override
    public void setOnRestaurantclick(int position, boolean liked) {

    }

    @Override
    public void onItemClickBanner(int position) {

    }


    @Override
    public void onResturentBannerSelect(String iCompanyId, String latitude1, String longitude1, String vCompany,
                                        String deliveryPrice, String ispriceshow, String vAvgRating, String Restaurant_Status,
                                        String Restaurant_Cuisine, String Restaurant_OrderPrepareTime) {


        if (internetConnection.isNetworkConnected()) {

            Bundle bn = new Bundle();
            bn.putString("iCompanyId", iCompanyId);
            bn.putString("Restaurant_Status", Restaurant_Status);
            bn.putString("ispriceshow", ispriceshow);
            bn.putString("lat", latitude);
            bn.putString("long", longitude);
            bn.putString("vCompany", vCompany);
            bn.putString("deliveryPrice", deliveryPrice);
            bn.putString("vAvgRating", vAvgRating);
            bn.putString("Restaurant_Cuisine", Restaurant_Cuisine);
            bn.putString("Restaurant_OrderPrepareTimeConverted", generalFunc.convertNumberWithRTL(Restaurant_OrderPrepareTime));

            new StartActProcess(getActContext()).startActForResult(RestaurantAllDetailsNewActivity.class, bn, 111);
        } else {
            generalFunc.showMessage(menuImgView, generalFunc.retrieveLangLBl("", "LBL_NO_INTERNET_TXT"));
        }


    }


    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            int i = view.getId();

            if (i == deliveryAreaTxt.getId()) {
                isgpsview = true;
                new StartActProcess(getActContext()).
                        startActForResult(Settings.ACTION_LOCATION_SOURCE_SETTINGS, Utils.REQUEST_CODE_GPS_ON);

            } else if (i == nolocbackImgView.getId()) {
                nolocmenuImgView.setVisibility(View.VISIBLE);
                nolocbackImgView.setVisibility(View.GONE);

            } else if (i == nolocmenuImgView.getId()) {
                addDrawer.checkDrawerState(true);
            }

        }
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

    int checkcnt = 0;

    public void openFilterDilaog() {

        int height = Utils.dpToPx(400, getActContext());
        // if (cabTypeList.get(pos).get("total_fare") != null && !cabTypeList.get(pos).get("total_fare").equalsIgnoreCase("")) {

        final BottomSheetDialog faredialog = new BottomSheetDialog(getActContext());


        View contentView = View.inflate(getActContext(), R.layout.deliver_all_dialog_filter, null);


        // faredialog.setContentView(contentView);
        faredialog.setContentView(contentView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                height));
        BottomSheetBehavior mBehavior = BottomSheetBehavior.from((View) contentView.getParent());
        mBehavior.setPeekHeight(height);
        View bottomSheetView = faredialog.getWindow().getDecorView().findViewById(R.id.design_bottom_sheet);
        // BottomSheetBehavior.from(bottomSheetView).setHideable(false);
        // setCancelable(faredialog, false);

        TextView menuTitle = (TextView) faredialog.findViewById(R.id.menuTitle);
        TextView closeTxt = (TextView) faredialog.findViewById(R.id.closeTxt);
        TextView TitleTxt = (TextView) faredialog.findViewById(R.id.TitleTxt);
        TextView resetTxt = (TextView) faredialog.findViewById(R.id.resetTxt);
        TextView offerTxtView = (TextView) faredialog.findViewById(R.id.offerTxtView);
        CheckBox offerchkBox = (CheckBox) faredialog.findViewById(R.id.offerchkBox);
        LinearLayout offerArea = (LinearLayout) faredialog.findViewById(R.id.offerArea);
        View offerview = (View) faredialog.findViewById(R.id.offerview);

        LinearLayout favArea = (LinearLayout) faredialog.findViewById(R.id.favArea);
        TextView favTxtView = (TextView) faredialog.findViewById(R.id.favTxtView);
        CheckBox favchkBox = (CheckBox) faredialog.findViewById(R.id.favchkBox);
        View favview = (View) faredialog.findViewById(R.id.favview);


        favTxtView.setText(generalFunc.retrieveLangLBl("", "LBL_FAVOURITE_STORE"));

        if (!generalFunc.getMemberId().equalsIgnoreCase("") &&
                generalFunc.getJsonValueStr("ENABLE_FAVORITE_STORE_MODULE",
                        obj_userProfile).equalsIgnoreCase("Yes")) {
            favArea.setVisibility(View.VISIBLE);
            favview.setVisibility(View.VISIBLE);
        } else {
            favArea.setVisibility(View.GONE);
            favview.setVisibility(View.GONE);
        }


        offerTxtView.setText(generalFunc.retrieveLangLBl("", "LBL_OFFER"));
        LinearLayout detailsArea = (LinearLayout) faredialog.findViewById(R.id.detailsArea);
        @SuppressLint("WrongViewCast") MButton applyRatingBtn = ((MaterialRippleLayout) faredialog.findViewById(R.id.btn_type2)).getChildView();
        applyRatingBtn.setText(generalFunc.retrieveLangLBl("", "LBL_APPLY_FILTER"));
        closeTxt.setText(generalFunc.retrieveLangLBl("", "LBL_CLOSE_TXT"));
        resetTxt.setText(generalFunc.retrieveLangLBl("", "LBL_RESET"));
        applyRatingBtn.setEnabled(false);
        menuTitle.setText(generalFunc.retrieveLangLBl("", "LBL_SHOW_RESTAURANTS_WITH"));
        applyRatingBtn.setBackgroundColor(Color.parseColor("#BABABA"));

        offerArea.setOnClickListener(v -> offerchkBox.performClick());
        favArea.setOnClickListener(v -> favchkBox.performClick());


        if (isOfferCheck.equalsIgnoreCase("Yes")) {
            offerchkBox.setChecked(true);
        }

        if (isFavCheck.equalsIgnoreCase("Yes")) {
            favchkBox.setChecked(true);
        }


        if (isOfferApply.equalsIgnoreCase("Yes")) {
            offerArea.setVisibility(View.VISIBLE);
        } else {
            offerArea.setVisibility(View.GONE);
        }


        applyRatingBtn.setOnClickListener(v -> {

            List<String> searchList = new ArrayList<>();
            selectedFilterId = "";
            for (int i = 0; i < filterList.size(); i++) {
                int pos = i;
                HashMap<String, String> indexData = filterList.get(i);
                String isCheck = indexData.get("isCheck");
                if (isCheck != null && isCheck.equalsIgnoreCase("Yes")) {
                    searchList.add(indexData.get("cuisineId"));
                } else {
                    if (searchList.contains(indexData.get("cuisineId"))) {
                        searchList.remove(i);
                    }
                }
            }

            if (searchList != null && searchList.size() > 0) {
                for (int j = 0; j < searchList.size(); j++) {
                    if (searchList.size() == 1) {
                        selectedFilterId = searchList.get(j);
                    } else {
                        if (selectedFilterId.equalsIgnoreCase("")) {
                            selectedFilterId = searchList.get(j);
                        } else {
                            selectedFilterId = selectedFilterId + "," + searchList.get(j);
                        }
                    }
                }
            } else {
                selectedFilterId = "";
            }
            if (filterLocation != null) {
                if (searchList == null || searchList.size() == 0) {
                    if (isOfferCheck.equalsIgnoreCase("Yes") || isFavCheck.equalsIgnoreCase("Yes")) {
                        filterImage.setVisibility(View.VISIBLE);
                        if (filterImage.getVisibility() == View.VISIBLE) {
                            resetTxt.setVisibility(View.VISIBLE);
                        }

                    } else {
                        selectedFilterId = "";
                        filterImage.setVisibility(View.GONE);
                        resetTxt.setVisibility(View.GONE);

                    }


                } else {
                    filterImage.setVisibility(View.VISIBLE);
                    resetTxt.setVisibility(View.VISIBLE);

                }
                //do by abubakar
                getRestaurantListnew(filterLocation.getLatitude(), filterLocation.getLongitude(), false);
            }
            faredialog.dismiss();

        });


        if (detailsArea.getChildCount() > 0) {
            detailsArea.removeAllViewsInLayout();
        }

        offerchkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (isChecked) {
                checkcnt++;
                isOfferCheck = "Yes";
            } else {
                isOfferCheck = "No";
                checkcnt--;
            }

            if (checkcnt > 0) {
                applyRatingBtn.setEnabled(true);

                applyRatingBtn.setBackgroundColor(getActContext().getResources().getColor(R.color.appThemeColor_1));
                if (filterImage.getVisibility() == View.VISIBLE) {
                    resetTxt.setVisibility(View.VISIBLE);
                }
            } else {
                if (filterImage.getVisibility() == View.GONE) {
                    applyRatingBtn.setEnabled(false);
                    resetTxt.setVisibility(View.GONE);
                    applyRatingBtn.setBackgroundColor(Color.parseColor("#BABABA"));
                }

            }

        });

        favchkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (isChecked) {

                checkcnt++;
                isFavCheck = "Yes";
            } else {
                isFavCheck = "No";
                checkcnt--;
            }

            if (checkcnt > 0) {
                applyRatingBtn.setEnabled(true);

                applyRatingBtn.setBackgroundColor(getActContext().getResources().getColor(R.color.appThemeColor_1));
                if (filterImage.getVisibility() == View.VISIBLE) {
                    resetTxt.setVisibility(View.VISIBLE);
                }
            } else {
                if (filterImage.getVisibility() == View.GONE) {
                    applyRatingBtn.setEnabled(false);
                    resetTxt.setVisibility(View.GONE);
                    applyRatingBtn.setBackgroundColor(Color.parseColor("#BABABA"));
                }

            }

        });


        for (int i = 0; i < filterList.size(); i++) {
            final int pos = i;
            LayoutInflater inflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.item_filter, null);
            TextView rowDataTxtView = (TextView) view.findViewById(R.id.rowDataTxtView);
            TextView rowTitleTxtView = (TextView) view.findViewById(R.id.rowTitleTxtView);
            LinearLayout rowarea = (LinearLayout) view.findViewById(R.id.rowarea);
            View nameView = (View) view.findViewById(R.id.nameView);
            CheckBox chkBox = (CheckBox) view.findViewById(R.id.chkBox);

            String[] selectedFilterArray = selectedFilterId.split(",");
            if (selectedFilterId != null && !selectedFilterId.equalsIgnoreCase("") && Arrays.asList(selectedFilterArray).contains(filterList.get(pos).get("cuisineId"))) {
                chkBox.setChecked(true);
            }
            chkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {

                if (isChecked) {
                    checkcnt++;
                    if (checkcnt > 0) {
                        HashMap<String, String> checkBox = filterList.get(pos);
                        checkBox.put("isCheck", "Yes");
                        filterList.set(pos, checkBox);
                    }
                } else {
                    checkcnt--;
                    HashMap<String, String> checkBox = filterList.get(pos);
                    checkBox.put("isCheck", "No");
                    filterList.set(pos, checkBox);
                }

                if (checkcnt > 0) {
                    applyRatingBtn.setEnabled(true);
                    applyRatingBtn.setBackgroundColor(getActContext().getResources().getColor(R.color.appThemeColor_1));
                    if (filterImage.getVisibility() == View.VISIBLE) {
                        resetTxt.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (filterImage.getVisibility() == View.GONE) {
                        applyRatingBtn.setEnabled(false);
                        applyRatingBtn.setBackgroundColor(Color.parseColor("#BABABA"));
                        resetTxt.setVisibility(View.GONE);
                    }
                }

            });
            if (i == 0) {
                rowTitleTxtView.setText(generalFunc.retrieveLangLBl("", "LBL_CUISINES"));
                rowTitleTxtView.setVisibility(View.VISIBLE);
                nameView.setVisibility(View.VISIBLE);
            } else {
                rowTitleTxtView.setVisibility(View.GONE);
                nameView.setVisibility(View.GONE);
            }

            rowDataTxtView.setText(filterList.get(i).get("cuisineName"));

            rowarea.setOnClickListener(v -> chkBox.performClick());


            detailsArea.addView(view);
        }

        if (checkcnt > 0) {
            applyRatingBtn.setEnabled(true);
            applyRatingBtn.setBackgroundColor(getActContext().getResources().getColor(R.color.appThemeColor_1));
            if (filterImage.getVisibility() == View.VISIBLE) {
                resetTxt.setVisibility(View.VISIBLE);
            }
        } else {
            if (filterImage.getVisibility() == View.GONE) {
                applyRatingBtn.setEnabled(false);
                resetTxt.setVisibility(View.GONE);
                applyRatingBtn.setBackgroundColor(Color.parseColor("#BABABA"));
            }
        }


        closeTxt.setOnClickListener(v -> faredialog.dismiss());

        resetTxt.setOnClickListener(v -> {
            faredialog.dismiss();
            selectedFilterId = "";
            isOfferCheck = "No";
            isFavCheck = "No";
            checkcnt = 0;
            if (filterLocation != null) {
                getRestaurantListnew(filterLocation.getLatitude(), filterLocation.getLongitude(), false);
                getFilterList(filterLocation.getLatitude(), filterLocation.getLongitude());
                filterImage.setVisibility(View.GONE);
                resetTxt.setVisibility(View.GONE);
            }
        });


        faredialog.show();
    }

    public void openRelevenceDilaog() {

        final BottomSheetDialog faredialog = new BottomSheetDialog(getActContext());

        View contentView = View.inflate(getActContext(), R.layout.dialog_relevance, null);

        if (generalFunc.getServiceId().equalsIgnoreCase("") || generalFunc.getServiceId().equalsIgnoreCase("1")) {

            faredialog.setContentView(contentView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    Utils.dpToPx(300, getActContext())));
        } else {
            faredialog.setContentView(contentView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    Utils.dpToPx(200, getActContext())));

        }
        BottomSheetBehavior mBehavior = BottomSheetBehavior.from((View) contentView.getParent());
        if (generalFunc.getServiceId().equalsIgnoreCase("") || generalFunc.getServiceId().equalsIgnoreCase("1")) {
            mBehavior.setPeekHeight(Utils.dpToPx(300, getActContext()));
        } else {
            mBehavior.setPeekHeight(Utils.dpToPx(200, getActContext()));
        }
        View bottomSheetView = faredialog.getWindow().getDecorView().findViewById(R.id.design_bottom_sheet);
        // BottomSheetBehavior.from(bottomSheetView).setHideable(false);
        // setCancelable(faredialog, false);

        TextView menuTitle = (TextView) faredialog.findViewById(R.id.menuTitle);
        TextView closeTxt = (TextView) faredialog.findViewById(R.id.closeTxt);
        TextView TitleTxt = (TextView) faredialog.findViewById(R.id.TitleTxt);
        LinearLayout detailsArea = (LinearLayout) faredialog.findViewById(R.id.detailsArea);
        // TitleTxt.setText("sort by");
        menuTitle.setText(generalFunc.retrieveLangLBl("Sort By", "LBL_SORT_BY"));
        TitleTxt.setText(generalFunc.retrieveLangLBl("Sort By", "LBL_SORT_BY"));


        if (detailsArea.getChildCount() > 0) {
            detailsArea.removeAllViewsInLayout();
        }

        for (int i = 0; i < sortby_List.size(); i++) {
            int pos = i;
            LayoutInflater inflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.item_sort, null);
            TextView rowTitleTxtView = (TextView) view.findViewById(R.id.rowTitleTxtView);


            rowTitleTxtView.setText(sortby_List.get(i).get("label"));

            rowTitleTxtView.setOnClickListener(v -> {
                faredialog.dismiss();
                selectedSort = sortby_List.get(pos).get("value");
                relevenceTxt.setText(generalFunc.retrieveLangLBl("", sortby_List.get(pos).get(("label"))));
                if (filterLocation != null) {
                    getRestaurantListnew(filterLocation.getLatitude(), filterLocation.getLongitude(), false);
                }

            });
            detailsArea.addView(view);
        }

        closeTxt.setOnClickListener(v -> faredialog.dismiss());

        faredialog.show();
    }

    public class setOnClickLst implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Utils.hideKeyboard(getActContext());
            Bundle bn = new Bundle();

            switch (v.getId()) {
                case R.id.uberXHeaderLayout:
                    bn.putString("locationArea", "source");
                    bn.putBoolean("isaddressview", true);
                    bn.putDouble("lat", GeneralFunctions.parseDoubleValue(0.0, latitude));
                    bn.putDouble("long", GeneralFunctions.parseDoubleValue(0.0, longitude));
                    bn.putString("address", headerLocAddressTxt.getText().toString() + "");
                    bn.putString("eSystem", Utils.eSystem_Type);
                    new StartActProcess(getActContext()).startActForResult(SearchLocationActivity.class,
                            bn, Utils.UBER_X_SEARCH_PICKUP_LOC_REQ_CODE);
                    break;
                case R.id.changeLocBtn:
                case R.id.editLocationBtn:
                case R.id.headerArrow:
                case R.id.headerTxt:
                    bn.putString("locationArea", "source");
                    bn.putBoolean("isaddressview", true);
                    bn.putDouble("lat", GeneralFunctions.parseDoubleValue(0.0, latitude));
                    bn.putDouble("long", GeneralFunctions.parseDoubleValue(0.0, longitude));
                    bn.putString("address", headerAddress);
                    bn.putString("eSystem", Utils.eSystem_Type);
                    new StartActProcess(getActContext()).startActForResult(SearchLocationActivity.class,
                            bn, Utils.UBER_X_SEARCH_PICKUP_LOC_REQ_CODE);
                    break;
                case R.id.searchLAy:
                    if (internetConnection.isNetworkConnected()) {
                        Bundle bundle = new Bundle();
                        bundle.putDouble("lat", filterLocation.getLatitude());
                        bundle.putDouble("long", filterLocation.getLongitude());
                        bundle.putString("address", headerLocAddressTxt.getText().toString());
                        new StartActProcess(getActContext()).startActForResult(RestaurantsSearchActivity.class, bundle, 111);
                    } else {
                        generalFunc.showMessage(menuImgView, generalFunc.retrieveLangLBl("", "LBL_NO_INTERNET_TXT"));
                    }
                    break;
                case R.id.menuImgView:
                    addDrawer.checkDrawerState(true);
                    break;
                case R.id.backImgView:
                    onBackPressed();
                    break;
                case R.id.filter_lay:
                    if (!filterClick) {
                        filterClick = true;
                        Handler handler = new Handler();
                        handler.postDelayed(() -> filterClick = false, 1000);
                        if (filterList != null && filterList.size() > 0) {
                            openFilterDilaog();
                        }
                    }
                    break;
                case R.id.relevenceTxt:
                    openRelevenceDilaog();
                    break;
                case R.id.noThanksBtn:
                    locationPopup.setVisibility(View.GONE);
                    break;
                case R.id.fabcartIcon:
                    new StartActProcess(getActContext()).startAct(EditCartActivity.class);
                    break;
            }
        }
    }
}
