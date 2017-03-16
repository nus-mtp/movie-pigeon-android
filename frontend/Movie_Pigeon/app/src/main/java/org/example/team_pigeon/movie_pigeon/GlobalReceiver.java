package org.example.team_pigeon.movie_pigeon;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.util.Log;

/**
 * Created by Guo Mingxuan on 11/2/2017.
 */

class GlobalReceiver extends BroadcastReceiver {
    String emailSignin, passwordSignin;
    String TAG = "GlobalReceiver";
    Handler uiHandler;
    private final static int VCodeSuccess = 0;
    private final static int ResetSuccess = 1;
    private final static int changeUsername = 0;
    private static UserInfoSingleton userInfoBulk = UserInfoSingleton.getInstance();

    GlobalReceiver() {
        uiHandler = null;
    }

    GlobalReceiver(Handler handler) {
        uiHandler = handler;
    }

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
                Log.i(TAG, "Registration completed. Proceed to sign in");
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

            case "killRegistration":
                Log.i(TAG, "Kill Registration received");
                ((Activity) context).finish();
                break;

            case "userUpdate":
                Log.i(TAG, "User Update received");
                ((Activity) context).finish();
                break;

            case "vCodeSuccessful":
                Log.i(TAG, "Successful VCode received");
                uiHandler.sendEmptyMessage(VCodeSuccess);
                break;

            case "ResetSuccessful":
                Log.i(TAG, "Successful VCode received");
                uiHandler.sendEmptyMessage(ResetSuccess);
                break;

            case "dataPullingSuccess":
                Log.i(TAG, "Successful data pulling received");
                Intent startRegistration = new Intent(context, RegistrationActivity.class);
                bundle = intent.getExtras();
                startRegistration.putExtras(bundle);
                context.startActivity(startRegistration);
                ((Activity)context).finish();
                break;

            case "changeUsername":
                Log.i(TAG, "Received msg to update username");
                userInfoBulk.reset();
                break;
        }
    }
}
