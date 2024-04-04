package com.example.gymlog;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;

import com.example.gymlog.database.GymLogRepository;
import com.example.gymlog.database.entities.GymLog;
import com.example.gymlog.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    private static final String MAIN_ACTIVITY_USER_ID = "com.example.gymlog.MAIN_ACTIVITY_USER_ID";
    private ActivityMainBinding binding;

    private GymLogRepository repository;

    public static final String TAG = "DAC_GYMLOG";
    String mExercise = "";
    double mWeight = 0.0;
    int mReps = 0;

    //TODO: Add login information;
    int loggedInUserId = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        //Login stuff
        loginUser();

        if(loggedInUserId==-1){
            Intent intent = LoginActivity.loginIntentFactory(getApplicationContext());
            startActivity(intent);
        }


        repository = GymLogRepository.getRepository(getApplication());
        binding.logDisplayTextView.setMovementMethod(new ScrollingMovementMethod());
        updateDisplay();
        binding.logButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getInformationFromDisplay();
                insertGymlogRecord();
                updateDisplay();
            }
        });

        binding.exerciseInputEditTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDisplay();
            }
        });

    }

    //TODO: CREATE LOGIN METHOD
    private void loginUser() {

        loggedInUserId=getIntent().getIntExtra(MAIN_ACTIVITY_USER_ID,-1);

    }

    static Intent mainActivityIntentFactory(Context context, int userId){

        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(MAIN_ACTIVITY_USER_ID,userId);
        return intent;

    }

    private void insertGymlogRecord(){

        if(mExercise.isEmpty()){
            return;
        }

        GymLog log = new GymLog(mExercise,mWeight,mReps, loggedInUserId);
        repository.insertGymLog(log);

    }

    private void updateDisplay(){

        ArrayList<GymLog> allLogs = repository.getAllLogs();

        if(allLogs.isEmpty()){
            binding.logDisplayTextView.setText(R.string.nothing_to_show_time_to_hit_the_gym);
        }

        StringBuilder sb = new StringBuilder();
        for(GymLog log: allLogs){
            sb.append(log);
        }

        binding.logDisplayTextView.setText(sb.toString());

    }


    private void getInformationFromDisplay(){
        mExercise = binding.exerciseInputEditTextView.getText().toString();

        try {
            mWeight= Double.parseDouble(binding.weightInputEditTextView.getText().toString());
        }
        catch (NumberFormatException e){
            Log.d(TAG, "Error reading value from Weight edit Text.");
        }

        try {
            mReps= Integer.parseInt(binding.repInputEditTextView.getText().toString());
        }
        catch (NumberFormatException e){
            Log.d(TAG, "Error reading value from reps edit Text.");
        }

    }
}