package brianrossi.runforyourlife;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.GregorianCalendar;

/**
 * Created by Family on 2/23/2016.
 */
public class PulseManager implements SensorEventListener {
    public int validity = 0; //0 means not valid at all, 1 means questionable, 2 means old, 3 means good
    private int currentaccuracy;
    private double heartrate;
    private PulseListener mReciever;
    private int badreads;
    private SensorManager mSensorManager;
    private Sensor mHRMSensor;
    private Context mContext;
    private long lastRead;
    private final int MAXDIFF = 1000;
    PulseManager(PulseListener reci, Context context){
        mReciever = reci;
        mContext = context;
        mSensorManager = (SensorManager) mContext.getSystemService(mContext.SENSOR_SERVICE);
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE) != null){
            mHRMSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        }

    }
    public void start(){
        badreads = 0;
        mSensorManager.registerListener(this,mHRMSensor, SensorManager.SENSOR_DELAY_GAME);
    }
    public void stop(){
        mSensorManager.unregisterListener(this,mHRMSensor);
    }
    public void onSensorChanged(SensorEvent event) {
        System.out.println("Heart Rate Event ---> " + event.values[0]);
        if (currentaccuracy > 0){
            validity = 3;
            heartrate = event.values[0];
            badreads = 0;
            lastRead = System.currentTimeMillis();
        }
        else{
            badreads ++;
            validity = 2;
            if (badreads > 10 || System.currentTimeMillis() - lastRead > MAXDIFF){
                validity = 0;
            }
        }
        mReciever.recievePulse(heartrate, validity);
        System.out.println(lastRead);
        System.out.println(System.currentTimeMillis());
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        currentaccuracy = accuracy;
    }
}
