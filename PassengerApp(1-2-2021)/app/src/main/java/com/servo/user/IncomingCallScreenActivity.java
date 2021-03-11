package com.servo.user;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.general.files.AudioPlayer;
import com.general.files.GeneralFunctions;
import com.general.files.MyApp;
import com.general.files.SinchService;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallEndCause;
import com.sinch.android.rtc.calling.CallListener;
import com.squareup.picasso.Picasso;
import com.utils.CommonUtilities;
import com.utils.Utils;
import com.view.CreateRoundedView;
import com.view.MButton;
import android.widget.TextView;
import com.view.SelectableRoundedImageView;

import org.json.JSONObject;

import java.util.List;

public class IncomingCallScreenActivity extends BaseActivity {

    static final String TAG = IncomingCallScreenActivity.class.getSimpleName();
    private String mCallId;
    // private AudioPlayer mAudioPlayer;
    GeneralFunctions generalFunctions;
    String userProfileJson = "";
    String driverName = "";
    String deriverImage = "";
    SelectableRoundedImageView driverImageView;
    TextView callState;
    MediaPlayer mp = new MediaPlayer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
        setContentView(R.layout.incoming);

        generalFunctions = MyApp.getInstance().getGeneralFun(this);

        if (generalFunctions.getMemberId().equals("")) {
            return;
        }
        userProfileJson = generalFunctions.retrieveValue(Utils.USER_PROFILE_JSON);

//        JSONObject driverDetailJson = generalFunctions.getJsonObject("DriverDetails", userProfileJson);
//        deriverImage = generalFunctions.getJsonValue("vImage", driverDetailJson.toString());
//        driverName = generalFunctions.getJsonValue("vName", driverDetailJson.toString());


        driverImageView = (SelectableRoundedImageView) findViewById(R.id.driverImageView);
        callState = (TextView) findViewById(R.id.callState);
        callState.setText(generalFunctions.retrieveLangLBl("", "LBL_CALLING"));
        deriverImage = getIntent().getStringExtra("PImage");
        driverName = getIntent().getStringExtra("Name");

        String type = getIntent().getStringExtra("type");
        if (type != null && type.equalsIgnoreCase("Driver")) {
            if (deriverImage != null && !deriverImage.equals("")) {
                deriverImage = CommonUtilities.SERVER_URL_PHOTOS + "upload/Driver/" + getIntent().getStringExtra("Id") + "/" + deriverImage;
            }
        } else if (type != null && type.equalsIgnoreCase("Company")) {
            if (deriverImage != null && !deriverImage.equals("")) {
                deriverImage = CommonUtilities.SERVER_URL_PHOTOS + "upload/Company/" + getIntent().getStringExtra("Id") + "/" + deriverImage;
            }
        }

        driverImageView.setImageDrawable(getResources().getDrawable(R.mipmap.ic_no_pic_user));
        if (deriverImage != null && !deriverImage.equals("")) {
            Picasso.with(this).load(deriverImage).error(R.mipmap.ic_no_pic_user).into(driverImageView);
        }


        MButton answer = (MButton) findViewById(R.id.answerButton);
        answer.setOnClickListener(mClickListener);
        MButton decline = (MButton) findViewById(R.id.declineButton);
        decline.setOnClickListener(mClickListener);

        new CreateRoundedView(Color.parseColor("#d2494a"), 5, 0, 0, decline);
        new CreateRoundedView(Color.parseColor("#1a9574"), 5, 0, 0, answer);


        Uri soundUri = Settings.System.DEFAULT_NOTIFICATION_URI;

        if (generalFunctions.getJsonValue("VOIP_NOTIFICATION", userProfileJson).equalsIgnoreCase("voip_notification_1.mp3")) {
            soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + MyApp.getInstance().getPackageName() + "/" + R.raw.notification_1);
        } else if (generalFunctions.getJsonValue("VOIP_NOTIFICATION", userProfileJson).equalsIgnoreCase("voip_notification_2.mp3")) {
            soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + MyApp.getInstance().getPackageName() + "/" + R.raw.notification_2);
        } else if (generalFunctions.getJsonValue("VOIP_NOTIFICATION", userProfileJson).equalsIgnoreCase("voip_notification_3.mp3")) {
            soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + MyApp.getInstance().getPackageName() + "/" + R.raw.notification_3);
        } else if (generalFunctions.getJsonValue("VOIP_NOTIFICATION", userProfileJson).equalsIgnoreCase("voip_notification_4.mp3")) {
            soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + MyApp.getInstance().getPackageName() + "/" + R.raw.notification_4);
        } else if (generalFunctions.getJsonValue("VOIP_NOTIFICATION", userProfileJson).equalsIgnoreCase("voip_notification_5.mp3")) {
            soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + MyApp.getInstance().getPackageName() + "/" + R.raw.notification_5);
        } else if (generalFunctions.getJsonValue("VOIP_NOTIFICATION", userProfileJson).equalsIgnoreCase("voip_notification_6.mp3")) {
            soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + MyApp.getInstance().getPackageName() + "/" + R.raw.notification_6);
        } else if (generalFunctions.getJsonValue("VOIP_NOTIFICATION", userProfileJson).equalsIgnoreCase("voip_notification_7.mp3")) {
            soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + MyApp.getInstance().getPackageName() + "/" + R.raw.notification_7);
        } else if (generalFunctions.getJsonValue("VOIP_NOTIFICATION", userProfileJson).equalsIgnoreCase("voip_notification_8.mp3")) {
            soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + MyApp.getInstance().getPackageName() + "/" + R.raw.notification_8);
        } else if (generalFunctions.getJsonValue("VOIP_NOTIFICATION", userProfileJson).equalsIgnoreCase("voip_notification_9.mp3")) {
            soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + MyApp.getInstance().getPackageName() + "/" + R.raw.notification_9);
        } else if (generalFunctions.getJsonValue("VOIP_NOTIFICATION", userProfileJson).equalsIgnoreCase("voip_notification_10.mp3")) {
            soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + MyApp.getInstance().getPackageName() + "/" + R.raw.notification_10);
        }

        mp = MediaPlayer.create(getApplicationContext(), soundUri);
        if (mp != null) {
            mp.setLooping(true);
            mp.start();
        }
        mCallId = getIntent().getStringExtra(SinchService.CALL_ID);
    }

    @Override
    protected void onServiceConnected() {
        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            call.addCallListener(new SinchCallListener());
            TextView remoteUser = (TextView) findViewById(R.id.remoteUser);
            // remoteUser.setText(call.getRemoteUserId());
            remoteUser.setText(driverName);
        } else {
            Log.e(TAG, "Started with invalid callId, aborting");
            finish();
        }
    }

    private void answerClicked() {
        if (mp != null) {
            mp.stop();
        }
        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            call.answer();
            Intent intent = new Intent(this, CallScreenActivity.class);

            intent.putExtra(SinchService.CALL_ID, mCallId);
            intent.putExtra("vImage", deriverImage);
            intent.putExtra("vName", driverName);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            startActivity(intent);
        } else {
            finish();
        }
    }

    private void declineClicked() {
        if (mp != null) {
            mp.stop();
        }
        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            call.hangup();
        }
        finish();
    }

    private class SinchCallListener implements CallListener {

        @Override
        public void onCallEnded(Call call) {
            CallEndCause cause = call.getDetails().getEndCause();
            Log.d(TAG, "Call ended, cause: " + cause.toString());
            if (mp != null) {
                mp.stop();
            }
            finish();
        }

        @Override
        public void onCallEstablished(Call call) {
            Log.d(TAG, "Call established");
        }

        @Override
        public void onCallProgressing(Call call) {
            Log.d(TAG, "Call progressing");
        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> pushPairs) {
            // Send a push through your push provider here, e.g. GCM
        }
    }

    private OnClickListener mClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.answerButton:

                    if (generalFunctions.isCallPermissionGranted(false) == false) {
                        generalFunctions.isCallPermissionGranted(true);
                        return;
                    }
                    answerClicked();
                    break;
                case R.id.declineButton:
                    declineClicked();
                    break;
            }
        }
    };
}
