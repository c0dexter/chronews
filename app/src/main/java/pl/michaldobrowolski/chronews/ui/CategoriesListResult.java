package pl.michaldobrowolski.chronews.ui;

import java.util.List;

import pl.michaldobrowolski.chronews.utils.Category;

public class CategoriesListResult {
    private final List<Category> categoryList;

    public CategoriesListResult(List<Category> categoryList) {
        this.categoryList = categoryList;
    }

    public List<Category> getCategoryList() {
        return categoryList;
    }

}
