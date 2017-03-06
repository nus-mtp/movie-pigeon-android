package org.example.team_pigeon.movie_pigeon;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
 * Created by Guo Mingxuan on 4/3/2017.
 */

public class ResetPasswordActivity extends AppCompatActivity {
    EditText emailField, vCodeField, newPwdField;
    Button BConfirm, BBack;
    Boolean vCodeButtonClicked = false;
    String email, type, vCode, newPwd;
    String TAG = "ResetPwd";
    String[] vCodeBundle = new String[2];
    String[] resetBundle = new String[3];
    GlobalReceiver resetPwdReceiver;
    private final static int VCodeSuccess = 0;
    private final static int ResetSuccess = 1;
    TextView hint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = getLayoutInflater();
        View resetView = inflater.inflate(R.layout.activity_reset_password, null);
        setContentView(resetView);

        emailField = (EditText) resetView.findViewById(R.id.rsETEmail);
        vCodeField = (EditText) resetView.findViewById(R.id.rsETVerificationCode);
        newPwdField = (EditText) resetView.findViewById(R.id.rsETNewPwd);
        BConfirm = (Button) resetView.findViewById(R.id.rsBVerificationCode);
        BBack = (Button) resetView.findViewById(R.id.rsBBack);
        hint = (TextView) resetView.findViewById(R.id.rsHint);

        resetPwdReceiver = new GlobalReceiver(new Handler(){
            public void handleMessage(Message msg) {
                final int what = msg.what;
                switch(what) {
                    case VCodeSuccess:
                        Toast.makeText(getApplicationContext(), "Email sent to " + email, Toast.LENGTH_SHORT).show();
                        BConfirm.setText("Confirm");
                        vCodeButtonClicked = true;
                        hint.setText("Please fill in the rest and click 'Confirm'");
                        Log.i(TAG, "Successfully sent VCode");
                        break;
                    case ResetSuccess:
                        Toast.makeText(getApplicationContext(), "Password Reset Successful", Toast.LENGTH_SHORT).show();
                        finish();
                        break;
                }
            }
        });
        IntentFilter filter = new IntentFilter();
        filter.addAction("vCodeSuccessful");
        filter.addAction("ResetSuccessful");
        registerReceiver(resetPwdReceiver, filter);

        BConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!vCodeButtonClicked) {
                    // get email from UI
                    email = String.valueOf(emailField.getText());
                    type = "GetVCode";
                    vCodeBundle[0] = email;
                    vCodeBundle[1] = type;

                    // send email to server to get vCode
                    new ResetPwdWorkingThread(getApplicationContext()).execute(vCodeBundle);
                } else {
                    // send to server with vCode
                    vCode = String.valueOf(vCodeField.getText());
                    newPwd = String.valueOf(newPwdField.getText());
                    if (vCode.equals("")) {
                        Toast.makeText(getApplicationContext(), "Verification code can't be empty", Toast.LENGTH_SHORT).show();
                    } else if (newPwd.equals("")) {
                        Toast.makeText(getApplicationContext(), "New password can't be empty", Toast.LENGTH_SHORT).show();
                    } else {
                        resetBundle[0] = vCode;
                        resetBundle[1] = "Reset";
                        resetBundle[2] = newPwd;

                        new ResetPwdWorkingThread(getApplicationContext()).execute(resetBundle);
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
        unregisterReceiver(resetPwdReceiver);
    }
}

class ResetPwdWorkingThread extends AsyncTask<String, Void, Void> {
    String vCodeUrl = "http://128.199.231.190:8080/api/users/resetPassword";
    String resetPwdUrl = "http://128.199.231.190:8080/api/users/password";
    HttpURLConnection connection;
    String charset = java.nio.charset.StandardCharsets.UTF_8.name();
    String TAG = "ResetPwdWT";
    Context mContext;
    String type = "";
    String id, query;
    int status;
    private InputStream response;
    boolean correctEmail = false;
    boolean forbidden = false;
    private boolean connectionError = false;
    boolean samePwd = false;

    ResetPwdWorkingThread(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    protected Void doInBackground(String... params) {
        type = params[1];
        if (type.equals("GetVCode")) {
            // get v code
            getVCode(params[0]);
        } else if (type.equals("Reset")) {
            // reset
            resetPwd(params[0], params[2]);
        } else {
            Log.e(TAG, "Unexpected type");
        }

        return null;
    }

    private void getVCode(String email) {
        // method for getting verification code
        String userEmail = email;
        try {
            connection = (HttpURLConnection) new URL(vCodeUrl).openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Accept-Charset", charset);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + charset);

            id = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);

            query = formQueryForVCode(userEmail, id);

            Log.i(TAG, "query is " + query);

            connection.connect();

            try (OutputStream output = connection.getOutputStream()) {
                output.write(query.getBytes(charset));
            } catch (IOException e) {
                e.printStackTrace();
            }

            status = connection.getResponseCode();

            Log.i(TAG, "registering client response status is " + status);

            if (status == 200) {
                try {
                    response = connection.getInputStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                BufferedReader br = new BufferedReader(new InputStreamReader(response));
                StringBuffer sb = new StringBuffer();
                String line = "";
                String resp = "";
                Log.i("rHttpBuilder", "Starting to read response");
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                    resp = "Response>>>" + line;
                    Log.i(TAG, resp);
                }
                // check whether email entered is correct
                if (!resp.contains("fail")) {
                    correctEmail = true;
                }

            } else {
                Log.e(TAG, "getVCode" + status);
                connectionError = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        connection.disconnect();
    }

    private void resetPwd(String vCode, String newPwd) {
        // method for resetting pwd
        try {
            connection = (HttpURLConnection) new URL(resetPwdUrl).openConnection();
            connection.setRequestMethod("PUT");
            connection.setDoOutput(true);
            connection.setRequestProperty("Authorization", "Bearer " + vCode);
            connection.setRequestProperty("Accept-Charset", charset);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + charset);

            connection.connect();

            query = formQueryForReset(newPwd);

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
                String line, serverResponse = "";
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                    serverResponse = "Registration Response>>>" + line;
                    Log.i(TAG, serverResponse);
                }

                correctEmail = true;

                if (serverResponse.contains("Same Password")) {
                    samePwd = true;
                }

            } else if (status == 401) {
                forbidden = true;

            } else {
                connectionError = true;
                Log.i(TAG, "Error code " + status);
            }

        } catch (IOException e) {
            e.printStackTrace();
            connectionError = true;
            Log.e(TAG, "Unable to connect to server");
        }
    }

    private String formQueryForVCode(String email, String id) {
        try {
            return String.format("email=%s&id=%s",
                    URLEncoder.encode(email, charset),
                    URLEncoder.encode(id, charset));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Log.e(TAG, "Unable to encode message");
            return "";
        }
    }

    private String formQueryForReset(String password) {
        try {
            String field = "password=%s";
            return String.format(field, URLEncoder.encode(password, charset));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Log.e(TAG, "Unable to encode message");
            return "";
        }
    }

    @Override
    protected void onPostExecute(Void v) {
        if (forbidden) {
            Toast.makeText(mContext, "Please check your verification code", Toast.LENGTH_SHORT).show();
        } else if (!correctEmail) {
            Toast.makeText(mContext, "Wrong email entered", Toast.LENGTH_SHORT).show();
        } else if (samePwd) {
            Toast.makeText(mContext, "Same password as old one", Toast.LENGTH_SHORT).show();
        } else if (connectionError) {
            Toast.makeText(mContext, "Unable to connect to server", Toast.LENGTH_SHORT).show();
        } else {
            if (type.equals("GetVCode")) {
                // send vCode intent
                Intent intent = new Intent("vCodeSuccessful");
                mContext.sendBroadcast(intent);
            } else if (type.equals("Reset")) {
                // send reset intent
                Intent intent = new Intent("ResetSuccessful");
                mContext.sendBroadcast(intent);
            }
        }
    }
}
