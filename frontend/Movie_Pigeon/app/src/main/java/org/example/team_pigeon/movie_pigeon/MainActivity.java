package org.example.team_pigeon.movie_pigeon;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private final Receiver receiver = new Receiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set up broadcast receiver to receive request from background thread
        IntentFilter filter = new IntentFilter();
        filter.addAction("startHomePageActivity");
        registerReceiver(receiver, filter);

        SigninPage sp = new SigninPage(MainActivity.this, this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}
