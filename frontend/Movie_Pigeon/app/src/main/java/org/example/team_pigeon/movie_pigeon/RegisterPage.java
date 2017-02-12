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

    RegisterPage(final Context mContext, final Activity mActivity, View signin) {
        final String[] email = new String[1];
        final String[] username = new String[1];
        final String[] password = new String[1];
        final String[] confirmPassword = new String[1];
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
                email[0] = String.valueOf(etEmail.getText());
                username[0] = String.valueOf(etUsername.getText());
                password[0] = String.valueOf(etPassword.getText());
                confirmPassword[0] = String.valueOf(etConfirmPassword.getText());

                if (email[0].equals("") | username[0].equals("") | password[0].equals("") | confirmPassword[0].equals("")) {
                    Toast.makeText(mContext, "Must fill in all fields", Toast.LENGTH_SHORT).show();
                } else {
                    // double check password match
                    if (!password[0].equals(confirmPassword[0])) {
                        Toast toast = Toast.makeText(mContext, "Passwords entered don't match!", Toast.LENGTH_SHORT);
                        toast.show();

                    } else {
                        System.out.println("Registering");
                        String[] registrationDetails = new String[3];
                        registrationDetails[0] = email[0];
                        registrationDetails[1] = username[0];
                        registrationDetails[2] = password[0];

                        Log.e("RegistrationPage", "3 parameters to be passed are " + registrationDetails[0] + " " + registrationDetails[1] + " " + registrationDetails[2]);
                        new RegistrationHttpBuilder(mContext).execute(registrationDetails);
                    }
                }

            }
        });
    }
}
