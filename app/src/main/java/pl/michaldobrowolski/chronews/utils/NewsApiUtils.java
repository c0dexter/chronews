package pl.michaldobrowolski.chronews.utils;

public final class NewsApiUtils {
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

    /**
     * A category object to populate RecyclerView on the Category screen
     */
    public static class CategoryObject{
        private String categoryName;
        private String categoryImageUrl;

        public String getCategoryName() {
            return categoryName;
        }

        public void setCategoryName(String categoryName) {
            this.categoryName = categoryName;
        }

        public String getCategoryImageUrl() {
            return categoryImageUrl;
        }

        public void setCategoryImageUrl(String categoryImageUrl) {
            this.categoryImageUrl = categoryImageUrl;
        }

        public CategoryObject(String categoryName, String categoryImageUrl) {
            this.categoryName = categoryName;
            this.categoryImageUrl = categoryImageUrl;
        }
    }

}
