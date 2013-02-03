package ss12.usc.audioalert;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;


public class Alert extends Activity {
	
	Vibrator v;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alert);
		
		//Intent i=new Intent(getApplicationContext(), sample.class);
        //i.putExtra("id", id);
        //startActivity();
				
		int alertType = getIntent().getExtras().getInt("alert-id");
		//String soundSequence = getIntent().getExtras().getString("seq");
		// Log.i("Alert", "Printing " + soundSequence);

		v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		
		TextView tv = (TextView)findViewById(R.id.textView1);
		
		long[] _pattern = new long[]{0,100,50,100,50,100};
		long[] _pattern2 = new long[]{0,50,50,50,50,50,50,50,50,50,50,50};
		
		if(alertType == 1){
			// 3 x long
			tv.setText("Police Siren");
			v.vibrate(_pattern2, 2);
		}		
		else if(alertType == 2){
			// 6 x short
			tv.setText("Tornado Warning");
			v.vibrate(_pattern, 2);	
		}
	}
    
    public void okayStop(View view) {

		v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		long[] stopthis = new long[]{0,0};
		v.vibrate(stopthis,0);
    	
		finish();
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();  // Always call the superclass
	    Intent intent = new Intent(this, MainActivity.class);
	    startActivity(intent);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_alert, menu);
		return true;
	}

}
