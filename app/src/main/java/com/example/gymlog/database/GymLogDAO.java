package com.example.gymlog.database;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.gymlog.database.entities.GymLog;


import java.util.List;


/**
 * Represents the queries we are making in our database
 */

@Dao
public interface GymLogDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(GymLog gymLog);

    @Query("Select * from " + GymLogDatabase.GYM_LOG_TABLE)
    List<GymLog> getAllRecords();

}
