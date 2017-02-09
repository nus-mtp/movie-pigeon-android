package org.example.team_pigeon.movie_pigeon;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import android.provider.Settings.Secure;
import android.util.Log;

import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Guo Mingxuan on 2/2/2017.
 */

class SignInHttpBuilder extends AsyncTask<String, Void, Void> {
    String registerClientUrl = "http://128.199.231.190:8080/api/clients";
    HttpURLConnection connection;
    String charset = java.nio.charset.StandardCharsets.UTF_8.name();
    String query, param1, param2, param3;
    String id, deviceName, secret;
    Context mContext;
    private InputStream response;

    SignInHttpBuilder(Context mContext) {
        this.mContext = mContext;

//        request(mContext, email, password);
    }

    private void request(Context mContext, String email, String password) {

        /*-------------------Login step 1-------------------------------*/
        try {
            connection = (HttpURLConnection) new URL(registerClientUrl).openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Accept-Charset", charset);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + charset);

            // set up basic authentication

            String base64EncodedCredentials = "Basic " + Base64.encodeToString(
                    (email + ":" + password).getBytes(),
                    Base64.NO_WRAP);

            Log.e("sHttpBuilder", "1st ciphertext is " + base64EncodedCredentials);

//            String authEncode = email + ":" + password;
//            connection.setRequestProperty("Authorization", "Basic " +
//                    Base64.encode(authEncode.getBytes(), Base64.NO_WRAP));
//
//            Log.e("sHttpBuilder", "BA code is " + Base64.encode(authEncode.getBytes(), Base64.NO_WRAP));

            connection.setRequestProperty("Authorization", base64EncodedCredentials);

            BluetoothAdapter myDevice = BluetoothAdapter.getDefaultAdapter();
            deviceName = myDevice.getName();

            id = Secure.getString(mContext.getContentResolver(), Secure.ANDROID_ID);

            // TODO not implemented yet -> secret = id
            secret = id;

            query = formQuery(deviceName, id, secret);

            Log.e("sHttpBuilder", "query is " + query);

            connection.connect();

            try (OutputStream output = connection.getOutputStream()) {
                output.write(query.getBytes(charset));
            } catch (IOException e) {
                e.printStackTrace();
            }

            int status = connection.getResponseCode();

            Log.e("sHttpBuilder", "response code is " + status);

            try {
                response = connection.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (status == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader(response));
                StringBuffer sb = new StringBuffer();
                String line = "";
                Log.e("rHttpBuilder", "Starting to read response");
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                    System.out.println("Response>>>" + line);
                }
            } else if (status == 401) {
                // TODO wrong email or password
                Log.e("sHttpBuilder", "Unauthorized");

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*-------------------End of Login step 1-------------------------------*/

        // TODO implement step 2 and 3
    }

    private String formQuery(String name, String id, String secret) {
        param1 = "name=" + name;
        param2 = "id=" + id;
        param3 = "secret=" + secret;
        try {
            return String.format("name=%s&id=%s&secret=%s",
                    URLEncoder.encode(param1, charset),
                    URLEncoder.encode(param2, charset),
                    URLEncoder.encode(param3, charset));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            System.out.println("Unable to encode message");
            return "";
        }
    }

    @Override
    protected Void doInBackground(String... params) {
        String p1, p2;
        p1=params[0];
        p2=params[1];
        Log.e("sHttpBuilder", "Passed in parameters are " + p1 + " " + p2);
        request(mContext, p1, p2);
        return null;
    }
}
