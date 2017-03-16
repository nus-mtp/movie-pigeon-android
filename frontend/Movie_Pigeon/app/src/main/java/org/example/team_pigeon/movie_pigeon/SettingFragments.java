package org.example.team_pigeon.movie_pigeon;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
 * Created by Guo Mingxuan on 16/3/2017.
 */

public class SettingFragments extends Fragment {
    private EditText username, pwd, repeatPwd, nowPwd;
    private TextView tvUsername, tvPwd, tvRepeatPwd, tvNowPwd;
    private String type;
    private Button confirm, cancel;
    private RequestHttpBuilderSingleton builder = RequestHttpBuilderSingleton.getInstance();
    private String[] inputPackage;
    private String TAG = "SettingFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_change_user_info, container, false);
        type = getArguments().getString("type");
        initViews(view);
        setHasOptionsMenu(true);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                talkToServer();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        return view;
    }

    private void initViews(View view) {
        username = (EditText) view.findViewById(R.id.setting_fragment_username_et);
        pwd = (EditText) view.findViewById(R.id.setting_fragment_password_et);
        repeatPwd = (EditText) view.findViewById(R.id.setting_fragment_repeat_password_et);
        nowPwd = (EditText) view.findViewById(R.id.setting_fragment_now_password_et);
        tvUsername = (TextView) view.findViewById(R.id.setting_fragment_username_tv);
        tvPwd = (TextView) view.findViewById(R.id.setting_fragment_password_tv);
        tvRepeatPwd = (TextView) view.findViewById(R.id.setting_fragment_repeat_password_tv);
        tvNowPwd = (TextView) view.findViewById(R.id.setting_fragment_now_password_tv);
        confirm = (Button) view.findViewById(R.id.setting_fragment_confirm);
        cancel = (Button) view.findViewById(R.id.setting_fragment_cancel);
        if (type.equals("username")) {
            pwd.setVisibility(View.GONE);
            repeatPwd.setVisibility(View.GONE);
            nowPwd.setVisibility(View.GONE);
            tvPwd.setVisibility(View.GONE);
            tvRepeatPwd.setVisibility(View.GONE);
            tvNowPwd.setVisibility(View.GONE);
        } else if (type.equals("password")){
            username.setVisibility(View.GONE);
            tvUsername.setVisibility(View.GONE);
            nowPwd.requestFocus();
        }
    }

    private void talkToServer() {
        if (type.equals("username")) {
            inputPackage = new String[3];
            inputPackage[0] = "Change Username";
            inputPackage[1] = String.valueOf(username.getText());
            inputPackage[2] = builder.getToken();
            if (inputPackage[1].equals("")) {
                Log.e(TAG, "inputPackage 1 is " + inputPackage[1]);
                Toast.makeText(getActivity().getApplicationContext(), "Please enter your changes", Toast.LENGTH_SHORT).show();
            } else {
                new SFWorkingThread(getActivity().getApplicationContext()).execute(inputPackage);
            }
        } else if (type.equals("password")) {
            inputPackage = new String[3];
            inputPackage[0] = "Change Password";
            inputPackage[1] = String.valueOf(pwd.getText());
            inputPackage[2] = String.valueOf(nowPwd.getText());
            if (inputPackage[1].equals("")) {
                Toast.makeText(getActivity().getApplicationContext(), "Please enter your changes", Toast.LENGTH_SHORT).show();
            } else if (!inputPackage[1].equals(String.valueOf(repeatPwd.getText()))) {
                Toast.makeText(getActivity().getApplicationContext(), "New passwords don't match", Toast.LENGTH_SHORT).show();
            } else {
                new SFWorkingThread(getActivity().getApplicationContext()).execute(inputPackage);
            }
        }
    }
}

class SFWorkingThread extends AsyncTask<String, Void, Void> {
    String[] inputParams;
    String type;
    String modification;
    private String TAG = "SFWorkingThread";
    String charset = java.nio.charset.StandardCharsets.UTF_8.name();
    String body;
    private HttpURLConnection connection;
    String criticalInfo;
    Boolean connectionError = false;
    Boolean authorized = true;
    Boolean newPassword = true;
    Context mContext;
    private UserInfoSingleton userInfoBulk = UserInfoSingleton.getInstance();
    SFWorkingThread(Context context) {
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
        criticalInfo = inputParams[2];
        Log.i(TAG, "Type is " + type + " and modification is " + modification + " and critical info is " + criticalInfo);
        body = formBody(type);
        request(type);

        return null;
    }

    private String formBody(String type) {
        try {
            String field = type + "=%s";
            return String.format(field, URLEncoder.encode(modification, charset));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Log.e(TAG, "Unable to encode message");
            return "";
        }
    }

    private void request(String type) {
        // find type of the request
        String url = "http://128.199.167.57:8080/api/users/" + type;

        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("PUT");
            connection.setDoOutput(true);
            if (type.equals("username")) {
                connection.setRequestProperty("Authorization", "Bearer " + criticalInfo);
            } else if (type.equals("password")) {
                String email = userInfoBulk.getEmail();
                Log.i(TAG, "Email is " + email);
                String base64EncodedCredentialsForToken = "Basic " + Base64.encodeToString(
                        (email + ":" + criticalInfo).getBytes(),
                        Base64.NO_WRAP);
                connection.setRequestProperty("Authorization", base64EncodedCredentialsForToken);
            }
            connection.setRequestProperty("Accept-Charset", charset);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + charset);

            connection.connect();

            try (OutputStream output = connection.getOutputStream()) {
                output.write(body.getBytes(charset));
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
                    serverResponse = "Modification Response>>>" + line;
                    Log.i(TAG, serverResponse);
                }
                if (serverResponse.contains("Same Password")) {
                    newPassword = false;
                }
            } else if (status == 401) {
                Log.i(TAG, "Error code " + status);
                authorized = false;
                InputStream response = connection.getErrorStream();
                // process the response
                BufferedReader br = new BufferedReader(new InputStreamReader(response));
                StringBuffer sb = new StringBuffer();
                Log.i(TAG, "Starting to read response");
                String line, serverResponse = "";
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                    serverResponse = "Modification Response>>>" + line;
                    Log.i(TAG, serverResponse);
                }
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

    @Override
    protected void onPostExecute(Void v) {
        if (connectionError) {
            Toast.makeText(mContext, "Unable to connect to server", Toast.LENGTH_SHORT).show();
        } else if (!authorized) {
            Toast.makeText(mContext, "Wrong current password", Toast.LENGTH_SHORT).show();
        } else if (!newPassword) {
            Toast.makeText(mContext, "New password is the same as old one", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext, "Change " + type + " successful", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent("userUpdate");
            mContext.sendBroadcast(intent);
        }
    }
}
