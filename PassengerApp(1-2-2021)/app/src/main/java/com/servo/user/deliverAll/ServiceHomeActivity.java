package com.servo.user.deliverAll;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.adapter.files.deliverAll.FoodDeliveryHomeAdapter;
import com.adapter.files.deliverAll.ServiceHomeAdapter;
import com.servo.user.R;
import com.general.files.AddDrawer;
import com.general.files.AppFunctions;
import com.general.files.DividerItemDecoration;
import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.general.files.InternetConnection;
import com.general.files.MyApp;
import com.general.files.OpenAdvertisementDialog;
import com.general.files.StartActProcess;
import com.general.files.UpdateFrequentTask;
import com.utils.Utils;
import com.view.ErrorView;
import com.view.LoopingCirclePageIndicator;
import android.widget.TextView;
import com.view.SelectableRoundedImageView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ServiceHomeActivity extends AppCompatActivity implements ServiceHomeAdapter.OnItemClickList, UpdateFrequentTask.OnTaskRunCalled,
        ViewPager.OnPageChangeListener {

    public AddDrawer addDrawer;
    public String userProfileJson = "";
    GeneralFunctions generalFunc;
    ServiceHomeAdapter serviceHomeAdapter;
    ArrayList<HashMap<String, String>> generalCategoryList;
    RecyclerView serviceListRecyclerView;
    DividerItemDecoration itemDecoration;
    TextView headerTxt;
    TextView titleTxt;
    JSONObject obj_userProfile;

    LinearLayout errorViewArea;
    ErrorView errorView;
    InternetConnection internetConnection;
    int position = -1;
    boolean isClicked = false;
    ImageView headerLogo;
    RelativeLayout headerArea;

    ViewPager bannerViewPager;
    LoopingCirclePageIndicator bannerCirclePageIndicator;
    FoodDeliveryHomeAdapter bannerAdapter;
    UpdateFrequentTask updateBannerChangeFreqTask;
    int BANNER_AUTO_ROTATE_INTERVAL = 4000;
    int currentBannerPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_home);

        Toast.makeText(this,"Home Act",Toast.LENGTH_SHORT).show();

        generalFunc = MyApp.getInstance().getGeneralFun(getActContext());
        internetConnection = new InternetConnection(getActContext());
        userProfileJson = generalFunc.retrieveValue(Utils.USER_PROFILE_JSON);
        String advertise_banner_data = generalFunc.getJsonValue("advertise_banner_data", userProfileJson);
        if (advertise_banner_data != null && !advertise_banner_data.equalsIgnoreCase("")) {

            if (generalFunc.getJsonValue("image_url", advertise_banner_data) != null && !generalFunc.getJsonValue("image_url", advertise_banner_data).equalsIgnoreCase("")) {
                HashMap<String, String> map = new HashMap<>();
                map.put("image_url", generalFunc.getJsonValue("image_url", advertise_banner_data));
                map.put("tRedirectUrl", generalFunc.getJsonValue("tRedirectUrl", advertise_banner_data));
                map.put("vImageWidth", generalFunc.getJsonValue("vImageWidth", advertise_banner_data));
                map.put("vImageHeight", generalFunc.getJsonValue("vImageHeight", advertise_banner_data));
                new OpenAdvertisementDialog(getActContext(), map, generalFunc);
            }
        }

        addDrawer = new AddDrawer(getActContext(), userProfileJson);
        headerLogo = (ImageView) findViewById(R.id.headerLogo);
        headerTxt = (TextView) findViewById(R.id.headerTxt);
        titleTxt = (TextView) findViewById(R.id.titleTxt);

        headerArea = (RelativeLayout) findViewById(R.id.headerArea);
        headerArea.setVisibility(View.VISIBLE);
        if (getIntent().hasExtra("showBackBtn")) {
            findViewById(R.id.menuImgView).setVisibility(View.GONE);
            findViewById(R.id.backImgView).setVisibility(View.VISIBLE);
            findViewById(R.id.backImgView).setOnClickListener(new setOnClickList());
            headerLogo.setVisibility(View.GONE);
            titleTxt.setVisibility(View.VISIBLE);
            titleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_DELIVER_ALL_APP_DELIVERY"));
            headerArea.setVisibility(View.GONE);
            addDrawer.setMenuState(false);
        } else {
            findViewById(R.id.menuImgView).setVisibility(View.VISIBLE);
            findViewById(R.id.backImgView).setVisibility(View.GONE);
            findViewById(R.id.menuImgView).setOnClickListener(new setOnClickList());
            addDrawer.setMenuState(true);
            headerArea.setVisibility(View.VISIBLE);
            headerLogo.setVisibility(View.VISIBLE);
            titleTxt.setVisibility(View.GONE);
        }

        itemDecoration = new DividerItemDecoration(getActContext(), DividerItemDecoration.VERTICAL_LIST);
        serviceListRecyclerView = (RecyclerView) findViewById(R.id.serviceListRecyclerView);

        errorViewArea = (LinearLayout) findViewById(R.id.errorViewArea);
        errorView = (ErrorView) findViewById(R.id.errorView);

        generalCategoryList = (ArrayList<HashMap<String, String>>) getIntent().getSerializableExtra("servicedata");
        serviceHomeAdapter = new ServiceHomeAdapter(getActContext(), generalCategoryList, generalFunc);
        serviceHomeAdapter.setOnItemClickList(ServiceHomeActivity.this);
        serviceListRecyclerView.setAdapter(serviceHomeAdapter);

        bannerViewPager = (ViewPager) findViewById(R.id.bannerViewPager);
        bannerCirclePageIndicator = (LoopingCirclePageIndicator) findViewById(R.id.bannerCirclePageIndicator);

    }


    public void getBanners() {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "getBanners");
        parameters.put("iMemberId", generalFunc.getMemberId());
        parameters.put("eSystem", Utils.eSystem_Type);

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(responseString -> {
            if (responseString != null && !responseString.equals("")) {
                boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);
                if (isDataAvail) {
                    JSONArray arr = generalFunc.getJsonArray(Utils.message_str, responseString);

                    if (arr != null) {
                        ArrayList<String> imagesList = new ArrayList<String>();

                        int bannerWidth=Utils.getWidthOfBanner(getActContext(), 0);
                        int bannerHeight=Utils.getHeightOfBanner(getActContext(), Utils.dipToPixels(getActContext(), 0), "16:9");



                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj_temp = generalFunc.getJsonObject(arr, i);

                            String vImage = generalFunc.getJsonValueStr("vImage", obj_temp);

                            String imageURL = Utils.getResizeImgURL(getActContext(), vImage, bannerWidth, bannerHeight);

                            imagesList.add(imageURL);
                        }
                        FoodDeliveryHomeAdapter bannerAdapter = new FoodDeliveryHomeAdapter(getActContext(), imagesList);
                        bannerViewPager.setAdapter(bannerAdapter);
                        ServiceHomeActivity.this.bannerAdapter = bannerAdapter;
                        bannerCirclePageIndicator.setDataSize(imagesList.size());
                        bannerCirclePageIndicator.setViewPager(bannerViewPager);

                        if (imagesList.size() > 1) {
                            bannerCirclePageIndicator.setVisibility(View.VISIBLE);
                            updateBannerChangeFreqTask = new UpdateFrequentTask(BANNER_AUTO_ROTATE_INTERVAL);
                            updateBannerChangeFreqTask.setTaskRunListener(ServiceHomeActivity.this);
                            updateBannerChangeFreqTask.avoidFirstRun();
                            updateBannerChangeFreqTask.startRepeatingTask();
                        } else {
                            bannerCirclePageIndicator.setVisibility(View.GONE);

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

        (new AppFunctions(getActContext())).checkProfileImage((SelectableRoundedImageView) view.findViewById(R.id.userImgView), userProfileJson, "vImgName");
    }


    void getLanguageLabelServiceWise(int position) {
        generateErrorView();

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "getUserLanguagesAsPerServiceType");
        parameters.put("LanguageCode", generalFunc.retrieveValue(Utils.LANGUAGE_CODE_KEY));
        parameters.put("eSystem", Utils.eSystem_Type);

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(String responseString) {
                if (responseString != null && !responseString.equals("")) {

                    boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

                    if (isDataAvail) {
                        HashMap<String,String> storeData=new HashMap<>();
                        storeData.put(Utils.languageLabelsKey, generalFunc.getJsonValue(Utils.message_str, responseString));
                        storeData.put(Utils.LANGUAGE_CODE_KEY, generalFunc.getJsonValue("vLanguageCode", responseString));
                        storeData.put(Utils.LANGUAGE_IS_RTL_KEY, generalFunc.getJsonValue("langType", responseString));

                        storeData.put(Utils.GOOGLE_MAP_LANGUAGE_CODE_KEY, generalFunc.getJsonValue("vGMapLangCode", responseString));

                        generalFunc.storeData(storeData);
                        Utils.setAppLocal(getActContext());

                        Bundle bn = new Bundle();
                        bn.putBoolean("isback", true);
                        resetData();
                        new StartActProcess(getActContext()).startActWithData(FoodDeliveryHomeActivity.class, bn);

                    } else {
                        resetData();
                    }
                } else {
                    resetData();
                }

            }
        });
        exeWebServer.execute();
    }

    private void resetData() {
        isClicked=false;
        position=-1;
    }

    public Context getActContext() {
        return ServiceHomeActivity.this;
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

                    getLanguageLabelServiceWise(position);

                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (generalFunc.prefHasKey(Utils.iServiceId_KEY) && generalFunc != null /*&& !generalFunc.isDeliverOnlyEnabled()*/) {

                generalFunc.removeValue(Utils.iServiceId_KEY);
            }


            userProfileJson = generalFunc.retrieveValue(Utils.USER_PROFILE_JSON);
            if (addDrawer != null) {
                addDrawer.Managemenu();
            }

            obj_userProfile = generalFunc.getJsonObject(userProfileJson);
            setUserInfo();

        } catch (Exception e) {

        }

    }

    @Override
    public void onBackPressed() {

        if (findViewById(R.id.backImgView).getVisibility() == View.GONE) {
            if (addDrawer.checkDrawerState(false)) {
                return;
            }
        }

        if (findViewById(R.id.backImgView).getVisibility() == View.VISIBLE) {
            if (generalFunc.prefHasKey(Utils.iServiceId_KEY) && generalFunc != null) {
                generalFunc.removeValue(Utils.iServiceId_KEY);
            }
        }

        super.onBackPressed();
    }

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Utils.hideKeyboard(getActContext());
            int i = view.getId();
            if (i == R.id.backImgView) {
                onBackPressed();
            } else if (i == R.id.menuImgView) {
                addDrawer.checkDrawerState(true);
            }
        }
    }

    @Override
    public void onItemClick(int position) {
        if (!isClicked && this.position!=position) {
            isClicked = true;
            this.position = position;
            generalFunc.storeData(Utils.iServiceId_KEY, generalCategoryList.get(position).get("iServiceId"));
            getLanguageLabelServiceWise(position);
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
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        currentBannerPosition = position;

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
