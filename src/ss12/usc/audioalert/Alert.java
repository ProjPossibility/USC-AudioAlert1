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
		
		//Intent i=new Intent(getApplicationContext(), sample.class);
        //i.putExtra("id", id);
        //startActivity();
		
		String i =	getIntent().getExtras().getSerializable("alert-id").toString();
		
		Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);


		long[] _pattern = new long[]{0,100,50,100,50,100};
		long[] _pattern2 = new long[]{0,50,50,50,50,50,50,50,50,50,50,50};
		
		if(i == "1"){
			// 3 x long
			v.vibrate(_pattern, -1);
		}		
		else if(i == "2"){
			// 6 x short
			v.vibrate(_pattern2, -1);	
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_alert, menu);
		return true;
	}

}
