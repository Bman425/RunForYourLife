package brianrossi.runforyourlife;

import android.preference.PreferenceActivity;
import android.os.Bundle;

/**
 * Created by Brian on 1/23/2016.
 */
public class Preferences extends PreferenceActivity {

@Override
public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    //MainMenu theme = new MainMenu();
    //theme.makeTheme();
    addPreferencesFromResource(R.xml.prefs);
}



}
