package org.example.team_pigeon.movie_pigeon;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Base64;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import android.provider.Settings.Secure;
import android.util.Log;

import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

/**
 * Created by Guo Mingxuan on 2/2/2017.
 */

class SignInHttpBuilder extends AsyncTask<String, Void, Void> {
    String registerClientUrl = "http://128.199.231.190:8080/api/clients";
    String getUrl;
    String authorizeUrl;
    HttpURLConnection connection;
    String charset = java.nio.charset.StandardCharsets.UTF_8.name();
    String query, param1, param2, param3;
    String id, deviceName, secret;
    Context mContext;
    String base64EncodedCredentials;
    String transactionId, transactionBody;
    private InputStream response;
    private int status;
    private String code;
    static final String COOKIES_HEADER = "Set-Cookie";
    static java.net.CookieManager msCookieManager = new java.net.CookieManager();

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

            Log.e("sHttpBuilder", "1st ciphertext is " + base64EncodedCredentials);

            connection.setRequestProperty("Authorization", base64EncodedCredentials);

            BluetoothAdapter myDevice = BluetoothAdapter.getDefaultAdapter();
            deviceName = myDevice.getName();

            id = Secure.getString(mContext.getContentResolver(), Secure.ANDROID_ID);

//            id = "12345";

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

            status = connection.getResponseCode();

            Log.e("sHttpBuilder", "registering client response status is " + status);

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

        connection.disconnect();

        /*-------------------End of Login step 1-------------------------------*/

//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

         /*-------------------Login step 2-------------------------------*/

        Log.e("sHttpBuilder", "id is " + id);
        getUrl = "http://128.199.231.190:8080/api/oauth2/authorize/transactionId?client_id=" + id + "&response_type=code&redirect_uri=baidu.com/";
//        getUrl = "http://192.168.0.101:8080/api/oauth2/authorize/transactionId?client_id=test&response_type=code&redirect_uri=baidu.com/";
        Log.e("sHttpBuilder", "get url is " + getUrl);

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
            Log.e("sHttpBuilder", "Cookies stored");

            status = connection.getResponseCode();
            Log.e("sHttpBuilder", "1st post response status is " + status);

            try {
                response = connection.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            for (String header : connection.getHeaderFields().keySet()) {
                System.out.println(header + ":" + connection.getHeaderField(header));
            }

            if (status == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader(response));
                StringBuffer sb = new StringBuffer();
                String line = "";
                Log.e("rHttpBuilder", "Starting to read response");
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                    System.out.println("Response>>>" + line);
                    transactionId = line;
                }
            } else if (status == 401) {
                // TODO wrong email or password
                Log.e("sHttpBuilder", "Unauthorized");

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        connection.disconnect();
        Log.e("sHttpBuilder", "Obtained transactionId: " + transactionId);

//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        // step 2 part 2
        authorizeUrl = "http://128.199.231.190:8080/api/oauth2/authorize/";
//        authorizeUrl = "http://192.168.0.101:8080/api/oauth2/authorize/";
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
                        TextUtils.join(";",  msCookieManager.getCookieStore().getCookies()));
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
                System.out.println("Unable to encode message");
            }

            Log.e("sHttpBuilder", "transaction body is " + transactionBody);

            try (OutputStream output = connection.getOutputStream()) {
                    output.write(transactionBody.getBytes(charset));
                } catch (IOException e) {
                    e.printStackTrace();
            }

            status = connection.getResponseCode();
            Log.e("sHttpBuilder", "2nd Post response status is " + status);

//            try {
//                response = connection.getInputStream();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

            if (status == 404) {
                response = connection.getErrorStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(response));
                StringBuffer sb = new StringBuffer();
                String line = "";
                Log.e("rHttpBuilder", "Starting to read response");
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                    System.out.println("Response>>>" + line);
                    code = line.replaceAll(".*=", "");
                }
            } else if (status == 401) {
                // TODO wrong email or password
                Log.e("sHttpBuilder", "Unauthorized");

            }

            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.e("sHttpBuilder", "Obtained code: " + code);


        /*-------------------End of Login step 2-------------------------------*/

        /*-------------------Login step 3-------------------------------*/



        /*-------------------End of Login step 3-------------------------------*/

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
