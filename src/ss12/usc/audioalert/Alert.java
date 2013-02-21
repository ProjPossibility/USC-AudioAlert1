package ss12.usc.audioalert;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Alert extends Activity {
	
	Vibrator v;
	AnimationDrawable a;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);
    }
	
	@SuppressLint("NewApi")
	@Override
	protected void onResume(){
		super.onResume();
		
        Log.d("AlertActivity", "In onResume!");
		
		//Intent i=new Intent(getApplicationContext(), sample.class);
        //i.putExtra("id", id);
        //startActivity();
				
		int alertType = getIntent().getExtras().getInt("alert-id");

		v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		
		TextView tv = (TextView)findViewById(R.id.textView1);
		ImageView iv = (ImageView)findViewById(R.id.imageView1);
		
		long[] _pattern = new long[]{0,100,50,100,50,100};
		long[] _pattern2 = new long[]{0,50,50,50,50,50,50,50,50,50,50,50};
		long[] _pattern3 = new long[]{0,30,150,30,150,30,150,30,150};
		
		NotificationManager notificationManager = (NotificationManager) 
				  getSystemService(NOTIFICATION_SERVICE); 
		
		int notiIcon = android.R.drawable.stat_sys_warning;
		String alert_name = "";
		
		Resources res = getResources();
		int resourceId = res.getIdentifier("", "drawable", getPackageName() );
		
		if(alertType == 1){
			// 3 x long
			v.vibrate(_pattern2, 2);
		    alert_name = "Police Siren";
		    resourceId = res.getIdentifier("siren", "drawable", getPackageName() );
		}		
		else if(alertType == 2){
			// 2 x short, 2 x long
			v.vibrate(_pattern, 2);	
			alert_name = "Tornado Warning";
			resourceId = res.getIdentifier("tornado", "drawable", getPackageName() );
		}
		else if(alertType == 3){
			v.vibrate(_pattern3, 2);	
			alert_name = "Smoke Alarm";
			resourceId = res.getIdentifier("smoke", "drawable", getPackageName() );
		}
		
		// displays icon for each alert
		iv.setImageResource(resourceId);
		
		// prints alert type to screen
		tv.setText(alert_name);
		
		Notification noti = new Notification.Builder(this)
			.setSmallIcon(notiIcon)
	        .setContentTitle("EMERGENCY ALERT DETECTED")
	        .setContentText(alert_name).build();		
		NotificationManager notiManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);		
		noti.flags = Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL;
		notificationManager.notify(0,noti);
		
		/*
		View view= findViewById(R.id.top_layout);
	    final int red = Color.argb(255,225,0,0);
	    view.setBackgroundColor(red);
		*/
		
		final int DELAY = 750;
		final int red = Color.argb(255,225,0,0);
		final int white = Color.argb(255,225,225,225);
		
            RelativeLayout flash = (RelativeLayout) findViewById(R.id.bottom_layout);

            ColorDrawable f = new ColorDrawable(red);
            ColorDrawable f2 = new ColorDrawable(white);

            a = new AnimationDrawable();
            
            a.addFrame(f, DELAY);
            a.addFrame(f2, DELAY);
            
            flash.setBackground(a);      
            
            a.setOneShot(false);
            a.start();
            
	}
	
	public void dispachBackKey() {
	    dispatchKeyEvent(new KeyEvent (KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
	    dispatchKeyEvent(new KeyEvent (KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));
	}
	
    public void okayStop(View view) {
    	dispachBackKey();
    }
    
    @Override
    public void onPause() {
        super.onPause();  // Always call the superclass
    	a.stop();
		v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		long[] stopthis = new long[]{0,0};
		v.vibrate(stopthis,0);
    	v.cancel();
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_alert, menu);
		return true;
	}

}
