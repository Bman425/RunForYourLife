package brianrossi.runforyourlife;

import android.database.Cursor;
import android.util.Log;

import com.samsung.android.sdk.healthdata.HealthDataStore;

import com.samsung.android.sdk.healthdata.HealthConstants;
import com.samsung.android.sdk.healthdata.HealthDataObserver;
import com.samsung.android.sdk.healthdata.HealthDataResolver;
import com.samsung.android.sdk.healthdata.HealthDataResolver.Filter;
import com.samsung.android.sdk.healthdata.HealthDataResolver.ReadRequest;
import com.samsung.android.sdk.healthdata.HealthDataResolver.ReadResult;
import com.samsung.android.sdk.healthdata.HealthResultHolder;

import java.util.Calendar;

/**
 * Created by Family on 2/2/2016.
 */
public class PulseReader {
    private final HealthDataStore HealthStore;
    private final PulseReaderListener listenerActivity;
    public PulseReader(HealthDataStore store, PulseReaderListener listener){
        HealthStore = store;
        listenerActivity = listener;
    }
    public void start(){
        HealthDataObserver.addObserver(HealthStore, HealthConstants.HeartRate.HEALTH_DATA_TYPE, Observer);
    }
    private void readHeartRate() {
        HealthDataResolver resolver = new HealthDataResolver(HealthStore, null);

        long startTime = getStartTimeOfToday();
        long endTime = System.currentTimeMillis();
        Filter filter = Filter.and(Filter.greaterThanEquals(HealthConstants.HeartRate.START_TIME, startTime),
                Filter.lessThanEquals(HealthConstants.HeartRate.START_TIME, endTime));

        HealthDataResolver.ReadRequest request = new ReadRequest.Builder()
                .setDataType(HealthConstants.HeartRate.HEALTH_DATA_TYPE)
                .setProperties(new String[] {HealthConstants.HeartRate.HEART_RATE})
                .setFilter(filter)
                .build();

        try {
            resolver.read(request).setResultListener(mListener);
        } catch (Exception e) {
            Log.e(MainMenu.APP_TAG, e.getClass().getName() + " - " + e.getMessage());
            Log.e(MainMenu.APP_TAG, "Getting heart rate fails.");
        }
    }

    private long getStartTimeOfToday() {
        Calendar today = Calendar.getInstance();

        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        return today.getTimeInMillis();
    }

    private final HealthResultHolder.ResultListener<ReadResult> mListener = new HealthResultHolder.ResultListener<ReadResult>() {
        @Override
        public void onResult(ReadResult result) {
            int count = 0;
            Cursor c = null;

            try {
                c = result.getResultCursor();
                if (c != null) {
                    while (c.moveToNext()) {
                        count = c.getInt(c.getColumnIndex(HealthConstants.HeartRate.HEART_RATE));
                    }
                }
            } finally {
                if (c != null) {
                    c.close();
                }
            }
            listenerActivity.drawHeartRate(String.valueOf(count));
        }
    };
    private final HealthDataObserver Observer = new HealthDataObserver(null) {

        @Override
        public void onChange(String dataTypeName) {
            Log.d(MainMenu.APP_TAG, "Observer receives a data changed event");
            readHeartRate();
        }
    };
}
