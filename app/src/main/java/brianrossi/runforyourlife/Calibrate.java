package brianrossi.runforyourlife;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;



import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Calibrate extends AppCompatActivity implements PulseListener  {
    PulseManager mPulse;
    TextView textBPM;
    TextView textBPMwarn;
    EditText restHR;
    EditText maxHR;
    EditText age;
    Globals g;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPulse = new PulseManager(this, this);
        setContentView(R.layout.activity_calibrate);
        textBPM = (TextView) findViewById(R.id.tvBPM);
        textBPMwarn = (TextView)findViewById(R.id.tvBPMwarn);
        restHR = (EditText)findViewById(R.id.tfRestingRate);
        maxHR = (EditText)findViewById(R.id.tfMaxRate);
        age = (EditText)findViewById(R.id.tfAge);
        g = (Globals)getApplication();
        restHR.setText(Integer.toString(g.getRestHR()));
        maxHR.setText(Integer.toString(g.getMaxXHR()));
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
    public void btnOnClickSave(View v){
        g.setMaxHR(Integer.valueOf(maxHR.getText().toString()));
        g.setRestHR(Integer.valueOf(restHR.getText().toString()));
    }
    public void btnOnClickCalc(View v){
        maxHR.setText(Integer.toString(220 - Integer.valueOf(age.getText().toString())));
    }

}
