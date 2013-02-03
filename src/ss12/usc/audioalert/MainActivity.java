package ss12.usc.audioalert;

import java.util.Arrays;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
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
    public final static int NUM_TOP_MAGNITUDES = 10;
	int[] mSampleRates = new int[]{44100, 22050, 11025, 8000};
    String shortSequence;
    
    @Override    
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
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
			
			//AudioRecordTest art = new AudioRecordTest();
			//art.record(true);
			recorder.startRecording();
			Log.i("MainActivity", "Started recording!");
			recorder.read(audioBuffer, 0, bufferSize);
			recorder.stop();
			//art.record(false);
			tv_status.setText("" + tv_status.getText() + "LISTENING HAS STOPPED");
			//art.play(true);
			long time = System.currentTimeMillis();
			//art.play(false);
			// this doesn't work :(
			/*
			AudioTrack playback =
					new AudioTrack(AudioManager.STREAM_ALARM, sampleSize, channel_config, format, bufferSize, AudioTrack.MODE_STATIC);
			playback.write(audioBuffer, 0, bufferSize);
			playback.play();
			*/
			
			/*
			 * FFT analysis here - Source:
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
		    FreqMag[] fm_array = new FreqMag[fftArray.length];
		    for(int i = 0; i < fm_array.length; i++)
		    {
		    	Complex what = fftArray[i];
		    	fm_array[i] = new FreqMag(getFreq(i, sampleSize, fftArray.length), Math.sqrt(what.re()*what.re() + what.im()*what.im()));
		    }
		    
		    /*
		    FreqMag[] max = new FreqMag[NUM_TOP_MAGNITUDES];
		    double smallestMax = Double.MAX_VALUE;
		    int indSM = -1;
		    for(int i = 0; i < fm_array.length; i++)
		    {
		    	double theMag = fm_array[i].mag;
		    	if(i < NUM_TOP_MAGNITUDES)
		    	{
		    		max[i] = fm_array[i];
		    		if(smallestMax > theMag)
		    		{
		    			smallestMax = theMag;
		    			indSM = i;
		    		}
		    	}
		    	else
	    		{
		    		if(smallestMax < theMag)
		    		{
		    			max[indSM] = new FreqMag(getFreq(i, sampleSize, fftArray.length), theMag);
		    			smallestMax = Double.MAX_VALUE;
		    			for(int j = 0; j < max.length; j++)
		    			{
	    		    		if(smallestMax > max[j].mag)
	    		    		{
	    		    			smallestMax = max[j].mag;
	    		    			indSM = j;
	    		    		}
		    			}
		    		}
	    		}
		    }
		    */
		    
		    FreqMag[] debug_mags = new FreqMag[fm_array.length];
		    for(int a = 0; a < debug_mags.length; a++)
		    	debug_mags[a] = fm_array[a];
		    Arrays.sort(debug_mags);
		    String debug_largestMags = " ";
		    for(int a = 0; a < NUM_TOP_MAGNITUDES; a++)
		    	debug_largestMags += debug_mags[a].toString() + "\n";
		    Log.i("EXPECTED GREATEST MAGNITUDES", debug_largestMags);
		    
			shortSequence = "Sequence:";
			for(short s : audioBuffer)
			{
				shortSequence += " " + s;
			}
			String doubleSequence = "";
			/*
			String complexSequence = "";
			for(Complex c : fftArray)
				complexSequence += " " + c;
			*/
			Log.i("AFTER CONVERSION", doubleSequence);
		}
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    public void sendMessage(View view) {
    	finish();
    }
    
    public double getFreq(int index, int fs, int N)
    {
    	return index * fs / (N * 1.0);
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();  // Always call the superclass

	    Intent intent = new Intent(this, Alert.class);
	    intent.putExtra("alert-id", 2);
	    Log.i("MainActivity", "Printing " + shortSequence);
	    intent.putExtra("seq", shortSequence);
	    startActivity(intent);
    }
}

class FreqMag implements Comparable<FreqMag>
{
	public double freq;
	public double mag;
	public FreqMag(double one, double two)
	{
		freq = one;
		mag = two;
	}
	public String toString()
	{
		return "" + freq + " " + mag;
	}
	public int compareTo(FreqMag other)
	{
		return (int)(Double.compare(other.mag, mag));
	}
}