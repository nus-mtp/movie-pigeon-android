package org.example.team_pigeon.movie_pigeon;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Guo Mingxuan on 1/2/2017.
 */

class RegistrationHttpBuilder extends AsyncTask<String, Void, Void> {
    String TAG = "rHttpBuilder";
    String url = "http://128.199.231.190:8080";
    String registrationURL = "http://128.199.231.190:8080/api/users";
    String charset = java.nio.charset.StandardCharsets.UTF_8.name();
    String uEmail, uUsername, uPassword, u3rdInfo, query;
    String u3rdParty = "NONE";
    HttpURLConnection connection = null;
    Context mContext;
    private boolean connectionError = false;
    private boolean userExisted = false;
    String line;
    String serverResponse = null;

    RegistrationHttpBuilder(Context mContext) {
        this.mContext = mContext;
    }

    private void request(String query) {
        // build registration request here
        try {
            // check if using 3rd party sign up
            if (u3rdParty.equalsIgnoreCase("TraktTV")) {
                registrationURL = "http://128.199.231.190:8080/api/traktTV";
            }
            connection = (HttpURLConnection) new URL(registrationURL).openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Accept-Charset", charset);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + charset);

            connection.connect();

            try (OutputStream output = connection.getOutputStream()) {
                output.write(query.getBytes(charset));
            }

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

                if (serverResponse.contains("fail")) {
                    userExisted = true;
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

    private String formQuery(String email, String username, String password) {
        uEmail = email;
        uUsername = username;
        uPassword = password;
        try {
            return String.format("email=%s&username=%s&password=%s",
                    URLEncoder.encode(uEmail, charset),
                    URLEncoder.encode(uUsername, charset),
                    URLEncoder.encode(uPassword, charset));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Log.e(TAG, "Unable to encode message");
            return "";
        }
    }

    private String formQuery3rdParty(String email, String username, String password, String thirdParty, String thirdPartyInfo) {
        uEmail = email;
        uUsername = username;
        uPassword = password;
        u3rdParty = thirdParty;
        u3rdInfo = thirdPartyInfo;
        String thirdPartyBodyField="";
        if (thirdParty.equalsIgnoreCase("TraktTV")) {
            thirdPartyBodyField = "traktTVId";
        } else {
            Log.e(TAG, "Error in identifying 3rd party");
        }
        String strFormat = "email=%s&username=%s&password=%s&" + thirdPartyBodyField + "=%s";
        try {
            return String.format(strFormat,
                    URLEncoder.encode(uEmail, charset),
                    URLEncoder.encode(uUsername, charset),
                    URLEncoder.encode(uPassword, charset),
                    URLEncoder.encode(u3rdInfo, charset));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Log.e(TAG, "Unable to encode message");
            return "";
        }
    }

    @Override
    protected Void doInBackground(String... params) {
        String pEmail, pUsername, pPassword, pThirdParty, pUserInfo;
        pEmail=params[0];
        pUsername=params[1];
        pPassword=params[2];
        pThirdParty=params[3];
        pUserInfo=params[4];
        Log.i(TAG, "Passed in parameters are " + pEmail + " " + pUsername + " " + pPassword + " " + pThirdParty + " " + pUserInfo);
        if (pThirdParty.equals("TraktTV")) {
            query = formQuery3rdParty(pEmail, pUsername, pPassword, pThirdParty, pUserInfo);
            Log.i(TAG, "3rd party query formed");
            Log.i(TAG, "Query is " + query);
            request(query);
        } else {
            query = formQuery(pEmail, pUsername, pPassword);
            Log.i(TAG, "non 3rd party query formed");
            Log.i(TAG, "Query is " + query);
            request(query);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void v) {
        if (connectionError) {
            Toast.makeText(mContext, "Unable to register", Toast.LENGTH_SHORT).show();
        } else if (userExisted) {
            Toast.makeText(mContext, "Email already registered!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext, "Registration successful!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent("automaticSignin");
            Bundle bundle = new Bundle();
            bundle.putString("email", uEmail);
            bundle.putString("password", uPassword);
            intent.putExtras(bundle);
            mContext.sendBroadcast(intent);
        }
    }
}
