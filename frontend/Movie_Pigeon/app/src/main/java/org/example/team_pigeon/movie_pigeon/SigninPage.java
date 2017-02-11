package org.example.team_pigeon.movie_pigeon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by Guo Mingxuan on 29/1/2017.
 */

class SigninPage {
    Context mContext;
    Activity mActivity;

    SigninPage(final Context mContext, final Activity mActivity) {
        this.mContext = mContext;
        this.mActivity = mActivity;
        final View signin = LayoutInflater.from(mActivity.getApplication()).inflate(R.layout.signin_page, null);
        final String[] email = new String[1];
        final String[] password = new String[1];
        Button BSignIn = (Button) signin.findViewById(R.id.buttonSignIn);
        Button BRegister = (Button) signin.findViewById(R.id.buttonRegister);
        mActivity.setContentView(signin);

        final EditText etEmail = (EditText) signin.findViewById(R.id.editTextUsername);
        final EditText etPassword = (EditText) signin.findViewById(R.id.editTextPassword);

        BSignIn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Check Credentials and respond accordingly
                email[0] = String.valueOf(etEmail.getText());
                password[0] = String.valueOf(etPassword.getText());

                String[] signInDetails = new String[2];
                signInDetails[0] = email[0];
                signInDetails[1] = password[0];
                SignInHttpBuilder sBuilder = new SignInHttpBuilder(mContext);
                sBuilder.execute(signInDetails);
            }
        });

        BRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                RegisterPage register = new RegisterPage(mContext, mActivity, signin);
            }
        });
    }
}
