package ss12.usc.audioalert;

import android.app.Activity;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder.AudioSource;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Menu;


public class Alert extends Activity {
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alert);
		
//****************//		
		int channel_config = AudioFormat.CHANNEL_IN_MONO;
		int format = AudioFormat.ENCODING_PCM_16BIT;
		int sampleSize= 8000;
		int bufferSize = AudioRecord.getMinBufferSize(sampleSize, channel_config, format);
		AudioRecord audioInput = new AudioRecord(AudioSource.MIC, sampleSize, channel_config, format, bufferSize);


		short[] audioBuffer = new short[bufferSize];
		audioInput.startRecording();
		audioInput.read(audioBuffer, 0, bufferSize);
		
//****************//
		
		Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

		long[] _pattern = new long[]{0,300,0,300,0,300};
		v.vibrate(_pattern, -1);
		
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_alert, menu);
		return true;
	}

}
