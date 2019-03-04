package pl.michaldobrowolski.chronews.ui.SharedPreferences;

import android.content.Context;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import pl.michaldobrowolski.chronews.R;

public class SettingsActivity extends AppCompatPreferenceActivity {
    private static final String TAG = SettingsActivity.class.getSimpleName();

    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String stringValue = newValue.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else if (preference instanceof SwitchPreference) {
                sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                        PreferenceManager
                                .getDefaultSharedPreferences(preference.getContext())
                                .getBoolean(preference.getKey(), false));

            } else if (preference instanceof CheckBoxPreference) {
                sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                        PreferenceManager
                                .getDefaultSharedPreferences(preference.getContext())
                                .getBoolean(preference.getKey(), true));

            } else if (preference instanceof EditTextPreference) {
                EditTextPreference editTextPreference = (EditTextPreference) preference;
                String oldSearchPhrase = editTextPreference.getText();
                String newSearchPhrase = String.valueOf(editTextPreference.getEditText().getText());

                if (!newSearchPhrase.equals("")) {
                    preference.setSummary(newSearchPhrase.trim());
                } else {
                    preference.setSummary(oldSearchPhrase.trim());
                }
            }
            return true;
        }
    };

    private static void bindPreferenceSummaryToValue(Preference preference) {
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
        // load fragment of settings
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MainPreferenceFragment()).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            this.finish();

        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Setup a Toolbar on a SharedPref screen
     */
    private void setupActionBar() {
        ViewGroup rootView = findViewById(R.id.action_bar_root);
        View view = getLayoutInflater().inflate(R.layout.toolbar, rootView, false);
        rootView.addView(view, 0);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Up button
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.settings_title);
        }
    }

    public static class MainPreferenceFragment extends PreferenceFragment {
        Context context;
        Preference switchLanguagePreference;
        Preference switchTopHeadlinesOnHomeScreen;
        Preference switchCategoryForTopHeadlines;
        Preference switchSearchPhraseForTopHeadlines;
        Preference countryPreference;
        Preference languagePreference;
        Preference sortingTypePreference;
        Preference topHeadlinesCategoryPreference;
        Preference defaultSearchPhrasePreference;

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            context = getActivity();
            addPreferencesFromResource(R.xml.pref_main);

            switchLanguagePreference = findPreference(getString(R.string.key_switch_specific_news_language));
            switchTopHeadlinesOnHomeScreen = findPreference(getString(R.string.key_switch_top_headlines_home_screen));
            switchCategoryForTopHeadlines = findPreference(getString(R.string.key_switch_category_top_headlines));
            switchSearchPhraseForTopHeadlines = findPreference(getString(R.string.key_switch_search_phrase_top_headlines));

            countryPreference = findPreference(getString(R.string.key_country_code_top_headlines));
            languagePreference = findPreference(getString(R.string.key_language_code));
            sortingTypePreference = findPreference(getString(R.string.key_sorting_code));
            topHeadlinesCategoryPreference = findPreference(getString(R.string.key_top_headlines_category));
            defaultSearchPhrasePreference = findPreference(getString(R.string.key_default_phrase_top_headlines));

            // country ListPreference change listener
            bindPreferenceSummaryToValue(findPreference(getString(R.string.key_country_code_top_headlines)));
            // language ListPreference change listener
            bindPreferenceSummaryToValue(findPreference(getString(R.string.key_language_code)));
            // sorting type ListPreference change listener
            bindPreferenceSummaryToValue(findPreference(getString(R.string.key_sorting_code)));
            // favourite category ListPreference change listener
            bindPreferenceSummaryToValue(findPreference(getString(R.string.key_top_headlines_category)));
            // default search phrase ListPreference change listener
            bindPreferenceSummaryToValue(findPreference(getString(R.string.key_default_phrase_top_headlines)));

        }
    }
}