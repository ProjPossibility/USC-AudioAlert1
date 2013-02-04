package ss12.usc.audioalert;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder.AudioSource;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.view.Menu;

// code for checking for finding a valid AudioRecord comes from this source:
// http://stackoverflow.com/questions/4843739/audiorecord-object-not-initializing

public class MainActivity extends Activity {
	
    public final static String EXTRA_MESSAGE = "ss12.usc.audioalert.MESSAGE";
    public final static int NUM_TOP_MAGNITUDES = 10;
    public final static long RECORD_TIME = 500;
	int[] mSampleRates = new int[]{44100, 22050, 11025, 8000};
    String shortSequence;
    AudioRecord recorder;
    byte[] audioBuffer;
    int bufferSize, sampleSize;
    boolean flag = false;
    
    long starttime = 0;
	Timer timer = new Timer();
	class FirstTask extends TimerTask { 
		 
        @Override 
        public void run() {
            h.sendEmptyMessage(0); 
        }
	};
	final Handler h = new Handler(new Callback() { 
		 
        public boolean handleMessage(Message msg) { 
           long millis = System.currentTimeMillis() - starttime;
           
           if(millis > RECORD_TIME)
           {
        	   Timer timerTemp = new Timer();
        	   timerTemp.schedule(new SecondTask(), 0);
           }
           return false;
        }
    });
    class SecondTask extends TimerTask { 
		 
       @Override 
       public void run() { 
           h2.sendEmptyMessage(0); 
       }
   };
   final Handler h2 = new Handler(new Callback() { 
		 
       public boolean handleMessage(Message msg) { 
          endRecording();
          return false;
       }
   }); 
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // The activity is being created.
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }    
    @Override
    protected void onStart() {
        super.onStart();
        // The activity is about to become visible.
    }
    
    @Override
    protected void onResume() {
        super.onResume();

        if(getIntent() != null)
        	flag = false;
        
		recorder = null;	
		for (int rate : mSampleRates) {
	        for (short audioFormat : new short[] { AudioFormat.ENCODING_PCM_8BIT, AudioFormat.ENCODING_PCM_16BIT }) {
	            for (short channelConfig : new short[] { AudioFormat.CHANNEL_IN_MONO, AudioFormat.CHANNEL_IN_STEREO }) {
	                try {
	                    Log.d("MainActivity", "Attempting rate "+rate+"Hz, bits: "+audioFormat+", channel: "+channelConfig);
	                    int bS = AudioRecord.getMinBufferSize(rate, channelConfig, audioFormat);

	                    if (bS != AudioRecord.ERROR_BAD_VALUE) {
	                        // check if we can instantiate and have a success
	                        recorder = new AudioRecord(AudioSource.DEFAULT, rate, channelConfig, audioFormat, bS);
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
			
			flag = false;
			sampleSize = recorder.getSampleRate();
			int channel_config = recorder.getChannelConfiguration();
			int format = recorder.getAudioFormat();
			bufferSize = AudioRecord.getMinBufferSize(sampleSize, channel_config, format);
			// ^ this it the buffersize used later!
			audioBuffer = new byte[bufferSize];
			Log.i("MainActivity", "Started recording at time " + System.currentTimeMillis() + "!");
			
			setTimerOn();
			
			// split
			
		} else {
			Log.e("MainActivity", "No supported audio format found");
		}
    }
    
    public void endRecording()
    {
    	setTimerOff();
    	
    	// here goes nothing...
    	Log.i("MainActivity", "Stopped recording at time " + System.currentTimeMillis() + "!");
    	int newBufferSize = 1;
		while(newBufferSize < bufferSize)
			newBufferSize *= 2;
		double[] micBufferData = new double[newBufferSize];
	    final int bytesPerSample = 2; // As it is 16bit PCM
	    final double amplification = 100.0;
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
	        fftTempArray[i] = new Complex(micBufferData[i], 0);
	    Complex[] fftArray = FFT.fft(fftTempArray);
	    
	    FreqMag[] fm_array = new FreqMag[fftArray.length];
	    for(int i = 0; i < fm_array.length; i++)
	    {
	    	Complex what = fftArray[i];
	    	fm_array[i] = new FreqMag(getFreq(i, sampleSize, fftArray.length), Math.sqrt(what.re()*what.re() + what.im()*what.im()));
	    }
	    
	    FreqMag[] largestMags = new FreqMag[fm_array.length];
	    for(int a = 0; a < largestMags.length; a++)
	    	largestMags[a] = fm_array[a];
	    Arrays.sort(largestMags);

	    double limLowerA = 1200, limUpperA = 1500, minAmpA = 5000;
	    int ACount = 0; // alarm type: police siren
	    
	    double limLowerB = 1000, limUpperB = 1200, minAmpB = 5000;
	    int BCount = 0;	// alarm type: tornado warning
	    
	    int threshold = 2;
	    // String debug_largestMags = " ";
	    for(int a = 0; a < NUM_TOP_MAGNITUDES; a++)
	    {
	    	double theMag = largestMags[a].freq;
	    	if(theMag >= limLowerA && theMag <= limUpperA)
	    		ACount++;
	    	if(theMag >= limLowerB && theMag <= limUpperB)
	    		BCount++;
	    	// debug_largestMags += largestMags[a].toString() + "\n";
	    }
	    // Log.i("EXPECTED GREATEST MAGNITUDES", debug_largestMags);
	    
	    if(ACount >= threshold && !flag)
	    {
	    	flag = true;
	    	sendMessage(1);
	    }
	    else if(BCount >= threshold && !flag)
	    {
	    	flag = true;
	    	sendMessage(2);
	    }
	    
	    setTimerOn();
    }
    
    public void sendMessage(int alertType) {
    	Log.i("sendMessage", "SENDING ALERT TYPE " + alertType);
	    Intent intent = new Intent(this, Alert.class);
	    intent.putExtra("alert-id", alertType);
	    startActivity(intent);
    }
    
    public void setTimerOn() {
    	recorder.startRecording();
		recorder.read(audioBuffer, 0, bufferSize);
		starttime = System.currentTimeMillis(); 
		timer = new Timer(); 
		timer.schedule(new FirstTask(), 0, 50);
	}
	
	public void setTimerOff() {
		timer.cancel();
		timer.purge();
	}
    
    @Override
    protected void onPause() {
        super.onPause();
        // Another activity is taking focus (this activity is about to be "paused").
    }
    @Override
    protected void onStop() {
        super.onStop();
        // The activity is no longer visible (it is now "stopped")
    }    
    @Override
    public void onDestroy() {
        super.onDestroy();  // Always call the superclass
    }
    
    /*
    if(start == 1)
    CODE
    
    start = 0
    SEND MESSAGE
    
    ALERT:
    	send back to main
    	btw start = 1
    	*/
    
    public double getFreq(int index, int fs, int N)
    {
    	return index * fs / (N * 1.0);
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