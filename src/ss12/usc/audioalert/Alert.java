package ss12.usc.audioalert;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;


@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
@SuppressLint("NewApi")
public class Alert extends Activity {
	
	Vibrator v;
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alert);
		
		//Intent i=new Intent(getApplicationContext(), sample.class);
        //i.putExtra("id", id);
        //startActivity();
				
		int alertType = getIntent().getExtras().getInt("alert-id");

		v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		
		TextView tv = (TextView)findViewById(R.id.textView1);
		
		long[] _pattern = new long[]{0,100,50,100,50,100};
		long[] _pattern2 = new long[]{0,50,50,50,50,50,50,50,50,50,50,50};
		
		if(alertType == 1){
			// 3 x long
			tv.setText("Police Siren");
			v.vibrate(_pattern2, 2);
			NotificationManager notificationManager = (NotificationManager) 
					  getSystemService(NOTIFICATION_SERVICE); 
			Intent intent = new Intent(this, Alert.class);
			PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
			Notification noti = new Notification.Builder(this)
				.setSmallIcon(android.R.drawable.stat_sys_warning)
		        .setContentTitle("EMERGENCY ALERT DETECTED")
		        .setContentText("Police Siren detected").build();
			NotificationManager notiManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			noti.flags = Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL;
			notificationManager.notify(0,noti);
		}		
		else if(alertType == 2){
			// 6 x short
			tv.setText("Tornado Warning");
			v.vibrate(_pattern, 2);	
			
			NotificationManager notificationManager = (NotificationManager) 
					  getSystemService(NOTIFICATION_SERVICE); 
			Intent intent = new Intent(this, Alert.class);
			PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
			Notification noti = new Notification.Builder(this)
			.setSmallIcon(android.R.drawable.stat_sys_warning)
		        .setContentTitle("EMERGENCY ALERT DETECTED")
		        .setContentText("Tornado warning detected").build();
			NotificationManager notiManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			noti.flags = Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL;
			notificationManager.notify(0,noti);
		}
		
		/*start notification
		
			NotificationManager notificationManager = (NotificationManager) 
					  getSystemService(NOTIFICATION_SERVICE); 
			
			Intent intent = new Intent(this, Alert.class);
			PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
			Notification noti = new Notification.Builder(this)
				.setSmallIcon(icon)
		        .setContentTitle("EMERGENCY ALERT DETECTED")
		        .setContentText(alert_name).build();
			
			NotificationManager notiManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			
			noti.flags = Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL;

			notificationManager.notify(0,noti);
			
			end notification*/
			
	}
	
	
    public void okayStop(View view) {

		v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		long[] stopthis = new long[]{0,0};
		v.vibrate(stopthis,0);
    	v.cancel();

	    Intent intent = new Intent(this, MainActivity.class);
	    //intent.putExtra("redo", true);
	    startActivity(intent);
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();  // Always call the superclass
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_alert, menu);
		return true;
	}

}
