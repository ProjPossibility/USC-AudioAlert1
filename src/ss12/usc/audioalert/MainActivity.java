package ss12.usc.audioalert;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder.AudioSource;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;

// code for checking for finding a valid AudioRecord comes from this source:
// http://stackoverflow.com/questions/4843739/audiorecord-object-not-initializing

public class MainActivity extends Activity {
	
    public final static String EXTRA_MESSAGE = "ss12.usc.audioalert.MESSAGE";
    String shortSequence;
    
    @Override    
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
		int channel_config = AudioFormat.CHANNEL_CONFIGURATION_MONO;
		int format = AudioFormat.ENCODING_PCM_16BIT;
		int[] mSampleRates = new int[]{44100, 22050, 11025, 8000};
		AudioRecord recorder = null;
		
		for (int rate : mSampleRates) {
	        for (short audioFormat : new short[] { AudioFormat.ENCODING_PCM_8BIT, AudioFormat.ENCODING_PCM_16BIT }) {
	            for (short channelConfig : new short[] { AudioFormat.CHANNEL_IN_MONO, AudioFormat.CHANNEL_IN_STEREO }) {
	                try {
	                    Log.d("MainActivity", "Attempting rate "+rate+"Hz, bits: "+audioFormat+", channel: "+channelConfig);
	                    int bufferSize = AudioRecord.getMinBufferSize(rate, channelConfig, audioFormat);

	                    if (bufferSize != AudioRecord.ERROR_BAD_VALUE) {
	                        // check if we can instantiate and have a success
	                        recorder = new AudioRecord(AudioSource.DEFAULT, rate, channelConfig, audioFormat, bufferSize);
	                        if (recorder.getState() == AudioRecord.STATE_INITIALIZED)
	                            recorder = null;
	                    }
	                } catch (Exception e) {
	                    Log.e("MainActivity", rate + "Exception, keep trying.",e);
	                }
	            }
	        }
	    }
		if(recorder != null)
		{
			Log.d("MainActivity", "LOLOLOL IT WORKS :D");
			int sampleSize = recorder.getSampleRate();
			int bufferSize = AudioRecord.getMinBufferSize(sampleSize, channel_config, format);
			AudioRecord audioInput = new AudioRecord(AudioSource.MIC, sampleSize, channel_config, format, bufferSize);
			
			short[] audioBuffer = new short[bufferSize];
			audioInput.startRecording();
			audioInput.read(audioBuffer, 0, bufferSize);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			audioInput.stop();
			
			shortSequence = "Sequence: ";
			for(short s : audioBuffer)
			{
				shortSequence += " " + s;
			}
		}
    }
    
    public void sendMessage(View view) {
	    Intent intent = new Intent(this, Alert.class);
	    intent.putExtra("alert-id", 2);
	    intent.putExtra("seq", shortSequence);
	    startActivity(intent);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
}

