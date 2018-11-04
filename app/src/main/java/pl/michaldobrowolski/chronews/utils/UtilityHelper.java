package pl.michaldobrowolski.chronews.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
        String inputPattern = "yyyy-MM-dd'T'HH:mm:ss'Z'";
        String outputPattern = "d MMM yyyy";
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

    /**
     * This function is displaying a time since news have been published
     *
     * @param publishedDate - String value with date provided by API
     * @return String value with message to inform user how long time ago a news has been published
     */
    public static String publishTimeCounter(String publishedDate) {
        String counterResult;

        Calendar cal = Calendar.getInstance();
        String inputPattern = "yyyy-MM-dd'T'HH:mm:ss'Z'";
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

        int days = (int) (diff / (1000*60*60*24));
        int hours   = (int) ((diff / (1000*60*60)) % 24);

        if (hours > 24){
            if(days < 2 ){
                counterResult = " \u2022 " + String.valueOf(days) + " day ago";
            } else
            counterResult = " \u2022 " + String.valueOf(days) + " days ago";

        } else {
            if(hours < 2){
                counterResult = " \u2022 " + String.valueOf(hours) + " hour ago";
            } else{
                counterResult = " \u2022 " + String.valueOf(hours) + " hours ago";
            }
        }

        return String.valueOf(counterResult);
    }



    /**
     * Country codes for query parameter
     */
    public enum CountryCodes {
        ARGENTINA ("ar"),
        AUSTRALIA ("au"),
        AUSTRIA ("at"),
        BELGIUM ("be"),
        BRAZIL ("br"),
        BULGARIA ("bg"),
        CANADA ("ca"),
        CHINA ("cn"),
        COLOMBIA ("co"),
        CUBA ("cu"),
        CZECH_REPUBLIC ("cz"),
        EGYPT ("eg"),
        FRANCE ("fr"),
        GERMANY ("de"),
        GREECE ("gr"),
        HONG_KONG ("hk"),
        HUNGARY ("hu"),
        INDIA ("in"),
        INDONESIA ("id"),
        IRELAND ("ie"),
        ISRAEL ("il"),
        ITALY ("it"),
        JAPAN ("jp"),
        KOREA ("kr"),
        LATVIA ("lv"),
        LITHUANIA ("lt"),
        MALAYSIA ("my"),
        MEXICO ("mx"),
        MOROCCO ("ma"),
        NETHERLANDS ("nl"),
        NEW_ZEALAND ("nz"),
        NIGERIA ("ng"),
        NORWAY ("no"),
        PHILIPPINES ("ph"),
        POLAND ("pl"),
        PORTUGAL ("pt"),
        ROMANIA ("ro"),
        RUSSIAN_FEDERATION ("ru"),
        SAUDI_ARABIA ("sa"),
        SERBIA ("rs"),
        SINGAPORE ("sg"),
        SLOVAKIA ("sk"),
        SLOVENIA ("si"),
        SOUTH_AFRICA ("za"),
        SWEDEN ("se"),
        SWITZERLAND ("ch"),
        TAIWAN ("tw"),
        THAILAND ("th"),
        TURKEY ("tr"),
        UKRAINE ("ua"),
        UNITED_ARAB_EMIRATES ("ae"),
        UNITED_KINGDOM ("gb"),
        UNITED_STATES ("us"),
        VENEZUELA ("ve");

        private String countryCode;

        CountryCodes(String country) {
            this.countryCode = country;
        }

        public String getCountryCode() {
            return countryCode;
        }
    }

    /**
     * Category codes for query parameter
     */
    public enum Category{
        BUSINESS ("business"),
        ENTERTAINMENT ("entertainment"),
        GENERAL ("general"),
        HEALTH ("health"),
        SCIENCE ("science"),
        SPORTS ("sports"),
        TECHNOLOGY("technology");

        private String categoryCode;

        Category(String category) {
            this.categoryCode = category;
        }

        public String getCategory() {
            return categoryCode;
        }
    }

    /**
     * Language codes for query parameter
     */
    public enum Language{
        ARABIC ("ar"),
        GERMAN ("de"),
        ENGLISH ("en"),
        FRENCH ("fr"),
        HEBREW ("he"),
        ITALIAN ("it"),
        DUTCH_FLEMISH ("nl"),
        NORWEGIAN ("no"),
        PORTUGUESE ("pt"),
        RUSSIAN ("ru"),
        NORTHERN_SAMI ("se"),
        CHINESE ("zh");

        private String languageCode;

        Language(String language) {
            this.languageCode = language;
        }

        public String getLanguageCode() {
            return languageCode;
        }
    }

    /**
     * Sorting options for query parameter
     * relevancy - articles more closely related to [q] come first.
     * popularity - articles from popular sources and publishers come first.
     * publishedAt - newest articles come first.
     */
    public enum SortOption{
        RELEVANCY ("relevancy"),
        POPULARITY ("popularity"),
        PUBLISHED_AT ("publishedAt");

        private String sortingOption;

        SortOption(String sortingOption) {
            this.sortingOption = sortingOption;
        }

        public String getSortingOption() {
            return sortingOption;
        }
    }







}
