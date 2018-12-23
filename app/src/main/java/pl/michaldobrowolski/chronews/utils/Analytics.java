package pl.michaldobrowolski.chronews.utils;

import android.content.Context;

import com.google.firebase.analytics.FirebaseAnalytics;

public class Analytics {
    private static volatile FirebaseAnalytics firebaseAnalytics;

    /**
     * This method checks if we have only one instance of Google Analytics.
     * @param context of place where you call the method
     * @return instance of firebase analytics
     */
    public static FirebaseAnalytics get(Context context) {
        if (firebaseAnalytics == null) {
            synchronized (Analytics.class) {
                if (firebaseAnalytics == null) {
                    firebaseAnalytics = FirebaseAnalytics.getInstance(context);
                }
            }
        }
        return firebaseAnalytics;
    }
    private Analytics() {
    }
}
