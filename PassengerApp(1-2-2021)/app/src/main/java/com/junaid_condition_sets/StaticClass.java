package com.junaid_condition_sets;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.text.Html;
import android.text.Spanned;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import java.util.Random;

public class StaticClass {


    Context context;

    public StaticClass(Context context) {
        this.context = context;
    }

    public void toast(String text){

        Toast.makeText(context,text, Toast.LENGTH_SHORT).show();

    }

//    private void closeKeyboard() {
//        View view = context.getApplicationContext().getCurrentFocus();
//        if (view != null) {
//            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
//            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
//        }
//    }

    public boolean isConnected(){

        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        }
        else
            connected = false;

        return connected;

    }

    public Spanned htmlText(String text){

        Spanned spanned = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            spanned = Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT);
        } else {
            spanned = Html.fromHtml(text);
        }

        return spanned;
    }

}