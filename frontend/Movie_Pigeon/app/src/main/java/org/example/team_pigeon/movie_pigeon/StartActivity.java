package org.example.team_pigeon.movie_pigeon;

import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class StartActivity extends AppCompatActivity {

    private final Receiver receiver = new Receiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        // set up broadcast receiver to receive request from background thread
        IntentFilter filter = new IntentFilter();
        filter.addAction("startHomePageActivity");
        filter.addAction("automaticSignin");
        registerReceiver(receiver, filter);

        SigninPage sp = new SigninPage(StartActivity.this, this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}
