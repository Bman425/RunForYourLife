package brianrossi.runforyourlife;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by Family on 2/26/2016.
 */
public class HRMRecCalc implements PulseListener {
    private Context mContext;
    private PulseManager mPulseMan;
    private int trial = 0;
    private ArrayList<Double> currentTrial;

    HRMRecCalc(Context context){
        mContext = context;
        mPulseMan = new PulseManager(this, mContext);
        mPulseMan.start();
    }
    public void startTrial(){
        currentTrial = new ArrayList<>();
    }
    @Override
    public void recievePulse(double BPM, int validity) {
        if (validity == 3){
            currentTrial.add(BPM);
        }

    }
    public void start(){
        mPulseMan.start();
    }
    public void stop(){
        mPulseMan.start();
    }
}
