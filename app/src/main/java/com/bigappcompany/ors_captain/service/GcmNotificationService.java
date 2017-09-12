package com.bigappcompany.ors_captain.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.bigappcompany.ors_captain.R;
import com.bigappcompany.ors_captain.activity.HomeActivity;
import com.bigappcompany.ors_captain.util.Preference;
import com.google.android.gms.gcm.GcmListenerService;

import java.io.IOException;
import java.net.URL;

public class GcmNotificationService extends GcmListenerService {
	private static final String TAG = "GcmNotificationService";
	private static final int ORDER_TEXT_NOTIFICATION = 1;
	private static final int ORDER_IMAGE_NOTIFICATION = 2;
	
	public GcmNotificationService() {
		super();
	}
	
	@Override
	public void onMessageReceived(String from, Bundle data) {
		Log.e(TAG, data.toString());
		
		Preference pref = new Preference(this);
		
		if (pref.isLoggedIn()) {
			int gcmOrder = Integer.parseInt(data.getString("order"));
			try {
				switch (gcmOrder) {
					case ORDER_TEXT_NOTIFICATION:
						String title = data.getString("title");
						String message = data.getString("message");
						textNotification(title, message);
						break;
					
					case ORDER_IMAGE_NOTIFICATION:
						title = data.getString("title");
						message = data.getString("message");
						String imageUrl = data.getString("image_link");
						imageNotification(title, message, imageUrl);
						break;
				}
			} catch (IOException e) {
				Log.e(TAG, e.getMessage(), e);
			}
		}
	}
	
	private void textNotification(String title, String message) {
		Intent intent = new Intent(this, HomeActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
		
		Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
		    .setSmallIcon(R.mipmap.ic_launcher)
		    .setContentTitle(title)
		    .setContentText(message)
		    .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
		    .setAutoCancel(true)
		    .setSound(defaultSoundUri)
		    .setVibrate(new long[]{300, 300})
		    .setContentIntent(pendingIntent);
		
		NotificationManager notificationManager =
		    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		
		notificationManager.notify(0, notificationBuilder.build());
	}
	
	private void imageNotification(final String title, final String message, String imageUrl) throws IOException {
		URL url = new URL(imageUrl);
		Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
		
		Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent pendingIntent = PendingIntent.getActivity(
		    getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT
		);
		
		Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext())
		    .setSmallIcon(R.mipmap.ic_launcher)
		    .setStyle(new NotificationCompat.BigPictureStyle()
			.bigPicture(bitmap)
			.setSummaryText(message)
		    )
		    .setAutoCancel(true)
		    .setContentText(message)
		    .setContentTitle(title)
		    .setSound(defaultSoundUri)
		    .setVibrate(new long[]{300, 300})
		    .setContentIntent(pendingIntent);
		
		NotificationManager notificationManager =
		    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		
		notificationManager.notify(0, notificationBuilder.build());
	}
}