package app.com.thetechnocafe.expensemanager;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by gurleensethi on 10/07/16.
 */
public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
    }
}
