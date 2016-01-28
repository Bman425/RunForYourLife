package brianrossi.runforyourlife;

import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.SensorManager;
import android.widget.TextView;

public class MainMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.makeTheme();
        setContentView(R.layout.activity_main_menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_menu, menu);
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
    public void btnOnClickCali(View v){
        Intent cali = new Intent(this, Calibrate.class);
        startActivity(cali);
    }

    public void btnOnClickSet(View v){
        Intent settings = new Intent(this, Preferences.class);
        startActivity(settings);
    }

    public void btnOnClickPlay(View v){
        //bring to game screen
    }

    public void makeTheme() {
        setTheme(android.R.style.Theme_Material_Light);
        getWindow().getDecorView().setBackgroundColor(Color.LTGRAY);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        boolean first = settings.getBoolean("first", false); //key for preference followed by default value for preference
        if (first) {
            setTheme(android.R.style.Theme_Material);
            getWindow().getDecorView().setBackgroundColor(Color.DKGRAY);
        }
    }


}
