package org.example.team_pigeon.movie_pigeon;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

/**
 * Created by Guo Mingxuan on 16/3/2017.
 */

public class ChangeUserInfoActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private String type;
    private FragmentManager fm;
    private String TAG = "CUIA";
    private GlobalReceiver globalReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = getLayoutInflater().inflate(R.layout.activity_change_user_info, null);
        setContentView(view);
        toolbar = (Toolbar) findViewById(R.id.toolbar_user_activity);
        type = getIntent().getExtras().getString("type");
        Log.i(TAG, "the type is " + type);
        toolbar.setTitle("Change " + type);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        fm = getFragmentManager();

        globalReceiver = new GlobalReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("changeUsername");
        filter.addAction("userUpdate");
        registerReceiver(globalReceiver, filter);

        if (type.equals("username")) {
            Log.i(TAG, "Type is username");
            toolbar.setTitle("Change Username");
            SettingFragments sf = new SettingFragments();
            Bundle args = new Bundle();
            args.putString("type", "username");
            sf.setArguments(args);
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.replace(R.id.fl_user_activity, sf);
            transaction.commit();
        } else if (type.equals("password")) {
            Log.i(TAG, "Type is password");
            toolbar.setTitle("Change password");
            SettingFragments sf = new SettingFragments();
            Bundle args = new Bundle();
            args.putString("type", "password");
            sf.setArguments(args);
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.replace(R.id.fl_user_activity, sf);
            transaction.commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
            Log.i(TAG, "Back arrow pressed on home");
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(globalReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
