package com.hcrpurdue.jason.hcrhousepoints.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hcrpurdue.jason.hcrhousepoints.Models.House;
import com.hcrpurdue.jason.hcrhousepoints.Models.PointType;
import com.hcrpurdue.jason.hcrhousepoints.Models.Reward;
import com.hcrpurdue.jason.hcrhousepoints.Models.SystemPreferences;
import com.hcrpurdue.jason.hcrhousepoints.R;
import com.hcrpurdue.jason.hcrhousepoints.Utils.Singleton;
import com.hcrpurdue.jason.hcrhousepoints.Utils.UtilityInterfaces.SingletonInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AppInitializationActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private Singleton singleton;
    private ProgressBar loadingBar;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_app_initialization);
        auth = FirebaseAuth.getInstance();
        singleton = Singleton.getInstance(getApplicationContext());
        initializeViews();
        initializeUserData();

    }

    private void initializeViews(){
        loadingBar = findViewById(R.id.initialization_progress_bar);
        loadingBar.setVisibility(View.VISIBLE);
    }

    /**
     * Method will ensure data is cached before loading is complete
     */
    private void initializeUserData(){
        if(isLoggedIn()){
            if (singleton.cacheFileExists()) {
                singleton.getUserData(new SingletonInterface() {
                    public void onSuccess() {
                        initializeCompetitionData();
                    }
                    public void onError(Exception e, Context context){
                        if(e.getMessage().equals("User does not exist.")){
                            handleMissingUserInformation();
                        }
                        else{
                            Toast.makeText(AppInitializationActivity.this,
                                    e.getLocalizedMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                singleton.getUserDataNoCache(new SingletonInterface() {
                    public void onSuccess() {
                        initializeCompetitionData();
                    }
                    public void onError(Exception e, Context context){
                        if(e.getMessage().equals("User does not exist.")){
                            handleMissingUserInformation();
                        }
                        else{
                            Toast.makeText(AppInitializationActivity.this,
                                    e.getLocalizedMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
        else{
            launchSignInActivity();
        }
    }

    private void initializeCompetitionData(){
        try {
            singleton.getPointTypes(new SingletonInterface() {

                public void onPointTypeComplete(List<PointType> data) {
                    singleton.getSystemPreferences(new SingletonInterface() {
                        @Override
                        public void onGetSystemPreferencesSuccess(SystemPreferences systemPreferences) {
                            singleton.getPointStatistics(new SingletonInterface() {
                                @Override
                                public void onGetPointStatisticsSuccess(List<House> houses, int userPoints, List<Reward> rewards) {
                                    launchNavigationActivity();
                                }

                                @Override
                                public void onError(Exception e, Context context) {
                                    handleDataInitializationError(e);
                                }
                            });
                        }

                        @Override
                        public void onError(Exception e, Context context) {
                            handleDataInitializationError(e);
                        }
                    });
                }

                @Override
                public void onError(Exception e, Context context) {
                    handleDataInitializationError(e);
                }
            });

        } catch (Exception e) {
            handleDataInitializationError(e);
        }

    }

    private void handleDataInitializationError(Exception e){
        Toast.makeText(this, "Failed to load house data. ", Toast.LENGTH_LONG).show();
        Log.e("PointSubmissionFragment", "Error loading point types", e);
        launchSignInActivity();
    }

    /**
     * Return true if the user is logged in and false otherwise
     * @return boolean isLoggedIn
     */
    private boolean isLoggedIn(){
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Transition to Sign In Activity
     */
    private void launchSignInActivity(){
        Intent intent = new Intent(this, LogInActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * transition to Navigation Activity
     */
    private void launchNavigationActivity(){
        Intent intent = new Intent(this, NavigationDrawer.class);
        intent.putExtra("PointSubmitted", false);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
        finish();
    }

    private void launchHouseSignUpActivity(){
        Intent intent = new Intent(this, HouseSignUpActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
        finish();
    }

    private void handleMissingUserInformation(){
        new AlertDialog.Builder(this)
                    .setTitle("Welcome Back")
                    .setMessage("Welcome back to Purdue HCR! Please connect with a new house to start earning points.")
                    .setPositiveButton("Let's Go!", (dialog, whichButton) -> {
                        launchHouseSignUpActivity();
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            launchSignInActivity();
                        }
                    }).show();



    }
}
