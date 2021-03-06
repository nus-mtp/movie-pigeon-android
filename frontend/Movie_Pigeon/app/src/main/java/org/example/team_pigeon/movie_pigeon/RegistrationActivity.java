package org.example.team_pigeon.movie_pigeon;

import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Guo Mingxuan on 1/2/2017.
 */

public class RegistrationActivity extends AppCompatActivity {

    String email, username, password, confirmPassword;
    EditText etEmail, etUsername, etPassword, etConfirmPassword;
    View register;
    int count = 0;
    GlobalReceiver globalReceiver = new GlobalReceiver();
    String thirdParty = "NONE";
    String userInfo = "NONE";
    String thirdPartyPassword = "NONE";
    String TAG = "RegActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        register = getLayoutInflater().inflate(R.layout.register_page, null);
        setContentView(register);

        try {
            Bundle bundle = getIntent().getExtras();
            thirdParty = bundle.getString("ThirdParty");
            userInfo = bundle.getString("UserInfo");
            thirdPartyPassword = bundle.getString("ThirdPartyPassword");
            Log.i(TAG, "3rd party sign up");
        } catch (NullPointerException e) {
            Log.e(TAG, "No 3rd party sign up");
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction("killRegistration");
        registerReceiver(globalReceiver, filter);

        etEmail = (EditText) register.findViewById(R.id.rETEmail);
        etUsername = (EditText) register.findViewById(R.id.rETUsername);
        etPassword = (EditText) register.findViewById(R.id.rETPassword);
        etConfirmPassword = (EditText) register.findViewById(R.id.rETConfirmPassword);

        Button registerButton = (Button) register.findViewById(R.id.rpRegisterButton);
        Button backButton = (Button) register.findViewById(R.id.rpBackButton);

        // checking whether using third party sign up
        if (thirdParty.equals("TraktTV")) {
            etUsername.setText(userInfo);
        } else if (thirdParty.equals("The Movie DB")) {
            etUsername.setText(userInfo);
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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
                    Toast.makeText(getApplicationContext(), "Must fill in all fields", Toast.LENGTH_SHORT).show();
                } else {
                    // double check password match
                    if (!password.equals(confirmPassword)) {
                        Toast toast = Toast.makeText(getApplicationContext(), "Passwords entered don't match!", Toast.LENGTH_SHORT);
                        toast.show();
                    } else {
                        count = emailChecker(email);
                        // check whether email field contains: no @, or 2 or more @ or does not have enough length as format a@b
                        if (count == 0 || count > 1 || email.length()<=2) {
                            Toast.makeText(getApplicationContext(), "Please enter correct email address", Toast.LENGTH_SHORT).show();
                        } else {
                            System.out.println("Registering");
                            String[] registrationDetails = new String[6];
                            registrationDetails[0] = email;
                            registrationDetails[1] = username;
                            registrationDetails[2] = password;
                            registrationDetails[3] = thirdParty;
                            registrationDetails[4] = userInfo;
                            registrationDetails[5] = thirdPartyPassword;

                            Log.i("RegistrationPage", "5 parameters to be passed are " + registrationDetails[0] + " " + registrationDetails[1] + " " + registrationDetails[2] + " "
                                    + registrationDetails[3] + " " + registrationDetails[4] + " " + registrationDetails[5]);
                            new RegistrationHttpBuilder(getApplicationContext()).execute(registrationDetails);
                        }
                    }
                }

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(globalReceiver);
    }

    private int emailChecker(String email) {
        Pattern pattern = Pattern.compile("[@]");
        Matcher matcher = pattern.matcher(email);
        int countCharacter = 0;
        while(matcher.find()) {
            countCharacter++;
        }
        return countCharacter;
    }
}
