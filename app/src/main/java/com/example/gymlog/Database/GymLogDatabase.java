package com.example.gymlog.Database;


import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.gymlog.Database.entities.GymLog;
import com.example.gymlog.MainActivity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {GymLog.class}, version = 1, exportSchema = false)
public abstract class GymLogDatabase extends RoomDatabase {


    private static final String DATABASE_NAME = "GymLog_database";
    //The name of our table
    public static final String GYM_LOG_TABLE = "gymLogTable";


    //Ensure only one database exists
    private static volatile GymLogDatabase INSTANCE;

    /**   Make database not run on main thread 28:23 VIDEO 2
     * create a service that will supply threads for us to
     * do database operations. Create them all at Startup,
     * put them in a pool, and as we need to do database operations,
     * pull something out of the pool. When you're done using it,
     * it goes back in the pool. So this means that our database
     * will only ever have a maximum of 4 threads.
     * This is for efficiency, security and a few other things.
     */
    private static final int NUMBER_OF_THREADS = 4;

    static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static GymLogDatabase getDatabase(final Context context){

        if(INSTANCE == null){
            synchronized (GymLogDatabase.class){
                if(INSTANCE == null){
                    INSTANCE= Room.databaseBuilder(
                            context.getApplicationContext(),
                            GymLogDatabase.class,DATABASE_NAME)
                            .fallbackToDestructiveMigration()
                            .addCallback(addDefaultValues)
                            .build();

                }
            }
        }
        return INSTANCE;
    }

    private static final RoomDatabase.Callback addDefaultValues = new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db){
            super.onCreate(db);
            Log.i(MainActivity.TAG,"DATABASE CREATED!");
            //TODO: add databaseWriteExecutor.execute(() -> {..}
        }
    };

    public abstract GymLogDAO gymLogDAO();
}
