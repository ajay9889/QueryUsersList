package com.userdatautilities.DatabaseHelper;

import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.support.annotation.NonNull;

/**
 * Created by Ajay on 08/09/18.
 * Created database "user_info"
 * Created database instance
 */
@Database(entities = {User.class}, version = 2)
public abstract class Databasehelper extends RoomDatabase {
    public abstract UserDatabaseQueryDao userDao();
    private static Databasehelper INSTANCE;

    @NonNull
    @Override
    public SupportSQLiteOpenHelper getOpenHelper() {
        return super.getOpenHelper();
    }

    //   get Database instance to perform the Query functions
   public static Databasehelper getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (Databasehelper.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            Databasehelper.class, "user_info")
                            .build();

                }
            }
        }
        return INSTANCE;
    }
}