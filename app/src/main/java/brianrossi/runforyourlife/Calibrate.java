package brianrossi.runforyourlife;

import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.jar.Manifest;

public class Calibrate extends AppCompatActivity implements SensorEventListener, ActivityCompat.OnRequestPermissionsResultCallback {

    SensorManager hrmManager ;  //Declares Sensor manager field
    Sensor hrmSensor;  // Declares Sensor field
    private static final String TAG = "MainActivity";  //Declars Tag for log, probably should be better defined
    private TextView mTextViewHeart;
    private static final int PERM_REQUEST_HRM = 1;  //ID for permision request call, not used
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestPermissions(new String[]{android.Manifest.permission.BODY_SENSORS}, PERM_REQUEST_HRM);  //Requires Android 6
        hrmManager = (SensorManager)getSystemService(SENSOR_SERVICE);  //Sets the manager
        //hrmSensor = hrmManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);  //sets the sensor
        hrmSensor = hrmManager.getDefaultSensor(65562);  //Attempt to get sensor
        setContentView(R.layout.activity_calibrate);
        hrmManager.registerListener((SensorEventListener) this, hrmSensor, SensorManager.SENSOR_DELAY_NORMAL);  //registers as listener to sensor

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
    public void onSensorChanged(SensorEvent event) {  //Logs sensor updates
        if (event.sensor.getType() == Sensor.TYPE_HEART_RATE) {
            String msg = "" + (int)event.values[0];
            mTextViewHeart.setText(msg);
            Log.d(TAG, msg);
        }
        else
            Log.d(TAG, "Unknown sensor type");
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {  //Logs changes in sensor accuaracy
        Log.d(TAG, "onAccuracyChanged - accuracy: " + accuracy);
    }
    /*public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        if (requestCode == PERM_REQUEST_HRM && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            hrmManager.registerListener((SensorEventListener) this, hrmSensor, SensorManager.SENSOR_DELAY_NORMAL);
            //mTextViewHeart = (TextView) findViewById(R.id.heart);
        }
    }*/
}
