package ss12.usc.audioalert;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Menu;


public class Alert extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alert);
		
		Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

		long[] _pattern = new long[]{0,100,50,100,50,100};
		v.vibrate(_pattern, -1);		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_alert, menu);
		return true;
	}

}
