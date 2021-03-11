package com.adapter.files.deliverAll;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.servo.user.R;
import com.servo.user.deliverAll.RestaurantAllDetailsNewActivity;
import com.general.files.GeneralFunctions;
import com.squareup.picasso.Picasso;
import com.view.CreateRoundedView;
import android.widget.TextView;
import com.view.SelectableRoundedImageView;

import java.util.ArrayList;
import java.util.HashMap;

public class RestaurantmenuAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int TYPE_HEADER = 0;
    private final int TYPE_GRID = 1;
    private final int TYPE_LIST = 2;

    ArrayList<HashMap<String, String>> list;
    Context mContext;
    GeneralFunctions generalFunc;
    private OnItemClickListener mItemClickListener;
    int cnt = 0;
    int grayColor = -1;
    Drawable noIcon = null;

    RestaurantAllDetailsNewActivity restAllDetailsNewAct;

    int imageBackColor;
    boolean isRTLmode= false;

    public RestaurantmenuAdapter(Context mContext, ArrayList<HashMap<String, String>> list, GeneralFunctions generalFunc) {
        this.mContext = mContext;
        this.list = list;
        this.generalFunc = generalFunc;

        if (mContext instanceof RestaurantAllDetailsNewActivity) {
            restAllDetailsNewAct = (RestaurantAllDetailsNewActivity) mContext;
        }

        grayColor = mContext.getResources().getColor(R.color.gray);
        imageBackColor = mContext.getResources().getColor(R.color.appThemeColor_1);
        noIcon = mContext.getResources().getDrawable(R.mipmap.ic_no_icon);
        isRTLmode = generalFunc.isRTLmode();
    }

    public void setOnItemClickListener(OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_menu_headerview, parent, false);
            ViewHolder headerViewGolder = new ViewHolder(view);
            return headerViewGolder;
        } else if (viewType == TYPE_GRID) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_resmenu_gridview, parent, false);
            GridViewHolder gridViewGolder = new GridViewHolder(view);
            return gridViewGolder;
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_menu_list, parent, false);
            ListViewHolder listViewGolder = new ListViewHolder(view);
            return listViewGolder;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        HashMap<String, String> mapData = list.get(position);
        if (holder instanceof ViewHolder) {
            ViewHolder headerholder = (ViewHolder) holder;
            headerholder.menuHeaderTxt.setText(mapData.get("menuName"));

            if (cnt == 0) {
                headerholder.topLine.setVisibility(View.GONE);
            } else {
                headerholder.topLine.setVisibility(View.VISIBLE);
            }
            cnt = 1;
        }
        else if (holder instanceof GridViewHolder) {
            GridViewHolder grideholder = (GridViewHolder) holder;
            grideholder.title.setText(mapData.get("vItemType"));

            String eFoodType = mapData.get("eFoodType");

            if (eFoodType.equals("NonVeg")) {
                grideholder.nonVegImage.setVisibility(View.VISIBLE);
                grideholder.vegImage.setVisibility(View.GONE);
            } else if (eFoodType.equals("Veg")) {
                grideholder.nonVegImage.setVisibility(View.GONE);
                grideholder.vegImage.setVisibility(View.VISIBLE);
            }

            if (mapData.get("prescription_required").equalsIgnoreCase("Yes")) {
                grideholder.presImage.setVisibility(View.VISIBLE);
            } else {
                grideholder.presImage.setVisibility(View.GONE);
            }
            if (mapData.get("fOfferAmtNotZero").equalsIgnoreCase("Yes")) {
                grideholder.price.setText(mapData.get("StrikeoutPriceConverted"));

                grideholder.price.setTextColor(grayColor);
                /*SpannableStringBuilder spanBuilder = new SpannableStringBuilder();
                SpannableString origSpan = new SpannableString(grideholder.price.getText());

                origSpan.setSpan(new StrikethroughSpan(), 0, grideholder.price.getText().length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

                spanBuilder.append(origSpan);

                String priceStr = "\n" + mapData.get("fDiscountPricewithsymbolConverted");

                SpannableString discountSpan = new SpannableString(priceStr);
                discountSpan.setSpan(new ForegroundColorSpan(Color.parseColor("#272727")), 0, priceStr.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                spanBuilder.append(discountSpan);
                grideholder.price.setText(spanBuilder);*/

            } else {
                grideholder.price.setText(mapData.get("StrikeoutPriceConverted"));
                grideholder.price.setPaintFlags(0);
            }


            String vImage = mapData.get("vImage");
            boolean isBlank = (vImage != null && !TextUtils.isEmpty(vImage));

            if (!isBlank) {
                grideholder.menuImage.setVisibility(View.VISIBLE);
            }

//            String imageURL = Utils.getResizeImgURL(mContext, vImage,width , heightOfImage);

            Picasso.with(mContext)
                    .load(isBlank ? vImage : "https://www.test.com/ghg").placeholder(R.mipmap.ic_no_icon).error(noIcon)
                    .into(grideholder.menuImage);


            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) grideholder.menuImage.getLayoutParams();

            layoutParams.height = GeneralFunctions.parseIntegerValue(0, mapData.get("heightOfImage"));
            grideholder.menuImage.setLayoutParams(layoutParams);

            grideholder.addBtn.setText(mapData.get("LBL_ADD"));

            new CreateRoundedView(imageBackColor, 5, 0, 0, grideholder.addBtn);

            grideholder.addBtn.setOnClickListener(v -> {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClickList(grideholder.addBtn, position);
                }
            });

            if (isRTLmode) {
                grideholder.tagImage.setRotation(180);
                grideholder.tagTxt.setPadding(10, 15, 0, 0);
            }

            String vHighlightName = mapData.get("vHighlightName");

            if (vHighlightName != null && !vHighlightName.equals("")) {
                grideholder.tagImage.setVisibility(View.VISIBLE);
                grideholder.tagTxt.setVisibility(View.VISIBLE);
                grideholder.tagTxt.setText(mapData.get("vHighlightNameLBL"));
            } else {
                grideholder.tagImage.setVisibility(View.GONE);
                grideholder.tagTxt.setVisibility(View.GONE);
            }

            grideholder.vCategoryNameTxt.setText(mapData.get("vCategoryName"));

        } else {

            ListViewHolder listholder = (ListViewHolder) holder;
            listholder.title.setText(mapData.get("vItemType"));
            listholder.desc.setText(mapData.get("vItemDesc"));

            String eFoodType = mapData.get("eFoodType");

            if (eFoodType.equals("NonVeg")) {
                listholder.nonVegImage.setVisibility(View.VISIBLE);
                listholder.vegImage.setVisibility(View.GONE);
            } else if (eFoodType.equals("Veg")) {
                listholder.nonVegImage.setVisibility(View.GONE);
                listholder.vegImage.setVisibility(View.VISIBLE);
            }

            if (mapData.get("prescription_required").equalsIgnoreCase("Yes")) {
                listholder.presImage.setVisibility(View.VISIBLE);
            } else {
                listholder.presImage.setVisibility(View.GONE);
            }

            if (mapData.get("fOfferAmtNotZero").equalsIgnoreCase("Yes")) {
                listholder.price.setText(mapData.get("StrikeoutPriceConverted"));
                listholder.price.setPaintFlags(listholder.price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                listholder.offerPrice.setText(mapData.get("fDiscountPricewithsymbolConverted"));
                listholder.offerPrice.setVisibility(View.VISIBLE);
            } else {
                listholder.price.setText(mapData.get("StrikeoutPriceConverted"));
                listholder.price.setPaintFlags(0);
                listholder.offerPrice.setVisibility(View.GONE);
            }
            listholder.addBtn.setText(mapData.get("LBL_ADD"));
            new CreateRoundedView(imageBackColor, 5, 0, 0, listholder.addBtn);
            listholder.addBtn.setOnClickListener(v -> {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClickList(listholder.addBtn, position);
                }
            });

            String vHighlightName = mapData.get("vHighlightName");

            if (vHighlightName != null && !vHighlightName.equals("")) {
                listholder.tagArea.setVisibility(View.VISIBLE);
                listholder.tagTxt.setText(mapData.get("vHighlightNameLBL"));
            } else {
                listholder.tagArea.setVisibility(View.GONE);

            }

            String isLastLine = mapData.get("isLastLine");

            if (isLastLine != null && isLastLine.equals("Yes")) {
                listholder.bottomLine.setVisibility(View.VISIBLE);
            } else {
                listholder.bottomLine.setVisibility(View.GONE);
            }
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView menuHeaderTxt;
        View topLine;

        public ViewHolder(View view) {
            super(view);
            menuHeaderTxt = (TextView) view.findViewById(R.id.menuHeaderTxt);
            topLine = (View) view.findViewById(R.id.topLine);
        }
    }

    public class GridViewHolder extends RecyclerView.ViewHolder {

        ImageView vegImage, nonVegImage, presImage;
        TextView title;
        TextView price;
        TextView addBtn;
        ImageView tagImage;
        TextView tagTxt;
        SelectableRoundedImageView menuImage;
        TextView vCategoryNameTxt;

        public GridViewHolder(View view) {
            super(view);
            menuImage = (SelectableRoundedImageView) view.findViewById(R.id.menuImage);
            vegImage = (ImageView) view.findViewById(R.id.vegImage);
            nonVegImage = (ImageView) view.findViewById(R.id.nonVegImage);
            presImage = (ImageView) view.findViewById(R.id.presImage);
            title = (TextView) view.findViewById(R.id.title);
            price = (TextView) view.findViewById(R.id.price);
            addBtn = (TextView) view.findViewById(R.id.addBtn);
            tagImage = (ImageView) view.findViewById(R.id.tagImage);
            tagTxt = (TextView) view.findViewById(R.id.tagTxt);
            vCategoryNameTxt = (TextView) view.findViewById(R.id.vCategoryNameTxt);
        }
    }

    public class ListViewHolder extends RecyclerView.ViewHolder {

        ImageView vegImage, nonVegImage, presImage;
        TextView title, price, offerPrice, desc, addBtn, tagTxt;
        View bottomLine;
        LinearLayout tagArea;

        public ListViewHolder(View view) {
            super(view);

            vegImage = (ImageView) view.findViewById(R.id.vegImage);
            nonVegImage = (ImageView) view.findViewById(R.id.nonVegImage);
            presImage = (ImageView) view.findViewById(R.id.presImage);
            title = (TextView) view.findViewById(R.id.title);
            price = (TextView) view.findViewById(R.id.price);
            offerPrice = (TextView) view.findViewById(R.id.offerPrice);
            desc = (TextView) view.findViewById(R.id.desc);
            addBtn = (TextView) view.findViewById(R.id.addBtn);
            tagArea = (LinearLayout) view.findViewById(R.id.tagArea);
            tagTxt = (TextView) view.findViewById(R.id.tagTxt);
            bottomLine = (View) view.findViewById(R.id.bottomLine);
        }
    }


    @Override
    public int getItemViewType(int position) {

        String rowType = list.get(position).get("Type");
        if (rowType.equals("HEADER")) {
            return TYPE_HEADER;
        } else if (rowType.equals("GRID")) {
            return TYPE_GRID;
        } else {
            return TYPE_LIST;
        }

    }

    public interface OnItemClickListener {
        void onItemClickList(View v, int position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
