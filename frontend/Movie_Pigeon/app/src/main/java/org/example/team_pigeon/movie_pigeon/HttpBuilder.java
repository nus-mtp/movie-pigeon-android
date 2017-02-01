package org.example.team_pigeon.movie_pigeon;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Created by Guo Mingxuan on 1/2/2017.
 */

class HttpBuilder {
    String url = "128.199.231.190";
    String typeURL;
    String charset = java.nio.charset.StandardCharsets.UTF_8.name();
    String param1, param2, param3;
    URLConnection connection = null;
    final int signInType = 0;

    HttpBuilder(int type, String email, String username, String password) {
        String query = formQuery(email, username, password);
        // type 0 for sign in, type 1 for registration
        if (type == signInType) {
            request(query, type);
        } else {
            request(query, type);
        }
    }

    private void request(String query, int type) {
        // build registration request here
        if (type == signInType) {
            typeURL = url + "/api/clients";
        } else {
            typeURL = url + "/api/users";
        }
        try {
            connection = new URL(typeURL).openConnection();
            connection.setDoOutput(true); // Triggers POST.
            connection.setRequestProperty("Accept-Charset", charset);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + charset);

            try (OutputStream output = connection.getOutputStream()) {
                output.write(query.getBytes(charset));
            }

            InputStream response = connection.getInputStream();
            // TODO process the response



        } catch (IOException e) {
            e.printStackTrace();
            System.out.print("Unable to connect to server");
        }
    }

    private String formQuery(String email, String username, String password) {
        param1 = "email=" + email;
        param2 = "username=" + username;
        param3 = "password=" + password;
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
