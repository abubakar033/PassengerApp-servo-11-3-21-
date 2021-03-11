package com.servo.user;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.adapter.files.ViewPagerAdapter;
import com.fragments.WalletFragment;
import com.general.files.GeneralFunctions;
import com.general.files.MyApp;
import com.utils.Utils;
import android.widget.TextView;
import com.view.MaterialTabs;

import java.util.ArrayList;

/**
 * Created by Admin on 04-11-2016.
 */
public class MyWalletHistoryActivity extends AppCompatActivity {

    public GeneralFunctions generalFunc;
    TextView titleTxt;
    ImageView backImgView;
    CharSequence[] titles;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mywallet_history);


        titleTxt = (TextView) findViewById(R.id.titleTxt);
        backImgView = (ImageView) findViewById(R.id.backImgView);
        generalFunc = MyApp.getInstance().getGeneralFun(getActContext());
        backImgView.setOnClickListener(new setOnClickList());

        setLabels();

        ViewPager appLogin_view_pager = (ViewPager) findViewById(R.id.appLogin_view_pager);
        MaterialTabs material_tabs = (MaterialTabs) findViewById(R.id.material_tabs);
        final ArrayList<Fragment> fragmentList = new ArrayList<>();

        titles = new CharSequence[]{generalFunc.retrieveLangLBl("", "LBL_ALL"), generalFunc.retrieveLangLBl("", "LBL_MONEY_IN"), generalFunc.retrieveLangLBl("", "LBL_MONEY_OUT")};
        material_tabs.setVisibility(View.VISIBLE);
        fragmentList.add(generateWalletFrag(Utils.Wallet_all));
        fragmentList.add(generateWalletFrag(Utils.Wallet_credit));
        fragmentList.add(generateWalletFrag(Utils.Wallet_debit));


        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), titles, fragmentList);
        appLogin_view_pager.setAdapter(adapter);
        material_tabs.setViewPager(appLogin_view_pager);


        appLogin_view_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    public WalletFragment generateWalletFrag(String bookingType) {
        WalletFragment frag = new WalletFragment();
        Bundle bn = new Bundle();
        bn.putString("ListType", bookingType);

        frag.setArguments(bn);

        return frag;
    }


    public void setLabels() {

        titleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_Transaction_HISTORY"));

    }

    public Context getActContext() {
        return MyWalletHistoryActivity.this;
    }

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Utils.hideKeyboard(getActContext());
            switch (view.getId()) {
                case R.id.backImgView:
                    MyWalletHistoryActivity.super.onBackPressed();
                    break;

            }
        }
    }

}

