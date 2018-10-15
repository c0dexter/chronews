package pl.michaldobrowolski.chronews.utils;

public final class UtilityHelper {

    public static String removeRedundantCharactersFromText(String text) {
        return text.replaceAll("/[+.*?/]", "");
    }
}
