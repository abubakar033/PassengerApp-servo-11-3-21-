package com.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.servo.user.BookingSummaryActivity;
import com.servo.user.CouponActivity;
import com.servo.user.R;
import com.general.files.GeneralFunctions;
import com.general.files.StartActProcess;
import com.utils.Utils;
import com.view.MButton;
import android.widget.TextView;
import com.view.MaterialRippleLayout;
import com.view.editBox.MaterialEditText;

public class OrderDetailsFrag extends Fragment {

    public GeneralFunctions generalFunc;
    TextView serviceItemname, servicepriceTxtView, commentHname;
    MaterialEditText commentBox;

    String appliedPromoCode = "";
    MButton continueBtn;

    BookingSummaryActivity bookingSummaryActivity;

    TextView providerHname, providerVname;

    TextView bookingdateHname, bookingdateVname;
    TextView bookingtimeHname, bookingtimeVname;

    View v;
    View couponCodeArea;
    LinearLayout promocodeArea;
    TextView promocodeappliedHTxt;
    TextView promocodeappliedVTxt;

    ImageView couponCodeImgView;
    TextView applyCouponHTxt;
    TextView appliedPromoHTxtView;
    String LBL_APPLIED_COUPON_CODE;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        v = inflater.inflate(R.layout.activity_ufx_order_details, container, false);

        bookingSummaryActivity = (BookingSummaryActivity) getActivity();

        generalFunc = bookingSummaryActivity.generalFunc;
        LBL_APPLIED_COUPON_CODE = generalFunc.retrieveLangLBl("", "LBL_APPLIED_COUPON_CODE");
        serviceItemname = (TextView) v.findViewById(R.id.serviceItemname);
        applyCouponHTxt = (TextView) v.findViewById(R.id.applyCouponHTxt);
        appliedPromoHTxtView = (TextView) v.findViewById(R.id.appliedPromoHTxtView);
        servicepriceTxtView = (TextView) v.findViewById(R.id.servicepriceTxtView);
        commentBox = (MaterialEditText) v.findViewById(R.id.commentBox);
        commentBox.setInputType(InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE);
        commentBox.setSingleLine(false);
        commentBox.setHideUnderline(true);
        commentBox.setGravity(Gravity.START | Gravity.TOP);

        providerHname = (TextView) v.findViewById(R.id.providerHname);
        providerVname = (TextView) v.findViewById(R.id.providerVname);
        bookingdateHname = (TextView) v.findViewById(R.id.bookingdateHname);
        bookingdateVname = (TextView) v.findViewById(R.id.bookingdateVname);

        bookingtimeVname = (TextView) v.findViewById(R.id.bookingtimeVname);
        bookingtimeHname = (TextView) v.findViewById(R.id.bookingtimeHname);
        commentHname = (TextView) v.findViewById(R.id.commentHname);
        couponCodeArea = v.findViewById(R.id.couponCodeArea);
        promocodeArea = (LinearLayout) v.findViewById(R.id.promocodeArea);
        promocodeappliedHTxt = (TextView) v.findViewById(R.id.promocodeappliedHTxt);
        promocodeappliedVTxt = (TextView) v.findViewById(R.id.promocodeappliedVTxt);
        couponCodeImgView = (ImageView) v.findViewById(R.id.couponCodeImgView);

        providerVname.setText(bookingSummaryActivity.Pname);

        continueBtn = ((MaterialRippleLayout) v.findViewById(R.id.proceedToCheckOutBtn)).getChildView();
        continueBtn.setId(Utils.generateViewId());
        continueBtn.setOnClickListener(new setOnClickList());
        couponCodeArea.setOnClickListener(new setOnClickList());


        setLabel();

        String quantity = bookingSummaryActivity.Quantity;
        String quantityPrice = bookingSummaryActivity.Quantityprice;
        String serviceItemName = bookingSummaryActivity.serviceItemname;

        if (quantity.equals("0") || quantity.equals("")) {
            serviceItemname.setText(serviceItemName);
            servicepriceTxtView.setText((bookingSummaryActivity.serviceprice.equals("") || bookingSummaryActivity.serviceprice.equals("0") ? "--" : bookingSummaryActivity.serviceprice));
        } else {

            serviceItemname.setText((quantityPrice.equals("") ? serviceItemName : serviceItemName + " x" + quantity));
            servicepriceTxtView.setText((quantityPrice.equals("") ? "--" : quantityPrice));
        }


        return v;
    }

    public void setLabel() {

        providerHname.setText(generalFunc.retrieveLangLBl("Provider", "LBL_PROVIDER"));
        //  commentBox.setHint(generalFunc.retrieveLangLBl("Add Special Instruction for provider.", "LBL_COMMENT_BOX_TXT"));
        commentBox.setLines(5);
        commentBox.setBackgroundDrawable(getResources().getDrawable(R.drawable.login_fb_border));
        commentBox.setPaddings(10, 5, 0, 5);

        continueBtn.setText(generalFunc.retrieveLangLBl("", "LBL_CONTINUE_BTN"));
        bookingdateHname.setText(generalFunc.retrieveLangLBl("Booking Date", "LBL_BOOKING_DATE"));
        bookingtimeHname.setText(generalFunc.retrieveLangLBl("Booking Time", "LBL_BOOKING_TIME"));
        commentHname.setText(generalFunc.retrieveLangLBl("Add Special Instruction for provider below.", "LBL_INS_PROVIDER_BELOW"));
        promocodeappliedHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_APPLIED_COUPON_CODE"));


        if (!bookingSummaryActivity.Sdate.equals("")) {
            bookingdateVname.setText(bookingSummaryActivity.Sdate);

        } else {

            bookingdateVname.setText(generalFunc.getCurrentdate());
        }

        if (!bookingSummaryActivity.Stime.equals("")) {
            bookingtimeVname.setText(bookingSummaryActivity.Stime);
        } else {
            bookingtimeVname.setText(generalFunc.retrieveLangLBl("now", "LBL_NOW"));
        }

    }

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {

            Bundle bn = new Bundle();
            switch (view.getId()) {

                case R.id.backImgView:
                    bookingSummaryActivity.onBackPressed();
                    break;
                case R.id.couponCodeArea:
                    bn.putString("CouponCode", appliedPromoCode);
                    bn.putString("eType", Utils.CabGeneralType_UberX);
                    new StartActProcess(bookingSummaryActivity.getActContext()).startActForResult(OrderDetailsFrag.this, CouponActivity.class, Utils.SELECT_COUPON_REQ_CODE, bn);
                    break;

                case R.id.couponCodeImgView:
                    generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_DELETE_CONFIRM_COUPON_MSG"), generalFunc.retrieveLangLBl("", "LBL_NO"), generalFunc.retrieveLangLBl("", "LBL_YES"), buttonId -> {
                        if (buttonId == 1) {

                            appliedPromoCode = "";
                            setPromoCode();

                            generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_COUPON_REMOVE_SUCCESS"));
                        }
                    });
                    break;
            }

            if (view.getId() == continueBtn.getId()) {
                bookingSummaryActivity.comment = commentBox.getText().toString();
                bookingSummaryActivity.promocode = appliedPromoCode;
                bookingSummaryActivity.openPaymentFrag();
            }
        }
    }

    public void setPromoCode() {
        if (appliedPromoCode.equalsIgnoreCase("")) {
            defaultPromoView();
        } else {
            appliedPromoView();
        }
    }

    public void defaultPromoView() {
        promocodeArea.setVisibility(View.GONE);
        appliedPromoHTxtView.setVisibility(View.GONE);

        couponCodeImgView.setImageResource(R.mipmap.ic_arrow_right);
        couponCodeImgView.setOnClickListener(null);
        applyCouponHTxt.setTextColor(Color.parseColor("#333333"));
        applyCouponHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_APPLY_COUPON"));

        promocodeappliedHTxt.setText(LBL_APPLIED_COUPON_CODE);

    }

    public void appliedPromoView() {
        appliedPromoHTxtView.setVisibility(View.VISIBLE);
        applyCouponHTxt.setText(appliedPromoCode);
        applyCouponHTxt.setTextColor(getResources().getColor(R.color.appThemeColor_1));
        couponCodeImgView.setImageResource(R.mipmap.ic_close_icon_);
        couponCodeImgView.setOnClickListener(new setOnClickList());
        appliedPromoHTxtView.setText(LBL_APPLIED_COUPON_CODE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Utils.SELECT_COUPON_REQ_CODE && resultCode == bookingSummaryActivity.RESULT_OK) {
            String couponCode = data.getStringExtra("CouponCode");
            if (couponCode == null) {
                couponCode = "";
            }
            appliedPromoCode = couponCode;

            setPromoCode();
        }
    }
}
