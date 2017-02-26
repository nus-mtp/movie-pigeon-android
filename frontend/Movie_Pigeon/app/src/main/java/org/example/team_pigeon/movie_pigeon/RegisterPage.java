package org.example.team_pigeon.movie_pigeon;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Guo Mingxuan on 1/2/2017.
 */

class RegisterPage {

    String email, username, password, confirmPassword;

    RegisterPage(final Context mContext, final Activity mActivity) {

        final View register = LayoutInflater.from(mActivity.getApplication()).inflate(R.layout.register_page, null);
        mActivity.setContentView(register);

        final EditText etEmail = (EditText) register.findViewById(R.id.rETEmail);
        final EditText etUsername = (EditText) register.findViewById(R.id.rETUsername);
        final EditText etPassword = (EditText) register.findViewById(R.id.rETPassword);
        final EditText etConfirmPassword = (EditText) register.findViewById(R.id.rETConfirmPassword);

        Button registerButton = (Button) register.findViewById(R.id.rpRegisterButton);
        Button backButton = (Button) register.findViewById(R.id.rpBackButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ViewGroup)register.getParent()).removeView(register);
                SigninPage sp = new SigninPage(mContext, mActivity);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                email = String.valueOf(etEmail.getText());
                username = String.valueOf(etUsername.getText());
                password = String.valueOf(etPassword.getText());
                confirmPassword = String.valueOf(etConfirmPassword.getText());

                if (email.equals("") | username.equals("") | password.equals("") | confirmPassword.equals("")) {
                    Toast.makeText(mContext, "Must fill in all fields", Toast.LENGTH_SHORT).show();
                } else {
                    // double check password match
                    if (!password.equals(confirmPassword)) {
                        Toast toast = Toast.makeText(mContext, "Passwords entered don't match!", Toast.LENGTH_SHORT);
                        toast.show();
                    } else if (!email.contains("@")) {
                        Toast.makeText(mContext, "Please enter correct email address", Toast.LENGTH_SHORT).show();
                    } else {
                        System.out.println("Registering");
                        String[] registrationDetails = new String[3];
                        registrationDetails[0] = email;
                        registrationDetails[1] = username;
                        registrationDetails[2] = password;

                        Log.e("RegistrationPage", "3 parameters to be passed are " + registrationDetails[0] + " " + registrationDetails[1] + " " + registrationDetails[2]);
                        new RegistrationHttpBuilder(mContext).execute(registrationDetails);
                    }
                }

            }
        });
    }
}
