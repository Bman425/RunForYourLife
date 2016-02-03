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

import com.samsung.android.sdk.healthdata.HealthConnectionErrorResult;
import com.samsung.android.sdk.healthdata.HealthDataStore;
import com.samsung.android.sdk.healthdata.HealthPermissionManager;
import com.samsung.android.sdk.healthdata.HealthPermissionManager.PermissionKey;

import java.util.Set;
import java.util.jar.Manifest;

public class Calibrate extends AppCompatActivity implements HealthDataStore.ConnectionListener {
    private Set<PermissionKey> mKeySet;
    private HealthPermissionManager PermManag;
    private HealthDataStore HealthStore;

    private static final String TAG = "Calibrate";  //Declars Tag for log, probably should be better defined
    private TextView mTextViewHeart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HealthStore = new HealthDataStore(this, this);
        PermManag = new HealthPermissionManager(HealthStore);
        setContentView(R.layout.activity_calibrate);


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
    public void onConnected() {

    }

    @Override
    public void onConnectionFailed(HealthConnectionErrorResult healthConnectionErrorResult) {

    }

    @Override
    public void onDisconnected() {

    }
}
