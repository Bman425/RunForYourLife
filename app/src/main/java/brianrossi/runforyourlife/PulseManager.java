package brianrossi.runforyourlife;


import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

/**
 * Created by Family on 2/23/2016.
 */
public class PulseManager implements SensorEventListener {
    public int validity = 0; //0 means not valid at all, 1 means questionable, 2 means old, 3 means good
    private int currentaccuracy;
    private double heartrate;
    private PulseListener mReciever;
    private int badreads;
    PulseManager(PulseListener reci){
        mReciever = reci;
    }
    public void onSensorChanged(SensorEvent event) {
        if (currentaccuracy > 0){
            validity = 3;
            heartrate = event.values[0];
            badreads = 0;
        }
        else{
            badreads ++;
            validity = 2;
            if (badreads > 10){
                validity = 0;
            }
        }
        mReciever.recievePulse(heartrate, validity);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        currentaccuracy = accuracy;
    }
}
