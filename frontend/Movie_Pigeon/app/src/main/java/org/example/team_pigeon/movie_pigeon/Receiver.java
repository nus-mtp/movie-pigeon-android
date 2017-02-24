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
        } else if (intent.getAction().equals("automaticSignin")) {
            // sign-in after registration
            System.out.println("Registration completed. Proceed to sign in");
            Bundle bundle = intent.getExtras();
            String email = bundle.getString("email");
            String password = bundle.getString("password");
            String[] emailSignin = new String[1];
            String[] passwordSignin = new String[1];
            emailSignin[0] = email;
            passwordSignin[0] = password;

            String[] signInDetails = new String[2];
            signInDetails[0] = emailSignin[0];
            signInDetails[1] = passwordSignin[0];
            SignInHttpBuilder sBuilder = new SignInHttpBuilder(context);
            sBuilder.execute(signInDetails);
        } else if (intent.getAction().equals("UserUpdate")) {
            System.out.println("User Update received");
            ((Activity)context).finish();
        }
    }
}
