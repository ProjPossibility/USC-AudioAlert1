package ss12.usc.audioalert;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder.AudioSource;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

// code for checking for finding a valid AudioRecord comes from this source:
// http://stackoverflow.com/questions/4843739/audiorecord-object-not-initializing

public class MainActivity extends Activity {
	
    public final static String EXTRA_MESSAGE = "ss12.usc.audioalert.MESSAGE";
    String shortSequence;
    
    @Override    
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
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
	                        if (recorder.getState() != AudioRecord.STATE_INITIALIZED)
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
			int channel_config = recorder.getChannelConfiguration();
			int format = recorder.getAudioFormat();
			int bufferSize = AudioRecord.getMinBufferSize(sampleSize, channel_config, format);
			
			byte[] audioBuffer = new byte[bufferSize];
			
			TextView tv_status = (TextView)findViewById(R.id.textView_status);
			tv_status.setText("LISTENING HAS STARTED\n");
			
			recorder.startRecording();
			recorder.read(audioBuffer, 0, bufferSize);
			recorder.stop();
			tv_status.setText("" + tv_status.getText() + "LISTENING HAS STOPPED");
			
			// this doesn't work :(
			/*
			AudioTrack playback =
					new AudioTrack(AudioManager.STREAM_ALARM, sampleSize, channel_config, format, bufferSize, AudioTrack.MODE_STATIC);
			playback.write(audioBuffer, 0, bufferSize);
			playback.play();
			*/
			
			/*
			 * FFT analysis here
			 * Source:
			 *	http://stackoverflow.com/questions/5774104/
			 *	android-audio-fft-to-retrieve-specific-frequency-magnitude-using-audiorecord
			*/
			int newBufferSize = 1;
			while(newBufferSize < bufferSize)
				newBufferSize *= 2;
			double[] micBufferData = new double[newBufferSize];
		    final int bytesPerSample = 2; // As it is 16bit PCM
		    final double amplification = 100.0; // choose a number as you like
		    for (int index = 0, floatIndex = 0; index < bufferSize - bytesPerSample + 1; index += bytesPerSample, floatIndex++) {
		        double sample = 0;
		        for (int b = 0; b < bytesPerSample; b++) {
		            int v = audioBuffer[index + b];
		            if (b < bytesPerSample - 1 || bytesPerSample == 1) {
		                v &= 0xFF;
		            }
		            sample += v << (b * 8);
		        }
		        double sample32 = amplification * (sample / 32768.0);
		        micBufferData[floatIndex] = sample32;
		    }
		    Complex[] fftTempArray = new Complex[newBufferSize];
		    for (int i=0; i<newBufferSize; i++)
		    {
		        fftTempArray[i] = new Complex(micBufferData[i], 0);
		    }
		    Complex[] fftArray = FFT.fft(fftTempArray);
		    double[] magnitudes = new double[fftArray.length];
		    for(int i = 0; i < magnitudes.length; i++)
		    {
		    	Complex what = fftArray[i];
		    	magnitudes[i] = Math.sqrt(what.re()*what.re() + what.im()*what.im());
		    }
		    
			shortSequence = "Sequence:";
			for(short s : audioBuffer)
			{
				shortSequence += " " + s;
			}
			String doubleSequence = "";
			for(double d : micBufferData)
			{
				doubleSequence += " " + d;
			}
			String complexSequence = "";
			for(Complex c : fftArray)
			{
				complexSequence += " " + c;
			}
			Log.i("AFTER CONVERSION", doubleSequence);
			Log.i("AFTER FFT", complexSequence);
		}
    }
    
    public void sendMessage(View view) {
	    Intent intent = new Intent(this, Alert.class);
	    intent.putExtra("alert-id", 2);
	    Log.i("MainActivity", "Printing " + shortSequence);
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
