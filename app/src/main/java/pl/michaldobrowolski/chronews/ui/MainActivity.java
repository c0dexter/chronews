package pl.michaldobrowolski.chronews.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import pl.michaldobrowolski.chronews.R;

public class MainActivity
        extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener,
        FavouriteFragment.OnRemoveArticleListener {

    private static final String TAG = MainActivity.class.getClass().getSimpleName();
    private Fragment fragment = null;
    private Toolbar mainActivityToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainActivityToolbar = findViewById(R.id.main_activity_toolbar);
        setSupportActionBar(mainActivityToolbar);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);

        // Remember last fragment in savedInstanceState
        if (savedInstanceState == null) {
            fragment = new HomeFragment();
            loadFragment(fragment);
            mainActivityToolbar.setNavigationIcon(null);
        } else {
            loadFragment(fragment);
            mainActivityToolbar.setNavigationIcon(null);
        }
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
        switch (item.getItemId()) {
            case R.id.navigation_home:
                fragment = new HomeFragment();
                mainActivityToolbar.setNavigationIcon(null);
                break;
            case R.id.navigation_categories:
                fragment = new CategoriesBoardFragment();
                mainActivityToolbar.setNavigationIcon(null);
                break;

            case R.id.navigation_favourites:
                fragment = new FavouriteFragment();
                mainActivityToolbar.setNavigationIcon(null);
                break;
        }
        return loadFragment(fragment);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState, "myFragment", fragment);
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        fragment = getSupportFragmentManager().getFragment(savedInstanceState, "myFragment");
    }


    @Override
    public void onRemoveArticleButtonClickedCallback() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new FavouriteFragment())
                .commit();
    }
}
