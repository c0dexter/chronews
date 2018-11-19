package pl.michaldobrowolski.chronews.utils;

import android.os.Parcel;
import android.os.Parcelable;

public class Category implements Parcelable {
    private String categoryName;
    private String categoryImageUrl;

    Category(String categoryName, String categoryImageUrl) {
        this.categoryName = categoryName;
        this.categoryImageUrl = categoryImageUrl;
    }

    protected Category(Parcel in) {
        categoryName = in.readString();
        categoryImageUrl = in.readString();
    }

    public static final Creator<Category> CREATOR = new Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };

    public String getCategoryName() {
        return categoryName;
    }

    public String getCategoryImageUrl() {
        return categoryImageUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(categoryName);
        dest.writeString(categoryImageUrl);
    }
}
