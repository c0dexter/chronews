package pl.michaldobrowolski.chronews.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import pl.michaldobrowolski.chronews.R;
import pl.michaldobrowolski.chronews.api.service.ApiClient;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = MainActivity.class.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);
        loadFragment(new HomeFragment());
        Log.d(TAG, "A default HomeFragment has been loaded successfully!");
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();

            return true;
        }

        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;

        switch (item.getItemId()) {
            case R.id.navigation_home:
                fragment = new HomeFragment();
                Log.d(TAG, "Bottom Menu clicked: HomeFragment created!");
                break;
            case R.id.navigation_categories:
                fragment = new CategoriesFragment();
                Log.d(TAG, "Bottom Menu clicked: CategoriesFragment created!");
                break;

            case R.id.navigation_favourites:
                fragment = new FavouriteFragment();
                Log.d(TAG, "Bottom Menu clicked: FavouritesFragment created!");
                break;
        }

        return loadFragment(fragment);
    }
}
