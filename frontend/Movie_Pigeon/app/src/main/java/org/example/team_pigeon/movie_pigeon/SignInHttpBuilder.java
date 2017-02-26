package org.example.team_pigeon.movie_pigeon;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Base64;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import android.provider.Settings.Secure;
import android.util.Log;
import android.widget.Toast;

import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

/**
 * Created by Guo Mingxuan on 2/2/2017.
 */

class SignInHttpBuilder extends AsyncTask<String, Void, Void> {
    private final String TAG = "sHttpBuilder";
    String registerClientUrl = "http://128.199.231.190:8080/api/clients";
    String getUrl;
    String authorizeUrl;
    String tokenUrl = "http://128.199.231.190:8080/api/oauth2/token";
    HttpURLConnection connection;
    String charset = java.nio.charset.StandardCharsets.UTF_8.name();
    String query, param1, param2, param3;
    String id, deviceName, secret;
    Context mContext;
    String base64EncodedCredentials;
    String transactionId, transactionBody, tokenBody;
    private InputStream response;
    private int status;
    private String code, token;
    static final String COOKIES_HEADER = "Set-Cookie";
    static java.net.CookieManager msCookieManager = new java.net.CookieManager();
    String folderMain = "MoviePigeon";
    String folderSignin = "Signin";
    private File mainFolder, signinFolder, credential;
    private FileOutputStream fos;
    private boolean correctEmailPassword = false;
    private boolean connectionError = false;
    private boolean localStorageError = false;

    SignInHttpBuilder(Context mContext) {
        this.mContext = mContext;
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

            base64EncodedCredentials = "Basic " + Base64.encodeToString(
                    (email + ":" + password).getBytes(),
                    Base64.NO_WRAP);

            Log.i(TAG, "1st ciphertext is " + base64EncodedCredentials);

            connection.setRequestProperty("Authorization", base64EncodedCredentials);

            BluetoothAdapter myDevice = BluetoothAdapter.getDefaultAdapter();
            deviceName = myDevice.getName();

            id = Secure.getString(mContext.getContentResolver(), Secure.ANDROID_ID);

            secret = id;

            query = formQuery(deviceName, id, secret);

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
                Log.i("rHttpBuilder", "Starting to read response");
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                    Log.i(TAG, "Response>>>" + line);
                }
                correctEmailPassword = true;
            } else if (status == 401) {
                Log.e(TAG, "Unauthorized");

            } else {
                Log.e(TAG, "step 1" + status);
                connectionError = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        connection.disconnect();

        /*-------------------End of Login step 1-------------------------------*/

        // only proceed to run the following if the email and password matches
        if (correctEmailPassword) {
         /*-------------------Login step 2-------------------------------*/

            Log.i(TAG, "id is " + id);
            getUrl = "http://128.199.231.190:8080/api/oauth2/authorize/transactionId?client_id=" + id + "&response_type=code&redirect_uri=moviepigeon/";
            Log.i(TAG, "get url is " + getUrl);

            try {
                connection = (HttpURLConnection) new URL(getUrl).openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Accept-Charset", charset);
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + charset);
                connection.setRequestProperty("Authorization", base64EncodedCredentials);
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1500.29 Safari/537.36");

                connection.connect();

                Map<String, List<String>> headerFields = connection.getHeaderFields();
                List<String> cookiesHeader = headerFields.get(COOKIES_HEADER);
                if (cookiesHeader != null) {
                    for (String cookie : cookiesHeader) {
                        msCookieManager.getCookieStore().add(null, HttpCookie.parse(cookie).get(0));
                    }
                }
                Log.i(TAG, "Cookies stored");

                status = connection.getResponseCode();
                Log.i(TAG, "1st post response status is " + status);

                try {
                    response = connection.getInputStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                for (String header : connection.getHeaderFields().keySet()) {
                    Log.i(TAG, header + ":" + connection.getHeaderField(header));
                }

                if (status == 200) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(response));
                    StringBuffer sb = new StringBuffer();
                    String line = "";
                    Log.i("rHttpBuilder", "Starting to read response");
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                        Log.i(TAG, "Response>>>" + line);
                        transactionId = line;
                    }
                } else if (status == 401) {
                    // will never come here theoretically as email and password must be correct to pass step 1
                    Log.e(TAG, "Unauthorized");

                } else {
                    Log.e(TAG, "step 2 part 1" + status);
                    connectionError = true;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            connection.disconnect();
            Log.i(TAG, "Obtained transactionId: " + transactionId);

            // step 2 part 2
            authorizeUrl = "http://128.199.231.190:8080/api/oauth2/authorize/";
            try {
                connection = (HttpURLConnection) new URL(authorizeUrl).openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Accept-Charset", charset);
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + charset);
                connection.setRequestProperty("Authorization", base64EncodedCredentials);
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1500.29 Safari/537.36");

                // load cookies
                if (msCookieManager.getCookieStore().getCookies().size() > 0) {
                    // While joining the Cookies, use ',' or ';' as needed. Most of the servers are using ';'
                    connection.setRequestProperty("Cookie",
                            TextUtils.join(";", msCookieManager.getCookieStore().getCookies()));
                }

                connection.connect();

                // forming the body
                param1 = transactionId;
                param2 = "allow";
                try {
                    transactionBody = String.format("transaction_id=%s&Allow=%s",
                            URLEncoder.encode(param1, charset),
                            URLEncoder.encode(param2, charset));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    Log.e(TAG, "Unable to encode message");
                }

                Log.i(TAG, "transaction body is " + transactionBody);

                try (OutputStream output = connection.getOutputStream()) {
                    output.write(transactionBody.getBytes(charset));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                status = connection.getResponseCode();
                Log.i(TAG, "2nd Post response status is " + status);

                if (status == 404) {
                    // actually expected 404, code is inside the error message
                    response = connection.getErrorStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(response));
                    StringBuffer sb = new StringBuffer();
                    String line = "";
                    Log.i("rHttpBuilder", "Starting to read response");
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                        Log.i(TAG, "Response>>>" + line);
                        code = line.replaceAll(".*=", "");
                    }
                } else if (status == 401) {
                    // will never come here theoretically as email and password must be correct to pass step 1
                    Log.e(TAG, "Unauthorized");

                } else {
                    Log.e(TAG, "step 2 part 2" + status);
                    connectionError = true;
                }

                connection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.i(TAG, "Obtained code: " + code);
        /*-------------------End of Login step 2-------------------------------*/

        /*-------------------Login step 3-------------------------------*/

            try {
                connection = (HttpURLConnection) new URL(tokenUrl).openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Accept-Charset", charset);
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + charset);

                // different authentication info: clientid & secret
                String base64EncodedCredentialsForToken = "Basic " + Base64.encodeToString(
                        (id + ":" + secret).getBytes(),
                        Base64.NO_WRAP);
                connection.setRequestProperty("Authorization", base64EncodedCredentialsForToken);

                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1500.29 Safari/537.36");

                // load cookies
                if (msCookieManager.getCookieStore().getCookies().size() > 0) {
                    // While joining the Cookies, use ',' or ';' as needed. Most of the servers are using ';'
                    connection.setRequestProperty("Cookie",
                            TextUtils.join(";", msCookieManager.getCookieStore().getCookies()));
                }

                connection.connect();

                // forming the body
                param1 = code;
                param2 = "authorization_code";
                param3 = "moviepigeon/";
                try {
                    tokenBody = String.format("code=%s&grant_type=%s&redirect_uri=%s",
                            URLEncoder.encode(param1, charset),
                            URLEncoder.encode(param2, charset),
                            URLEncoder.encode(param3, charset));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    Log.e(TAG, "Unable to encode message");
                }

                Log.i(TAG, "body for requesting token is " + tokenBody);

                try (OutputStream output = connection.getOutputStream()) {
                    output.write(tokenBody.getBytes(charset));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                status = connection.getResponseCode();
                Log.i(TAG, "Login step 3 response status is " + status);

                try {
                    response = connection.getInputStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (status == 200) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(response));
                    StringBuffer sb = new StringBuffer();
                    String line = "";
                    Log.i("rHttpBuilder", "Starting to read response");
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                        Log.i(TAG, "Response>>>" + line);
                        token = line;
                    }
                } else if (status == 401) {
                    // will never come here theoretically as email and password must be correct to pass step 1
                    Log.e(TAG, "Unauthorized");
                } else {
                    Log.e(TAG, "step 3 " + status);
                    connectionError = true;
                }

                connection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.i(TAG, "Obtained token: " + token);

        /*-------------------End of Login step 3-------------------------------*/

        /*--------------------save token into a local file--------------------*/
            // check external sd card mounted and create folders
            if (isExternalStorageReadable() && isExternalStorageWritable()) {
                mainFolder = new File(Environment.getExternalStorageDirectory(), folderMain);
                if (!mainFolder.exists()) {
                    mainFolder.mkdirs();
                }
                signinFolder = new File(Environment.getExternalStorageDirectory() + "/" + folderMain, folderSignin);
                if (!signinFolder.exists()) {
                    signinFolder.mkdirs();
                }

                credential = new File(signinFolder.getAbsolutePath(), "credential");
                Log.i(TAG, credential.getAbsolutePath());

                // original token received is like: {"access_token":"AInKmwQRJLvylHTojqcNMqP7FvdXhWVoEIdgtTdRJW7rv68XHz6NpJ32dJPMUE8ZpYqF8zw8dOBGPRHtBJhWAHvniswYXynjH0xKnziVVYN486MLwiUd1WiuVntrTMBq","token_type":"Bearer"}
                // magic number 17 - remove part:   {"access_token":"
                // read till right before the last colon (the colon before "token_type") and -1 for the " sign
                token = token.substring(17, token.lastIndexOf(",") - 1);
                Log.i(TAG, "Trimmed token is " + token);

                try {
                    fos = new FileOutputStream(credential);
                    fos.write(token.getBytes());
                    fos.flush();
                    fos.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else {
                localStorageError = true;
            }
        }
    }

    private String formQuery(String name, String id, String secret) {
        param1 = name;
        param2 = id;
        param3 = secret;
        try {
            return String.format("name=%s&id=%s&secret=%s",
                    URLEncoder.encode(param1, charset),
                    URLEncoder.encode(param2, charset),
                    URLEncoder.encode(param3, charset));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Log.e(TAG, "Unable to encode message");
            return "";
        }
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    @Override
    protected Void doInBackground(String... params) {
        String p1, p2;
        p1=params[0];
        p2=params[1];
        Log.i(TAG, "Passed in parameters are " + p1 + " " + p2);
        request(mContext, p1, p2);
        return null;
    }

    @Override
    protected void onPostExecute(Void v) {
        if (!correctEmailPassword) {
            Toast.makeText(mContext, "Wrong email or password", Toast.LENGTH_SHORT).show();
        } else if (connectionError) {
            Toast.makeText(mContext, "Unable to sign in", Toast.LENGTH_SHORT).show();
        } else if (localStorageError) {
            Toast.makeText(mContext, "Unable to access SD card", Toast.LENGTH_SHORT).show();
        } else {
        /*--------------------Starting HomePageActivity-------------------*/

            Intent intent = new Intent("startHomePageActivity");
            intent.putExtra("Token", token);
            mContext.sendBroadcast(intent);

        /*--------------------End of starting HomePageActivity-------------------*/
        }
    }
}
