package grdp.emart.store.FCM;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.RingtoneManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import grdp.emart.store.Activities.SplashScreen;
import grdp.emart.store.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;


public class MyFirebaseMessagingService2 extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();
    public static final String ANDROID_CHANNEL_ID = "com.abhiandroid.ecommercestorein";
    public static final String ANDROID_CHANNEL_NAME = "ANDROID CHANNEL";
    String value, type;
    int count=0;
    NotificationManager notificationManager;
    String placeImage,placeId,placeTitle,placeMessage;
    String random_id;
    public static int NOTIFICATION_ID = 0;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage == null)
            return;

        notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannels();

            notificationManager.notify(0  ,  getAndroidChannelNotification(remoteMessage).build());
        }else{

            createNotification(remoteMessage);
        }


    }

    private void createNotification(RemoteMessage remoteMessage) {
        // Check if message contains a data payload.
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());
            Log.e(TAG, "Data Payload1: " + remoteMessage.getData().get("image"));
            handleDataMessage(remoteMessage.getData());
        } else if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
            handleNotification(remoteMessage.getNotification().getBody());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Notification.Builder getAndroidChannelNotification(RemoteMessage remoteMessage) {
        Intent resultIntent = null;

        if (remoteMessage.getData().size() > 0) {

            try {
                placeImage=remoteMessage.getData().get("image");
                placeTitle=remoteMessage.getData().get("title");
                placeMessage=remoteMessage.getData().get("message");
                placeId=remoteMessage.getData().get("product_id");
                random_id = remoteMessage.getData().get("random_id");

                resultIntent = new Intent(getApplicationContext(), SplashScreen.class);
                resultIntent.putExtra("id",placeId);
                //handleDataMessage(remoteMessage.getNotification().getBody(), json);
            } catch (Exception e) {
                placeTitle=remoteMessage.getNotification().getTitle();
                placeMessage=remoteMessage.getNotification().getBody();
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        } else if (remoteMessage.getNotification() != null) {
            resultIntent = new Intent(getApplicationContext(), SplashScreen.class);

            resultIntent.putExtra("id", "");
            // handleNotification(remoteMessage.getNotification().getBody());
        }

        Bitmap remote_picture = null;
        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0,   resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        try {
            remote_picture = BitmapFactory.decodeStream((InputStream) new URL(placeImage).getContent());
        } catch (IOException e) {
            e.printStackTrace();
        }


        return new Notification.Builder(getApplicationContext(), ANDROID_CHANNEL_ID)
                .setContentTitle(placeTitle)
                .setContentText(placeMessage)
                .setSmallIcon(R.drawable.appicon)
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .setContentIntent(contentIntent)
                .setStyle(new Notification.BigPictureStyle()
                        .bigPicture(remote_picture))
        ;

    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createChannels() {

        // create android channel
        NotificationChannel androidChannel = new NotificationChannel(ANDROID_CHANNEL_ID,
                ANDROID_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
        // Sets whether notifications posted to this channel should display notification lights
        androidChannel.enableLights(true);
        // Sets whether notification posted to this channel should vibrate.
        androidChannel.enableVibration(true);
        // Sets the notification light color for notifications posted to this channel
        androidChannel.setLightColor(Color.GREEN);
        // Sets whether notifications posted to this channel appear on the lockscreen or not
        androidChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        notificationManager.createNotificationChannel(androidChannel);
    }
    private void handleNotification(String message) {
        Intent resultIntent = new Intent(getApplicationContext(), SplashScreen.class);
        resultIntent.putExtra("id", "");
        // check for image attachment
        sendNotification("", message, "", resultIntent);
    }

    private void handleDataMessage(Map<String, String> data) {


        placeImage=data.get("image");
        placeTitle=data.get("title");
        placeMessage=data.get("message");
        placeId=data.get("product_id");
        random_id = data.get("random_id");
        if (NOTIFICATION_ID != Integer.parseInt(random_id)) {
            NOTIFICATION_ID= Integer.parseInt(random_id);
            handleMessage(getApplicationContext());
        }
    }


    private void sendNotification(String placeMessage, String title, String placeImage, Intent resultIntent) {
        int requestID = (int) System.currentTimeMillis();


        PendingIntent intent =
                PendingIntent.getActivity(getApplicationContext(), requestID, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mNotifyBuilder;
        NotificationManager mNotificationManager;

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP_MR1) {
            mNotifyBuilder = new NotificationCompat.Builder(this)
                    .setContentTitle(title)
                    .setPriority(1);
            mNotifyBuilder.setSmallIcon(R.drawable.appicon);

        } else {
            // Lollipop specific setColor method goes here.
            mNotifyBuilder = new NotificationCompat.Builder(this)
                    .setContentTitle(title)
                    .setColor(Color.WHITE)
                    .setPriority(1);

            mNotifyBuilder.setSmallIcon(R.drawable.appicon);
            mNotifyBuilder.setLargeIcon(((BitmapDrawable) getResources().getDrawable(R.drawable.appicon)).getBitmap());

        }


        // Set pending intent

        // Set Vibrate, Sound and Light
        int defaults = 0;
        defaults = defaults | Notification.DEFAULT_LIGHTS;
        defaults = defaults | Notification.DEFAULT_VIBRATE;
        defaults = defaults | Notification.DEFAULT_SOUND;

        mNotifyBuilder.setDefaults(defaults);
        // Set autocancel
        mNotifyBuilder.setAutoCancel(true);
        mNotifyBuilder.setContentIntent(intent);
        // Post a notification
        mNotificationManager.notify(requestID, mNotifyBuilder.build());
    }



    @SuppressWarnings("deprecation")
    private void handleMessage(Context mContext) {
        Bitmap remote_picture = null;
        int icon = R.drawable.appicon;
        //if message and image url
        if (placeMessage!= null && placeImage != null) {
            try {


                Log.v("TAG_MESSAGE", "" + placeMessage);
                Log.v("TAG_IMAGE", "" + placeImage);


                NotificationCompat.BigPictureStyle notiStyle = new NotificationCompat.BigPictureStyle();
                notiStyle.setSummaryText(placeMessage);

                try {
                    remote_picture = BitmapFactory.decodeStream((InputStream) new URL(placeImage).getContent());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                notiStyle.bigPicture(remote_picture);
                notificationManager = (NotificationManager) mContext
                        .getSystemService(Context.NOTIFICATION_SERVICE);
                PendingIntent contentIntent = null;

                Intent gotoIntent = new Intent();
                gotoIntent.putExtra("id",placeId);
                gotoIntent.setClassName(mContext, getApplicationContext().getPackageName()+".Activities.SplashScreen");//Start activity when user taps on notification.
                contentIntent = PendingIntent.getActivity(mContext,
                        (int) (Math.random() * 100), gotoIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                        mContext);
                Notification notification = mBuilder.setSmallIcon(icon).setTicker(placeTitle).setWhen(0)
                        .setLargeIcon(((BitmapDrawable) getResources().getDrawable(icon)).getBitmap())
                        .setAutoCancel(true)
                        .setContentTitle(placeTitle)
                        .setPriority(1)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(placeMessage))
                        .setContentIntent(contentIntent)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))

                        .setContentText(placeMessage)
                        .setStyle(notiStyle).build();


                notification.flags = Notification.FLAG_AUTO_CANCEL;
                count++;
                notificationManager.notify(count, notification);//This will generate seperate notification each time server sends.

            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }
}