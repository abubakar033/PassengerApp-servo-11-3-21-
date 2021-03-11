package com.general.files;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import androidx.core.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.servo.user.BuildConfig;
import com.servo.user.R;
import com.utils.Utils;

/**
 * Created by Admin on 20/03/18.
 */

public class LocalNotification {
    static Context mContext;

    private static String CHANNEL_ID = BuildConfig.APPLICATION_ID + "";
    private static NotificationManager mNotificationManager = null;

    public static void dispatchLocalNotification(Context context, String message, boolean onlyInBackground) {
        mContext = context;

        Log.d("hello ", "I am from "+ MyApp.getInstance().getCurrentAct() + mContext);
        Log.d("hello ", "I am from masg "+message);

        if (MyApp.getInstance().getCurrentAct() == null && mContext == null) {
            Log.d("hello ", "I am from return ");

            return;
        }

        //  createNotification("some title","my body",context,101);


        continueDispatchNotification(message, onlyInBackground);
    }


    private static void continueDispatchNotification(String message1, boolean onlyInBackground) {
        Intent intent = null;






        if (Utils.getPreviousIntent(mContext) != null) {
            intent = Utils.getPreviousIntent(mContext);
        } else {
            intent = mContext
                    .getPackageManager()
                    .getLaunchIntentForPackage(mContext.getPackageName());

            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT |
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        }

        PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        GeneralFunctions generalFunctions = MyApp.getInstance().getGeneralFun(mContext);
        String userProfileJson = generalFunctions.retrieveValue(Utils.USER_PROFILE_JSON);

        Uri soundUri = Settings.System.DEFAULT_NOTIFICATION_URI;

        if (generalFunctions.getJsonValue("USER_NOTIFICATION", userProfileJson).equalsIgnoreCase("notification_1.mp3")) {
            soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + mContext.getPackageName() + "/" + R.raw.notification_1);
        } else if (generalFunctions.getJsonValue("USER_NOTIFICATION", userProfileJson).equalsIgnoreCase("notification_2.mp3")) {
            soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + mContext.getPackageName() + "/" + R.raw.notification_2);
        }

        if (mNotificationManager != null) {
            mNotificationManager.cancelAll();
            mNotificationManager = null;
        }

        // Receive Notifications in >26 version devices
        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        String id1 = CHANNEL_ID;
        int importance = NotificationManager.IMPORTANCE_LOW;
        NotificationChannel mChannel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel(id1, "khan", importance);
            mChannel.enableLights(true);
            mNotificationManager.createNotificationChannel(mChannel);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //  mBuilder.setChannelId(BuildConfig.APPLICATION_ID);
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    mContext.getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_HIGH
            );
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            // channel.setSound(soundUri, audioAttributes);

            //fixme abubakar panding testing
            if (mNotificationManager != null) {
                mNotificationManager.createNotificationChannel(channel);
            }
        }

        Bitmap mIcon = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.icon);

        NotificationCompat.BigPictureStyle notifystyle = new NotificationCompat.BigPictureStyle();
        notifystyle.bigPicture(mIcon);
        RemoteViews contentView = new RemoteViews(mContext.getPackageName(), R.layout.coustom_notificaion_layout);
        contentView.setImageViewBitmap(R.id.image, mIcon);

        if (message1.contains("@title@")) {
            String currentString = message1;
            String[] separated = currentString.split("@title@");
            String title = separated[0];
            String msgnew = separated[1];
            contentView.setTextViewText(R.id.title_text,title);
            contentView.setTextViewText(R.id.detailTV, msgnew);
        }
        else {
            contentView.setTextViewText(R.id.title_text,"Servo");
            contentView.setTextViewText(R.id.detailTV, message1);
        }


        RemoteViews smallcontentView = new RemoteViews(mContext.getPackageName(), R.layout.small_noti_laout);
        contentView.setImageViewBitmap(R.id.image, mIcon);
        if (message1.contains("@title@")) {
            String currentString = message1;
            String[] separated = currentString.split("@title@");
            String title = separated[0];
            String msgnew = separated[1];
            contentView.setTextViewText(R.id.title_textS,title);
            contentView.setTextViewText(R.id.detailTVS, msgnew);
        }
        else {
            contentView.setTextViewText(R.id.title_textS,"Servo");
            contentView.setTextViewText(R.id.detailTVS, message1);
        }
        /*NotificationCompat.Builder mBuilder*/
        Notification notification = new NotificationCompat.Builder(mContext, CHANNEL_ID)
                // .setSmallIcon(R.drawable.ic_stat_rider_logo)
                .setSmallIcon(R.drawable.icon)
                // .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher))
                // .setContentTitle(mContext.getString(R.string.app_name))
                // .setContentText(message)
                .setAutoCancel(true)
                .setCustomBigContentView(contentView)
                .setCustomContentView(smallcontentView)
                .setContentIntent(contentIntent)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())

                //  .setSound(soundUri)
                //  .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_HIGH).build();
        //  .setPriority(NotificationCompat.PRIORITY_HIGH).build();
        // mNotificationManager.notify(101,notification);


        if (onlyInBackground && MyApp.getInstance().isMyAppInBackGround()) {
//            mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            //  mNotificationManager.notify(Utils.NOTIFICATION_ID, mBuilder.build());
            mNotificationManager.notify(Utils.NOTIFICATION_ID, notification);
            playNotificationSound(soundUri);
        } else if (!onlyInBackground) {
//            mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            // mNotificationManager.notify(Utils.NOTIFICATION_ID, mBuilder.build());
            mNotificationManager.notify(Utils.NOTIFICATION_ID, notification);
            playNotificationSound(soundUri);
        }
    }

    public static void playNotificationSound(Uri nofifyUrl) {
        try {

            Ringtone r = RingtoneManager.getRingtone(mContext, nofifyUrl);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void clearAllNotifications() {
        if (mNotificationManager != null) {
            mNotificationManager.cancelAll();
            mNotificationManager = null;
        }
    }
}
