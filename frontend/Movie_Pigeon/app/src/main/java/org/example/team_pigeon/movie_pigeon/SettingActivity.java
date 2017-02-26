package org.example.team_pigeon.movie_pigeon;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
 * Created by Guo Mingxuan on 22/2/2017.
 */

public class SettingActivity extends AppCompatActivity {
    EditText userInput;
    Button bConfirm;
    Button bCancel;
    String input;
    Context mContext;
    Spinner spinnerList;
    String choice;
    private String TAG = "SettingActivity";
    private RequestHttpBuilderSingleton singleton = RequestHttpBuilderSingleton.getInstance();
    private GlobalReceiver globalReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        LayoutInflater inflater = getLayoutInflater();
        View settingView = inflater.inflate(R.layout.activity_setting, null);
        setContentView(settingView);

        // set up broadcast globalReceiver to receive request from background thread
        globalReceiver = new GlobalReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("UserUpdate");
        registerReceiver(globalReceiver, filter);

        userInput = (EditText) settingView.findViewById(R.id.popupUserUpdate);
        bConfirm = (Button) settingView.findViewById(R.id.popupButtonConfirm);
        bCancel = (Button) settingView.findViewById(R.id.popupButtonCancel);
        spinnerList = (Spinner) settingView.findViewById(R.id.popupTitle);

        bConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                input = String.valueOf(userInput.getText());
                choice = spinnerList.getSelectedItem().toString();
                if (input.equals("")) {
                    Toast.makeText(mContext, "Please enter your change", Toast.LENGTH_SHORT).show();
                } else {
                    String[] toPassIn = new String[3];
                    toPassIn[0] = choice;
                    Log.e(TAG, "User wants to change " + choice);
                    toPassIn[1] = input;
                    toPassIn[2] = singleton.getToken();
                    Log.e(TAG, "Starting working thread");
                    new WorkingThread(mContext).execute(toPassIn);
                }
            }
        });

        bCancel.setOnClickListener(new View.OnClickListener() {
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

class WorkingThread extends AsyncTask<String, Void, Void> {
    String[] inputParams;
    String type;
    String modification;
    private String TAG = "WorkingThread";
    String charset = java.nio.charset.StandardCharsets.UTF_8.name();
    String body;
    private HttpURLConnection connection;
    String token;
    Boolean connectionError = false;
    Context mContext;
    WorkingThread(Context context) {
        mContext = context;
    }

    @Override
    protected Void doInBackground(String... params) {
        inputParams = params;
        if (inputParams[0].equals("Change Username")) {
            type = "username";
        } else if (inputParams[0].equals("Change Password")) {
            type = "password";
        } else {
            Log.e(TAG, "Invalid type");
        }
        modification = inputParams[1];
        token = inputParams[2];
        Log.e(TAG, "Type is " + type + " and modification is " + modification);
        body = formBody();
        request(type);

        return null;
    }

    private String formBody() {
        try {
            String field = type + "=%s";
            return String.format(field, URLEncoder.encode(modification, charset));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            System.out.println("Unable to encode message");
            return "";
        }
    }

    private void request(String type) {
        // find type of the request

        String url = "http://128.199.231.190:8080/api/users/" + type;


        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("PUT");
            connection.setDoOutput(true);
            connection.setRequestProperty("Authorization", "Bearer " + token);
            connection.setRequestProperty("Accept-Charset", charset);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + charset);

            connection.connect();

            try (OutputStream output = connection.getOutputStream()) {
                output.write(body.getBytes(charset));
            }

            Log.e(TAG, "Finished sending to server");

            int status = connection.getResponseCode();
            Log.e(TAG, "response status transactionId is " + status);

            if (status == 200) {
                InputStream response = connection.getInputStream();
                // process the response
                BufferedReader br = new BufferedReader(new InputStreamReader(response));
                StringBuffer sb = new StringBuffer();
                Log.e(TAG, "Starting to read response");
                String line, serverResponse = "";
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                    serverResponse = "Registration Response>>>" + line;
                    System.out.println(serverResponse);
                }
            } else {
                connectionError = true;
                System.out.println(status);
            }

        } catch (IOException e) {
            e.printStackTrace();
            connectionError = true;
            System.out.print("Unable to connect to server");
        }
    }

    @Override
    protected void onPostExecute(Void v) {
        if (connectionError) {
            Toast.makeText(mContext, "Unable to connect to server", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext, "Operation successful", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent("UserUpdate");
            mContext.sendBroadcast(intent);
        }
    }
}
