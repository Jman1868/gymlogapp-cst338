package com.example.gymlog.database;


import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.gymlog.database.entities.GymLog;
import com.example.gymlog.MainActivity;
import com.example.gymlog.database.entities.User;
import com.example.gymlog.database.typeConverters.LocalDateTypeConverter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@TypeConverters(LocalDateTypeConverter.class)
@Database(entities = {GymLog.class, User.class}, version = 1, exportSchema = false)
public abstract class GymLogDatabase extends RoomDatabase {


    public static final String USER_TABLE = "usertable";
    private static final String DATABASE_NAME = "GymLogdatabase";
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
           databaseWriteExecutor.execute(() -> {
               UserDAO dao = INSTANCE.userDAO();
               dao.deleteAll();
               User admin = new User("admin1", "admin1");
               admin.setAdmin(true);
               dao.insert(admin);
               User testUser1 = new User("testuser1", "testuser1");
               dao.insert(testUser1);
           });
        }
    };

    public abstract GymLogDAO gymLogDAO();

    public abstract UserDAO userDAO();
}
