package brianrossi.runforyourlife;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.samsung.android.sdk.healthdata.HealthConnectionErrorResult;
import com.samsung.android.sdk.healthdata.HealthConstants;
import com.samsung.android.sdk.healthdata.HealthDataService;
import com.samsung.android.sdk.healthdata.HealthDataStore;
import com.samsung.android.sdk.healthdata.HealthPermissionManager;
import com.samsung.android.sdk.healthdata.HealthPermissionManager.PermissionKey;
import com.samsung.android.sdk.healthdata.HealthResultHolder;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Calibrate extends AppCompatActivity implements PulseReaderListener {
    private static AppCompatActivity instanceAsActivity = null;
    private static PulseReaderListener instanceAsPulseListener = null;
    private Set<PermissionKey> keySet;
    private HealthDataStore healthStore;
    private HealthConnectionErrorResult healthConnError;
    private TextView mTextViewHeart;
    private PulseReader pulseFeed;
    private final int MENU_ITEM_PERMISSION_SETTING = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        healthStore = new HealthDataStore(this, mConnectionListener);
        setContentView(R.layout.activity_calibrate);

        instanceAsActivity = this;
        instanceAsPulseListener = this;
        keySet = new HashSet<PermissionKey>();
        keySet.add(new PermissionKey(HealthConstants.HeartRate.HEALTH_DATA_TYPE, HealthPermissionManager.PermissionType.READ));
        HealthDataService healthDataService = new HealthDataService();
        try {
            healthDataService.initialize(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create a HealthDataStore instance and set its listener
        healthStore = new HealthDataStore(this, mConnectionListener);

        // Request the connection to the health data store

        healthStore.connectService();



    }
    private void showPermissionAlarmDialog() {
        if (isFinishing()) {
            return;
        }

        AlertDialog.Builder alert = new AlertDialog.Builder(Calibrate.this);
        alert.setTitle("Notice");
        alert.setMessage("All permissions should be acquired");
        alert.setPositiveButton("OK", null);
        alert.show();
    }

    private void showConnectionFailureDialog(HealthConnectionErrorResult error) {

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        healthConnError = error;
        String message = "Connection with S Health is not available";

        if (healthConnError.hasResolution()) {
            switch(error.getErrorCode()) {
                case HealthConnectionErrorResult.PLATFORM_NOT_INSTALLED:
                    message = "Please install S Health";
                    break;
                case HealthConnectionErrorResult.OLD_VERSION_PLATFORM:
                    message = "Please upgrade S Health";
                    break;
                case HealthConnectionErrorResult.PLATFORM_DISABLED:
                    message = "Please enable S Health";
                    break;
                case HealthConnectionErrorResult.USER_AGREEMENT_NEEDED:
                    message = "Please agree with S Health policy";
                    break;
                default:
                    message = "Please make S Health available";
                    break;
            }
        }

        alert.setMessage(message);

        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if (healthConnError.hasResolution()) {
                    healthConnError.resolve(instanceAsActivity);
                }
            }
        });

        if (error.hasResolution()) {
            alert.setNegativeButton("Cancel", null);
        }

        alert.show();
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
        /*if(item.getItemId() == (MENU_ITEM_PERMISSION_SETTING)) {
            HealthPermissionManager pmsManager = new HealthPermissionManager(healthStore);
            try {
                // Show user permission UI for allowing user to change options
                pmsManager.requestPermissions(keySet, Calibrate.this).setResultListener(mPermissionListener);
            } catch (Exception e) {
                Log.e(MainMenu.APP_TAG, e.getClass().getName() + " - " + e.getMessage());
                Log.e(MainMenu.APP_TAG, "Permission setting fails.");
            }
        }*/

        return super.onOptionsItemSelected(item);
    }
    public void drawHeartRate(String count){
        System.out.println(count);
    }
    private final HealthDataStore.ConnectionListener mConnectionListener = new HealthDataStore.ConnectionListener() {

        @Override
        public void onConnected() {
            Log.d(MainMenu.APP_TAG, "Health data service is connected.");
            HealthPermissionManager pmsManager = new HealthPermissionManager(healthStore);
            pulseFeed = new PulseReader(healthStore, instanceAsPulseListener);

            try {

                Map<PermissionKey, Boolean> resultMap = pmsManager.isPermissionAcquired(keySet);

                if (resultMap.containsValue(Boolean.FALSE)) {

                    pmsManager.requestPermissions(keySet, Calibrate.this).setResultListener(mPermissionListener);

                } else {

                    pulseFeed.start();

                }
            } catch (Exception e) {
                Log.e(MainMenu.APP_TAG, e.getClass().getName() + " - " + e.getMessage());
                Log.e(MainMenu.APP_TAG, "Permission setting fails.");
            }

        }

        @Override
        public void onConnectionFailed(HealthConnectionErrorResult error) {
            Log.d(MainMenu.APP_TAG, "Health data service is not available.");
            showConnectionFailureDialog(error);
        }

        @Override
        public void onDisconnected() {
            Log.d(MainMenu.APP_TAG, "Health data service is disconnected.");
        }
    };
    private final HealthResultHolder.ResultListener<HealthPermissionManager.PermissionResult> mPermissionListener =
            new HealthResultHolder.ResultListener<HealthPermissionManager.PermissionResult>() {

                @Override
                public void onResult(HealthPermissionManager.PermissionResult result) {
                    Log.d(MainMenu.APP_TAG, "Permission callback is received.");
                    Map<PermissionKey, Boolean> resultMap = result.getResultMap();

                    if (resultMap.containsValue(Boolean.FALSE)) {
                        drawHeartRate("");
                        showPermissionAlarmDialog();
                    } else {

                        pulseFeed.start();
                    }
                }
            };

}
