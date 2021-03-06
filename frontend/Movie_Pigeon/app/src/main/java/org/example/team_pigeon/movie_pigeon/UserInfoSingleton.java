package org.example.team_pigeon.movie_pigeon;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.stream.JsonReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Guo Mingxuan on 16/3/2017.
 */

class UserInfoSingleton {
    private static UserInfoSingleton instance = null;
    private static String TAG = "UserInfo";
    private static String email, username;
    private static String token;
    private File credential = new File(Environment.getExternalStorageDirectory() + "/MoviePigeon/Signin/credential");

    protected UserInfoSingleton() {
    }

    static UserInfoSingleton getInstance() {
        if (instance == null) {
            instance = new UserInfoSingleton();
            instance.reset();
        }
        return instance;
    }

    String getEmail() {
        return email;
    }

    String getUsername() {
        return username;
    }

    String getToken() {
        return token;
    }

    void reset() {
        token = getStringFromFile(credential.getAbsolutePath());
        new UserInfoGetter().execute();
    }

    String getStringFromFile (String filePath) {
        File fl = new File(filePath);
        FileInputStream fin = null;
        String result = null;
        try {
            fin = new FileInputStream(fl);
            result = convertStreamToString(fin);
            fin.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    static class UserInfoGetter extends AsyncTask<Void, Void, Void> {
        private HttpURLConnection connection;
        private String token, serverResponse;
        private String charset = java.nio.charset.StandardCharsets.UTF_8.name();
        private boolean connectionError = false;
        private String[] userInfo = new String[2];

        @Override
        protected Void doInBackground(Void... params) {
            token = UserInfoSingleton.getInstance().getToken();
            request();
            return null;
        }

        private void request() {
            String url = "http://128.199.167.57:8080/api/users/";

            try {
                connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Authorization", "Bearer " + token);
                connection.setRequestProperty("Accept-Charset", charset);

                Log.i(TAG, "Token is " + token);

                connection.connect();

                Log.i(TAG, "Finished sending to server");

                int status = connection.getResponseCode();
                Log.i(TAG, "get user info status is " + status);

                if (status == 200) {
                    InputStream response = connection.getInputStream();
                    // process the response
                    BufferedReader br = new BufferedReader(new InputStreamReader(response));
                    StringBuffer sb = new StringBuffer();
                    Log.i(TAG, "Starting to read response");
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                        serverResponse = line;
                        Log.i(TAG, "Registration Response>>>" + serverResponse);
                    }
                    try {
                        JsonReader reader = new JsonReader(new StringReader(serverResponse));
                        reader.beginObject();
                        while (reader.hasNext()) {
                            String readStr = reader.nextName();
                            if (readStr.equals("email")) {
                                userInfo[0] = reader.nextString();
                                Log.i(TAG, userInfo[0]);
                            } else if (readStr.equals("username")) {
                                userInfo[1] = reader.nextString();
                                Log.i(TAG, userInfo[1]);
                            } else {
                                reader.skipValue(); //avoid some unhandled events
                            }
                        }
                        reader.endObject();
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (status == 400) {
                    Log.i(TAG, "Error code " + status);
                    InputStream response = connection.getErrorStream();
                    // process the response
                    BufferedReader br = new BufferedReader(new InputStreamReader(response));
                    StringBuffer sb = new StringBuffer();
                    Log.i(TAG, "Starting to read response");
                    String line, serverResponse = "";
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                        serverResponse = "Retrieve userInfo Response>>>" + line;
                        Log.i(TAG, serverResponse);
                    }
                } else if (status == 401) {
                    Log.i(TAG, "Error code " + status);
                    InputStream response = connection.getErrorStream();
                    // process the response
                    BufferedReader br = new BufferedReader(new InputStreamReader(response));
                    StringBuffer sb = new StringBuffer();
                    Log.i(TAG, "Starting to read response");
                    String line, serverResponse = "";
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                        serverResponse = "Retrieve userInfo Response>>>" + line;
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
                Log.e(TAG, "Error in retrieving user info");
            } else {
                Log.i(TAG, "Getting user info successful");
                email = userInfo[0];
                username = userInfo[1];
            }
        }
    }
}
