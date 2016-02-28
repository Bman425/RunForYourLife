package brianrossi.runforyourlife;

import java.nio.channels.UnsupportedAddressTypeException;

/**
 * Created by Family on 2/26/2016.
 */

//I know this class seems too simple, but I figured it would be bad to be doing
// a whole bunch of comparisons the RecCalc
public class HeartRateWindow {
    private int maxHR; //Maximum Heart Rate for this range
    private int minHR; //Minumum Heart Rate for this range
    private int reserveHR;  //Difference between resting heart rate and max trainging heart rate (not maxHR)
    private int restHR;  //Resting Heart rate
    HeartRateWindow(int min, int max, int hRResrve, int hRRest){
        reserveHR = hRResrve;
        maxHR = max;
        minHR = min;
        restHR = hRRest;
    }
    public int getMax(){
        return maxHR;
    }
    public int getMin(){
        return minHR;
    }
    public double getPercentMax(){
        return (maxHR - (restHR + 10)) / (reserveHR + .1);
    }
    public double getPercentMin(){
        return (minHR - (restHR - 10)) / (reserveHR + .1);
    }
}
