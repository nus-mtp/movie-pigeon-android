package org.example.team_pigeon.movie_pigeon;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewManager;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by Guo Mingxuan on 1/2/2017.
 */

class RegisterPage {
    final int registerType = 1;

    RegisterPage(Context mContext, Activity mActivity, View signin) {
        View register = LayoutInflater.from(mActivity.getApplication()).inflate(R.layout.register_page, null);
        mActivity.setContentView(register);
        // remove signin page view
        ((ViewManager)signin.getParent()).removeView(signin);

        EditText etEmail = (EditText) register.findViewById(R.id.rETEmail);
        EditText etUsername = (EditText) register.findViewById(R.id.rETUsername);
        EditText etPassword = (EditText) register.findViewById(R.id.rETPassword);
        EditText etConfirmPassword = (EditText) register.findViewById(R.id.rETConfirmPassword);

        final String email = String.valueOf(etEmail.getText());
        final String username = String.valueOf(etUsername.getText());
        final String password = String.valueOf(etPassword.getText());
        final String confirmPassword = String.valueOf(etConfirmPassword.getText());
        // TODO double check the password entered are correct



        Button registerButton = (Button) register.findViewById(R.id.rpRegisterButton);
        registerButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                HttpBuilder hb = new HttpBuilder(registerType, email, username, password);
                // TODO process server response

            }
        });
    }
}
