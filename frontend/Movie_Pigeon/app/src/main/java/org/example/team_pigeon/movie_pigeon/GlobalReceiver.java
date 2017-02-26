package org.example.team_pigeon.movie_pigeon;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Switch;

/**
 * Created by Guo Mingxuan on 11/2/2017.
 */

class GlobalReceiver extends BroadcastReceiver {
    String emailSignin, passwordSignin;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Bundle bundle;

        switch (action) {
            case "startHomePageActivity":
                // get token from the broadcast
                Intent homePageIntent = new Intent(context, HomePageActivity.class);
                // pass token to the new activity
                bundle = new Bundle();
                bundle.putString("Token", intent.getStringExtra("Token"));
                homePageIntent.putExtras(bundle);
                context.startActivity(homePageIntent);
                ((Activity) context).finish();
                break;

            case "automaticSignin":
                // sign-in after registration
                System.out.println("Registration completed. Proceed to sign in");
                bundle = intent.getExtras();
                String email = bundle.getString("email");
                String password = bundle.getString("password");
                emailSignin = email;
                passwordSignin = password;

                String[] signInDetails = new String[2];
                signInDetails[0] = emailSignin;
                signInDetails[1] = passwordSignin;
                SignInHttpBuilder sBuilder = new SignInHttpBuilder(context);
                sBuilder.execute(signInDetails);
                break;

            case "UserUpdate":
                System.out.println("User Update received");
                ((Activity) context).finish();
                break;
        }
    }
}
