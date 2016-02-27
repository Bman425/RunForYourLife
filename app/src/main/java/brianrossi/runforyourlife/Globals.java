package brianrossi.runforyourlife;

import android.app.Application;

/**
 * Created by Family on 2/26/2016.
 */
public class Globals extends Application {

    private int MaxHR = 213;  //Fill in default values
    private int RestHR = 75;

    public int getMaxXHR(){
        return MaxHR;
    }
    public int getRestHR(){
        return RestHR;
    }
    public void setMaxHR(int HR){
        MaxHR = HR;
    }
    public void setRestHR(int HR){
        RestHR = HR;
    }


}
