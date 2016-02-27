package brianrossi.runforyourlife;

/**
 * Created by Family on 2/26/2016.
 */

//I know this class seems too simple, but I figured it would be bad to be doing
// a whole bunch of comparisons the RecCalc
public class HeartRateWindow {
    private int maxHR;
    private int minHR;
    HeartRateWindow(int min, int max){
        maxHR = max;
        minHR = min;
    }
    public int check(double HR){  //Put in the hr your checking
        if (HR > maxHR){return 1;}  //1 if it is over
        else if (HR < minHR){return -1;}  //-1 if it is under
        else {return 0;} //0 if is good      Boolean doesnt do under/over/good, just bad/good so I went with this
    }
}
