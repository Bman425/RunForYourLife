package brianrossi.runforyourlife;

import android.app.Activity;
import android.content.Context;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Family on 2/26/2016.
 */

/*
This class gathers the user data from calibrate and calculates heart rate windows from it.
It also takes in the hr from the pulse manager and outputs an average and percent within the range.
 */
public class HRMRecCalc implements PulseListener {
    private Context mContext;   //Used to give the pulse manager a context to register its listener
                                //You can pass the "this" of an activity to it
    private Activity mActivity; // Used to find the Globals class to get global variables
                                // Again, You can pass the "this" of an activity to it
    private PulseManager mPulseMan;  //The instance of the pulse manager
    private ArrayList<Double> currentTrial;//The list of hr from the heart reader from current trial
    private ArrayList<Double> record;  //The recent averaged heart rates, which are averaged again
    private int maxHR;  //Maximum Heart Rate
    private int restHR;  //Resting heart rate
    private int hRReserve;  //Differnce between the two above
    private Globals g;  //The instance of the globals class

    public HeartRateWindow Anaerobic; //This is a heart rate window, specifically anaerobic exercise
    public HeartRateWindow Limits;  //The complete range, min to max
    //Add more here!

    //NOTE: heart rate windows are public, this means you call their .check(double hr) method e.g. mHRMRecCalc.Anaerobic.check(mHRMRecCalc.viewrecord)


    HRMRecCalc(Context context, Activity activity){  //SUPER DUPER IMPORTANT! MUST CALL .start() WHEN READY FOR ACTUAL DATA COLLECTION!!!!  <---
        mContext = context;                         //                                                                      |
        mActivity = activity;                       //                                                                     /|\
        mPulseMan = new PulseManager(this, mContext);  //Makes the pulse manager                                          /_|_\
        g = (Globals)mActivity.getApplication();  //Make the globals class                                                  |
        maxHR = g.getMaxXHR();  //Get the max hr from the globals class                                                     |
        restHR = g.getRestHR(); //Get the resting hr from the globals class                                                 |
        record = new ArrayList<>(Collections.nCopies(15, (double)restHR)); //Fill the record with resting Hr                |
        calcRanges();  //Calculates heart rate ranges
    }
    private void calcRanges(){  //Calculates heart rate ranges
        hRReserve = maxHR - restHR;  //Defines reserver hr
        Anaerobic = new HeartRateWindow((int)(restHR + 0.9 * (hRReserve)), maxHR);  //calculates anaerobic range, 90% to max
        Limits = new HeartRateWindow(restHR, maxHR);  //establishes the limits

    }
    public void recordTrial(){ //Run this to record all the latest heart rates as a data point, do it often and regularly, every few seconds
        if (currentTrial.size() == 0){  //If the trial is empty, mark it as -1, code for fail
            recordUpdate(-1.0);
        }
        else{ //Otherwise
            int n = 0;
            double sum = 0;
            for (Double d:
                    currentTrial) {
                n++;
                sum += d;
            }
            recordUpdate(sum / n);  //Averages the trial
        }
        currentTrial = new ArrayList<Double>();  //Redeclares the trial resetting content and size
    }

    @Override
    public void recievePulse(double BPM, int validity) {  //Is run every time the Heart rate manager talks
        if (validity == 3) {  //Using 3 means only actual readings are used, not guesses or duds
            currentTrial.add(BPM);  //Records it
        }
    }

    public double viewRecord(){ //Gets the last 10 data points
        double sum = 0;
        int n = 0;
        for (int i = 0; i < 11; i++){
            if (record.get(i) != -1){  //Only if the data point is not -1 (fail) is it counted
                n++;  //this way n only increases for good data
                sum += record.get(i);
            }
        }
        if (n!=0){
            return sum/n;  //averages if there is at least one data point
        }
        else{ //If n is 0, then that means all of the data points are failed, and the output -1, this number indicates failure
            return -1.0;
        }
    }
    public double percentInRange(){ //Looks at the last 10 data points and outputs a percent, 0 being resting hr, 100 being max, Use for alitiude of character?
        return 100 * ((viewRecord() - restHR) / hRReserve);
    }
    private void recordUpdate(double next){  //moves over each data points in the record adding the newest one to the begining
        for(int i = record.size()-1; i > 0; i--)
        {
            record.set(i, record.get(i - 1));
        }
        record.set(0,next);
    }
    public void start(){ //Call this when unpausing or when ready to begin recording
        mPulseMan.start();
    }
    public void stop(){ //Call this on pausing to let the pulse manager know to unregister
        mPulseMan.start();
    }
}
