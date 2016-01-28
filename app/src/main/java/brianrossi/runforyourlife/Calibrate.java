package brianrossi.runforyourlife;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class Calibrate extends AppCompatActivity implements SensorEventListener {

    SensorManager hrmManager ;
    Sensor hrmSensor;
    private static final String TAG = "MainActivity";
    private TextView mTextViewHeart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hrmManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        hrmSensor = hrmManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        setContentView(R.layout.activity_calibrate);
        hrmManager.registerListener((SensorEventListener) this, hrmSensor, SensorManager.SENSOR_DELAY_NORMAL);
        //mTextViewHeart = (TextView) findViewById(R.id.heart);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_calibrate, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_HEART_RATE) {
            String msg = "" + (int)event.values[0];
            mTextViewHeart.setText(msg);
            Log.d(TAG, msg);
            System.out.println(msg);
        }
        else
            Log.d(TAG, "Unknown sensor type");
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d(TAG, "onAccuracyChanged - accuracy: " + accuracy);
    }
}
