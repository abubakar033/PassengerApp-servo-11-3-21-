package com.servo.user.deliverAll;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;
import androidx.core.widget.NestedScrollView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.JResponseClasses.RecomendedAddItemCallBack;
import com.adapter.StorePayAdapter;
import com.adapter.files.deliverAll.RestaurantmenuAdapter;
import com.adapter.files.deliverAll.RestaurantmenuAdapter2;
import com.jAdapter.RecomendedRecAdapter;
import com.servo.user.PrescriptionActivity;
import com.servo.user.R;
import com.general.files.AppFunctions;
import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.general.files.MyApp;
import com.general.files.StartActProcess;
import com.kyleduo.switchbutton.SwitchButton;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.realmModel.Cart;
import com.realmModel.Options;
import com.realmModel.Topping;
import com.utils.Logger;
import com.utils.Utils;
import com.view.CounterFab;
import com.view.DividerView;
import com.view.MButton;
import com.MyProgressDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import io.realm.Realm;
import io.realm.RealmResults;

import static java.sql.DriverManager.println;

public class RestaurantAllDetailsNewActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener,
        RestaurantmenuAdapter2.OnItemClickListener, RecomendedAddItemCallBack {

    GeneralFunctions generalFunc;
    ImageView backarrorImgView;
    private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR = 0.30f;
    private static final float PERCENTAGE_TO_HIDE_TITLE_DETAILS = 0.015f;
    private boolean mIsTheTitleVisible = false;
    private boolean mIsTheTitleContainerVisible = true;
    private static final int ALPHA_ANIMATIONS_DURATION = 200;
    MyProgressDialog myPDialog;
    ArrayList<HashMap<String, String>> recoList;
    boolean loadMore = false;
    boolean isFirstTime = true;
    boolean isNextPageAvailable = false;
    ArrayList<HashMap> hashList = new ArrayList<>();


    String next_page_str = "";
    boolean mIsLoading = false;

    EditText searchEdittext;
    private CollapsingToolbarLayout collapsingToolbarLayout = null;
    /*    private CollapsingToolbarLayout collapsingToolbarLayout1 = null;
        private CollapsingToolbarLayout collapsingToolbarLayout2 = null;*/
    private AppBarLayout appbar;

    RecyclerView resMenuRecyclerview;

    TextView restNametxt, restcusineNameTxt, ratingCntTxt, ratingdecTxt, deliveryValTxt, deliveryLBLTxt, minOrderValTxt, minOrderLBLTxt, offerMsgTxt, foodTypetxt;
    LinearLayout offerArea, VegNovegFilterArea;
    ImageView infoImage, rightFoodImgView;


    String eNonVegToggleDisplay = "";
    String vCompany = "";
    String fMinOrderValue = "0";
    String iMaxItemQty = "0";
    String Restaurant_Cuisine = "";


    ArrayList<HashMap<String, String>> list = new ArrayList<>();


    ArrayList<HashMap<String, String>> filterlist;

    RestaurantmenuAdapter restaurantmenuAdapter;
    RestaurantmenuAdapter2 restaurantmenuAdapter1;
    CounterFab cartFoodImgView;
    RealmResults<Cart> realmCartList;
    LinearLayout bottomCartView;
    TextView itemNpricecartTxt, viewCartTxt, menutxt;
    LinearLayout infoInnerLayout, dialogsLayout, resHeaderViewInfoLayout;
    RelativeLayout dialogsLayoutArea;
    CardView informationDesignCardView, ratingDesignCardView;
    TextView openingHourTxt, timeHTxt, timeVTxt, titleDailogTxt, addressDailogTxt, timeSatTxt, timeVSatTxt, reco_TV_recoAdp;
    TextView reco_nametxt, reco_dectxt, reco_rating_Txt, reco_dev_time_Txt, reco_order_price;
    MButton closeBtn;
    SwitchButton vegNonVegSwitch;
    RelativeLayout fabMenuLayout;

    LinearLayout restaurantViewFloatingBtn, menuContainer;
    ArrayList<Cart> cartList;
    double finalTotal = 0;
    int item = 0;
    String CurrencySymbol = "";
    String isSearchVeg = "No";
    int selMenupos = 0;
    LinearLayout nestedscroll;
    View menubackView;
    DividerView minDivider;
    LinearLayout minOrderArea;

    ImageView menuImg;
    LinearLayout resDetails;

    //Fav Store Features
    private LikeButton likeButton;
    boolean isLike = false;
    String isFavStore = "No";
    String isVeg = "";
    String userProfileJson;
    boolean isFavChange = false;

    String iCompanyId = "";

    //junaid extra code

    TabLayout tabLayout;
    HashMap<String, String> paymentTypeHashmap = new HashMap<>();

    RecyclerView reco_Recycler;
    TextView rating_tvL, dev_time_tvL, dev_prive_tvL;
    LinearLayout addviewsLayout;
    LinearLayout reco_lay;
    RecyclerView res_pay_recucler;
    ArrayList<String> stringList = new ArrayList<>();
    TextView paymentMethodHeder;

    String totalPage = "";
    String nextPage = "";
    String curentPage = "1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_item);
        //setContentView(R.layout.activity_restaurant_all_details_new);
        generalFunc = MyApp.getInstance().getGeneralFun(getActContext());
        new AppFunctions(getActContext()).runGAC();
        iCompanyId = getIntent().getStringExtra("iCompanyId");

        userProfileJson = generalFunc.retrieveValue(Utils.USER_PROFILE_JSON);
        tabLayout = findViewById(R.id.sliding_tabs);
        addviewsLayout = findViewById(R.id.addviewsLayout);
        reco_lay = findViewById(R.id.reco_lay);
        paymentMethodHeder = findViewById(R.id.paymentMethodHeder);
        reco_TV_recoAdp = findViewById(R.id.reco_TV_recoAdp);
        res_pay_recucler = findViewById(R.id.res_pay_recucler);
        backarrorImgView = findViewById(R.id.backArrowImgView);
        reco_Recycler = findViewById(R.id.recoemended_recyclerView);
        restaurantViewFloatingBtn = findViewById(R.id.restaurantViewFloatingBtn);
        menubackView = findViewById(R.id.menubackView);
        menubackView.setOnClickListener(new setOnClickList());
        restaurantViewFloatingBtn.setOnClickListener(new setOnClickList());
        menuContainer = findViewById(R.id.menuContainer);
        vegNonVegSwitch = findViewById(R.id.vegNonVegSwitch);
        dialogsLayout = findViewById(R.id.dialogsLayout);
        dialogsLayoutArea = findViewById(R.id.dialogsLayoutArea);
        fabMenuLayout = findViewById(R.id.fabMenuLayout);
        resDetails = findViewById(R.id.resDetails);
        closeBtn = findViewById(R.id.closeBtn);
        closeBtn.setOnClickListener(new setOnClickList());
        menutxt = findViewById(R.id.menutxt);
        informationDesignCardView = findViewById(R.id.informationDesignCardView);

        //top name
        reco_nametxt = findViewById(R.id.reco_name);
        reco_dectxt = findViewById(R.id.reco_decvd);
        reco_rating_Txt = findViewById(R.id.reco_ratingText);
        reco_dev_time_Txt = findViewById(R.id.reco_dev_time_text);
        reco_order_price = findViewById(R.id.reco_order_price);
        openingHourTxt = findViewById(R.id.openingHourTxt);
        titleDailogTxt = findViewById(R.id.titleDailogTxt);
        addressDailogTxt = findViewById(R.id.addressDailogTxt);
        timeHTxt = findViewById(R.id.timeHTxt);
        timeVSatTxt = findViewById(R.id.timeVSatTxt);
        timeSatTxt = findViewById(R.id.timeSatTxt);
        timeVTxt = findViewById(R.id.timeVTxt);
        rating_tvL = findViewById(R.id.rating_tvL);
        dev_prive_tvL = findViewById(R.id.dev_price_tvL);
        dev_time_tvL = findViewById(R.id.dev_time_tvL);
        rating_tvL.setText(generalFunc.retrieveLangLBl("Rating", "LBL_RATING_STORE"));
        dev_prive_tvL.setText(generalFunc.retrieveLangLBl("Delivery Price", "LBL_DELIVERY_PRICE"));
        dev_time_tvL.setText(generalFunc.retrieveLangLBl("Delivery time", "LBL_DELIVERY_TIME"));
        reco_TV_recoAdp.setText(generalFunc.retrieveLangLBl("", "LBL_RECOMMENDED"));
        paymentMethodHeder.setText(generalFunc.retrieveLangLBl("Payment method", "LBL_PAYMENT_METHOD"));
        ratingDesignCardView = findViewById(R.id.ratingDesignCardView);
        rightFoodImgView = findViewById(R.id.rightFoodImgView);
        // rightFoodImgView.setOnClickListener(new setOnClickList());
        backarrorImgView.setOnClickListener(new setOnClickList());
//        searchImgView = findViewById(R.id.searchImgView);
        searchEdittext = findViewById(R.id.titleTxtView);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        collapsingToolbarLayout.setTitle("");

        appbar = (AppBarLayout) findViewById(R.id.app_bar_layout);
        appbar.addOnOffsetChangedListener(this);
        resMenuRecyclerview = (RecyclerView) findViewById(R.id.resMenuRecyclerview);
        nestedscroll = findViewById(R.id.nestedscroll);
        restNametxt = (TextView) findViewById(R.id.restNametxt);
        restcusineNameTxt = (TextView) findViewById(R.id.restcusineNameTxt);
        ratingCntTxt = (TextView) findViewById(R.id.ratingCntTxt);
        ratingdecTxt = (TextView) findViewById(R.id.ratingdecTxt);
        deliveryValTxt = (TextView) findViewById(R.id.deliveryValTxt);
        deliveryLBLTxt = (TextView) findViewById(R.id.deliveryLBLTxt);
        minOrderValTxt = (TextView) findViewById(R.id.minOrderValTxt);
        minOrderLBLTxt = (TextView) findViewById(R.id.minOrderLBLTxt);
        offerMsgTxt = (TextView) findViewById(R.id.offerMsgTxt);
        foodTypetxt = (TextView) findViewById(R.id.foodTypetxt);
        offerArea = (LinearLayout) findViewById(R.id.offerArea);
        VegNovegFilterArea = (LinearLayout) findViewById(R.id.VegNovegFilterArea);
        infoImage = (ImageView) findViewById(R.id.infoImage);
        infoImage.setOnClickListener(new setOnClickList());
        cartFoodImgView = findViewById(R.id.cartFoodImgView);
        cartFoodImgView.setOnClickListener(new setOnClickList());
        bottomCartView = (LinearLayout) findViewById(R.id.bottomCartView);
        bottomCartView.setOnClickListener(new setOnClickList());
        itemNpricecartTxt = (TextView) findViewById(R.id.itemNpricecartTxt);
        viewCartTxt = (TextView) findViewById(R.id.viewCartTxt);
        minDivider = (DividerView) findViewById(R.id.minDivider);
        minOrderArea = (LinearLayout) findViewById(R.id.minOrderArea);
        menuImg = (ImageView) findViewById(R.id.menuImg);

        //Fav Store Features
        likeButton = (LikeButton) findViewById(R.id.likeButton);




        likeButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                isFavChange = true;
                isLike = true;
                isFavStore = "Yes";
                // getRestaurantDetails(isVeg, true);
                GetRestaurantDetailsPagination(isVeg, true, nextPage);
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                isFavChange = true;
                isLike = false;
                isFavStore = "No";
                // addRemoveFav();
                // getRestaurantDetails(isVeg, true);
                GetRestaurantDetailsPagination(isVeg, true, nextPage);

            }
        });
        searchEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 1) {

                    filterArrey(s.toString());
                    reco_Recycler.setVisibility(View.GONE);
                    tabLayout.setVisibility(View.GONE);
                    reco_TV_recoAdp.setVisibility(View.GONE);


                } else {

                    reco_Recycler.setVisibility(View.VISIBLE);
                    tabLayout.setVisibility(View.VISIBLE);
                    reco_TV_recoAdp.setVisibility(View.VISIBLE);
                    addlist();


                }


            }
        });
        if (generalFunc.getJsonValue("ENABLE_FAVORITE_STORE_MODULE", userProfileJson).equalsIgnoreCase("Yes") && !generalFunc.getMemberId().equalsIgnoreCase("")) {
            likeButton.setVisibility(View.VISIBLE);
        } else {
            likeButton.setVisibility(View.GONE);

        }

        if (generalFunc.getServiceId().equals("") || generalFunc.getServiceId().equals("1")) {
            menuImg.setVisibility(View.VISIBLE);
        } else {
            menuImg.setVisibility(View.GONE);
        }

        // getRestaurantDetails("", false);
//        old
//        GetRestaurantDetailsPagination(isVeg, false, "1");
        setLabel();
        //  addHideView();


        //junaid comment

//        setParentCategoryLayoutManager();
        vegNonVegSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (isChecked) {
                isVeg = "Yes";
                isSearchVeg = isVeg;
            } else {
                isVeg = "No";
                isSearchVeg = isVeg;
            }

//            getRestaurantDetails(isVeg, false);

        });
        if (generalFunc.isRTLmode()) {
            backarrorImgView.setRotation(180);
        }
        reco_nametxt.setText(getIntent().getStringExtra("vCompany"));
        reco_rating_Txt.setText(getIntent().getStringExtra("vAvgRating"));
        reco_dectxt.setText(getIntent().getStringExtra("Restaurant_Cuisine"));
        reco_dev_time_Txt.setText(getIntent().getStringExtra("Restaurant_OrderPrepareTimeConverted"));
        reco_order_price.setText(getIntent().getStringExtra("deliveryPrice").trim().equalsIgnoreCase("") ?
                "Not Set" : "$ " + getIntent().getStringExtra("deliveryPrice"));





        //call
        getRestaurantDetailstest(isVeg,false);

    }

    private void filterArrey(String seachKeyWord) {

        ArrayList<HashMap<String, String>> foodArrList = new ArrayList<>();
        for (HashMap<String, String> e : list) {
            if (e.get("vItemType").toLowerCase().trim().contains(seachKeyWord.toLowerCase())) {
                foodArrList.add(e);
            }
        }
        restaurantmenuAdapter1 = new RestaurantmenuAdapter2(getActContext(), foodArrList, generalFunc, false);
        resMenuRecyclerview.setAdapter(restaurantmenuAdapter1);
        restaurantmenuAdapter1.setOnItemClickListener(this);
        // }


    }

    private void addlist() {
        restaurantmenuAdapter1 = new RestaurantmenuAdapter2(getActContext(), list, generalFunc, false);
        resMenuRecyclerview.setAdapter(restaurantmenuAdapter1);
        restaurantmenuAdapter1.setOnItemClickListener(this);
    }

    private void addTabs(ArrayList<HashMap> productData) {

        tabLayout.removeAllTabs();

        for (int i = 0; i < productData.size(); i++) {

            HashMap<String, String> mapData = productData.get(i);

            if (!mapData.get("menuName").equalsIgnoreCase("") && !TextUtils.isEmpty(mapData.get("menuName"))) {
                //tabLayout.addTab(tabLayout.newTab().setText(mapData.get("menuName").length()>0?mapData.get("menuName"):"Empty"));
                tabLayout.addTab(tabLayout.newTab().setText(mapData.get("menuName")));
            }
        }

        //  tabLayout.getTabAt(0).select();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
//                tabLayout.getTabAt(tab.getPosition()).select();


                if (tab.getPosition() == 0) {
                    restaurantmenuAdapter1.update(list);
                } else {
                    filtertotab(tab.getText().toString());

                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

    }

    public void setLabel() {
        deliveryLBLTxt.setText(generalFunc.retrieveLangLBl("", "LBL_DELIVERY_TIME"));
        viewCartTxt.setText(generalFunc.retrieveLangLBl("", "LBL_VIEW_CART"));
        openingHourTxt.setText(generalFunc.retrieveLangLBl("", "LBL_OPENING_HOURS"));
        menutxt.setText(generalFunc.retrieveLangLBl("MENU", "LBL_MENU"));
        closeBtn.setText(generalFunc.retrieveLangLBl("", "LBL_CLOSE_TXT"));
        minOrderLBLTxt.setText(generalFunc.retrieveLangLBl("", "LBL_FOR_ONE"));
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {

            realmCartList = getCartData();
            cartList = new ArrayList<>(realmCartList);


            if (realmCartList.size() > 0) {

                int cnt = 0;
                for (int i = 0; i < realmCartList.size(); i++) {
                    cnt = cnt + GeneralFunctions.parseIntegerValue(0, realmCartList.get(i).getQty());
                }

                cartFoodImgView.setCount(cnt);

                bottomCartView.setVisibility(View.VISIBLE);
                calculateItemNprice();

            } else {
                cartFoodImgView.setImageDrawable(getResources().getDrawable(R.drawable.cart_new));

                cartFoodImgView.setCount(0);

                bottomCartView.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            Logger.d("Exception", "::" + e.toString());
        }
    }

    public void calculateItemNprice() {
        finalTotal = 0;
        item = 0;
        for (int i = 0; i < cartList.size(); i++) {
            int pos = i;
            CurrencySymbol = cartList.get(pos).getCurrencySymbol();
            double total = GeneralFunctions.parseDoubleValue(0, cartList.get(i).getfDiscountPrice());
            String[] selToppingarray = cartList.get(pos).getiToppingId().split(",");
            double toppingPrice = 0;

            if (selToppingarray != null && selToppingarray.length > 0) {
                for (int j = 0; j < selToppingarray.length; j++) {
                    toppingPrice = toppingPrice + GeneralFunctions.parseDoubleValue(0, getToppingPrice(selToppingarray[j]));
                }
            }
            double optionPrice = 0;
            String itemList = "";

            if (cartList.get(pos).getiOptionId() != null && !cartList.get(pos).getiOptionId().equalsIgnoreCase("")) {
                Options options = getOptionObject(cartList.get(pos).getiOptionId());
                if (options != null) {
                    itemList = options.getvOptionName();
                    optionPrice = GeneralFunctions.parseDoubleValue(0, options.getfUserPrice());
                }
            }
            if (cartList.get(pos).getiToppingId() != null && !cartList.get(pos).getiToppingId().equalsIgnoreCase("")) {
                for (int j = 0; j < selToppingarray.length; j++) {
                    Topping topping = getToppingObject(selToppingarray[j]);

                    if (topping != null) {
                        if (itemList.equalsIgnoreCase("")) {
                            itemList = topping.getvOptionName();
                        } else {

                            itemList = itemList + "," + topping.getvOptionName();
                        }
                    }
                }

            }
            if (getIntent().getStringExtra("ispriceshow").equalsIgnoreCase("separate")) {
                if (optionPrice == 0) {
                    total = GeneralFunctions.parseDoubleValue(0, cartList.get(i).getQty()) * (toppingPrice + total);
                } else {
                    total = GeneralFunctions.parseDoubleValue(0, cartList.get(i).getQty()) * (toppingPrice + optionPrice);
                }
            } else {
                total = GeneralFunctions.parseDoubleValue(0, cartList.get(i).getQty()) * (total + toppingPrice + optionPrice);
            }
            item = item + GeneralFunctions.parseIntegerValue(0, cartList.get(pos).getQty());
            finalTotal = finalTotal + total;
        }
        String itemPriceVal = "";
        if (item == 1) {
            itemPriceVal = generalFunc.convertNumberWithRTL(item + "") + " " + generalFunc.retrieveLangLBl("Item", "LBL_ITEM") + " | " +
                    CurrencySymbol + " " + generalFunc.convertNumberWithRTL(GeneralFunctions.convertDecimalPlaceDisplay(finalTotal));
        } else {
            itemPriceVal = generalFunc.convertNumberWithRTL(item + "") + " " + generalFunc.retrieveLangLBl("Item", "LBL_ITEMS") + " | " +
                    CurrencySymbol + " " + generalFunc.convertNumberWithRTL(GeneralFunctions.convertDecimalPlaceDisplay(finalTotal));

        }

        itemNpricecartTxt.setText(itemPriceVal);


    }

    public Options getOptionObject(String id) {
        Realm realm = MyApp.getRealmInstance();
        Options options = realm.where(Options.class).equalTo("iOptionId", id).findFirst();
        if (options != null) {
            return options;
        }
        return null;
    }

    public Topping getToppingObject(String id) {

        Realm realm = MyApp.getRealmInstance();
        Topping topping = realm.where(Topping.class).equalTo("iOptionId", id).findFirst();
        if (topping != null) {
            return topping;
        }
        return topping;

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

    public RealmResults<Cart> getCartData() {
        Realm realm = MyApp.getRealmInstance();
        return realm.where(Cart.class).findAll();
    }

    public Context getActContext() {
        return RestaurantAllDetailsNewActivity.this;
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(verticalOffset) / (float) maxScroll;

        handleAlphaOnTitle(percentage);
        handleToolbarTitleVisibility(percentage);
    }

    private void handleAlphaOnTitle(float percentage) {

        if (percentage <= PERCENTAGE_TO_HIDE_TITLE_DETAILS) {
            if (mIsTheTitleContainerVisible) {
                mIsTheTitleContainerVisible = false;
            }
        } else {
            if (!mIsTheTitleContainerVisible) {
                mIsTheTitleContainerVisible = true;
            }
        }
    }

    public Integer getNumOfColumns() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float dpWidth = (displayMetrics.widthPixels - Utils.dipToPixels(getActContext(), 10)) / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / 170);
        noOfColumns = noOfColumns < 2 ? 2 : noOfColumns;
        noOfColumns = 2;
        return noOfColumns;
    }

  /*  private void setParentCategoryLayoutManager() {
        GridLayoutManager gridLay = new GridLayoutManager(getActContext(), getNumOfColumns());
        gridLay.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (list.get(position).get("Type") != null && !list.get(position).get("Type").equalsIgnoreCase("GRID")) {
                    if (getNumOfColumns() > gridLay.getSpanCount()) {
                        return gridLay.getSpanCount();
                    } else {
                        return getNumOfColumns();
                    }
                }
                return 1;
            }
        });
        resMenuRecyclerview.setLayoutManager(gridLay);
    }*/

    public void getRestaurantDetailstest(String isVeg, boolean isLikeclick) {
        new AppFunctions(getActContext()).runGAC();

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "GetRestaurantDetails");
        parameters.put("iCompanyId", iCompanyId);
        parameters.put("iUserId", generalFunc.getMemberId());
        parameters.put("CheckNonVegFoodType", isVeg);
        if (isLikeclick) {
            parameters.put("eFavStore", isFavStore);
        }
        parameters.put("PassengerLat", getIntent().getStringExtra("lat"));
        parameters.put("PassengerLon", getIntent().getStringExtra("long"));
        parameters.put("eSystem", Utils.eSystem_Type);
        parameters.put("vLang", generalFunc.retrieveValue(Utils.LANGUAGE_CODE_KEY));

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setCancelAble(false);
        exeWebServer.setDataResponseListener(responseString -> {
            JSONObject responseObj=generalFunc.getJsonObject(responseString);
            if (responseObj != null && !responseObj.equals("")) {
                boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseObj);

                JSONArray dataClassArrayList = new JSONArray();

                if (isDataAvail) {

                    JSONObject message_obj = generalFunc.getJsonObject("message", responseObj);

                    if ("paymentMethod" != null && message_obj != null) {
                        try {
                            // dataClassArrayList = message_obj.getJSONArray("paymentMethod");
                            dataClassArrayList = message_obj.getJSONArray("paymentMethod");
                            Log.d("parameters::", "dataClassArrayList111" + dataClassArrayList);
                            for (int i = 0; i < dataClassArrayList.length(); i++) {

                                String id = dataClassArrayList.getJSONObject(i).getString("id");
                                String title = dataClassArrayList.getJSONObject(i).getString("title");


                                stringList.add(title);
                                // String amount = jsoncargo.getJsonObject(i).getInt("amount");
                                paymentTypeHashmap.put(id, title);
                                //  modelClassList.add((HashMap<String, String>) dataClassArrayList.get(i));
                            }


                            Log.d("parameters::", "modelClassList" + paymentTypeHashmap);


                        } catch (Exception var4) {
                            Log.d("parameters::", "Exception: " + var4);

                        }



                        res_pay_recucler.setAdapter(new StorePayAdapter(getActContext(), stringList));
                    }


                    if (list != null) {
                        list.clear();
                    }
                    list = new ArrayList<>();

                    eNonVegToggleDisplay = generalFunc.getJsonValueStr("eNonVegToggleDisplay", message_obj);
                    if (eNonVegToggleDisplay.equalsIgnoreCase("Yes")) {
                        VegNovegFilterArea.setVisibility(View.VISIBLE);
                        foodTypetxt.setText(generalFunc.retrieveLangLBl("", "LBL_VEG_ONLY"));
                    }

                    String eFavStore = generalFunc.getJsonValueStr("eFavStore", message_obj);
                    if (eFavStore.equalsIgnoreCase("Yes")) {
                        likeButton.setLiked(true);
                    } else {
                        likeButton.setLiked(false);
                    }

                    JSONObject companyDetails = generalFunc.getJsonObject("CompanyDetails", message_obj);
                    Restaurant_Cuisine = generalFunc.getJsonValueStr("Restaurant_Cuisine", companyDetails);
                    JSONArray restaurant_Arr = generalFunc.getJsonArray("CompanyFoodData", companyDetails);

                    setData(message_obj);

                    JSONArray Recomendation_Arr = generalFunc.getJsonArray("Recomendation_Arr", companyDetails);

                    if (Recomendation_Arr != null && Recomendation_Arr.length() > 0) {

                        String LBL_ADD=generalFunc.retrieveLangLBl("", "LBL_ADD");

                        int numOfColumn = getActContext() == null ? 2 : getNumOfColumns();
                        int heightOfImage = (int) (((Utils.getScreenPixelWidth(getActContext()) / numOfColumn) - Utils.dipToPixels(getActContext(), 16)) / 1.77777778);
                        int width = ((int) Utils.getScreenPixelWidth(getActContext()) / numOfColumn) - Utils.dipToPixels(getActContext(), 16);



                        HashMap<String, String> recommandHeaderObj = new HashMap<>();
                        recommandHeaderObj.put("Type", "HEADER");
                        recommandHeaderObj.put("menuName", generalFunc.retrieveLangLBl("", "LBL_RECOMMENDED"));
                        recommandHeaderObj.put("vMenuItemCount", Recomendation_Arr.length() + "");
                        recommandHeaderObj.put("iFoodMenuId", generalFunc.getJsonValue("iFoodMenuId", "1"));
                        recommandHeaderObj.put("LBL_ADD",LBL_ADD);
                        hashList.add(recommandHeaderObj);
                       // list.add(recommandHeaderObj);

                        for (int j = 0; j < Recomendation_Arr.length(); j++) {
                            JSONObject category_obj = generalFunc.getJsonObject(Recomendation_Arr, j);

                            HashMap<String, String> recommandMenuObj = new HashMap<>();
                            recommandMenuObj.put("Type", "GRID");
                            recommandMenuObj.put("fPrice", generalFunc.getJsonValueStr("fPrice", category_obj));
                            recommandMenuObj.put("iDisplayOrder", generalFunc.getJsonValueStr("iDisplayOrder", category_obj));
                            recommandMenuObj.put("iFoodMenuId", generalFunc.getJsonValueStr("iFoodMenuId", category_obj));
                            recommandMenuObj.put("iMenuItemId", generalFunc.getJsonValueStr("iMenuItemId", category_obj));

                            recommandMenuObj.put("heightOfImage",""+heightOfImage);


                            recommandMenuObj.put("vImage", generalFunc.getJsonValueStr("vImage", category_obj));
                            recommandMenuObj.put("vItemType", generalFunc.getJsonValueStr("vItemType", category_obj));
                            recommandMenuObj.put("vItemDesc", generalFunc.getJsonValueStr("vItemDesc", category_obj));
                            String fOfferAmt=generalFunc.getJsonValueStr("fOfferAmt", category_obj);
                            recommandMenuObj.put("fOfferAmt",fOfferAmt);
                            recommandMenuObj.put("fOfferAmtNotZero", generalFunc.parseDoubleValue(0, fOfferAmt) > 0?"Yes":"No");

                            String StrikeoutPrice=generalFunc.getJsonValueStr("StrikeoutPrice", category_obj);
                            recommandMenuObj.put("StrikeoutPrice", StrikeoutPrice);
                            recommandMenuObj.put("StrikeoutPriceConverted", generalFunc.convertNumberWithRTL(StrikeoutPrice));

                            recommandMenuObj.put("fDiscountPrice", generalFunc.getJsonValueStr("fDiscountPrice", category_obj));
                            recommandMenuObj.put("eFoodType", generalFunc.getJsonValueStr("eFoodType", category_obj));

                            String fDiscountPricewithsymbol=generalFunc.getJsonValueStr("fDiscountPricewithsymbol", category_obj);
                            recommandMenuObj.put("fDiscountPricewithsymbol", fDiscountPricewithsymbol);
                            recommandMenuObj.put("fDiscountPricewithsymbolConverted", generalFunc.convertNumberWithRTL(generalFunc.convertNumberWithRTL(fDiscountPricewithsymbol)));

                            recommandMenuObj.put("currencySymbol", generalFunc.getJsonValueStr("currencySymbol", category_obj));
                            recommandMenuObj.put("MenuItemOptionToppingArr", generalFunc.getJsonValueStr("MenuItemOptionToppingArr", category_obj));
                            recommandMenuObj.put("vCategoryName", generalFunc.getJsonValueStr("vCategoryName", category_obj));

                            String vHighlightName=generalFunc.getJsonValueStr("vHighlightName", category_obj);
                            recommandMenuObj.put("vHighlightName", vHighlightName);
                            recommandMenuObj.put("vHighlightNameLBL", generalFunc.retrieveLangLBl("",vHighlightName));

                            recommandMenuObj.put("prescription_required", generalFunc.getJsonValueStr("prescription_required", category_obj));
                            recommandMenuObj.put("LBL_ADD",LBL_ADD);

                                list.add(recommandMenuObj);


                        }

                    }



                    for (int i = 0; i < restaurant_Arr.length(); i++) {
                        String LBL_ADD=generalFunc.retrieveLangLBl("", "LBL_ADD");

                        JSONObject headerObj = generalFunc.getJsonObject(restaurant_Arr, i);
                        JSONArray categoryListObj = generalFunc.getJsonArray("menu_items", headerObj);
                        HashMap<String, String> HeaderObj = new HashMap<>();
                        HeaderObj.put("Type", "HEADER");
                        HeaderObj.put("menuName", generalFunc.getJsonValueStr("vMenu", headerObj));
                        HeaderObj.put("vMenuItemCount", generalFunc.getJsonValueStr("vMenuItemCount", headerObj));
                        HeaderObj.put("iFoodMenuId", generalFunc.getJsonValueStr("iFoodMenuId", headerObj));
                        HeaderObj.put("LBL_ADD",LBL_ADD);

                        hashList.add(HeaderObj);}
                    addTabs(hashList);
/*
                       // list.add(HeaderObj);


                        if (categoryListObj != null) {
                            for (int j = 0; j < categoryListObj.length(); j++) {
                                JSONObject category_obj = generalFunc.getJsonObject(categoryListObj, j);

                                HashMap<String, String> MenuObj = new HashMap<>();
                                MenuObj.put("Type", "LIST");

                                MenuObj.put("fPrice", generalFunc.getJsonValueStr("fPrice", category_obj));
                                MenuObj.put("iDisplayOrder", generalFunc.getJsonValueStr("iDisplayOrder", category_obj));
                                MenuObj.put("iFoodMenuId", generalFunc.getJsonValueStr("iFoodMenuId", category_obj));

                                MenuObj.put("iMenuItemId", generalFunc.getJsonValueStr("iMenuItemId", category_obj));

                                MenuObj.put("vImage", generalFunc.getJsonValueStr("vImage", category_obj));
                                MenuObj.put("vItemType", generalFunc.getJsonValueStr("vItemType", category_obj));
                                MenuObj.put("vItemDesc", generalFunc.getJsonValueStr("vItemDesc", category_obj));
                                String fOfferAmt=generalFunc.getJsonValueStr("fOfferAmt", category_obj);
                                MenuObj.put("fOfferAmt",fOfferAmt);
                                MenuObj.put("fOfferAmtNotZero", generalFunc.parseDoubleValue(0, fOfferAmt) > 0?"Yes":"No");

                                String StrikeoutPrice=generalFunc.getJsonValueStr("StrikeoutPrice", category_obj);
                                MenuObj.put("StrikeoutPrice", StrikeoutPrice);
                                MenuObj.put("StrikeoutPriceConverted", generalFunc.convertNumberWithRTL(StrikeoutPrice));

                                MenuObj.put("fDiscountPrice", generalFunc.getJsonValueStr("fDiscountPrice", category_obj));
                                MenuObj.put("eFoodType", generalFunc.getJsonValueStr("eFoodType", category_obj));

                                String fDiscountPricewithsymbol=generalFunc.getJsonValueStr("fDiscountPricewithsymbol", category_obj);
                                MenuObj.put("fDiscountPricewithsymbol", fDiscountPricewithsymbol);
                                MenuObj.put("fDiscountPricewithsymbolConverted", generalFunc.convertNumberWithRTL(generalFunc.convertNumberWithRTL(fDiscountPricewithsymbol)));

                                MenuObj.put("currencySymbol", generalFunc.getJsonValueStr("currencySymbol", category_obj));
                                MenuObj.put("MenuItemOptionToppingArr", generalFunc.getJsonValueStr("MenuItemOptionToppingArr", category_obj));

                                String vHighlightName=generalFunc.getJsonValueStr("vHighlightName", category_obj);
                                MenuObj.put("vHighlightName", vHighlightName);
                                MenuObj.put("vHighlightNameLBL", generalFunc.retrieveLangLBl("",vHighlightName));


                                MenuObj.put("prescription_required", generalFunc.getJsonValueStr("prescription_required", category_obj));
                                MenuObj.put("LBL_ADD",LBL_ADD);

                                MenuObj.put("isLastLine", "Yes");

                                if (j == categoryListObj.length() - 1 && i != restaurant_Arr.length() - 1) {
                                    MenuObj.put("isLastLine", "No");
                                }


                                if (!generalFunc.getJsonValueStr("vImage", category_obj).equalsIgnoreCase("")){
                                list.add(MenuObj);}
                            }
                        }
                    }
*/
                    setAadapter();

                    if (menuContainer.getChildCount() > 0) {
                        menuContainer.removeAllViews();
                    }
                    addMenuViewdata();
                } else {
                    //  mBiodataExapandable.notifyDataSetChanged();
                }
            } else {
                generalFunc.showError();
            }
        });
        exeWebServer.execute();
    }

    public void getRestaurantDetails(String isVeg, boolean isLikeclick) {
        new AppFunctions(getActContext()).runGAC();

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "GetRestaurantDetails");
        parameters.put("iCompanyId", iCompanyId);
        parameters.put("iUserId", generalFunc.getMemberId());
        parameters.put("CheckNonVegFoodType", isVeg);
        if (isLikeclick) {
            parameters.put("eFavStore", isFavStore);
        }
        parameters.put("PassengerLat", getIntent().getStringExtra("lat"));
        parameters.put("PassengerLon", getIntent().getStringExtra("long"));
        parameters.put("eSystem", Utils.eSystem_Type);
        parameters.put("vLang", generalFunc.retrieveValue(Utils.LANGUAGE_CODE_KEY));

        Log.d("parameters::", "parameters:::" + parameters);

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);

        exeWebServer.setCancelAble(false);
        exeWebServer.setDataResponseListener(responseString -> {
            println("here i am in responce" + responseString);
            JSONObject responseObj = generalFunc.getJsonObject(responseString);
            Log.d("parameters::", "responseObj:::" + responseObj);


            println("here i am in responce 2" + responseString);
            if (responseObj != null && !responseObj.equals("")) {
                println("here i am in responce 3" + responseString);

                boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseObj);

                ArrayList<HashMap> hashList = new ArrayList<>();
                JSONArray dataClassArrayList = new JSONArray();
                paymentTypeHashmap = new HashMap<>();


                if (isDataAvail) {

                    JSONObject message_obj = generalFunc.getJsonObject("message", responseObj);
                    Log.d("parameters::", "message_obj:::" + message_obj);

//                    dataClassArrayList= generalFunc.getJsonArray("paymentMethod",message_obj);

                    if ("paymentMethod" != null && message_obj != null) {
                        try {
                            // dataClassArrayList = message_obj.getJSONArray("paymentMethod");
                            dataClassArrayList = message_obj.getJSONArray("paymentMethod");
                            Log.d("parameters::", "dataClassArrayList111" + dataClassArrayList);
                            for (int i = 0; i < dataClassArrayList.length(); i++) {

                                String id = dataClassArrayList.getJSONObject(i).getString("id");
                                String title = dataClassArrayList.getJSONObject(i).getString("title");


                                stringList.add(title);
                                // String amount = jsoncargo.getJsonObject(i).getInt("amount");
                                paymentTypeHashmap.put(id, title);
                                //  modelClassList.add((HashMap<String, String>) dataClassArrayList.get(i));
                            }


                            Log.d("parameters::", "modelClassList" + paymentTypeHashmap);


                        } catch (Exception var4) {
                            Log.d("parameters::", "Exception: " + var4);

                        }



                        res_pay_recucler.setAdapter(new StorePayAdapter(getActContext(), stringList));
                    }


                    //  Log.d("parameters::","dataClassArrayList:::"+ generalFunc.getJsonValueStr("paymentMethod", message_obj));


                    if (list != null) {
                        list.clear();
                    }
                    list = new ArrayList<>();

                    eNonVegToggleDisplay = generalFunc.getJsonValueStr("eNonVegToggleDisplay", message_obj);
                    if (eNonVegToggleDisplay.equalsIgnoreCase("Yes")) {
                        VegNovegFilterArea.setVisibility(View.VISIBLE);
                        foodTypetxt.setText(generalFunc.retrieveLangLBl("", "LBL_VEG_ONLY"));
                    }

                    String eFavStore = generalFunc.getJsonValueStr("eFavStore", message_obj);
                    if (eFavStore.equalsIgnoreCase("Yes")) {
                        likeButton.setLiked(true);
                    } else {
                        likeButton.setLiked(false);
                    }

                    JSONObject companyDetails = generalFunc.getJsonObject("CompanyDetails", message_obj);

                    //  Log.d("parameters::","companyDetails"+generalFunc.getJsonArray("paymentMethod", companyDetails));

                    Restaurant_Cuisine = generalFunc.getJsonValueStr("Restaurant_Cuisine", companyDetails);
                    JSONArray restaurant_Arr = generalFunc.getJsonArray("CompanyFoodData", companyDetails);
                    //  Log.d("parameters::","companyDetails:::"+companyDetails);
                    // Log.d("parameters::","Restaurant_Cuisine:::"+Restaurant_Cuisine);
                    //  Log.d("parameters::","restaurant_Arr:::"+restaurant_Arr);

                    setData(message_obj);

                    JSONArray Recomendation_Arr = generalFunc.getJsonArray("Recomendation_Arr", companyDetails);
                    Log.d("parameters::", "Recomendation_Arr::: " + Recomendation_Arr);


                    if (Recomendation_Arr != null && Recomendation_Arr.length() > 0) {

                        String LBL_ADD = generalFunc.retrieveLangLBl("", "LBL_ADD");

                        int numOfColumn = getActContext() == null ? 2 : getNumOfColumns();

                        int heightOfImage = 720;
                        int width = 1080;


                        HashMap<String, String> recommandHeaderObj = new HashMap<>();
                        recommandHeaderObj.put("Type", "HEADER");
                        recommandHeaderObj.put("menuName", generalFunc.retrieveLangLBl("", "LBL_RECOMMENDED"));
                        recommandHeaderObj.put("vMenuItemCount", Recomendation_Arr.length() + "");
                        recommandHeaderObj.put("iFoodMenuId", generalFunc.getJsonValue("iFoodMenuId", "1"));
                        recommandHeaderObj.put("LBL_ADD", LBL_ADD);


                        //juniad comment
//                        list.add(recommandHeaderObj);
                        hashList.add(recommandHeaderObj);
//old
                        boolean isone= true;
                        int i=0;
                        setAadapter();
                        int time = (int) System.currentTimeMillis();
                        for (int j = 0; j < Recomendation_Arr.length(); j++) {
                            JSONObject category_obj = generalFunc.getJsonObject(Recomendation_Arr, j);

                            i++;

                            Log.d("searchListsearchList", "catagory::" + category_obj);
                            Log.d("searchListsearchList", "iFoodMenuId::" + generalFunc.getJsonValueStr("iFoodMenuId", category_obj));


                            HashMap<String, String> recommandMenuObj = new HashMap<>();
                            //  recommandMenuObj.put("Type", "GRID");
                            recommandMenuObj.put("Type", "LIST");


                            recommandMenuObj.put("fPrice", generalFunc.getJsonValueStr("fPrice", category_obj));
                            recommandMenuObj.put("iDisplayOrder", generalFunc.getJsonValueStr("iDisplayOrder", category_obj));
                            recommandMenuObj.put("iFoodMenuId", generalFunc.getJsonValueStr("iFoodMenuId", category_obj));
                            recommandMenuObj.put("RegistrationDate", generalFunc.getJsonValueStr("RegistrationDate", category_obj));
                            recommandMenuObj.put("iMenuItemId", generalFunc.getJsonValueStr("iMenuItemId", category_obj));
                           // recommandMenuObj.put("vImage", Utils.getResizeImgURL(getActContext(), generalFunc.getJsonValueStr("vImage", category_obj), width, heightOfImage));
                            recommandMenuObj.put("vImage", generalFunc.getJsonValueStr("vImage", category_obj));
                            recommandMenuObj.put("heightOfImage", "" + heightOfImage);

                            recommandMenuObj.put("vItemType", generalFunc.getJsonValueStr("vItemType", category_obj));
                            recommandMenuObj.put("vItemDesc", generalFunc.getJsonValueStr("vItemDesc", category_obj));
                            String fOfferAmt = generalFunc.getJsonValueStr("fOfferAmt", category_obj);
                            recommandMenuObj.put("fOfferAmt", fOfferAmt);
                            recommandMenuObj.put("fOfferAmtNotZero", generalFunc.parseDoubleValue(0, fOfferAmt) > 0 ? "Yes" : "No");

                            String StrikeoutPrice = generalFunc.getJsonValueStr("StrikeoutPrice", category_obj);
                            recommandMenuObj.put("StrikeoutPrice", StrikeoutPrice);
                            recommandMenuObj.put("StrikeoutPriceConverted", generalFunc.convertNumberWithRTL(StrikeoutPrice));

                            recommandMenuObj.put("fDiscountPrice", generalFunc.getJsonValueStr("fDiscountPrice", category_obj));
                            recommandMenuObj.put("eFoodType", generalFunc.getJsonValueStr("eFoodType", category_obj));

                            String fDiscountPricewithsymbol = generalFunc.getJsonValueStr("fDiscountPricewithsymbol", category_obj);
                            recommandMenuObj.put("fDiscountPricewithsymbol", fDiscountPricewithsymbol);
                            recommandMenuObj.put("fDiscountPricewithsymbolConverted", generalFunc.convertNumberWithRTL(generalFunc.convertNumberWithRTL(fDiscountPricewithsymbol)));

                            recommandMenuObj.put("currencySymbol", generalFunc.getJsonValueStr("currencySymbol", category_obj));
                            recommandMenuObj.put("MenuItemOptionToppingArr", generalFunc.getJsonValueStr("MenuItemOptionToppingArr", category_obj));
                            recommandMenuObj.put("vCategoryName", generalFunc.getJsonValueStr("vCategoryName", category_obj));

                            String vHighlightName = generalFunc.getJsonValueStr("vHighlightName", category_obj);
                            recommandMenuObj.put("vHighlightName", vHighlightName);
                            recommandMenuObj.put("vHighlightNameLBL", generalFunc.retrieveLangLBl("", vHighlightName));

                            recommandMenuObj.put("prescription_required", generalFunc.getJsonValueStr("prescription_required", category_obj));
                            recommandMenuObj.put("LBL_ADD", LBL_ADD);


                            list.add(recommandMenuObj);

                            Log.d("listsizeeeeeeeee", "infor::" + list);

                            println("here i am in responce 9" + responseString);

                            if (isone){

                                isone= false;

                            }
                            else {
                                restaurantmenuAdapter1.update(list);
                            }
                        }


                        Log.d("listsizeeeeeeeee", "stopgridfor::");
                        Log.d("listsizeeeeeeeee", "count::"+i);
                        Log.d("listsizeeeeeeeee", "time::"+time);
                    }


                    println("here i am in responce 10" + responseString);
                    Log.d("listsizeeeeeeeee", "calladapter::");
//byumar

                    addTabs(hashList);


                    if (menuContainer.getChildCount() > 0) {
                        menuContainer.removeAllViews();
                        println("here i am in responce 4" + responseString);

                    }
                    addMenuViewdata();
                    println("here i am in responce 5" + responseString);

                } else {
                    //  mBiodataExapandable.notifyDataSetChanged();
                    println("here i am in responce 6" + responseString);

                }
            } else {
                println("here i am in responce 7" + responseString);

                generalFunc.showError();
            }
        });
        //by umar
        exeWebServer.execute();
        println("here i am in responce 8");

    }

    public void GetRestaurantDetailsPagination(String isVeg, boolean isLikeclick, String page) {
        new AppFunctions(getActContext()).runGAC();

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "GetRestaurantDetailsPagination");
        parameters.put("iCompanyId", iCompanyId);
        parameters.put("iUserId", generalFunc.getMemberId());
        parameters.put("CheckNonVegFoodType", isVeg);
        parameters.put("page", page);
        if (isLikeclick) {
            parameters.put("eFavStore", isFavStore);
        }

        parameters.put("PassengerLat", getIntent().getStringExtra("lat"));
        parameters.put("PassengerLon", getIntent().getStringExtra("long"));
        parameters.put("eSystem", Utils.eSystem_Type);
        parameters.put("vLang", generalFunc.retrieveValue(Utils.LANGUAGE_CODE_KEY));

        Log.d("parameters::", "parameters:::" + parameters);

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);

        exeWebServer.setCancelAble(false);
        exeWebServer.setDataResponseListener(responseString -> {
            println("here i am in responce" + responseString);
            JSONObject responseObj = generalFunc.getJsonObject(responseString);
            Log.d("parameters::", "responseObj:::" + responseObj);


            println("here i am in responce 2" + responseString);
            if (responseObj != null && !responseObj.equals("")) {
                println("here i am in responce 3" + responseString);

                boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseObj);

                ArrayList<HashMap> hashList = new ArrayList<>();
                JSONArray dataClassArrayList = new JSONArray();
                paymentTypeHashmap = new HashMap<>();


                if (isDataAvail) {


                    JSONObject message_obj = generalFunc.getJsonObject("message", responseObj);
                    Log.d("parameters::", "message_obj:::" + message_obj);


                    try {
                        totalPage = message_obj.getJSONArray("totalPages").getString(0);
                        nextPage = message_obj.getJSONArray("nextPage").getString(0);
                        Log.d("parameters::", "total::" + totalPage);
                        Log.d("parameters::", "next" + nextPage);
                        if (!nextPage.equals("") && !nextPage.equals("0")) {
                            // loadMore = false;
                            isNextPageAvailable = true;
                        } else {
                            // loadMore = true;
                            isNextPageAvailable = false;

                        }


                    } catch (Exception var4) {
                        Log.d("parameters::", "Exception: " + var4);

                    }


//                    dataClassArrayList= generalFunc.getJsonArray("paymentMethod",message_obj);

                    if ("paymentMethod" != null && message_obj != null) {
                        try {
                            // dataClassArrayList = message_obj.getJSONArray("paymentMethod");
                            dataClassArrayList = message_obj.getJSONArray("paymentMethod");
                            Log.d("parameters::", "dataClassArrayList111" + dataClassArrayList);
                            for (int i = 0; i < dataClassArrayList.length(); i++) {

                                String id = dataClassArrayList.getJSONObject(i).getString("id");
                                String title = dataClassArrayList.getJSONObject(i).getString("title");


                                stringList.add(title);
                                // String amount = jsoncargo.getJsonObject(i).getInt("amount");
                                paymentTypeHashmap.put(id, title);
                                //  modelClassList.add((HashMap<String, String>) dataClassArrayList.get(i));
                            }


                            Log.d("parameters::", "modelClassList" + paymentTypeHashmap);


                        } catch (Exception var4) {
                            Log.d("parameters::", "Exception: " + var4);

                        }

                        LinearLayoutManager layoutManager
                                = new LinearLayoutManager(this, LinearLayoutManager.
                                VERTICAL, false);
                        res_pay_recucler.setLayoutManager(layoutManager);
                        res_pay_recucler.setLayoutManager(new GridLayoutManager(this, 2));
                        res_pay_recucler.setAdapter(new StorePayAdapter(getActContext(), stringList));
                    }


                    //  Log.d("parameters::","dataClassArrayList:::"+ generalFunc.getJsonValueStr("paymentMethod", message_obj));


                    if (list != null) {
                        list.clear();
                    }
                    list = new ArrayList<>();

                    eNonVegToggleDisplay = generalFunc.getJsonValueStr("eNonVegToggleDisplay", message_obj);
                    if (eNonVegToggleDisplay.equalsIgnoreCase("Yes")) {
                        VegNovegFilterArea.setVisibility(View.VISIBLE);
                        foodTypetxt.setText(generalFunc.retrieveLangLBl("", "LBL_VEG_ONLY"));
                    }

                    String eFavStore = generalFunc.getJsonValueStr("eFavStore", message_obj);
                    if (eFavStore.equalsIgnoreCase("Yes")) {
                        likeButton.setLiked(true);
                    } else {
                        likeButton.setLiked(false);
                    }

                    JSONObject companyDetails = generalFunc.getJsonObject("CompanyDetails", message_obj);

                    //  Log.d("parameters::","companyDetails"+generalFunc.getJsonArray("paymentMethod", companyDetails));

                    Restaurant_Cuisine = generalFunc.getJsonValueStr("Restaurant_Cuisine", companyDetails);
                    JSONArray restaurant_Arr = generalFunc.getJsonArray("CompanyFoodData", companyDetails);
                    //  Log.d("parameters::","companyDetails:::"+companyDetails);
                    // Log.d("parameters::","Restaurant_Cuisine:::"+Restaurant_Cuisine);
                    //  Log.d("parameters::","restaurant_Arr:::"+restaurant_Arr);
                    setData(message_obj);
                    JSONArray Recomendation_Arr = generalFunc.getJsonArray("Recomendation_Arr", companyDetails);
                    Log.d("parameters::", "Recomendation_Arr::: " + Recomendation_Arr);
                    if (Recomendation_Arr != null && Recomendation_Arr.length() > 0) {

                        String LBL_ADD = generalFunc.retrieveLangLBl("", "LBL_ADD");

                        int numOfColumn = getActContext() == null ? 2 : getNumOfColumns();

                        int heightOfImage = 1280;
                        int width = 1080;


                        HashMap<String, String> recommandHeaderObj = new HashMap<>();
                        recommandHeaderObj.put("Type", "HEADER");
                        recommandHeaderObj.put("menuName", generalFunc.retrieveLangLBl("", "LBL_RECOMMENDED"));
                        recommandHeaderObj.put("vMenuItemCount", Recomendation_Arr.length() + "");
                        recommandHeaderObj.put("iFoodMenuId", generalFunc.getJsonValue("iFoodMenuId", "1"));
                        recommandHeaderObj.put("LBL_ADD", LBL_ADD);


                        //juniad comment
//                        list.add(recommandHeaderObj);
                        hashList.add(recommandHeaderObj);
//old
                        for (int j = 0; j < Recomendation_Arr.length(); j++) {
                            JSONObject category_obj = generalFunc.getJsonObject(Recomendation_Arr, j);


                            Log.d("searchListsearchList", "catagory::" + category_obj);
                            Log.d("searchListsearchList", "iFoodMenuId::" + generalFunc.getJsonValueStr("iFoodMenuId", category_obj));


                            HashMap<String, String> recommandMenuObj = new HashMap<>();
                            //  recommandMenuObj.put("Type", "GRID");
                            recommandMenuObj.put("Type", "LIST");


                            recommandMenuObj.put("fPrice", generalFunc.getJsonValueStr("fPrice", category_obj));
                            recommandMenuObj.put("iDisplayOrder", generalFunc.getJsonValueStr("iDisplayOrder", category_obj));
                            recommandMenuObj.put("iFoodMenuId", generalFunc.getJsonValueStr("iFoodMenuId", category_obj));
                            recommandMenuObj.put("RegistrationDate", generalFunc.getJsonValueStr("RegistrationDate", category_obj));
                            recommandMenuObj.put("iMenuItemId", generalFunc.getJsonValueStr("iMenuItemId", category_obj));
                            recommandMenuObj.put("vImage", Utils.getResizeImgURL(getActContext(), generalFunc.getJsonValueStr("vImage", category_obj), width, heightOfImage));
                            recommandMenuObj.put("heightOfImage", "" + heightOfImage);
                            //  Log.d("searchListsearchList", "vImage::" + Utils.getResizeImgURL(getActContext(), generalFunc.getJsonValueStr("vImage", category_obj), width, heightOfImage));
//                            recommandMenuObj.put("vImage", generalFunc.getJsonValData:ueStr("vImage", category_obj));
                            recommandMenuObj.put("vItemType", generalFunc.getJsonValueStr("vItemType", category_obj));
                            recommandMenuObj.put("vItemDesc", generalFunc.getJsonValueStr("vItemDesc", category_obj));
                            recommandMenuObj.put("Re", generalFunc.getJsonValueStr("vItemDesc", category_obj));
                            String fOfferAmt = generalFunc.getJsonValueStr("fOfferAmt", category_obj);
                            recommandMenuObj.put("fOfferAmt", fOfferAmt);
                            recommandMenuObj.put("fOfferAmtNotZero", generalFunc.parseDoubleValue(0, fOfferAmt) > 0 ? "Yes" : "No");

                            String StrikeoutPrice = generalFunc.getJsonValueStr("StrikeoutPrice", category_obj);
                            recommandMenuObj.put("StrikeoutPrice", StrikeoutPrice);
                            recommandMenuObj.put("StrikeoutPriceConverted", generalFunc.convertNumberWithRTL(StrikeoutPrice));

                            recommandMenuObj.put("fDiscountPrice", generalFunc.getJsonValueStr("fDiscountPrice", category_obj));
                            recommandMenuObj.put("eFoodType", generalFunc.getJsonValueStr("eFoodType", category_obj));

                            String fDiscountPricewithsymbol = generalFunc.getJsonValueStr("fDiscountPricewithsymbol", category_obj);
                            recommandMenuObj.put("fDiscountPricewithsymbol", fDiscountPricewithsymbol);
                            recommandMenuObj.put("fDiscountPricewithsymbolConverted", generalFunc.convertNumberWithRTL(generalFunc.convertNumberWithRTL(fDiscountPricewithsymbol)));

                            recommandMenuObj.put("currencySymbol", generalFunc.getJsonValueStr("currencySymbol", category_obj));
                            recommandMenuObj.put("MenuItemOptionToppingArr", generalFunc.getJsonValueStr("MenuItemOptionToppingArr", category_obj));
                            recommandMenuObj.put("vCategoryName", generalFunc.getJsonValueStr("vCategoryName", category_obj));

                            String vHighlightName = generalFunc.getJsonValueStr("vHighlightName", category_obj);
                            recommandMenuObj.put("vHighlightName", vHighlightName);
                            recommandMenuObj.put("vHighlightNameLBL", generalFunc.retrieveLangLBl("", vHighlightName));

                            recommandMenuObj.put("prescription_required", generalFunc.getJsonValueStr("prescription_required", category_obj));
                            recommandMenuObj.put("LBL_ADD", LBL_ADD);


                            list.add(recommandMenuObj);

                            Log.d("listsizeeeeeeeee", "infor::" + list);

                            println("here i am in responce 9" + responseString);


                        }

                        Log.d("listsizeeeeeeeee", "stopgridfor::");
                    }
                    println("here i am in responce 10" + responseString);
                    Log.d("listsizeeeeeeeee", "calladapter::");
//byumar

                    setAadapter();
                    addTabs(hashList);
                    if (menuContainer.getChildCount() > 0) {
                        menuContainer.removeAllViews();
                        println("here i am in responce 4" + responseString);

                    }
                    addMenuViewdata();
                    println("here i am in responce 5" + responseString);

                } else {
                    //  mBiodataExapandable.notifyDataSetChanged();
                    println("here i am in responce 6" + responseString);

                }
            } else {
                println("here i am in responce 7" + responseString);

                generalFunc.showError();
            }
        });
        //by umar
        exeWebServer.execute();
        println("here i am in responce 8");

    }

    public void GetRestaurantDetailsPaginationSecondTime(String isVeg, boolean isLikeclick, String page) {
        new AppFunctions(getActContext()).runGAC();

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "GetRestaurantDetailsPagination");
        parameters.put("iCompanyId", iCompanyId);
        parameters.put("iUserId", generalFunc.getMemberId());
        parameters.put("CheckNonVegFoodType", isVeg);
        parameters.put("page", page);
        if (isLikeclick) {
            parameters.put("eFavStore", isFavStore);
        }

        parameters.put("PassengerLat", getIntent().getStringExtra("lat"));
        parameters.put("PassengerLon", getIntent().getStringExtra("long"));
        parameters.put("eSystem", Utils.eSystem_Type);
        parameters.put("vLang", generalFunc.retrieveValue(Utils.LANGUAGE_CODE_KEY));

        Log.d("parameters::", "parameters:::" + parameters);

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);

        exeWebServer.setLoader(getActContext(), generalFunc);

        exeWebServer.setCancelAble(false);
        exeWebServer.setDataResponseListener(responseString -> {
            println("here i am in responce" + responseString);
            JSONObject responseObj = generalFunc.getJsonObject(responseString);
            Log.d("parameters::", "responseObj:::" + responseObj);


            println("here i am in responce 2" + responseString);
            if (responseObj != null && !responseObj.equals("")) {
                println("here i am in responce 3" + responseString);

                boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseObj);


                ArrayList<HashMap> hashList = new ArrayList<>();
                paymentTypeHashmap = new HashMap<>();


                if (isDataAvail) {

                    JSONObject message_obj = generalFunc.getJsonObject("message", responseObj);
                    Log.d("parameters::", "message_obj:::" + message_obj);


                    try {
                        totalPage = message_obj.getJSONArray("totalPages").getString(0);
                        nextPage = message_obj.getJSONArray("nextPage").getString(0);
                        Log.d("parameters::", "total::" + totalPage);
                        Log.d("parameters::", "next" + nextPage);
                        if (!nextPage.equals("") && !nextPage.equals("0")) {
                            // loadMore = false;
                            isNextPageAvailable = true;
                        } else {
                            // loadMore = true;
                            isNextPageAvailable = false;

                        }


                    } catch (Exception var4) {
                        Log.d("parameters::", "Exception: " + var4);

                    }


                    if (list != null) {
                        list.clear();
                    }
                    list = new ArrayList<>();

                    eNonVegToggleDisplay = generalFunc.getJsonValueStr("eNonVegToggleDisplay", message_obj);
                    if (eNonVegToggleDisplay.equalsIgnoreCase("Yes")) {
                        VegNovegFilterArea.setVisibility(View.VISIBLE);
                        foodTypetxt.setText(generalFunc.retrieveLangLBl("", "LBL_VEG_ONLY"));
                    }

                    String eFavStore = generalFunc.getJsonValueStr("eFavStore", message_obj);
                    if (eFavStore.equalsIgnoreCase("Yes")) {
                        likeButton.setLiked(true);
                    } else {
                        likeButton.setLiked(false);
                    }

                    JSONObject companyDetails = generalFunc.getJsonObject("CompanyDetails", message_obj);


                    Restaurant_Cuisine = generalFunc.getJsonValueStr("Restaurant_Cuisine", companyDetails);
                    JSONArray restaurant_Arr = generalFunc.getJsonArray("CompanyFoodData", companyDetails);


                    JSONArray Recomendation_Arr = generalFunc.getJsonArray("Recomendation_Arr", companyDetails);
                    Log.d("parameters::", "Recomendation_Arr::: " + Recomendation_Arr);


                    if (Recomendation_Arr != null && Recomendation_Arr.length() > 0) {

                        String LBL_ADD = generalFunc.retrieveLangLBl("", "LBL_ADD");

                        int numOfColumn = getActContext() == null ? 2 : getNumOfColumns();

                        int heightOfImage = 1280;
                        int width = 1080;


                        HashMap<String, String> recommandHeaderObj = new HashMap<>();
                        recommandHeaderObj.put("Type", "HEADER");
                        recommandHeaderObj.put("menuName", generalFunc.retrieveLangLBl("", "LBL_RECOMMENDED"));
                        recommandHeaderObj.put("vMenuItemCount", Recomendation_Arr.length() + "");
                        recommandHeaderObj.put("iFoodMenuId", generalFunc.getJsonValue("iFoodMenuId", "1"));
                        recommandHeaderObj.put("LBL_ADD", LBL_ADD);


                        //juniad comment
//                        list.add(recommandHeaderObj);
                        // hashList.add(recommandHeaderObj);
//old
                        for (int j = 0; j < Recomendation_Arr.length(); j++) {
                            JSONObject category_obj = generalFunc.getJsonObject(Recomendation_Arr, j);


                            Log.d("searchListsearchList", "catagory::" + category_obj);
                            Log.d("searchListsearchList", "iFoodMenuId::" + generalFunc.getJsonValueStr("iFoodMenuId", category_obj));


                            HashMap<String, String> recommandMenuObj = new HashMap<>();
                            //  recommandMenuObj.put("Type", "GRID");
                            recommandMenuObj.put("Type", "LIST");


                            recommandMenuObj.put("fPrice", generalFunc.getJsonValueStr("fPrice", category_obj));
                            recommandMenuObj.put("iDisplayOrder", generalFunc.getJsonValueStr("iDisplayOrder", category_obj));
                            recommandMenuObj.put("iFoodMenuId", generalFunc.getJsonValueStr("iFoodMenuId", category_obj));
                            recommandMenuObj.put("RegistrationDate", generalFunc.getJsonValueStr("RegistrationDate", category_obj));
                            recommandMenuObj.put("iMenuItemId", generalFunc.getJsonValueStr("iMenuItemId", category_obj));
                            recommandMenuObj.put("vImage", Utils.getResizeImgURL(getActContext(), generalFunc.getJsonValueStr("vImage", category_obj), width, heightOfImage));
                            recommandMenuObj.put("heightOfImage", "" + heightOfImage);
                            //  Log.d("searchListsearchList", "vImage::" + Utils.getResizeImgURL(getActContext(), generalFunc.getJsonValueStr("vImage", category_obj), width, heightOfImage));
//                            recommandMenuObj.put("vImage", generalFunc.getJsonValData:ueStr("vImage", category_obj));
                            recommandMenuObj.put("vItemType", generalFunc.getJsonValueStr("vItemType", category_obj));
                            recommandMenuObj.put("vItemDesc", generalFunc.getJsonValueStr("vItemDesc", category_obj));
                            recommandMenuObj.put("Re", generalFunc.getJsonValueStr("vItemDesc", category_obj));
                            String fOfferAmt = generalFunc.getJsonValueStr("fOfferAmt", category_obj);
                            recommandMenuObj.put("fOfferAmt", fOfferAmt);
                            recommandMenuObj.put("fOfferAmtNotZero", generalFunc.parseDoubleValue(0, fOfferAmt) > 0 ? "Yes" : "No");

                            String StrikeoutPrice = generalFunc.getJsonValueStr("StrikeoutPrice", category_obj);
                            recommandMenuObj.put("StrikeoutPrice", StrikeoutPrice);
                            recommandMenuObj.put("StrikeoutPriceConverted", generalFunc.convertNumberWithRTL(StrikeoutPrice));

                            recommandMenuObj.put("fDiscountPrice", generalFunc.getJsonValueStr("fDiscountPrice", category_obj));
                            recommandMenuObj.put("eFoodType", generalFunc.getJsonValueStr("eFoodType", category_obj));

                            String fDiscountPricewithsymbol = generalFunc.getJsonValueStr("fDiscountPricewithsymbol", category_obj);
                            recommandMenuObj.put("fDiscountPricewithsymbol", fDiscountPricewithsymbol);
                            recommandMenuObj.put("fDiscountPricewithsymbolConverted", generalFunc.convertNumberWithRTL(generalFunc.convertNumberWithRTL(fDiscountPricewithsymbol)));

                            recommandMenuObj.put("currencySymbol", generalFunc.getJsonValueStr("currencySymbol", category_obj));
                            recommandMenuObj.put("MenuItemOptionToppingArr", generalFunc.getJsonValueStr("MenuItemOptionToppingArr", category_obj));
                            recommandMenuObj.put("vCategoryName", generalFunc.getJsonValueStr("vCategoryName", category_obj));

                            String vHighlightName = generalFunc.getJsonValueStr("vHighlightName", category_obj);
                            recommandMenuObj.put("vHighlightName", vHighlightName);
                            recommandMenuObj.put("vHighlightNameLBL", generalFunc.retrieveLangLBl("", vHighlightName));

                            recommandMenuObj.put("prescription_required", generalFunc.getJsonValueStr("prescription_required", category_obj));
                            recommandMenuObj.put("LBL_ADD", LBL_ADD);


                            list.add(recommandMenuObj);

                            Log.d("listsizeeeeeeeee", "infor::" + list);

                            println("here i am in responce 9" + responseString);


                        }
                        //by umar


                        // resMenuRecyclerview.getRecycledViewPool().clear();

                        Log.d("listsizeeeeeeeee", "stopgridfor::");
                    }


                    restaurantmenuAdapter1.notifyDataSetChanged();
                    println("here i am in responce 10" + responseString);
                    Log.d("listsizeeeeeeeee", "calladapter::");
//byumar


                    if (menuContainer.getChildCount() > 0) {
                        menuContainer.removeAllViews();
                        println("here i am in responce 4" + responseString);

                    }
                    //   addMenuViewdata();
                    println("here i am in responce 5" + responseString);

                } else {
                    //  mBiodataExapandable.notifyDataSetChanged();
                    println("here i am in responce 6" + responseString);

                }


            } else {
                println("here i am in responce 7" + responseString);

                generalFunc.showError();
            }
        });
        //by umar
        exeWebServer.execute();
        println("here i am in responce 8");


    }

    public void addMenuViewdata() {
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                int pos = i;

                if (list.get(i).get("Type").equals("HEADER")) {
                    LayoutInflater inflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View view = inflater.inflate(R.layout.item_menu_design, null);
                    TextView rowName = (TextView) view.findViewById(R.id.rowName);
                    TextView rowCnt = (TextView) view.findViewById(R.id.rowCnt);
                    TextView postxt = (TextView) view.findViewById(R.id.postxt);
                    ImageView selView = (ImageView) view.findViewById(R.id.selView);
                    View rowview = (View) view.findViewById(R.id.rowview);
                    rowview.setVisibility(View.VISIBLE);

                    if (selMenupos == pos) {
                        rowName.setTextColor(getActContext().getResources().getColor(R.color.appThemeColor_1));
                        rowCnt.setTextColor(getActContext().getResources().getColor(R.color.appThemeColor_1));
                        selView.setVisibility(View.VISIBLE);

                    } else {
                        rowName.setTextColor(getActContext().getResources().getColor(R.color.black));
                        rowCnt.setTextColor(getActContext().getResources().getColor(R.color.black));
                        selView.setVisibility(View.INVISIBLE);

                    }

                    LinearLayout rowArea = (LinearLayout) view.findViewById(R.id.rowArea);
                    rowArea.setOnClickListener(v -> {
                        fabMenuLayout.setVisibility(View.GONE);
                        int selpos = GeneralFunctions.parseIntegerValue(0, postxt.getText().toString());
                        selMenupos = selpos;

                        ((AppBarLayout) findViewById(R.id.app_bar_layout)).setExpanded(false);

                        //junaid comment
//                        ((GridLayoutManager) resMenuRecyclerview.getLayoutManager()).scrollToPositionWithOffset(selMenupos, 0);

                        if (menuContainer.getChildCount() > 0) {
                            menuContainer.removeAllViews();
                        }
                        addMenuViewdata();
                    });
                    rowName.setText(list.get(i).get("menuName"));
                    Toast.makeText(RestaurantAllDetailsNewActivity.this, list.get(i).get("menuName"), Toast.LENGTH_SHORT).show();
                    rowCnt.setText(generalFunc.convertNumberWithRTL(list.get(i).get("vMenuItemCount")));
                    postxt.setText(i + "");
                    menuContainer.addView(view);
                }
            }
        } else {
            restaurantViewFloatingBtn.setVisibility(View.GONE);
        }
    }

    public void setAadapter() {


        Log.d("listsizeeeeeeeee", "setAdapter::");
        setRecoAdapter(list);

        restaurantmenuAdapter1 = new RestaurantmenuAdapter2(getActContext(), list, generalFunc, false);
        resMenuRecyclerview.setAdapter(restaurantmenuAdapter1);

        restaurantmenuAdapter1.setOnItemClickListener(this);




}

    public void removeNextPageConfig() {
        next_page_str = "";
        mIsLoading = false;
        isNextPageAvailable = false;
    }

    public void tabfilter(ArrayList<HashMap<String, String>> filterlist) {
        restaurantmenuAdapter1 = new RestaurantmenuAdapter2(getActContext(), filterlist, generalFunc, false);
        resMenuRecyclerview.setAdapter(restaurantmenuAdapter1);
        restaurantmenuAdapter1.setOnItemClickListener(this);

    }

    public void setData(JSONObject message) {

        vCompany = generalFunc.getJsonValueStr("vCompany", message);
        iMaxItemQty = generalFunc.getJsonValueStr("iMaxItemQty", message);
        fMinOrderValue = generalFunc.getJsonValueStr("fMinOrderValue", message);
        restcusineNameTxt.setText(Restaurant_Cuisine);

        String vAvgRating = generalFunc.getJsonValueStr("vAvgRating", message);
        if (vAvgRating != null && !vAvgRating.equalsIgnoreCase("") && !vAvgRating.equalsIgnoreCase("0")) {
            ratingCntTxt.setText(generalFunc.convertNumberWithRTL(vAvgRating));
        } else {
            ratingCntTxt.setText(generalFunc.convertNumberWithRTL("0.0"));
        }

        String fPricePerPerson = generalFunc.getJsonValueStr("fPricePerPerson", message);
        if (fPricePerPerson != null && !fPricePerPerson.equalsIgnoreCase("")) {
            minOrderValTxt.setText(generalFunc.convertNumberWithRTL(fPricePerPerson));
        } else {
            minOrderValTxt.setText(generalFunc.convertNumberWithRTL("0"));
            minOrderArea.setVisibility(View.GONE);
            minDivider.setVisibility(View.GONE);
        }

        resDetails.setVisibility(View.VISIBLE);
        restNametxt.setText(vCompany);
        //  titleTxtView.setText(vCompany);
        //  titleTxtView.setAllCaps(true);

        String Restaurant_OrderPrepareTime = generalFunc.getJsonValueStr("Restaurant_OrderPrepareTime", message);
        if (Restaurant_OrderPrepareTime != null && !Restaurant_OrderPrepareTime.equalsIgnoreCase("")) {
            deliveryValTxt.setText(generalFunc.convertNumberWithRTL(Restaurant_OrderPrepareTime));
        }

        titleDailogTxt.setText(vCompany);
        addressDailogTxt.setText(generalFunc.getJsonValueStr("vCaddress", message));
        timeHTxt.setText(generalFunc.getJsonValueStr("monfritimeslot_TXT", message));
        timeSatTxt.setText(generalFunc.getJsonValueStr("satsuntimeslot_TXT", message));
        timeVTxt.setText(generalFunc.convertNumberWithRTL(generalFunc.getJsonValueStr("monfritimeslot_Time", message)));
        timeVSatTxt.setText(generalFunc.convertNumberWithRTL(generalFunc.getJsonValueStr("satsuntimeslot_Time", message)));


        String Restaurant_OfferMessage = generalFunc.getJsonValueStr("Restaurant_OfferMessage", message);
        if (!Restaurant_OfferMessage.equalsIgnoreCase("")) {
            offerArea.setVisibility(View.VISIBLE);
            offerMsgTxt.setText(generalFunc.convertNumberWithRTL(Restaurant_OfferMessage));
        } else {
            offerArea.setVisibility(View.GONE);
        }
        ratingdecTxt.setText(generalFunc.convertNumberWithRTL(generalFunc.getJsonValueStr("RatingCounts", message)));
    }

    private void handleToolbarTitleVisibility(float percentage) {
        if (percentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {
            if (!mIsTheTitleVisible) {
                // startAlphaAnimation(titleTxtView, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                startAlphaAnimation(backarrorImgView, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleVisible = true;
            }
        } else {

            if (mIsTheTitleVisible) {
                // startAlphaAnimation(titleTxtView, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleVisible = false;
            }
        }
    }

    public static void startAlphaAnimation(View v, long duration, int visibility) {
        AlphaAnimation alphaAnimation = (visibility == View.VISIBLE)
                ? new AlphaAnimation(0f, 1f)
                : new AlphaAnimation(1f, 0f);

        alphaAnimation.setDuration(duration);
        alphaAnimation.setFillAfter(true);
        v.startAnimation(alphaAnimation);
    }

    @Override
    public void onItemClickList(View v, int position, HashMap<String, String> mapData) {
        HashMap<String, String> mapData1 = list.get(position);
        // Log.d("searchListsearchList", "onclick::" + list);

        Bundle bn = new Bundle();
        HashMap<String, String> map = new HashMap<>();
        map.put("iMenuItemId", mapData.get("iMenuItemId"));
        map.put("iFoodMenuId", mapData.get("iFoodMenuId"));
        map.put("vItemType", mapData.get("vItemType"));
        map.put("vItemDesc", mapData.get("vItemDesc"));
        map.put("fPrice", mapData.get("fPrice"));
        map.put("eFoodType", mapData.get("eFoodType"));
        map.put("fOfferAmt", mapData.get("fOfferAmt"));
        map.put("vImage", mapData.get("vImage"));
        map.put("iDisplayOrder", mapData.get("iDisplayOrder"));
        map.put("StrikeoutPrice", mapData.get("StrikeoutPrice"));
        map.put("fDiscountPrice", mapData.get("fDiscountPrice"));
        map.put("fDiscountPricewithsymbol", mapData.get("fDiscountPricewithsymbol"));
        map.put("MenuItemOptionToppingArr", mapData.get("MenuItemOptionToppingArr"));
        map.put("currencySymbol", mapData.get("currencySymbol"));
        map.put("iCompanyId", iCompanyId);
        map.put("vCompany", vCompany);
        map.put("fMinOrderValue", fMinOrderValue);
        map.put("iMaxItemQty", iMaxItemQty);
        map.put("Restaurant_Status", getIntent().getStringExtra("Restaurant_Status"));
        map.put("ispriceshow", getIntent().getStringExtra("ispriceshow"));
        bn.putSerializable("data", map);
        bn.putSerializable("paymentMethod", paymentTypeHashmap);

        // saveMap(paymentTypeHashmap);

        new StartActProcess(getActContext()).startActForResult(AddBasketActivity.class, bn, Utils.ADD_TO_BASKET);
    }

    @Override
    public void onAddItemReco(int position) {
        HashMap<String, String> mapData = recoList.get(position);

        Bundle bn = new Bundle();
        HashMap<String, String> map = new HashMap<>();
        map.put("iMenuItemId", mapData.get("iMenuItemId"));
        map.put("iFoodMenuId", mapData.get("iFoodMenuId"));
        map.put("vItemType", mapData.get("vItemType"));
        map.put("vItemDesc", mapData.get("vItemDesc"));
        map.put("fPrice", mapData.get("fPrice"));
        map.put("eFoodType", mapData.get("eFoodType"));
        map.put("fOfferAmt", mapData.get("fOfferAmt"));
        map.put("vImage", mapData.get("vImage"));
        map.put("iDisplayOrder", mapData.get("iDisplayOrder"));
        map.put("StrikeoutPrice", mapData.get("StrikeoutPrice"));
        map.put("fDiscountPrice", mapData.get("fDiscountPrice"));
        map.put("fDiscountPricewithsymbol", mapData.get("fDiscountPricewithsymbol"));
        map.put("MenuItemOptionToppingArr", mapData.get("MenuItemOptionToppingArr"));
        map.put("currencySymbol", mapData.get("currencySymbol"));
        map.put("iCompanyId", iCompanyId);
        map.put("vCompany", vCompany);
        map.put("fMinOrderValue", fMinOrderValue);
        map.put("iMaxItemQty", iMaxItemQty);
        map.put("Restaurant_Status", getIntent().getStringExtra("Restaurant_Status"));
        map.put("ispriceshow", getIntent().getStringExtra("ispriceshow"));
        bn.putSerializable("data", map);
        bn.putSerializable("paymentMethod", paymentTypeHashmap);

        new StartActProcess(getActContext()).startActForResult(AddBasketActivity.class, bn, Utils.ADD_TO_BASKET);

    }

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            int i = v.getId();

            if (i == R.id.cartFoodImgView) {
                Bundle bn = new Bundle();
                bn.putSerializable("paymentMethod", paymentTypeHashmap);

                new StartActProcess(getActContext()).startActWithData(EditCartActivity.class, bn);
            } else if (i == R.id.backArrowImgView) {
                onBackPressed();
            } else if (i == R.id.bottomCartView) {
                if (generalFunc.retrieveValue(Utils.COMPANY_MINIMUM_ORDER) != null && !generalFunc.retrieveValue(Utils.COMPANY_MINIMUM_ORDER).equalsIgnoreCase("0")) {
                    double minimumAmt = GeneralFunctions.parseDoubleValue(0, generalFunc.retrieveValue(Utils.COMPANY_MINIMUM_ORDER));

                    if (finalTotal < minimumAmt) {

                        generalFunc.showMessage(backarrorImgView, generalFunc.retrieveLangLBl("", "LBL_MINIMUM_ORDER_NOTE") + " " + CurrencySymbol + " " + generalFunc.convertNumberWithRTL(GeneralFunctions.convertDecimalPlaceDisplay(GeneralFunctions.parseDoubleValue(0, generalFunc.retrieveValue(Utils.COMPANY_MINIMUM_ORDER)))));
                        return;
                    }
                }

                if (!generalFunc.getMemberId().equalsIgnoreCase("")) {
                    checkPrescription();
                } else {
                    ischeckPrescriptionClick = false;
                    Bundle bn = new Bundle();
                    bn.putBoolean("isFromEditCard", false);

                    //saveMap(paymentTypeHashmap);
                    bn.putSerializable("paymentMethod", paymentTypeHashmap);

                    //saveMap(paymentTypeHashmap);

                    // fixme abubakar call checkout
                    new StartActProcess(getActContext()).startActWithData(CheckOutActivity.class, bn);
                }
                //checkPrescription();
            } else if (i == R.id.rightFoodImgView) {
                // Bundle bn = new Bundle();

             /*   bn.putString("iCompanyId", iCompanyId);
                bn.putString("fMinOrderValue", fMinOrderValue);
                bn.putString("iMaxItemQty", iMaxItemQty);
                bn.putString("Restaurant_Status", getIntent().getStringExtra("Restaurant_Status"));
                bn.putString("ispriceshow", getIntent().getStringExtra("ispriceshow"));
                bn.putSerializable("paymentMethod", paymentTypeHashmap);*/



              /*  bn.putString("iCompanyId", iCompanyId);
                bn.putString("vCompany", vCompany);
                bn.putString("Restaurant_Status", getIntent().getStringExtra("Restaurant_Status"));
                bn.putString("ispriceshow", getIntent().getStringExtra("ispriceshow"));
                bn.putString("CheckNonVegFoodType", isSearchVeg);*/


              /*  if (list.size()>0){
                    Intent intent = new Intent(getActContext(), SearchFoodActivity.class);
                    intent.putExtra("arraylist", list);
                    startActivityForResult(intent, 500);
                }
                else {
                    Toast.makeText(RestaurantAllDetailsNewActivity.this, "No Item", Toast.LENGTH_SHORT).show();
                    return;
                }*/


                //  intent.putExtra("data",bn);

                // new StartActProcess(getActContext()).startActWithData(SearchFoodActivity.class, list);
            } else if (i == R.id.infoImage) {
                if (dialogsLayout.getVisibility() == View.GONE) {
                    dialogsLayout.setVisibility(View.VISIBLE);
                    dialogsLayoutArea.setVisibility(View.VISIBLE);
                    informationDesignCardView.setVisibility(View.VISIBLE);
                    ratingDesignCardView.setVisibility(View.GONE);
                } else {
                    dialogsLayout.setVisibility(View.GONE);
                    dialogsLayoutArea.setVisibility(View.GONE);
                }
            } else if (i == R.id.closeBtn) {
                if (dialogsLayout.getVisibility() == View.VISIBLE) {
                    dialogsLayout.setVisibility(View.GONE);
                    dialogsLayoutArea.setVisibility(View.GONE);
                }
            } else if (i == R.id.restaurantViewFloatingBtn) {
                if (fabMenuLayout.getVisibility() == View.GONE) {
                    fabMenuLayout.setVisibility(View.VISIBLE);
                } else {
                    fabMenuLayout.setVisibility(View.GONE);
                }
            } else if (i == R.id.menubackView) {
                fabMenuLayout.setVisibility(View.GONE);
            }
        }
    }

    boolean ischeckPrescriptionClick = false;

    private void filtertotab(String name) {

        filterlist = new ArrayList<>();
        for (HashMap<String, String> temp : list) {
            for (String data : temp.values()) {
                if (data.equals(name)) {
                    filterlist.add(temp);
                }
            }
        }


        // fixme data
        tabfilter(filterlist);
    }
/*
    private void saveMap(HashMap<String, String> inputMap) {
        SharedPreferences pSharedPref = getApplicationContext().getSharedPreferences("MyVariables",
                Context.MODE_PRIVATE);
        if (pSharedPref != null) {
            JSONObject jsonObject = new JSONObject(inputMap);
            String jsonString = jsonObject.toString();
            SharedPreferences.Editor editor = pSharedPref.edit();
            editor.remove("paymentMethod").apply();
            editor.putString("paymentMethod", jsonString);
            editor.commit();
        }}
*/

    public void checkPrescription() {
        if (ischeckPrescriptionClick) {
            return;
        }
        ischeckPrescriptionClick = true;
        ArrayList<String> menulist = new ArrayList<>();
        if (cartList != null && cartList.size() > 0) {

            for (int i = 0; i < cartList.size(); i++) {
                menulist.add(cartList.get(i).getiMenuItemId());
            }

        }
        final HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "CheckPrescriptionRequired");
        parameters.put("iMenuItemId", android.text.TextUtils.join(",", menulist));
        parameters.put("eSystem", Utils.eSystem_Type);
        boolean isValues = false;

        final ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(responseString -> {

            if (responseString != null && !responseString.equals("")) {


                if (GeneralFunctions.checkDataAvail(Utils.action_str, responseString) == true) {
                    ischeckPrescriptionClick = false;
                    Bundle bn = new Bundle();
                    bn.putBoolean("isFromEditCard", false);
                    bn.putSerializable("paymentMethod", paymentTypeHashmap);
                    // saveMap(paymentTypeHashmap);

                    new StartActProcess(getActContext()).startActWithData(PrescriptionActivity.class, bn);

                } else {
                    ischeckPrescriptionClick = false;
                    Bundle bn = new Bundle();
                    bn.putBoolean("isFromEditCard", false);
                    bn.putSerializable("paymentMethod", paymentTypeHashmap);
                    // saveMap(paymentTypeHashmap);
                    new StartActProcess(getActContext()).startActWithData(CheckOutActivity.class, bn);

                }
            } else {
                ischeckPrescriptionClick = false;
                Bundle bn = new Bundle();
                bn.putBoolean("isFromEditCard", false);
                bn.putSerializable("paymentMethod", paymentTypeHashmap);
                // saveMap(paymentTypeHashmap);
                new StartActProcess(getActContext()).startActWithData(CheckOutActivity.class, bn);
            }
        });
        exeWebServer.execute();
    }

    @Override
    public void onBackPressed() {
        if (fabMenuLayout.getVisibility() == View.VISIBLE) {
            fabMenuLayout.setVisibility(View.GONE);
            return;
        }


        if (dialogsLayout.getVisibility() == View.VISIBLE) {
            closeBtn.performClick();
            return;
        } else {
            if (isFavChange) {
                Bundle bn = new Bundle();
                bn.putBoolean("isFavChange", isFavChange);
                (new StartActProcess(getActContext())).setOkResult(bn);
                finish();
                return;
            }

            RestaurantAllDetailsNewActivity.super.onBackPressed();
        }
    }


    public ArrayList<HashMap<String, String>> getRecomendedFood(ArrayList<HashMap<String, String>> list) {
        ArrayList<HashMap<String, String>> recoList = new ArrayList<>();
        if (list.size() > 1) {
            for (int i = 0; i < list.size(); i++) {
                if ((i % 2) == 0) {
                    if (!TextUtils.isEmpty(list.get(i).get("vImage"))) {

                        recoList.add(list.get(i));
                    }
                }
                // even
                else {

                }
            }
            // odd

        }
        return recoList;

    }

    public void setRecoAdapter(ArrayList<HashMap<String, String>> list) {

        if (list.size() != 0) {
            if (generalFunc.retrieveLangLBl("Select", "LBL_SELECT_LANGUAGE_HINT_TXT").equalsIgnoreCase("اختار اللغة")) {

                final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
                layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                layoutManager.setReverseLayout(true);
                reco_Recycler.setLayoutManager(layoutManager);
            } else {

                final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
                layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                layoutManager.setReverseLayout(false);
                reco_Recycler.setLayoutManager(layoutManager);

                //  reco_Recycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

            }

            recoList = new ArrayList<>();

            recoList = getRecomendedFood(list);

            if (recoList.size() > 8) {
                recoList = getRecomendedFood(recoList);
            }
            reco_Recycler.setAdapter(new RecomendedRecAdapter(recoList, getActContext(), this, generalFunc));
        } else {
            Toast.makeText(this, "No Recomended", Toast.LENGTH_SHORT).show();
        }
    }

    public void setStoreData(HashMap<String, String> storeData) {

    }

}
