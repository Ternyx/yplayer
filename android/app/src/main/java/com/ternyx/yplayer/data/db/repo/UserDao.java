package com.ternyx.yplayer.data.db.repo;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.ternyx.yplayer.data.db.model.User;

@Dao
public interface UserDao {

    @Query("SELECT * FROM user WHERE google_id = :googleId")
    User findByGoogleId(String googleId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUser(User user);
}
