package com.jAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.general.files.GeneralFunctions;
import com.servo.user.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.utils.CommonUtilities;
import com.utils.Utils;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class J_gridview_base_adapter extends BaseAdapter {

    private final int TYPE_GRID = 0;
    private final int TYPE_BANNER = 1;
    private final int NO_TYPE = -1;

    public GeneralFunctions generalFunc;
    ArrayList<HashMap<String, String>> list_item;
    Context mContext;
    OnItemClickList onItemClickList;

    String CAT_TYPE_MODE = "1";

    boolean isForceToGrid = false;
    int positionOfSeperatorView = -1;
    boolean ismultiSelect = false;
    String userProfileJson;

    int dimension;
    int bannerHeight;
    int bannerWidth;
    int width;
    int margin;
    int grid;

    public J_gridview_base_adapter(Context mContext, ArrayList<HashMap<String,String>> list_item, GeneralFunctions generalFunc, boolean isForceToGrid) {
        this.mContext = mContext;
        this.list_item = list_item;
        this.generalFunc = generalFunc;
        this.isForceToGrid = isForceToGrid;
    }

    @Override
    public int getCount() {
        return list_item.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.j_cat_today_layout,viewGroup, false);
        myView(i,view);
        return view;
    }


    private void myView(int position, View viewHolder) {

        LinearLayout contentArea = viewHolder.findViewById(R.id.contentArea);
        ImageView catImgView = viewHolder.findViewById(R.id.catImgView);
//        TextView tv_cat_name = viewHolder.findViewById(R.id.tv_cat_name);
        TextView uberXCatNameTxtView = viewHolder.findViewById(R.id.uberXCatNameTxtView);


        HashMap<String, String> item = list_item.get(position);

        String vCategory = item.get("vCategory");
        String vLogo_image = item.get("vLogo_image");
        String vBannerImage = item.get("vBannerImage");

//        UberXCategoryAdapter.ViewHolder viewHolder = (UberXCategoryAdapter.ViewHolder) parentViewHolder;

//        try {
//            if (generalFunc.isRTLmode()) {
//                if (viewHolder.arrowImageView != null) {
//                    viewHolder.arrowImageView.setRotation(180);
//                }
//            }
//        } catch (Exception e) {
//        }
        if (vCategory.matches("\\w*")) {
            uberXCatNameTxtView.setMaxLines(1);

            uberXCatNameTxtView.setText(vCategory);
        } else {
            uberXCatNameTxtView.setMaxLines(2);

            uberXCatNameTxtView.setText(vCategory);
        }

//        tv_cat_name.setText(vCategory);

        String imageURL = Utils.getResizeImgURL(mContext, vLogo_image, grid, grid);

        Picasso.with(mContext).load(vLogo_image.equals("") ? CommonUtilities.SERVER : imageURL).placeholder(R.mipmap.ic_no_icon).into(catImgView, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {
                if (!vLogo_image.contains("http") && !vLogo_image.equals("")) {
                    Picasso.with(mContext).load(GeneralFunctions.parseIntegerValue(0, vLogo_image)).placeholder(R.mipmap.ic_no_icon).error(R.mipmap.ic_no_icon).into(catImgView);
                }
            }
        });

        contentArea.setOnClickListener(view -> {

            if (CAT_TYPE_MODE.equalsIgnoreCase("0")) {
                if (onItemClickList != null) {
                    onItemClickList.onItemClick(position);
                }
            } else {
                if (ismultiSelect) {
//                    if (viewHolder.serviceCheckbox != null) {
//                        viewHolder.serviceCheckbox.setChecked(!viewHolder.serviceCheckbox.isChecked());
//                    }
                } else {
                    if (onItemClickList != null) {
                        onItemClickList.onItemClick(position);
                    }
                }
            }
        });

//        if (CAT_TYPE_MODE.equalsIgnoreCase("0")) {
//            if (onItemClickList != null) {
//                onItemClickList.onItemClick(position);
//            }
//        } else {
//
////            onItemClickList.onItemClick(position);
//
//            if (ismultiSelect) {
//
////                if (viewHolder.serviceCheckbox != null) {
////                    viewHolder.serviceCheckbox.setChecked(!viewHolder.serviceCheckbox.isChecked());
////                }
//            } else {
//                if (onItemClickList != null) {
//                    onItemClickList.onItemClick(position);
//                }
//            }
//        }

//        if (CAT_TYPE_MODE.equals("0")) {
//            Toast.makeText(mContext,"0 1",Toast.LENGTH_SHORT).show();
//            contentArea.setOnTouchListener(new J_gridview_base_adapter.SetOnTouchList(TYPE_GRID, position, false));
//                /*viewHolder.contentArea.setOnTouchListener((view, motionEvent) -> {
//                    Logger.e("CONTENT_AREA", "TOUCH");
//                    switch (motionEvent.getAction()) {
//                        case MotionEvent.ACTION_DOWN:
//                            scaleView(view, 0.85f, 0.85f, motionEvent.getAction());
//                            break;
//                        case MotionEvent.ACTION_UP:
//                        case MotionEvent.ACTION_CANCEL:
//                            scaleView(view, (float) 1.0, (float) 1.0, motionEvent.getAction());
//                            break;
//                    }
//                    return true;
//                });*/
//        }
//        else {
//            Toast.makeText(mContext,"e 1",Toast.LENGTH_SHORT).show();
//            contentArea.setOnTouchListener(null);
//            if (ismultiSelect) {
////                viewHolder.serviceCheckbox.setVisibility(View.VISIBLE);
////                viewHolder.arrowImageView.setVisibility(View.GONE);
////                viewHolder.serviceCheckbox.setOnCheckedChangeListener((compoundButton, b) -> {
//                            onItemClickList.onMultiItem(item.get("iVehicleCategoryId"), b);
//                        }
//                );
//
//                String isCheck = item.get("isCheck");
//                if (isCheck != null && isCheck.equals("Yes")) {
////                    viewHolder.serviceCheckbox.setChecked(true);
//
//                } else if (isCheck != null && isCheck.equals("No")) {
////                    viewHolder.serviceCheckbox.setChecked(false);
//                }
//
//            } else {
////                viewHolder.serviceCheckbox.setVisibility(View.GONE);
////                viewHolder.arrowImageView.setVisibility(View.VISIBLE);
//            }
//        }

    }

    public void setOnItemClickList(OnItemClickList onItemClickList) {
        this.onItemClickList = onItemClickList;
    }

    private class SetOnTouchList implements View.OnTouchListener {
        int viewType;
        int item_position;
        boolean isBlockDownEvent = false;

        public SetOnTouchList(int viewType, int item_position, boolean isBlockDownEvent) {
            this.item_position = item_position;
            this.viewType = viewType;
            this.isBlockDownEvent = isBlockDownEvent;
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {

            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (!isBlockDownEvent) {
                        scaleView(view, viewType == TYPE_BANNER ? 0.97f : 0.85f, viewType == TYPE_BANNER ? 0.97f : 0.85f, motionEvent.getAction(), viewType, item_position);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (list_item.get(item_position).get("LAST_CLICK_TIME") == null || (System.currentTimeMillis() - GeneralFunctions.parseLongValue(0, list_item.get(item_position).get("LAST_CLICK_TIME"))) > 1000) {
                        scaleView(view, (float) 1.0, (float) 1.0, motionEvent.getAction(), viewType, item_position);
                    }else{
                        scaleView(view, (float) 1.0, (float) 1.0, MotionEvent.ACTION_CANCEL, viewType, item_position);
                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                    scaleView(view, (float) 1.0, (float) 1.0, motionEvent.getAction(), viewType, item_position);
                    break;
            }
            return true;
        }
    }

    public interface OnItemClickList {
        void onItemClick(int position);
        void onMultiItem(String id, boolean b);
    }

    public void scaleView(View v, float startScale, float endScale, int motionEvent, int viewType, int item_position) {

        v.setOnTouchListener(new SetOnTouchList(viewType, item_position, true));

        Animation anim = new ScaleAnimation(
                startScale, endScale, // Start and end values for the X axis scaling
                startScale, endScale, // Start and end values for the Y axis scaling
                Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
                Animation.RELATIVE_TO_SELF, 0.5f); // Pivot point of Y scaling
        anim.setFillAfter(true); // Needed to keep the result of the animation
        anim.setDuration(100);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (motionEvent == MotionEvent.ACTION_UP) {
                    list_item.get(item_position).put("LAST_CLICK_TIME", "" + System.currentTimeMillis());
                    v.performClick();
                }
                v.setOnTouchListener(new SetOnTouchList(viewType, item_position, false));
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        v.startAnimation(anim);
    }


}
