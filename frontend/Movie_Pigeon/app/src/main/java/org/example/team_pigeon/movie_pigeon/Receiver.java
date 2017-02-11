package org.example.team_pigeon.movie_pigeon;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by Guo Mingxuan on 11/2/2017.
 */

class Receiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("startHomePageActivity")) {
            // get token from the broadcast
            Intent homePageIntent = new Intent(context, HomePageActivity.class);
            // pass token to the new activity
            Bundle bundle = new Bundle();
            bundle.putString("Token", intent.getStringExtra("Token"));
            homePageIntent.putExtras(bundle);
            context.startActivity(homePageIntent);
            ((Activity)context).finish();
        }
    }
}
