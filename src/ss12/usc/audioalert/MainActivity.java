package ss12.usc.audioalert;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        /* @SuppressWarnings("deprecation")
		int channel_config = AudioFormat.CHANNEL_CONFIGURATION_MONO;
		int format = AudioFormat.ENCODING_PCM_16BIT;
		int sampleSize = 8000;
		int bufferSize = AudioRecord.getMinBufferSize(sampleSize, channel_config, format);
		AudioRecord audioInput = new AudioRecord(AudioSource.MIC, sampleSize, channel_config, format, bufferSize);
		
		short[] audioBuffer = new short[bufferSize];
		audioInput.startRecording();
		audioInput.read(audioBuffer, 0, bufferSize); */

		Intent intent = new Intent(this, DisplayMessageActivity.class);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    
}

