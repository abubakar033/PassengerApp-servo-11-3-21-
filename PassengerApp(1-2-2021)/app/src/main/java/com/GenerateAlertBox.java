package com;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AlertDialog.Builder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.general.files.GeneralFunctions;
import com.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;

public class GenerateAlertBox {
    public AlertDialog alertDialog;
    Context a;
    GenerateAlertBox.HandleAlertBtnClick b;
    Builder c;
    boolean d = false;
    GeneralFunctions e;
    View f = null;

    public GenerateAlertBox(Context mContext) {
        this.a = mContext;
        this.c = new Builder(mContext);
        this.e = new GeneralFunctions(this.a);
        this.a(mContext);
    }

    public GenerateAlertBox(Context mContext, boolean isCancelable) {
        this.a = mContext;
        this.d = isCancelable;
        this.a(this.a);
        this.c = new Builder(mContext);
        this.e = new GeneralFunctions(this.a);
    }

    public Builder getBuilder() {
        return this.c;
    }

    public void setContentMessage(String title_str, String message_str) {
        this.c.setTitle(title_str);
        this.c.setMessage(message_str);
    }

    public void setCancelable(boolean value) {
        this.d = value;
        this.c.setCancelable(value);
        if (this.alertDialog != null) {
            this.alertDialog.setCanceledOnTouchOutside(value);
            this.alertDialog.setCancelable(value);
        }

    }

    public void setNegativeBtn(String negative_btn_str) {
        this.c.setNegativeButton(negative_btn_str, (var1, var2) -> {
            if (this.b != null) {
                this.b.handleBtnClick(0);
            }

        });
    }

    public void setPositiveBtn(String positive_btn_str) {
        this.c.setPositiveButton(positive_btn_str, (var1, var2) -> {
            if (this.b != null) {
                this.b.handleBtnClick(1);
            }

        });
    }

    public void setNeutralBtn(String neutral_btn_str) {
        this.c.setNeutralButton(neutral_btn_str, (var1, var2) -> {
            if (this.b != null) {
                this.b.handleBtnClick(2);
            }

        });
    }

    public void resetBtn() {
        this.c.setNegativeButton((CharSequence)null, (OnClickListener)null);
        this.c.setPositiveButton((CharSequence)null, (OnClickListener)null);
    }

    public void showAlertBox() {
        if (this.a instanceof Activity) {
            ((Activity)this.a).runOnUiThread(new Runnable() {
                public void run() {
                    GenerateAlertBox.this.a();
                }
            });
        } else {
            this.a();
        }

    }

    public void setCustomView(int resourceId) {
        LayoutInflater var2 = (LayoutInflater)this.a.getSystemService("layout_inflater");
        View var3 = var2.inflate(resourceId, (ViewGroup)null);
        if (this.c != null) {
            this.f = var3;
            this.c.setView(var3);
        }

    }

    private void a(Context var1) {
        new InfoProvider(var1);
    }

    public View getView(int resourceId) {
        return this.f != null ? this.f.findViewById(resourceId) : null;
    }

    private void a() {
        try {
            if (this.alertDialog == null) {
                this.alertDialog = this.c.create();
                this.alertDialog.setCancelable(this.d);
                if (this.e.isRTLmode()) {
                    this.e.forceRTLIfSupported(this.alertDialog);
                } else {
                    this.e.forceLTRIfSupported(this.alertDialog);
                }
            }

            this.alertDialog.show();
        } catch (Exception var2) {
            System.out.println(var2.toString());
        }

    }

    public void createList(ArrayList<HashMap<String, String>> dataList, String keyToShow, com.view.GenerateAlertBox.OnItemClickListener onItemClickListener) {
        if (this.c != null && this.a != null) {
            ListView var4 = new ListView(this.a);
            var4.setDivider((Drawable)null);
            var4.setPadding(0, Utils.dipToPixels(this.a, 10.0F), 0, 0);
            GenerateAlertBox.a var5 = new GenerateAlertBox.a(dataList, this.a, keyToShow);
            var4.setAdapter(var5);
            var4.setOnItemClickListener((var1, var2, var3, var4x) -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(var3);
                }

            });
            this.c.setView(var4);
        }

    }

    public void closeAlertBox() {
        try {
            if (this.alertDialog != null) {
                this.alertDialog.dismiss();
            }
        } catch (Exception var2) {
        }

    }

    public void setBtnClickList(HandleAlertBtnClick listener) {
        this.b = listener;
    }

    public void showSessionOutAlertBox() {
        try {
            if (this.alertDialog != null && this.alertDialog.isShowing()) {
                return;
            }

            this.alertDialog = this.c.create();
            this.alertDialog.setCancelable(false);
            if (this.e.isRTLmode()) {
                this.e.forceRTLIfSupported(this.alertDialog);
            } else {
                this.e.forceLTRIfSupported(this.alertDialog);
            }

            this.alertDialog.show();
        } catch (Exception var2) {
        }

    }

    public interface HandleAlertBtnClick {
        void handleBtnClick(int var1);
    }

    public interface OnItemClickListener {
        void onItemClick(int var1);
    }

    private class a extends BaseAdapter {
        ArrayList<HashMap<String, String>> a;
        Context b;
        String c;

        public a(ArrayList<HashMap<String, String>> var2, Context var3, String var4) {
            this.a = var2;
            this.b = var3;
            this.c = var4;
        }

        public int getCount() {
            return this.a.size();
        }

        public Object getItem(int i) {
            return this.a.get(i);
        }

        public long getItemId(int i) {
            return (long)i;
        }

        public View getView(int position, View view, ViewGroup viewGroup) {
            LinearLayout var4;
            if (view == null) {
                var4 = new LinearLayout(this.b);
                TextView var5 = new TextView(this.b);
                var5.setTextSize(2, 16.0F);
                var5.setTextColor(Color.parseColor("#1c1c1c"));
                LayoutParams var6 = new LayoutParams(-1, -2);
                var5.setLayoutParams(var6);
                var5.setMinHeight(Utils.dipToPixels(this.b, 40.0F));
                var5.setPadding(Utils.dipToPixels(this.b, 25.0F), Utils.dipToPixels(this.b, 5.0F), Utils.dipToPixels(this.b, 25.0F), Utils.dipToPixels(this.b, 5.0F));
                var5.setTextAlignment(5);
                var5.setGravity(8388627);
                var4.addView(var5);
                view = var4;
            }

            var4 = (LinearLayout)view;
            if (var4.getChildCount() > 0 && var4.getChildAt(0) instanceof TextView) {
                String var7 = (String)((HashMap)this.a.get(position)).get(this.c);
                ((TextView)var4.getChildAt(0)).setText(var7 != null ? var7 : "");
            }

            return (View)view;
        }
    }
}

