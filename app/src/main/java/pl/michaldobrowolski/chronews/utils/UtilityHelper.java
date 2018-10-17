package pl.michaldobrowolski.chronews.utils;

import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import pl.michaldobrowolski.chronews.R;
import pl.michaldobrowolski.chronews.api.model.pojo.Article;
import pl.michaldobrowolski.chronews.ui.adapters.ArticleListAdapter;

public final class UtilityHelper {

    public static String removeRedundantCharactersFromText(String text) {
        return text.replaceAll("/[+.*?/]", "");
    }

    public static String displayShortDate(String dateToParse) {
        String inputPattern = "yyyy-MM-dd'T'HH:mm:ss'Z'";
        String outputPattern = "yyyy-MM-dd";
        SimpleDateFormat inputDateFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputDateFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String formattedDate = null;

        try {
            date = inputDateFormat.parse(dateToParse);
            formattedDate = outputDateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return formattedDate;
    }

}
