package pl.michaldobrowolski.chronews.utils;

public class Category {
    private String categoryName;
    private String categoryImageUrl;

    Category(String categoryName, String categoryImageUrl) {
        this.categoryName = categoryName;
        this.categoryImageUrl = categoryImageUrl;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getCategoryImageUrl() {
        return categoryImageUrl;
    }
}
