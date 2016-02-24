package brianrossi.runforyourlife;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;



import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Calibrate extends AppCompatActivity implements PulseListener  {
    PulseManager mPulse;
    TextView textBPM;
    TextView textBPMwarn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPulse = new PulseManager(this, this);
        setContentView(R.layout.activity_calibrate);
        textBPM = (TextView) findViewById(R.id.BPM);
        textBPMwarn = (TextView)findViewById(R.id.BPMwarn);
        mPulse.start();
    }


    @Override
    public void recievePulse(double BPM, int validity) {
        if (validity == 0){
            textBPM.setText("???");
            textBPMwarn.setText("Not reading anything");
        }
        else if (validity == 2){
            textBPM.setText(((int)BPM) + " BPM");
            textBPMwarn.setText("Place finger on sensor better");
        }
        else if (validity == 3){
            textBPM.setText(((int)BPM) + " BPM");
            textBPMwarn.setText("");
        }
    }
    public void onPause(){
        super.onPause();
        mPulse.stop();
    }
    public void onResume(){
        super.onResume();
        mPulse.start();
    }
}
