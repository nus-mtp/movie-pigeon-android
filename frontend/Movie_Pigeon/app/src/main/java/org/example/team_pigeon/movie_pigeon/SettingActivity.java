package org.example.team_pigeon.movie_pigeon;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.support.v7.widget.Toolbar;

/**
 * Created by Guo Mingxuan on 22/2/2017.
 */

public class SettingActivity extends AppCompatActivity {
    private String TAG = "SettingActivity";
    private TableRow usernameRow, passwordRow;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RelativeLayout settingView = (RelativeLayout) getLayoutInflater().inflate(R.layout.activity_setting, null);
        setContentView(settingView);
        toolbar = (Toolbar) findViewById(R.id.toolbar_setting_page);
        toolbar.setTitle("Settings");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        usernameRow = (TableRow) settingView.findViewById(R.id.username_setting_page);
        passwordRow = (TableRow) settingView.findViewById(R.id.password_setting_page);

        usernameRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Change username clicked");
                Intent intent = new Intent(getApplicationContext(), ChangeUserInfoActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("type", "username");
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        passwordRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Change password clicked");
                Intent intent = new Intent(getApplicationContext(), ChangeUserInfoActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("type", "password");
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
            Log.i(TAG, "Back arrow pressed on home");
        }
        return false;
    }
}
