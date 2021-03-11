package com;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.widget.TextView;

import com.general.files.GeneralFunctions;
import com.servo.user.R;

public class MyProgressDialog {
    Context a;
    boolean b;
    Dialog c;

    public MyProgressDialog(Context mContext, boolean cancelable, String message_str) {
        this.a = mContext;
        this.b = cancelable;
        this.a(this.a);
        this.build();
        this.setMessage(message_str);
    }

    public void build() {
        this.c = new Dialog(this.a, R.style.theme_my_progress_dialog);
        this.c.setContentView(R.layout.my_progress_dilalog_design_prj);
        Window var1 = this.c.getWindow();
        var1.setGravity(17);
        var1.setLayout(-2, -2);
        this.c.getWindow().setLayout(-2, -2);
        this.c.setCanceledOnTouchOutside(false);
        this.c.setCancelable(this.b);
    }

    public void setMessage(String msg_str) {
        TextView var2 = (TextView) this.c.findViewById(R.id.msgTxt);
        var2.setText(msg_str);
    }

    private void a(Context var1) {
        new InfoProvider(var1);
    }

    public void show() {
        try {
            GeneralFunctions var1 = new GeneralFunctions(this.a);
            if (var1.isRTLmode()) {
                var1.forceRTLIfSupported(this.c);
            } else {
                var1.forceLTRIfSupported(this.c);
            }

            this.c.show();
        } catch (Exception var2) {
        }

    }

    public void close() {
        try {
            this.c.dismiss();
        } catch (Exception var2) {
        }

    }}