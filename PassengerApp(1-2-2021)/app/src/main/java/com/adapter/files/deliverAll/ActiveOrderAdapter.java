package com.adapter.files.deliverAll;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.general.files.ExecuteWebServerUrl;
import com.general.files.InternetConnection;
import com.servo.user.R;
import com.general.files.GeneralFunctions;
import com.squareup.picasso.Picasso;
import com.utils.Logger;
import com.utils.Utils;
import com.view.CreateRoundedView;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.adapter.files.DrawerAdapter.view;

/**
 * Created by Admin on 09-07-2016.
 */
public class ActiveOrderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;
    public GeneralFunctions generalFunc;
    ArrayList<HashMap<String, String>> list;
    Context mContext;
    boolean isFooterEnabled = false;
    View footerView;
    FooterViewHolder footerHolder;
    private OnItemClickListener mItemClickListener;

    // fixme abubakar Active OderAdapter
    public ActiveOrderAdapter(Context mContext, ArrayList<HashMap<String, String>> list, GeneralFunctions generalFunc, boolean isFooterEnabled) {
        this.mContext = mContext;
        this.list = list;
        this.generalFunc = generalFunc;
        this.isFooterEnabled = isFooterEnabled;
    }

    public void setOnItemClickListener(OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_FOOTER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_list, parent, false);
            this.footerView = v;
            return new FooterViewHolder(v);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_orders, parent, false);
            return new ViewHolder(view);
        }

    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {


        if (holder instanceof ViewHolder) {
            final ViewHolder viewHolder = (ViewHolder) holder;

            HashMap<String, String> item = list.get(position);
            Picasso.with(mContext)
                    .load(item.get("vImage"))
                    .placeholder(R.mipmap.ic_no_icon)
                    .error(R.mipmap.ic_no_icon)
                    .into(viewHolder.OrderHotelImage);

            viewHolder.orderHotelName.setText(item.get("vCompany"));
            viewHolder.orderHotelAddress.setText(item.get("vRestuarantLocation"));
            viewHolder.orderDateVTxt.setText(item.get("tOrderRequestDate"));
            viewHolder.totalVtxt.setText(item.get("fNetTotal"));
            viewHolder.totalHtxt.setText(item.get("LBL_TOTAL_TXT"));
            viewHolder.orderDateHTxt.setText(item.get("LBL_ORDER_AT"));
            viewHolder.btn_help.setText(item.get("LBL_HELP"));
            viewHolder.btn_viewDetails.setText(item.get("LBL_VIEW_DETAILS"));
            viewHolder.receivedOrder.setText(generalFunc.retrieveLangLBl("recieved","LBL_RECIEVED_ORDER"));

            String vServiceCategoryName=item.get("vServiceCategoryName");

            //
            if (item.get("deliveryType").equalsIgnoreCase("Take away")){
                viewHolder.btn_trackOrder.setVisibility(View.GONE);
                viewHolder.receivedOrder.setVisibility(View.VISIBLE);
                viewHolder.orderStatus.setVisibility(View.INVISIBLE);

            }


            if (!vServiceCategoryName.equalsIgnoreCase("")) {
                android.view.animation.Animation marquee = AnimationUtils.loadAnimation(mContext, R.anim.marquee);
                viewHolder.serviceNameTxt.startAnimation(marquee);
                viewHolder.serviceNameTxt.setText(vServiceCategoryName);
                viewHolder.serviceNameTxt.setSelected(true);
                viewHolder.serviceNameTxt.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                viewHolder.serviceNameTxt.setSingleLine(true);
                viewHolder.serviceNameTxt.setMarqueeRepeatLimit(-1);
                viewHolder.serviceNameTxt.setVisibility(View.VISIBLE);
                viewHolder.tagImage.setVisibility(View.VISIBLE);

                if (generalFunc.isRTLmode()) {
                    viewHolder.tagImage.setRotationY(180);
                }

                try {
                    FrameLayout.LayoutParams tagLayoutParam = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                    tagLayoutParam.setMargins(0, Utils.dpToPx(20, mContext), 0, 0);
                    viewHolder.dataArea.setLayoutParams(tagLayoutParam);
                } catch (Exception e) {
                    Logger.d("ExcpetionView", "::" + e.toString());
                }

            } else {
                viewHolder.serviceNameTxt.setVisibility(View.GONE);
                viewHolder.serviceNameTxt.setSelected(false);
                viewHolder.serviceNameTxt.setEllipsize(null);
                viewHolder.tagImage.setVisibility(View.GONE);
                FrameLayout.LayoutParams tagLayoutParam = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                tagLayoutParam.setMargins(0, 0, 0, 0);
                viewHolder.dataArea.setLayoutParams(tagLayoutParam);
            }

            String iStatusCode=item.get("iStatusCode");

            if (iStatusCode != null && !iStatusCode.equals("")) {
                if (iStatusCode.equals("6")) {
                    viewHolder.orderStatus.setVisibility(View.VISIBLE);
                    viewHolder.orderStatus.setText(item.get("LBL_HISTORY_REST_DELIVERED"));
                    viewHolder.orderStatus.setTextColor(mContext.getResources().getColor(R.color.appThemeColor_1));
                } else if (iStatusCode.equals("7")) {
                    viewHolder.orderStatus.setVisibility(View.VISIBLE);
                    viewHolder.orderStatus.setText(item.get("LBL_REFUNDED"));
                    viewHolder.orderStatus.setTextColor(mContext.getResources().getColor(R.color.red));
                } else if (iStatusCode.equals("8")) {
                    viewHolder.orderStatus.setVisibility(View.VISIBLE);
                    viewHolder.orderStatus.setText(item.get("LBL_HISTORY_REST_CANCELLED"));
                    viewHolder.orderStatus.setTextColor(mContext.getResources().getColor(R.color.red));
                } else {
                    viewHolder.orderStatus.setVisibility(View.GONE);
                }
            }

            if (item.get("DisplayLiveTrack").equalsIgnoreCase("Yes")) {
                viewHolder.TrackOrderBtnArea.setVisibility(View.VISIBLE);
                viewHolder.vieDetailsArea.setVisibility(View.GONE);
            } else {
                viewHolder.TrackOrderBtnArea.setVisibility(View.GONE);
                viewHolder.vieDetailsArea.setVisibility(View.VISIBLE);
            }

            new CreateRoundedView(mContext.getResources().getColor(R.color.black), 5, 0, 0,   viewHolder.btn_trackOrder);

            viewHolder.receivedOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    InternetConnection intCheck = new InternetConnection(mContext);


                    Toast.makeText(mContext, generalFunc.retrieveLangLBl("Receive your order from the store, please!","LBL_RECEIVE_YOUR_ORDER_FROM_THE_STORE_PLEASE"), Toast.LENGTH_SHORT).show();

                }
            });

            viewHolder.btn_trackOrder.setOnClickListener(view -> {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClickList(view, position, "track");
                }
            });

            viewHolder.btn_trackOrder.setText(item.get("LBL_TRACK_ORDER"));

            viewHolder.btn_help.setOnClickListener(v -> {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClickList(view, position, "help");
                }
            });
            new CreateRoundedView(mContext.getResources().getColor(R.color.black), 5, 0, 0,   viewHolder.btn_viewDetails);
            viewHolder.btn_viewDetails.setOnClickListener(v -> mItemClickListener.onItemClickList(view, position, "view"));


        } else {
            FooterViewHolder footerHolder = (FooterViewHolder) holder;
            this.footerHolder = footerHolder;
        }
    }

    public void OrderTAReceivedbyUser(int position){
        HashMap<String, String> parameters = list.get(position);
        parameters.put("type", "updateTakeAwayOrder");
        parameters.put("iOrderId", list.get(position).get("iOrderId"));
        parameters.put("vCompany",list.get(position).get("vCompany"));
        parameters.put("vRestuarantLocation",list.get(position).get("vRestuarantLocation"));
        parameters.put("tOrderRequestDate",list.get(position).get("tOrderRequestDate"));
        parameters.put("fNetTotal",list.get(position).get("fNetTotal"));
        parameters.put("iCompanyId",list.get(position).get("iCompanyId"));
        parameters.put("vStatus",list.get(position).get("vStatus"));
        parameters.put("iStatusCode",list.get(position).get("iStatusCode"));
        parameters.put("deliveryType",list.get(position).get("deliveryType"));
        parameters.put("DisplayLiveTrack",list.get(position).get("DisplayLiveTrack"));
        parameters.put("vServiceCategoryName",list.get(position).get("vServiceCategoryName"));


        Log.d("***********",""+parameters);


        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(mContext, parameters);
        exeWebServer.setLoaderConfig(mContext, false, generalFunc);
        exeWebServer.setDataResponseListener(responseString -> {
            JSONObject responseObj=generalFunc.getJsonObject(responseString);
            if (responseObj != null && !responseObj.equals("")) {


                Log.d("***********",""+responseObj.toString());
            }
            else {
                generalFunc.showError();
                Log.d("***********",""+responseString);

            }


        });
        exeWebServer.execute();

    /*    ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(mContext, parameters);
        exeWebServer.setLoaderConfig(mContext, false, generalFunc);
        exeWebServer.setDataResponseListener(responseString -> {
            JSONObject responseObj=generalFunc.getJsonObject(responseString);
            if (responseObj != null && !responseObj.equals("")) {


                Log.d("***********",""+responseObj.toString());
            }
            else {
                generalFunc.showError();
                Log.d("***********",""+responseString);

            }

           // mProgressBar.setVisibility(View.GONE);
        });
        exeWebServer.execute();*/

    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionFooter(position) && isFooterEnabled) {
            return TYPE_FOOTER;
        }
        return TYPE_ITEM;
    }

    private boolean isPositionFooter(int position) {
        return position == list.size();
    }

    // Return the size of your itemsData (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if (isFooterEnabled) {
            return list.size() + 1;
        } else {
            return list.size();
        }
    }

    public interface OnItemClickListener {
        void onItemClickList(View v, int position, String isSelect);
    }

    // inner class to hold a reference to each item of RecyclerView
    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView OrderHotelImage;
        public TextView orderHotelName;
        public TextView orderHotelAddress;
        public TextView orderDateHTxt;
        public TextView orderDateVTxt;
        public TextView totalHtxt;
        public TextView totalVtxt;
        public LinearLayout TrackOrderBtnArea;
        public LinearLayout vieDetailsArea;

        public TextView btn_trackOrder;
        public TextView btn_help;
        public TextView btn_viewDetails;
        public TextView orderStatus;
        public TextView serviceNameTxt,receivedOrder;

        ImageView tagImage;
        LinearLayout dataArea;

        public ViewHolder(View view) {
            super(view);

            OrderHotelImage = (ImageView) view.findViewById(R.id.OrderHotelImage);
            orderHotelName = (TextView) view.findViewById(R.id.orderHotelName);
            orderHotelAddress = (TextView) view.findViewById(R.id.orderHotelAddress);
            orderDateHTxt = (TextView) view.findViewById(R.id.orderDateHTxt);
            orderDateVTxt = (TextView) view.findViewById(R.id.orderDateVTxt);
            totalHtxt = (TextView) view.findViewById(R.id.totalHtxt);
            totalVtxt = (TextView) view.findViewById(R.id.totalVtxt);
            TrackOrderBtnArea = (LinearLayout) view.findViewById(R.id.TrackOrderBtnArea);
            vieDetailsArea = (LinearLayout) view.findViewById(R.id.vieDetailsArea);
            btn_trackOrder = (TextView) view.findViewById(R.id.btn_trackOrder);
            btn_help = (TextView) view.findViewById(R.id.btn_help);
            btn_viewDetails = (TextView) view.findViewById(R.id.btn_viewDetails);
            orderStatus = (TextView) view.findViewById(R.id.orderStatus);
            receivedOrder = (TextView) view.findViewById(R.id.btn_pickuped);
            serviceNameTxt = (TextView) view.findViewById(R.id.serviceNameTxt);
            tagImage = (ImageView) view.findViewById(R.id.tagImage);
            dataArea = (LinearLayout) view.findViewById(R.id.dataArea);
        }
    }

    class FooterViewHolder extends RecyclerView.ViewHolder {
        LinearLayout progressArea;

        public FooterViewHolder(View itemView) {
            super(itemView);
            progressArea = (LinearLayout) itemView;
        }
    }
}
