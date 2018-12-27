package pl.michaldobrowolski.chronews.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import pl.michaldobrowolski.chronews.R;
import pl.michaldobrowolski.chronews.utils.Analytics;
import pl.michaldobrowolski.chronews.utils.UtilityHelper;

public class GoogleSignInActivity extends AppCompatActivity {
    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 2;

    private FirebaseAuth firebaseAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private SignInButton signInButton;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private ProgressBar loginProgressBar;
    private ImageView noInternetConnection;


    @Override
    public void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(firebaseAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (firebaseAuthListener != null) {
            firebaseAuth.removeAuthStateListener(firebaseAuthListener);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (firebaseAuthListener != null) {
            firebaseAuth.removeAuthStateListener(firebaseAuthListener);
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        firebaseAuth.addAuthStateListener(firebaseAuthListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        makeStatusBarTransparent();

        signInButton = findViewById(R.id.login_activity_google_login_btn);
        loginProgressBar = findViewById(R.id.login_progress_bar);
        noInternetConnection = findViewById(R.id.no_internet_dino_img);
        firebaseAuth = FirebaseAuth.getInstance();

        signInButton.setOnClickListener(v -> {
            if (UtilityHelper.isOnline(getApplicationContext())) {
                noInternetConnection.setVisibility(View.GONE);
                loginProgressBar.setVisibility(View.VISIBLE);
                signIn();
            } else {
                noInternetConnection.setVisibility(View.VISIBLE);
                Toast.makeText(GoogleSignInActivity.this, R.string.no_internet_connection_message, Toast.LENGTH_SHORT).show();
            }

        });

        if (firebaseAuth.getCurrentUser() != null) {
            openMainScreen();
        } else {
            firebaseAuthListener = firebaseAuth -> {
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                if (currentUser != null) {
                    FirebaseAnalytics analytics = Analytics.get(GoogleSignInActivity.this);
                    analytics.setUserId(currentUser.getUid());
                    analytics.logEvent("user_signed_in", null);

                    openMainScreen();
                }
            };
            this.firebaseAuth.addAuthStateListener(firebaseAuthListener);

            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();

            // Build a GoogleSignInClient with the options specified by gso.
            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        }
    }

    private void openMainScreen() {
        startActivity(new Intent(GoogleSignInActivity.this, MainActivity.class));
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    firebaseAuthWithGoogle(account);
                }
                loginProgressBar.setVisibility(View.GONE);
            } catch (ApiException e) {
                loginProgressBar.setVisibility(View.GONE);
                // Google Sign In failed, update UI appropriately
                Toast.makeText(this, R.string.google_sign_in_fail_message, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(GoogleSignInActivity.this, R.string.authentication_fail_message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void makeStatusBarTransparent() {
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(this, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    private void setWindowFlag(Activity activity, boolean on) {
        Window window = activity.getWindow();
        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        if (on) {
            windowAttributes.flags |= WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        } else {
            windowAttributes.flags &= ~WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        }
        window.setAttributes(windowAttributes);
    }
}
