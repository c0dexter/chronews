package pl.michaldobrowolski.chronews.api.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {ArticleEntity.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract MyDao myDao();
}
