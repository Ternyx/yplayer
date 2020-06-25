package com.ternyx.yplayer.data.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.ternyx.yplayer.data.db.model.User;
import com.ternyx.yplayer.data.db.repo.UserDao;

@Database(entities = {User.class}, version = 1)
@TypeConverters(Converters.class)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
}
