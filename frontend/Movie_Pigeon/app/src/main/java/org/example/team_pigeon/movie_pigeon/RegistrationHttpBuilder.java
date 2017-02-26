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
    String param1, param2, param3, query;
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
        param1 = email;
        param2 = username;
        param3 = password;
        try {
            return String.format("email=%s&username=%s&password=%s",
                    URLEncoder.encode(param1, charset),
                    URLEncoder.encode(param2, charset),
                    URLEncoder.encode(param3, charset));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Log.e(TAG, "Unable to encode message");
            return "";
        }
    }


    @Override
    protected Void doInBackground(String... params) {
        String p1, p2, p3;
        p1=params[0];
        p2=params[1];
        p3=params[2];
        Log.i(TAG, "Passed in parameters are " + p1 + " " + p2 + " " + p3);
        query = formQuery(p1, p2, p3);
        Log.i(TAG, "query formed");
        Log.i(TAG, "Query is " + query);
        request(query);
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
            bundle.putString("email", param1);
            bundle.putString("password", param3);
            intent.putExtras(bundle);
            mContext.sendBroadcast(intent);
        }
    }
}
