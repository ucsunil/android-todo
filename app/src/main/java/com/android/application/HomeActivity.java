package com.android.application;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.android.application.fragments.HomeFragment;
import com.android.application.services.TaskAlertService;
import com.android.application.storage.DatabaseHelper;


public class HomeActivity extends Activity {

    HomeFragment homeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        showHomeFragment();
    }

    private void showHomeFragment() {
        if(homeFragment == null) {
            homeFragment = HomeFragment.getInstance();
        }
        if(!homeFragment.isVisible()) {
            getFragmentManager().beginTransaction().replace(R.id.content, homeFragment).commit();
        }
    }

}
