package org.example.team_pigeon.movie_pigeon;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by Guo Mingxuan on 29/1/2017.
 */

class SigninPage {
    SigninPage(final Context mContext, final Activity mActivity) {
        final View signin = LayoutInflater.from(mActivity.getApplication()).inflate(R.layout.signin_page, null);
        Button BSignIn = (Button) signin.findViewById(R.id.buttonSignIn);
        Button BRegister = (Button) signin.findViewById(R.id.buttonRegister);
        mActivity.setContentView(signin);

        EditText etEmail = (EditText) signin.findViewById(R.id.editTextUsername);
        EditText etPassword = (EditText) signin.findViewById(R.id.editTextPassword);

        final String email = String.valueOf(etEmail.getText());
        final String password = String.valueOf(etPassword.getText());

        BSignIn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Check Credentials and respond accordingly
                HttpBuilder hb = new HttpBuilder(0, email, "", password);
                // TODO processing response from the server



                // for testing purpose
                System.out.println("Sign in button clicked");
            }
        });

        BRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                RegisterPage register = new RegisterPage(mContext, mActivity, signin);

                // for testing purpose
                System.out.println("Register button clicked");
            }
        });
    }
}
