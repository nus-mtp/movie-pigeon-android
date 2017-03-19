package org.example.team_pigeon.movie_pigeon;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Guo Mingxuan on 7/3/2017.
 */

public class ThirdPartySignupActivity extends AppCompatActivity {
    View mainView;
    TextView title, passwordTitle;
    EditText inputField, passwordField;
    String thirdParty, username, password;
    GlobalReceiver globalReceiver = new GlobalReceiver();
    Button BConfirm, BBack;
    String[] checkBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainView = getLayoutInflater().inflate(R.layout.activity_third_party_signup, null);
        setContentView(mainView);

        IntentFilter filter = new IntentFilter();
        filter.addAction("dataPullingSuccess");
        registerReceiver(globalReceiver, filter);


        thirdParty = getIntent().getExtras().getString("thirdParty");

        inputField = (EditText) mainView.findViewById(R.id.tpsInput);
        passwordField = (EditText) mainView.findViewById(R.id.tpsPassword);
        title = (TextView) mainView.findViewById(R.id.tpsTitle);
        title.setText("Username/Email/ID for " + thirdParty);
        passwordTitle = (TextView) mainView.findViewById(R.id.tpsPasswordTitle);
        BConfirm = (Button) mainView.findViewById(R.id.tpsConfirmButton);
        BBack = (Button) mainView.findViewById(R.id.tpsBackButton);

        if (thirdParty.equals("TraktTV")) {
            passwordTitle.setVisibility(View.GONE);
            passwordField.setVisibility(View.GONE);
        } else if (thirdParty.equals("The Movie DB")) {
            // nothing right now
        }

        BConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (thirdParty.equals("TraktTV")) {
                    username = String.valueOf(inputField.getText());
                    if (username.equals("")) {
                        Toast.makeText(getApplicationContext(), "Username or email can't be empty", Toast.LENGTH_SHORT).show();
                    } else {
                        checkBundle = new String[2];
                        checkBundle[0] = username;
                        checkBundle[1] = thirdParty;
                        new TPSWorkingThread(getApplicationContext()).execute(checkBundle);
                        Toast.makeText(getApplicationContext(), "Checking with " + thirdParty + " server", Toast.LENGTH_SHORT).show();
                    }
                } else if (thirdParty.equals("The Movie DB")) {
                    username = String.valueOf(inputField.getText());
                    password = String.valueOf(passwordField.getText());
                    if (username.equals("") || password.equals("")) {
                        Toast.makeText(getApplicationContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    } else {
                        checkBundle = new String[3];
                        checkBundle[0] = username;
                        checkBundle[1] = thirdParty;
                        checkBundle[2] = password;
                        new TPSWorkingThread(getApplicationContext()).execute(checkBundle);
                        Toast.makeText(getApplicationContext(), "Checking with " + thirdParty + " server", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        BBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(globalReceiver);
    }
}

class TPSWorkingThread extends AsyncTask<String, Void, Void> {
    Context mContext;
    String userInfo, thirdParty, password;
    String url="";
    String TAG = "TPSWT";
    HttpURLConnection connection;
    private boolean validated, connectionError = false;
    String charset = java.nio.charset.StandardCharsets.UTF_8.name();
    String line, serverResponse;

    TPSWorkingThread(Context context) {
        mContext = context;
    }

    @Override
    protected Void doInBackground(String... params) {
        userInfo = params[0];
        thirdParty = params[1];
        Log.i(TAG, "User info and tp are " + userInfo + " " + thirdParty);
        if (thirdParty.equals("TraktTV")) {
            url = "http://128.199.167.57:8080/api/traktTV";
            password = "NONE";
        } else if (thirdParty.equals("The Movie DB")) {
            url = "http://128.199.167.57:8080/api/tmdb";
            password = params[2];
        } else {
            Log.e(TAG, "Wrong type of third party");
        }

        validate();
        return null;
    }

    private void validate() {
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept-Charset", charset);
            connection.setRequestProperty("username", userInfo);
            if (thirdParty.equals("The Movie DB")) {
                connection.setRequestProperty("password", password);
            }

            connection.connect();

            Log.i(TAG, "Finished sending to server");

            int status = connection.getResponseCode();
            Log.i(TAG, "response status transactionId is " + status);

            if (status == 200) {
                InputStream response = connection.getInputStream();
                // process the response
                BufferedReader br = new BufferedReader(new InputStreamReader(response));
                StringBuffer sb = new StringBuffer();
                Log.i(TAG, "Starting to read response");
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                    serverResponse = "Registration Response>>>" + line;
                    Log.i(TAG, serverResponse);
                }
                if (serverResponse.contains("true")) {
                    validated = true;
                }
            } else {
                connectionError = true;
                Log.e(TAG, "Error code " + status);
            }

        } catch (IOException e) {
            e.printStackTrace();
            connectionError = true;
            Log.e(TAG,"Unable to connect to server");
        }
    }

    @Override
    protected void onPostExecute(Void v) {
        if (connectionError) {
            Toast.makeText(mContext, "Unable to connect to server", Toast.LENGTH_SHORT).show();
        } else if (!validated) {
            Toast.makeText(mContext, "Can't find the user you entered", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent("dataPullingSuccess");
            Bundle bundle = new Bundle();
            bundle.putString("ThirdParty", thirdParty);
            bundle.putString("UserInfo", userInfo);
            bundle.putString("ThirdPartyPassword", password);
            intent.putExtras(bundle);
            mContext.sendBroadcast(intent);
        }
    }
}
