package org.example.team_pigeon.movie_pigeon;

import android.os.AsyncTask;
import android.util.Log;

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
    String url = "http://128.199.231.190:8080";
    String registrationURL = "http://128.199.231.190:8080/api/users";
    String charset = java.nio.charset.StandardCharsets.UTF_8.name();
    String param1, param2, param3, query;
    HttpURLConnection connection = null;

//    public RegistrationHttpBuilder(String email, String username, String password) {
//        super();
//        query = formQuery(email, username, password);
//        Log.e("rHttpBuilder", "query built");
////       request(query);
//    }

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

            Log.e("rHttpBuilder", "Finished sending to server");

            int status = connection.getResponseCode();
            Log.e("rHttpBuilder", "response status code is " + status);

            InputStream response = connection.getInputStream();
            // TODO process the response
            BufferedReader br = new BufferedReader(new InputStreamReader(response));
            StringBuffer sb = new StringBuffer();
            String line = "";
            Log.e("rHttpBuilder", "Starting to read response");
            while ((line = br.readLine()) != null) {
                sb.append(line+"\n");
                System.out.println("Response>>>" + line);
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.out.print("Unable to connect to server");
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
            System.out.println("Unable to encode message");
            return "";
        }
    }


    @Override
    protected Void doInBackground(String... params) {
        String p1, p2, p3;
        p1=params[0];
        p2=params[1];
        p3=params[2];
        Log.e("rHttpBuilder", "Passed in parameters are " + p1 + " " + p2 + " " + p3);
        query = formQuery(p1, p2, p3);
        Log.e("rHttpBuilder", "query formed");
        Log.e("rHttpBuilder", "Query is " + query);
        request(query);
        return null;
    }
}
