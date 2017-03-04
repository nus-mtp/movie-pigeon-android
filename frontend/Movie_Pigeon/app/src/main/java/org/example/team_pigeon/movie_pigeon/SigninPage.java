package org.example.team_pigeon.movie_pigeon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Guo Mingxuan on 29/1/2017.
 */

class SigninPage {
    Context mContext;
    Activity mActivity;
    View signin;
    String email, password;
    EditText etEmail, etPassword;
    String TAG = "SigninPage";

    SigninPage(final Context mContext, final Activity mActivity) {
        this.mContext = mContext;
        this.mActivity = mActivity;
        signin = LayoutInflater.from(mActivity.getApplication()).inflate(R.layout.signin_page, null);
        Button BSignIn = (Button) signin.findViewById(R.id.buttonSignIn);
        Button BRegister = (Button) signin.findViewById(R.id.buttonRegister);
        mActivity.setContentView(signin);

        etEmail = (EditText) signin.findViewById(R.id.editTextUsername);
        etPassword = (EditText) signin.findViewById(R.id.editTextPassword);

        BSignIn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // Check Credentials and respond accordingly
                email = String.valueOf(etEmail.getText());
                password = String.valueOf(etPassword.getText());
                if (email.equals("") | password.equals("")) {
                    Toast.makeText(mContext, "Email or password can't be empty!", Toast.LENGTH_SHORT).show();
                } else if (!email.contains("@")) {
                    Toast.makeText(mContext, "Please enter correct email address", Toast.LENGTH_SHORT).show();
                } else {
                    Log.i(TAG, email + " and " + password);
                    String[] signInDetails = new String[2];
                    signInDetails[0] = email;
                    signInDetails[1] = password;
                    SignInHttpBuilder sBuilder = new SignInHttpBuilder(mContext);
                    sBuilder.execute(signInDetails);
                }
            }
        });

        BRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                RegisterPage register = new RegisterPage(mContext, mActivity);
            }
        });
    }
}
