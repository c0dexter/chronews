package pl.michaldobrowolski.chronews.utils;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import pl.michaldobrowolski.chronews.R;
import pl.michaldobrowolski.chronews.api.model.pojo.Article;
import pl.michaldobrowolski.chronews.widget.ChronewsWidgetProvider;

public final class UtilityHelper {

    public static String removeRedundantCharactersFromText(String text) {
        return text.replaceAll("\\[+.*?\\]", "");
    }

    /**
     * This function is changing format date distributed by API to simple format yyyy-MM-dd
     *
     * @param dateToParse - String value with date provided by API
     * @return String value with date in the new format
     */
    public static String displayShortDate(String dateToParse) {
        String inputPattern = detectTimePattern(dateToParse);
        String outputPattern = "d MMM yyyy";
        SimpleDateFormat inputDateFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputDateFormat = new SimpleDateFormat(outputPattern);

        Date date;
        String formattedDate = null;
        try {
            date = inputDateFormat.parse(dateToParse);
            formattedDate = outputDateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return formattedDate;
    }

    /**
     * Displays a time since news have been published
     *
     * @param publishedDate - String value with date provided by API
     * @return String value with message to inform user how long time ago a news has been published
     */
    public static String publishTimeCounter(String publishedDate) {
        String counterResult;

        Calendar cal = Calendar.getInstance();
        String inputPattern = detectTimePattern(publishedDate);
        SimpleDateFormat sdf = new SimpleDateFormat(inputPattern);

        Date publishedDateAt = null;
        try {
            publishedDateAt = sdf.parse(publishedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        cal.setTime(publishedDateAt);

        Calendar calendar = Calendar.getInstance();
        long now = calendar.getTimeInMillis();
        long time = cal.getTimeInMillis();
        long diff = now - time;

        int days = (int) (diff / (1000 * 60 * 60 * 24));
        int hours = (int) ((diff / (1000 * 60 * 60)) % 24);

        if (hours > 24) {
            if (days < 2) {
                counterResult = " \u2022 " + String.valueOf(days) + " day ago";
            } else
                counterResult = " \u2022 " + String.valueOf(days) + " days ago";
        } else {
            if (hours < 2) {
                counterResult = " \u2022 " + String.valueOf(hours) + " hour ago";
            } else {
                counterResult = " \u2022 " + String.valueOf(hours) + " hours ago";
            }
        }

        return String.valueOf(counterResult);
    }

    /**
     * Check which format of date should be used for given data
     *
     * @param dataTimeFromApi is a data source
     * @return String with a proper format
     */
    private static String detectTimePattern(String dataTimeFromApi) {
        String[] formatPatterns = {
                "EEE MMM dd HH:mm:ss Z yyyy", "yyyy-MM-dd'T'HH:mm:ss'Z'", "yyyy-MM-dd'T'HH:mm:ss",
                "yyyy-MM-dd'T'HH:mm:ssZ", "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                "yyyy-MM-dd'T'HH:mm:ss.SSSZ", "yyyy-MM-dd HH:mm:ss", "MM/dd/yyyy HH:mm:ss",
                "MM/dd/yyyy'T'HH:mm:ss.SSS'Z'", "MM/dd/yyyy'T'HH:mm:ss.SSS",
                "MM/dd/yyyy'T'HH:mm:ss.SSSZ", "MM/dd/yyyy'T'HH:mm:ss", "MM/dd/yyyy'T'HH:mm:ssZ",
                "yyyy:MM:dd HH:mm:ss", "yyyyMMdd", "yyyy-MM-dd", "yyyy-MM-dd HH:mm",
                "yyyy-MM-dd HH:mmZ", "yyyy-MM-dd HH:mm:ss.SSSZ"};

        if (dataTimeFromApi != null) {
            for (String pattern : formatPatterns) {
                SimpleDateFormat sdf = new SimpleDateFormat(pattern);
                try {
                    sdf.parse(dataTimeFromApi);
                    return pattern;

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * Opens article based onf URL provided in an Article object
     *
     * @param context- context of application
     * @param article  - object article
     */
    public static void openArticleInBrowser(Context context, Article article) {
        if (isOnline(context)) {
            String url = article.getUrl();
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            context.startActivity(i);
        } else {
            Toast.makeText(context, R.string.no_internet_connection_message, Toast.LENGTH_SHORT).show();
        }
    }


    public static String makeUpperString(String stringToChange) {
        return stringToChange.substring(0, 1).toUpperCase() + stringToChange.substring(1);
    }

    /**
     * Updates widget after data changed in the DB. Call this function after making changes in DB.
     *
     * @param context - context of application, get and pass the correct context
     */
    public static void updateWidget(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName thisWidget = new ComponentName(context, ChronewsWidgetProvider.class);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widgetCollectionList);
    }

    /**
     * Check if an Internet connection exist
     *
     * @param context of called activity
     * @return true if internet connection exist
     */
    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = null;
        if (cm != null) {
            netInfo = cm.getActiveNetworkInfo();
        }
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

}
