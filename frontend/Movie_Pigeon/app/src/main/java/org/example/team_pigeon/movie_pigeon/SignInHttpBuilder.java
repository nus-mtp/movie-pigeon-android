package org.example.team_pigeon.movie_pigeon;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.util.Base64;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import android.provider.Settings.Secure;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Guo Mingxuan on 2/2/2017.
 */

class SignInHttpBuilder {
    String registerClientUrl = "http://128.199.231.190:8080/api/clients";
    HttpURLConnection c;
    String charset = java.nio.charset.StandardCharsets.UTF_8.name();
    String query, param1, param2, param3;
    String id, deviceName, secret;

    SignInHttpBuilder(Context mContext, String email, String password) {

        request(mContext, email, password);
    }

    private void request(Context mContext, String email, String password) {

        /*-------------------Login step 1-------------------------------*/
        try {
            c = (HttpURLConnection) new URL(registerClientUrl).openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        c.setUseCaches(false);

        // set up basic authentication
        String authEncode = email + ":" + password;
        c.setRequestProperty("Authorization", "basic " +
                Base64.encode(authEncode.getBytes(), Base64.NO_WRAP));

        BluetoothAdapter myDevice = BluetoothAdapter.getDefaultAdapter();
        deviceName = myDevice.getName();

        id = Secure.getString(mContext.getContentResolver(), Secure.ANDROID_ID);

        // TODO not implemented yet -> secret = id
        secret = id;

        query = formQuery(deviceName, id, secret);

        try (OutputStream output = c.getOutputStream()) {
            output.write(query.getBytes(charset));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            InputStream response = c.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            c.connect();
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
            return String.format("param1=%s&param2=%s&param3=%s",
                    URLEncoder.encode(param1, charset),
                    URLEncoder.encode(param2, charset),
                    URLEncoder.encode(param3, charset));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            System.out.println("Unable to encode message");
            return "";
        }
    }
}
